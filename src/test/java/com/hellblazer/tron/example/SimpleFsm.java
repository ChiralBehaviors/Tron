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

/**
 * 
 * @author hhildebrand
 * 
 */
public interface SimpleFsm {
    Enum<?> accepted(BufferHandler buffer);

    Enum<?> closing();

    Enum<?> connected(BufferHandler buffer);

    Enum<?> protocolError();

    Enum<?> readError();

    Enum<?> readReady();

    Enum<?> sendGoodbye();

    Enum<?> transmitMessage(String message);

    Enum<?> writeError();

    Enum<?> writeReady();
}
