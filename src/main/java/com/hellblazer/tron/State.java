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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author hhildebrand
 * 
 */
public class State<T> {
    private Action                           entry;
    private Action                           exit;
    private final String                     name;
    private final Map<String, Transition<T>> transitions = new HashMap<>();

    public State(String name) {
        this.name = name;
    }

    public Action entry() {
        return entry;
    }

    public void entry(Action entry) {
        this.entry = entry;
    }

    public Action exit() {
        return exit;
    }

    public void exit(Action exit) {
        this.exit = exit;
    }

    public String name() {
        return name;
    }

    public Map<String, Transition<T>> transitions() {
        return transitions;
    }

    public Transition<T> getTransition(String transition) {
        return transitions.get(transition);
    }

    public Transition<T> transition(Transition<T> transition) {
        if (transitions.containsKey(transition.name())) {
            throw new IllegalArgumentException(
                                               String.format("State '%s' already defines transition '%s'",
                                                             name,
                                                             transition.name()));
        }
        transitions.put(transition.name(), transition);
        return transition;
    }
}
