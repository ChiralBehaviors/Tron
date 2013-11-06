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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;

/**
 * 
 * @author hhildebrand
 * 
 */
public class Fsm<T> {
    private State<T>                       currentState;
    private Logger                         log;
    private final Map<String, StateMap<T>> machines = new HashMap<>();
    private String                         name;
    private State<T>                       previousState;
    private final List<State<T>>           stack    = new ArrayList<>();
    @SuppressWarnings("unused")
    private final Semaphore                sync;
    private Transition<T>                  transition;

    public Fsm(boolean synchronizeTransitions) {
        if (synchronizeTransitions) {
            sync = new Semaphore(1);
        } else {
            sync = null;
        }
    }

    public State<T> currentState() {
        return currentState;
    }

    public void currentState(State<T> state) {
        currentState = state;
    }

    public Logger log() {
        return log;
    }

    public void log(Logger log) {
        this.log = log;
    }

    public Map<String, StateMap<T>> machines() {
        return machines;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public State<T> previousState() {
        return previousState;
    }

    public void previousState(State<T> state) {
        previousState = state;
    }

    public List<State<T>> stack() {
        return stack;
    }

    public Transition<T> transition() {
        return transition;
    }

    public void transition(Transition<T> transition) {
        this.transition = transition;
    }

    public void fire(String transition, Object... parameters) {
        Transition<T> next = currentState.getTransition(transition);
        if (next == null) {
            throw new NoTransitionException(currentState, transition);
        }
        next.evaluate(parameters);
    }

    public StateMap<?> stateMap(String name) {
        if (machines.containsKey(name)) {
            throw new IllegalArgumentException(
                                               String.format("State Map '%s' is already defined",
                                                             name));
        }
        StateMap<T> map = new StateMap<>(name);
        machines.put(name, map);
        return map;
    }
}
