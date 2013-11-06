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
public class StateMap<T> {
    private final String                name;
    private final Map<String, State<T>> states = new HashMap<>();

    public StateMap(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public Map<String, State<T>> states() {
        return states;
    }

    public State<T> state(String name) {
        if (states.containsKey(name)) {
            throw new IllegalArgumentException(
                                               String.format("State '%s' is already defined",
                                                             name));
        }
        State<T> state = new State<T>(name);
        states.put(name, state);
        return state;
    }
}
