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
import java.util.List;

/**
 * 
 * @author hhildebrand
 * 
 */
public class Transition<T> {
    private Action            action;
    private final List<Guard> guards = new ArrayList<>();
    private String            name;
    private State<T>          nextState;

    public Action action() {
        return action;
    }

    public void action(Action action) {
        this.action = action;
    }

    public List<Guard> guards() {
        return guards;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public State<T> nextState() {
        return nextState;
    }

    public void nextState(State<T> nextState) {
        this.nextState = nextState;
    }

    protected void evaluate(Object[] parameters) {
        // TODO Auto-generated method stub

    }
}
