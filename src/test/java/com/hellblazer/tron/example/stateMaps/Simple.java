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
import com.hellblazer.tron.State;
import com.hellblazer.tron.Transition;
import com.hellblazer.tron.example.BufferHandler;
import com.hellblazer.tron.example.SimpleFsm;
import com.hellblazer.tron.example.SimpleProtocol;

/**
 * 
 * @author hhildebrand
 * 
 */
public enum Simple implements State {
    INITIAL() {
        @Transition
        public State accepted(BufferHandler handler) {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().setHandler(handler);
            fsm.push(SimpleServer.ACCEPTED);
            return CONNECTED;
        }

        @Transition
        public State connected(BufferHandler handler) {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().setHandler(handler);
            fsm.push(SimpleClient.CONNECTED);
            return CONNECTED;
        }
    },
    CONNECTED() {
        @Transition
        public State closing() {
            return CLOSED;
        }

        @Transition
        public State readError() {
            return CLOSED;
        }

        @Transition
        public State writeError() {
            return CLOSED;
        }
    },
    CLOSED, PROTOCOL_ERROR() {

    };

    @Transition
    public State closing() {
        FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
        fsm.push(CONNECTED);
        @SuppressWarnings("unused")
        SimpleProtocol context = fsm.getContext();
        return CLOSED;
    }

    public State readError() {
        return CLOSED;
    }

    public State writeError() {
        return CLOSED;
    }
}