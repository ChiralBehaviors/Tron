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

package com.hellblazer.tron.documentation;

import java.util.ArrayList;
import java.util.List;

import com.sun.source.tree.ClassTree;

/**
 * @author hhildebrand
 * 
 */
@SuppressWarnings("unused")
public class StateMap {
    private ClassTree              enumeration;
    private final List<State>      states             = new ArrayList<>();
    private final List<Transition> defaultTransitions = new ArrayList<>();
}
