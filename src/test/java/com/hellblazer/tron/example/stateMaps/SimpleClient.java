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
import com.hellblazer.tron.Fsm;
import com.hellblazer.tron.IllegalTransition;
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
            context().ackReceived();
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
            context().establishClientSession();
        }
    },
    ESTABLISH_SESSION() {
        @Entry
        public void entry() {
            context().awaitAck();
        }

        @Override
        public SimpleFsm readReady() {
            context().enableSend();
            return SEND_MESSAGE;
        }
    },
    MessageSent() {
        @Entry
        public void entry() {
            context().awaitAck();
        }

        @Override
        public SimpleFsm writeReady() {
            return AWAIT_ACK;
        }
    },
    SEND_GOODBYE {
        @Entry
        public void entry() {
            context().sendGoodbye();
        }

        @Override
        public SimpleFsm readReady() {
            SimpleFsm popTransition = fsm().pop();
            popTransition.closing();
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
            context().transmitMessage(message);
            return MessageSent;
        }
    };

    private static SimpleProtocol context() {
        SimpleProtocol context = Fsm.thisContext();
        return context;
    }

    private static Fsm<SimpleProtocol, SimpleFsm> fsm() {
        Fsm<SimpleProtocol, SimpleFsm> fsm = Fsm.thisFsm();
        return fsm;
    }

    @Override
    public SimpleFsm accepted(BufferHandler buffer) {
        return null;
    }

    @Override
    public SimpleFsm closing() {
        SimpleFsm popTransition = fsm().pop();
        popTransition.closing();
        return null;
    }

    @Override
    public SimpleFsm connected(BufferHandler buffer) {
        return protocolError();
    }

    @Override
    public SimpleFsm protocolError() {
        SimpleFsm popTransition = fsm().pop();
        popTransition.protocolError();
        return null;
    }

    @Override
    public SimpleFsm readError() {
        return protocolError();
    }

    @Override
    public SimpleFsm readReady() {
        return protocolError();
    }

    @Override
    public SimpleFsm sendGoodbye() {
        throw new IllegalTransition(this, "sendGoodbye");
    }

    @Override
    public SimpleFsm transmitMessage(String message) {
        throw new IllegalTransition(this, "transmitMessage");
    }

    @Override
    public SimpleFsm writeError() {
        return protocolError();
    }

    @Override
    public SimpleFsm writeReady() {
        return protocolError();
    }
}