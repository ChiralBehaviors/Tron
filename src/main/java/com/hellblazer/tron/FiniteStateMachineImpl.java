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
 * @param <Context>
 *            the fsm context interface
 */
final class FiniteStateMachineImpl<Context, Transitions> implements
        FiniteStateMachine<Context, Transitions> {

    static final ThreadLocal<FiniteStateMachineImpl<?, ?>> thisFsm = new ThreadLocal<>();

    private final Context                                  context;
    private Enum<?>                                        current;
    private Enum<?>                                        pendingPush;
    private Enum<?>                                        previous;
    private Transitions                                    proxy;
    private final Deque<Enum<?>>                           stack   = new ArrayDeque<>();
    private final Lock                                     sync;
    private String                                         transition;
    final InvocationHandler                                handler = new InvocationHandler() {

                                                                       @Override
                                                                       public Object invoke(Object proxy,
                                                                                            Method method,
                                                                                            Object[] args)
                                                                                                          throws Throwable {
                                                                           return FiniteStateMachineImpl.this.fire(method,
                                                                                                                   args);
                                                                       }
                                                                   };

    FiniteStateMachineImpl(Context context, boolean sync) {
        this.context = context;
        this.sync = sync ? new ReentrantLock() : null;
    }

    @Override
    public void enterStartState() {
        executeEntryAction();
    }

    @Override
    public Context getContext() {
        return context;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Transitions getCurrentState() {
        return (Transitions) current;
    }

    @Override
    public Transitions getTransitions() {
        return proxy;
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

    @SuppressWarnings("unchecked")
    @Override
    public Transitions previous() {
        return (Transitions) previous;
    }

    @Override
    public void push(Transitions state) {
        pendingPush = (Enum<?>) state;
    }

    @Override
    public void setCurrentState(Transitions state) {
        current = (Enum<?>) state;
    }

    @Override
    public String transition() {
        return transition;
    }

    private void executeEntryAction() {
        for (Method action : current.getClass().getDeclaredMethods()) {
            if (action.isAnnotationPresent(Entry.class)) {
                action.setAccessible(true);
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
            if (action.isAnnotationPresent(Exit.class)) {
                action.setAccessible(true);
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

    private Object fire(Method t, Object[] arguments) {
        Method stateTransition;
        try {
            stateTransition = current.getClass().getDeclaredMethod(t.getName(),
                                                                   t.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new NoTransitionException(current, t.getName());
        }
        stateTransition.setAccessible(true);
        if (sync != null) {
            try {
                sync.lockInterruptibly();
            } catch (InterruptedException e) {
                return null;
            }
        }
        FiniteStateMachineImpl<?, ?> previousFsm = pushThis();
        previous = current;
        transition = t.toGenericString();
        try {
            Enum<?> nextState = (Enum<?>) stateTransition.invoke(current,
                                                                 arguments);
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

    private void pop(FiniteStateMachineImpl<?, ?> previous) {
        thisFsm.set(previous);
    }

    private FiniteStateMachineImpl<?, ?> pushThis() {
        FiniteStateMachineImpl<?, ?> current = thisFsm.get();
        thisFsm.set(this);
        return current;
    }

    void setProxy(Transitions proxy) {
        this.proxy = proxy;
    }
}
