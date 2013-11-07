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
package com.hellblazer.tron.example.stateMaps;

import com.hellblazer.tron.example.BufferHandler;
import com.hellblazer.tron.example.SimpleFsm;

/**
 * 
 * @author hhildebrand
 * 
 */
public enum SimpleServer implements SimpleFsm {
    ACCEPTED, AWAIT_MESSAGE, PROCESS_MESSAGE, SESSION_ESTABLISHED, ;

    @Override
    public Enum<?> accepted(BufferHandler buffer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enum<?> closing() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enum<?> connected(BufferHandler buffer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enum<?> protocolError() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enum<?> readError() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enum<?> readReady() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enum<?> sendGoodbye() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enum<?> transmitMessage(String message) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enum<?> writeError() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enum<?> writeReady() {
        // TODO Auto-generated method stub
        return null;
    }
}