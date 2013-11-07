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
        public SimpleFsm closing() {
            return CLOSED;
        }

        @Override
        public SimpleFsm readError() {
            return CLOSED;
        }

        @Override
        public SimpleFsm writeError() {
            return CLOSED;
        }
    },
    INITIAL() {
        @Override
        public SimpleFsm accepted(BufferHandler handler) {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().setHandler(handler);
            fsm.push(SimpleServer.ACCEPTED);
            return CONNECTED;
        }

        @Override
        public SimpleFsm connected(BufferHandler handler) {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().setHandler(handler);
            fsm.push(SimpleClient.CONNECTED);
            return CONNECTED;
        }
    }, PROTOCOL_ERROR() {

    };

    @Override
    public SimpleFsm accepted(BufferHandler buffer) {
        return PROTOCOL_ERROR;
    }

    @Override
    public SimpleFsm closing() {
        FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
        fsm.push(CONNECTED);
        fsm.getContext();
        return CLOSED;
    }

    @Override
    public SimpleFsm connected(BufferHandler buffer) {
        return PROTOCOL_ERROR;
    }

    @Override
    public SimpleFsm protocolError() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimpleFsm readError() {
        return CLOSED;
    }

    @Override
    public SimpleFsm readReady() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimpleFsm sendGoodbye() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimpleFsm transmitMessage(String message) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimpleFsm writeError() {
        return CLOSED;
    }

    @Override
    public SimpleFsm writeReady() {
        // TODO Auto-generated method stub
        return null;
    }
}