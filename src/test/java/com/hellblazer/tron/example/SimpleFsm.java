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
package com.hellblazer.tron.example;

import com.hellblazer.tron.FiniteStateMachine;
import com.hellblazer.tron.State;

/**
 * 
 * @author hhildebrand
 * 
 */
public interface SimpleFsm extends FiniteStateMachine<SimpleProtocol> {
    State accepted(BufferHandler buffer);

    State connected(BufferHandler buffer);

    State closing();

    State readError();

    State writeError();

    State protocolError();
}
