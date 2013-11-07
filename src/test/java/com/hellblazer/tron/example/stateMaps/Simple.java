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

import com.hellblazer.tron.FiniteStateMachine;
import com.hellblazer.tron.Fsm;
import com.hellblazer.tron.example.BufferHandler;
import com.hellblazer.tron.example.SimpleFsm;
import com.hellblazer.tron.example.SimpleProtocol;

/**
 * 
 * @author hhildebrand
 * 
 */
public enum Simple implements SimpleFsm {
    CLOSED,
    CONNECTED() {
        @Override
        public Enum<?> closing() {
            return CLOSED;
        }

        @Override
        public Enum<?> readError() {
            return CLOSED;
        }

        @Override
        public Enum<?> writeError() {
            return CLOSED;
        }
    },
    INITIAL() {
        @Override
        public Enum<?> accepted(BufferHandler handler) {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().setHandler(handler);
            fsm.push(SimpleServer.ACCEPTED);
            return CONNECTED;
        }

        @Override
        public Enum<?> connected(BufferHandler handler) {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().setHandler(handler);
            fsm.push(SimpleClient.CONNECTED);
            return CONNECTED;
        }
    }, PROTOCOL_ERROR() {

    };

    @Override
    public Enum<?> accepted(BufferHandler buffer) {
        return PROTOCOL_ERROR;
    }

    @Override
    public Enum<?> closing() {
        FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
        fsm.push(CONNECTED);
        fsm.getContext();
        return CLOSED;
    }

    @Override
    public Enum<?> connected(BufferHandler buffer) {
        return PROTOCOL_ERROR;
    }

    @Override
    public Enum<?> protocolError() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enum<?> readError() {
        return CLOSED;
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
        return CLOSED;
    }

    @Override
    public Enum<?> writeReady() {
        // TODO Auto-generated method stub
        return null;
    }
}