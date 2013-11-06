/*
 * Copyright (c) 2013 Hal Hildebrand, all rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hellblazer.tron;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * @author hhildebrand
 * 
 * @param <Transitions>
 *            the transition interface
 */
final class FiniteStateMachineImpl<Transitions extends FiniteStateMachine<? super Context>, Context>
        implements FiniteStateMachine<Context> {

    private static final ThreadLocal<FiniteStateMachineImpl<? extends FiniteStateMachine<?>, ?>> thisFsm = new ThreadLocal<>();

    static <Transitions> Transitions thisFsm() {
        @SuppressWarnings("unchecked")
        Transitions fsm = (Transitions) thisFsm.get();
        return fsm;
    }

    private State              pendingPush;
    private State              current;
    final InvocationHandler    handler = new InvocationHandler() {

                                           @Override
                                           public Object invoke(Object proxy,
                                                                Method method,
                                                                Object[] args)
                                                                              throws Throwable {
                                               return FiniteStateMachineImpl.this.invoke(method,
                                                                                         args);
                                           }
                                       };
    private State              previous;
    private Method             transition;
    private final Lock         sync;
    private final Deque<State> stack   = new ArrayDeque<>();
    private final Context      context;

    FiniteStateMachineImpl(Context context, boolean sync) {
        this.context = context;
        this.sync = sync ? new ReentrantLock() : null;
    }

    private Object invoke(Method method, Object[] arguments) {
        try {
            Method fsmMethod = current.getClass().getDeclaredMethod(method.getName(),
                                                                    method.getParameterTypes());
            try {
                return fsmMethod.invoke(this, arguments);
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        } catch (NoSuchMethodException e) {
            // do nothing, this is a transition
        }
        return fire(method, arguments);
    }

    private Object fire(Method t, Object[] arguments) {
        Method stateTransition;
        try {
            stateTransition = current.getClass().getDeclaredMethod(t.getName(),
                                                                   t.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new NoTransitionException(current, t.getName());
        }
        if (sync != null) {
            try {
                sync.lockInterruptibly();
            } catch (InterruptedException e) {
                return null;
            }
        }
        FiniteStateMachineImpl<? extends FiniteStateMachine<?>, ?> previousFsm = pushThis();
        previous = current;
        transition = t;
        try {
            State nextState = (State) stateTransition.invoke(current, arguments);
            if (nextState != null) {
                executeExitAction();
                current = nextState;
                executeEntryAction();
            }
            if (pendingPush != null) {
                stack.push(current);
                current = pendingPush;
                pendingPush = null;
                executeEntryAction();
            }

        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new IllegalStateException(
                                            String.format("Unable to invoke transition %s on state %s",
                                                          t, current), e);
        } finally {
            pop(previousFsm);
            if (sync != null) {
                sync.unlock();
            }
        }
        return null;
    }

    private void executeEntryAction() {
        for (Method action : current.getClass().getDeclaredMethods()) {
            if (action.isAnnotationPresent(EntryAction.class)) {
                try {
                    action.invoke(current, new Object[] {});
                    return;
                } catch (IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    private void executeExitAction() {
        for (Method action : current.getClass().getDeclaredMethods()) {
            if (action.isAnnotationPresent(ExitAction.class)) {
                try {
                    action.invoke(current, new Object[] {});
                    return;
                } catch (IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    private void pop(FiniteStateMachineImpl<? extends FiniteStateMachine<?>, ?> previous) {
        thisFsm.set(previous);
    }

    private FiniteStateMachineImpl<? extends FiniteStateMachine<?>, ?> pushThis() {
        FiniteStateMachineImpl<? extends FiniteStateMachine<?>, ?> current = thisFsm.get();
        thisFsm.set(this);
        return current;
    }

    @Override
    public void push(State state) {
        pendingPush = state;
    }

    @Override
    public void pop() {
        previous = current;
        executeExitAction();
        try {
            current = stack.pop();
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("State stack is empty");
        }
    }

    @Override
    public State current() {
        return current;
    }

    @Override
    public State previous() {
        return previous;
    }

    @Override
    public String transition() {
        return transition.toString();
    }

    @Override
    public Context getContext() {
        return context;
    }
}
