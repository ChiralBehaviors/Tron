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
public enum SimpleClient implements   SimpleFsm {
    CONNECTED() {
        @EntryAction
        public void establishClientSession() {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().establishClientSession();
        }
    },
    ESTABLISH_SESSION() {
        @EntryAction
        public void entry() {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().awaitAck();
        }
    },
    SEND_MESSAGE() {
        @Override
        public Enum<?> sendGoodbye() {
            return SEND_GOODBYE;
        }

        @Override
        public Enum<?> transmitMessage(String message) {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().transmitMessage(message);
            return MessageSent;
        }
    },
    MessageSent() {
        @EntryAction
        public void entry() {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().awaitAck();
        }

        @Override
        public Enum<?> writeReady() {
            return AWAIT_ACK;
        }
    },
    AWAIT_ACK() {
        @Override
        public Enum<?> readReady() {
            return ACK_MESSAGE;
        }
    },
    SEND_GOODBYE {
        @EntryAction
        public void entry() {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().sendGoodbye();
        }

        @Override
        public Enum<?> readReady() {
            return null;
        }
    },
    ACK_MESSAGE() {
        @EntryAction
        public void entry() {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().ackReceived();
        }
    };

    @Override
    public Enum<?> accepted(BufferHandler buffer) {
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
    public Enum<?> writeError() {
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
    public Enum<?> writeReady() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enum<?> readReady() {
        // TODO Auto-generated method stub
        return null;
    }
}