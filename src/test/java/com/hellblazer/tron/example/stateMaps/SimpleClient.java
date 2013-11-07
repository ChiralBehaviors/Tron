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

import com.hellblazer.tron.Entry;
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
public enum SimpleClient implements SimpleFsm {
    ACK_MESSAGE() {
        @Entry
        public void entry() {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().ackReceived();
        }
    },
    AWAIT_ACK() {
        @Override
        public SimpleFsm readReady() {
            return ACK_MESSAGE;
        }
    },
    CONNECTED() {
        @Entry
        public void establishClientSession() {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().establishClientSession();
        }
    },
    ESTABLISH_SESSION() {
        @Entry
        public void entry() {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().awaitAck();
        }

        @Override
        public SimpleFsm readReady() {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().enableSend();
            return SEND_MESSAGE;
        }
    },
    MessageSent() {
        @Entry
        public void entry() {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().awaitAck();
        }

        @Override
        public SimpleFsm writeReady() {
            return AWAIT_ACK;
        }
    },
    SEND_GOODBYE {
        @Entry
        public void entry() {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().sendGoodbye();
        }

        @Override
        public SimpleFsm readReady() {
            return null;
        }
    },
    SEND_MESSAGE() {
        @Override
        public SimpleFsm sendGoodbye() {
            return SEND_GOODBYE;
        }

        @Override
        public SimpleFsm transmitMessage(String message) {
            FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
            fsm.getContext().transmitMessage(message);
            return MessageSent;
        }
    };

    @Override
    public SimpleFsm accepted(BufferHandler buffer) {
        return null;
    }

    @Override
    public SimpleFsm closing() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimpleFsm connected(BufferHandler buffer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimpleFsm protocolError() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimpleFsm readError() {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimpleFsm writeReady() {
        // TODO Auto-generated method stub
        return null;
    }
}