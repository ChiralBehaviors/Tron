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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author hhildebrand
 * 
 */
public class TestFsm {
    private final URL       contextSourceUrl;
    private final List<URL> stateMapSources = new ArrayList<>();
    private final URL       transitionsSourceUrl;

    public TestFsm() throws MalformedURLException {
        File base = new File(
                             "src/test/java/com/hellblazer/tron/documentation/test/");
        contextSourceUrl = new File(base, "Telephone.java").toURI().toURL();
        transitionsSourceUrl = new File(base, "TelephoneFsm.java").toURI().toURL();
        stateMapSources.add(new File(base, "Call.java").toURI().toURL());
        stateMapSources.add(new File(base, "PhoneNumber.java").toURI().toURL());
    }

    @Test
    public void testTransitionsParsing() throws URISyntaxException {

        Fsm fsm = new Fsm(contextSourceUrl, stateMapSources,
                          transitionsSourceUrl);
        System.out.println(fsm);
    }
}
