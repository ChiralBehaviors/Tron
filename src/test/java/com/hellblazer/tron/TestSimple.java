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
package com.hellblazer.tron;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.hellblazer.tron.example.BufferHandler;
import com.hellblazer.tron.example.SimpleFsm;
import com.hellblazer.tron.example.SimpleProtocol;
import com.hellblazer.tron.example.impl.SimpleProtocolImpl;
import com.hellblazer.tron.example.stateMaps.Simple;
import com.hellblazer.tron.example.stateMaps.SimpleServer;

/**
 * 
 * @author hhildebrand
 * 
 */
public class TestSimple {
    @Test
    public void testIt() {
        SimpleProtocol protocol = new SimpleProtocolImpl();
        FiniteStateMachine<SimpleProtocol, SimpleFsm> fsm = Fsm.construct(protocol,
                                                                          SimpleFsm.class,
                                                                          Simple.INITIAL,
                                                                          true);
        assertNotNull(fsm);
        BufferHandler handler = new BufferHandler();
        fsm.getTransitions().accepted(handler);
        assertEquals(handler, ((SimpleProtocolImpl) protocol).getHandler());
        assertEquals(SimpleServer.ACCEPTED, fsm.getCurrentState());
    }
}