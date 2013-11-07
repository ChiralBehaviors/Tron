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
    @SafeVarargs
    public static <Context, T> FiniteStateMachine<Context, T> construct(Context fsmContext,
                                                                        Class<T> transitions,
                                                                        State initialState,
                                                                        boolean sync,
                                                                        Class<? extends State>... stateMaps) {
        FiniteStateMachineImpl<Context, T> fsm = new FiniteStateMachineImpl<>(
                                                                              fsmContext,
                                                                              sync);
        fsm.setCurrentState(initialState);
        Class<?> fsmContextClass = fsmContext.getClass();
        @SuppressWarnings("unchecked")
        FiniteStateMachine<Context, T> proxy = (FiniteStateMachine<Context, T>) Proxy.newProxyInstance(fsmContextClass.getClassLoader(),
                                                                                                       new Class<?>[] {
                                                                                                               transitions,
                                                                                                               FiniteStateMachine.class },
                                                                                                       fsm.handler);
        fsm.setProxy(proxy);
        return proxy;
    }

    public static <T extends FiniteStateMachine<?, ?>> T thisFsm() {
        @SuppressWarnings("unchecked")
        T fsm = (T) FiniteStateMachineImpl.thisFsm.get();
        return fsm;
    }
}
