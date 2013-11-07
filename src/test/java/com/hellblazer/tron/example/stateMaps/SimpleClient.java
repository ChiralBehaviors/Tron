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

import com.hellblazer.tron.EntryAction;
import com.hellblazer.tron.Fsm;
import com.hellblazer.tron.State;
import com.hellblazer.tron.Transition;
import com.hellblazer.tron.example.SimpleFsm;

/**
 * 
 * @author hhildebrand
 * 
 */
public enum SimpleClient implements State {
    CONNECTED() {
        @EntryAction
        public void establishClientSession() {
            SimpleFsm fsm = Fsm.thisFsm();
            fsm.getContext().establishClientSession();
        }
    },
    ESTABLISH_SESSION() {
        @EntryAction
        public void entry() {
            SimpleFsm fsm = Fsm.thisFsm();
            fsm.getContext().awaitAck();
        }
    },
    SEND_MESSAGE() {
        @Transition
        State transmitMessage(String message) {
            SimpleFsm fsm = Fsm.thisFsm();
            fsm.getContext().transmitMessage(message);
            return MessageSent;
        }

        @Transition
        State sendGoodbye() {
            return SEND_GOODBYE;
        }
    },
    MessageSent() {
        @EntryAction
        public void entry() {
            SimpleFsm fsm = Fsm.thisFsm();
            fsm.getContext().awaitAck();
        }

        @Transition
        public State writeReady() {
            return AWAIT_ACK;
        }
    },
    AWAIT_ACK() {
        @Transition
        public State readReady() {
            return ACK_MESSAGE;
        }
    },
    SEND_GOODBYE {
        @EntryAction
        public void entry() {
            SimpleFsm fsm = Fsm.thisFsm();
            fsm.getContext().sendGoodbye();
        }

        @Transition
        public State readReady() {
            return null;
        }
    },
    ACK_MESSAGE() {
        @EntryAction
        public void entry() {
            SimpleFsm fsm = Fsm.thisFsm();
            fsm.getContext().ackReceived();
        }
    };
}