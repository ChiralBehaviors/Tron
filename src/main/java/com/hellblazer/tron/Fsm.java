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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Finite State Machine implementation.
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
            if (this.method != null) {
                throw new IllegalStateException(
                                                String.format("Pop transition '%s' has already been established",
                                                              method.toGenericString()));
            }
            this.method = method;
            this.args = args;
            return null;
        }
    }

    private static Logger                       DEFAULT_LOG = LoggerFactory.getLogger(Fsm.class);
    private static final ThreadLocal<Fsm<?, ?>> thisFsm     = new ThreadLocal<>();

    /**
     * Construct a new instance of a finite state machine.
     * 
     * @param fsmContext
     *            - the object used as the action context for this FSM
     * @param transitions
     *            - the interface class used to define the transitions for this
     *            FSM
     * @param initialState
     *            - the initial state of the FSM
     * @param sync
     *            - true if this FSM is to synchronize state transitions. This
     *            is required for multi-threaded use of the FSM
     * @return the Fsm instance
     */
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

    /**
     * 
     * @return the Context of the currently executing Fsm
     */
    public static <Context> Context thisContext() {
        @SuppressWarnings("unchecked")
        Fsm<Context, ?> fsm = (Fsm<Context, ?>) thisFsm.get();
        return fsm.getContext();
    }

    /**
     * 
     * @return the currrently executing Fsm
     */
    public static <Context, Transitions> Fsm<Context, Transitions> thisFsm() {
        @SuppressWarnings("unchecked")
        Fsm<Context, Transitions> fsm = (Fsm<Context, Transitions>) thisFsm.get();
        return fsm;
    }

    private final Context            context;
    private Enum<?>                  current;
    private Logger                   log;
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
        this.log = DEFAULT_LOG;
        @SuppressWarnings("unchecked")
        Transitions facade = (Transitions) Proxy.newProxyInstance(context.getClass().getClassLoader(),
                                                                  new Class<?>[] { transitionsType },
                                                                  transitionsHandler());
        proxy = facade;
    }

    /**
     * Execute the initial state's entry action. Note that we do not guard
     * against multiple invocations.
     */
    public void enterStartState() {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Entering start state %s",
                                    prettyPrint(current)));
        }
        executeEntryAction();
    }

    /**
     * 
     * @return the action context object of this Fsm
     */
    public Context getContext() {
        return context;
    }

    /**
     * 
     * @return the current state of the Fsm
     */
    public Transitions getCurrentState() {
        @SuppressWarnings("unchecked")
        Transitions transitions = (Transitions) current;
        return transitions;
    }

    /**
     * 
     * @return the previous state of the Fsm, or null if no previous state
     */
    public Transitions getPreviousState() {
        @SuppressWarnings("unchecked")
        Transitions transitions = (Transitions) previous;
        return transitions;
    }

    /**
     * 
     * @return the String representation of the current transition
     */
    public String getTransition() {
        return transition;
    }

    /**
     * 
     * @return the Transitions object that drives this Fsm through its
     *         transitions
     */
    public Transitions getTransitions() {
        return proxy;
    }

    /**
     * Pop the state off of the stack of pushed states. This state will become
     * the current state of the Fsm. Answer the Transitions object that may be
     * used to send a transition to the popped state.
     * 
     * @return the Transitions object that may be used to send a transition to
     *         the popped state.
     */
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

    /**
     * Push the current state of the Fsm on the state stack. The supplied state
     * becomes the current state of the Fsm
     * 
     * @param state
     *            - the new current state of the Fsm.
     */
    public void push(Transitions state) {
        if (pendingPop) {
            throw new IllegalStateException("Cannot push after pop");
        }
        pendingPush = (Enum<?>) state;
    }

    /**
     * Set the current state of the Fsm. The entry action for this state will
     * not be called.
     * 
     * @param state
     *            - the new current state of the Fsm
     */
    public void setCurrentState(Transitions state) {
        current = (Enum<?>) state;
    }

    /**
     * Set the Logger for this Fsm.
     * 
     * @param log
     *            - the Logger of this Fsm
     */
    public void setLog(Logger log) {
        this.log = log;
    }

    @Override
    public String toString() {
        return "Fsm [current=" + current + ", previous=" + previous
               + ", transition=" + transition + "]";
    }

    private void executeEntryAction() {
        for (Method action : current.getClass().getDeclaredMethods()) {
            if (action.isAnnotationPresent(Entry.class)) {
                action.setAccessible(true);
                try {
                    if (log.isTraceEnabled()) {
                        log.trace(String.format("Executing entry action %s for state %s",
                                                prettyPrint(action),
                                                prettyPrint(current)));
                    }
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
                if (log.isTraceEnabled()) {
                    log.trace(String.format("Executing exit action %s for state %s",
                                            prettyPrint(action),
                                            prettyPrint(current)));
                }
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
            stateTransition = current.getClass().getMethod(t.getName(),
                                                           t.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                                            String.format("The state %s does not implement the transition %s",
                                                          prettyPrint(current),
                                                          prettyPrint(t)));
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
        transition = prettyPrint(t);
        if (log.isTraceEnabled()) {
            log.trace(String.format("Executing transition %s on state %s",
                                    transition, prettyPrint(current)));
        }
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
        if (nextState == null) { // internal loopback transition
            if (log.isTraceEnabled()) {
                log.trace(String.format("Internal loopback transition to state %s",
                                        prettyPrint(nextState)));
            }
            return;
        }
        executeExitAction();
        if (log.isTraceEnabled()) {
            log.trace(String.format("Transitioning to state %s from %s",
                                    prettyPrint(nextState),
                                    prettyPrint(current)));
        }
        current = nextState;
        executeEntryAction();
    }

    private void popTransition() {
        pendingPop = false;
        previous = current;
        executeExitAction();
        if (log.isTraceEnabled()) {
            log.trace(String.format("Popping to state %s",
                                    prettyPrint(stack.getFirst())));
        }
        current = stack.pop();
        if (popTransition != null) {
            PopTransition prev = popTransition;
            popTransition = null;
            if (log.isTraceEnabled()) {
                log.trace(String.format("Pop transition %s.%s",
                                        prettyPrint(current),
                                        prettyPrint(prev.method)));
            }
            fire(prev.method, prev.args);
        }
    }

    private String prettyPrint(Enum<?> state) {
        if (state == null) {
            return "null";
        }
        Class<?> enclosingClass = state.getClass().getEnclosingClass();
        return String.format("%s.%s",
                             (enclosingClass != null ? enclosingClass
                                                    : state.getClass()).getSimpleName(),
                             state.name());
    }

    private String prettyPrint(Method transition) {
        StringBuilder builder = new StringBuilder();
        builder.append(transition.getName());
        builder.append('(');
        Class<?>[] parameters = transition.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            builder.append(parameters[i].getSimpleName());
            if (i != parameters.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(')');
        return builder.toString();
    }

    private void pushTransition(Enum<?> nextState) {
        normalTransition(nextState);
        if (log.isTraceEnabled()) {
            log.trace(String.format("Pushing state %s", prettyPrint(nextState)));
        }
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
