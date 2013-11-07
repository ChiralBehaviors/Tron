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

import java.lang.reflect.Proxy;

/**
 * 
 * @author hhildebrand
 * 
 */
public final class Fsm {
    public static <Context, Transitions> FiniteStateMachine<Context, Transitions> construct(Context fsmContext,
                                                                                            Class<Transitions> transitions,
                                                                                            Enum<?> initialState,
                                                                                            boolean sync) {
        if (!transitions.isAssignableFrom(initialState.getClass())) {
            throw new IllegalArgumentException(
                                               String.format("Supplied initial state '%s' does not implement the transitions interface '%s'",
                                                             initialState,
                                                             transitions));
        }
        FiniteStateMachineImpl<Context, Transitions> fsm = new FiniteStateMachineImpl<>(
                                                                                        fsmContext,
                                                                                        sync);
        @SuppressWarnings("unchecked")
        Transitions initial = (Transitions) initialState;
        fsm.setCurrentState(initial);
        Class<?> fsmContextClass = fsmContext.getClass();
        @SuppressWarnings("unchecked")
        Transitions proxy = (Transitions) Proxy.newProxyInstance(fsmContextClass.getClassLoader(),
                                                                 new Class<?>[] { transitions },
                                                                 fsm.handler);
        fsm.setProxy(proxy);
        return fsm;
    }

    public static <T extends FiniteStateMachine<?, ?>> T thisFsm() {
        @SuppressWarnings("unchecked")
        T fsm = (T) FiniteStateMachineImpl.thisFsm.get();
        return fsm;
    }
}
