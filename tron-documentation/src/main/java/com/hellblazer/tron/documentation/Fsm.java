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

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.MethodTree;

/**
 * @author hhildebrand
 * 
 */
@SuppressWarnings("unused")
public class Fsm {
    private final List<URL>      stateMapSources;
    private final URL            contextSource;
    private final URL            transitionsSource;
    private final List<StateMap> stateMaps = new ArrayList<>();
    private List<MethodTree>     transitions;

    public Fsm(URL contextSource, List<URL> stateMapSources,
               URL transitionsSource) throws URISyntaxException {
        this.contextSource = contextSource;
        this.stateMapSources = stateMapSources;
        this.transitionsSource = transitionsSource;
        gatherTransitions();
    }

    private void gatherTransitions() throws URISyntaxException {
        CompilationUnitTree unit = JavaCompilerUtils.compile(transitionsSource);
        TransitionsVisitor visitor = new TransitionsVisitor();
        unit.accept(visitor, null);
        transitions = visitor.getTransitions();
    }
}
