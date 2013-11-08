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
import java.lang.reflect.Proxy;
import java.util.ArrayDeque;
import java.util.Deque;
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
public final class Fsm<Context, Transitions> {
    private static class PopTransition implements InvocationHandler {
        private Object[] args;
        private Method   method;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                                                                        throws Throwable {
            if (method != null) {
                throw new IllegalStateException(
                                                String.format("Pop transition '%s' has already been established",
                                                              method.toGenericString()));
            }
            this.method = method;
            this.args = args;
            return null;
        }
    }

    static final ThreadLocal<Fsm<?, ?>> thisFsm = new ThreadLocal<>();

    public static <Context, Transitions> Fsm<Context, Transitions> construct(Context fsmContext,
                                                                             Class<Transitions> transitions,
                                                                             Enum<?> initialState,
                                                                             boolean sync) {
        if (!transitions.isAssignableFrom(initialState.getClass())) {
            throw new IllegalArgumentException(
                                               String.format("Supplied initial state '%s' does not implement the transitions interface '%s'",
                                                             initialState,
                                                             transitions));
        }
        Fsm<Context, Transitions> fsm = new Fsm<>(fsmContext, sync, transitions);
        @SuppressWarnings("unchecked")
        Transitions initial = (Transitions) initialState;
        fsm.setCurrentState(initial);
        return fsm;
    }

    public static <Context> Context thisContext() {
        @SuppressWarnings("unchecked")
        Fsm<Context, ?> fsm = (Fsm<Context, ?>) thisFsm.get();
        return fsm.getContext();
    }

    public static <Context, Transitions> Fsm<Context, Transitions> thisFsm() {
        @SuppressWarnings("unchecked")
        Fsm<Context, Transitions> fsm = (Fsm<Context, Transitions>) thisFsm.get();
        return fsm;
    }

    private final Context            context;
    private Enum<?>                  current;
    private boolean                  pendingPop = false;
    private Enum<?>                  pendingPush;
    private PopTransition            popTransition;
    private Enum<?>                  previous;
    private final Transitions        proxy;
    private final Deque<Enum<?>>     stack      = new ArrayDeque<>();
    private final Lock               sync;

    private String                   transition;

    private final Class<Transitions> transitionsType;

    Fsm(Context context, boolean sync, Class<Transitions> transitionsType) {
        this.context = context;
        this.sync = sync ? new ReentrantLock() : null;
        this.transitionsType = transitionsType;
        @SuppressWarnings("unchecked")
        Transitions facade = (Transitions) Proxy.newProxyInstance(context.getClass().getClassLoader(),
                                                                  new Class<?>[] { transitionsType },
                                                                  transitionsHandler());
        proxy = facade;
    }

    public void enterStartState() {
        executeEntryAction();
    }

    public Context getContext() {
        return context;
    }

    public Transitions getCurrentState() {
        @SuppressWarnings("unchecked")
        Transitions transitions = (Transitions) current;
        return transitions;
    }

    public Transitions getPreviousState() {
        @SuppressWarnings("unchecked")
        Transitions transitions = (Transitions) previous;
        return transitions;
    }

    public String getTransition() {
        return transition;
    }

    public Transitions getTransitions() {
        return proxy;
    }

    public Transitions pop() {
        if (pendingPop) {
            throw new IllegalStateException("State has already been popped");
        }
        if (pendingPush != null) {
            throw new IllegalStateException("Cannot pop after pushing");
        }
        if (stack.size() == 0) {
            throw new IllegalStateException("State stack is empty");
        }
        pendingPop = true;
        popTransition = new PopTransition();
        @SuppressWarnings("unchecked")
        Transitions pendingTransition = (Transitions) Proxy.newProxyInstance(context.getClass().getClassLoader(),
                                                                             new Class<?>[] { transitionsType },
                                                                             popTransition);
        return pendingTransition;
    }

    public void push(Transitions state) {
        if (pendingPop) {
            throw new IllegalStateException("Cannot push after pop");
        }
        pendingPush = (Enum<?>) state;
    }

    public void setCurrentState(Transitions state) {
        current = (Enum<?>) state;
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

    private void fire(Method t, Object[] arguments) {
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
                return;
            }
        }
        Fsm<?, ?> previousFsm = thisFsm.get();
        thisFsm.set(this);
        previous = current;
        transition = t.toGenericString();
        try {
            Enum<?> nextState;
            try {
                nextState = (Enum<?>) stateTransition.invoke(current, arguments);
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                throw new IllegalStateException(
                                                String.format("Unable to invoke transition %s on state %s",
                                                              t, current), e);
            }
            transitionTo(nextState);
        } finally {
            thisFsm.set(previousFsm);
            if (sync != null) {
                sync.unlock();
            }
        }
    }

    private void normalTransition(Enum<?> nextState) {
        if (nextState != null) { // internal loopback transition
            return;
        }
        executeExitAction();
        current = nextState;
        executeEntryAction();
    }

    private void popTransition() {
        pendingPop = false;
        previous = current;
        executeExitAction();
        current = stack.pop();
        if (popTransition != null) {
            PopTransition prev = popTransition;
            popTransition = null;
            fire(prev.method, prev.args);
        }
    }

    private void pushTransition(Enum<?> nextState) {
        normalTransition(nextState);
        stack.push(current);
        current = pendingPush;
        pendingPush = null;
        executeEntryAction();
    }

    private InvocationHandler transitionsHandler() {
        return new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args)
                                                                            throws Throwable {
                Fsm.this.fire(method, args);
                return null;
            }
        };
    }

    private void transitionTo(Enum<?> nextState) {
        if (pendingPush != null) {
            pushTransition(nextState);
        } else if (pendingPop) {
            popTransition();
        } else {
            normalTransition(nextState);
        }
    }
}
