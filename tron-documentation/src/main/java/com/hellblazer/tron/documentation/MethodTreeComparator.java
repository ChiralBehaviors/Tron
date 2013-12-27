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

import java.util.Comparator;

import com.sun.source.tree.MethodTree;

/**
 * @author hhildebrand
 * 
 */
public class MethodTreeComparator implements Comparator<MethodTree> {

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(MethodTree m1, MethodTree m2) {
        int compare = m1.getName().toString().compareTo(m2.getName().toString());
        if (compare < 0) {
            return compare;
        }
        if (compare > 0) {
            return compare;
        }
        if (m1.getParameters().size() < m2.getParameters().size()) {
            return -1;
        }
        if (m1.getParameters().size() > m2.getParameters().size()) {
            return 1;
        }
        return m1.getParameters().get(0).toString().compareTo(m2.getParameters().toString());
    }

}
