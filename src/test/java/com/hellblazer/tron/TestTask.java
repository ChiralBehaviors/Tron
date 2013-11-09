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

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.hellblazer.tron.examples.task.Task;
import com.hellblazer.tron.examples.task.TaskFsm;
import com.hellblazer.tron.examples.task.TaskModel;

/**
 * 
 * @author hhildebrand
 * 
 */
public class TestTask {
    @Test
    public void testIt() {
        long timeslice = 100;
        TaskModel model = mock(TaskModel.class);
        Fsm<TaskModel, TaskFsm> fsm = Fsm.construct(model, TaskFsm.class,
                                                    Task.Suspended, false);
        TaskFsm transitions = fsm.getTransitions();
        assertEquals(Task.Suspended, fsm.getCurrentState());
        transitions.start(timeslice);
        assertEquals(Task.Running, fsm.getCurrentState());
        transitions.suspended();
        assertEquals(Task.Suspended, fsm.getCurrentState());
        transitions.start(timeslice);
        transitions.block();
        assertEquals(Task.Blocked, fsm.getCurrentState());
        transitions.unblock();
        assertEquals(Task.Suspended, fsm.getCurrentState());
        transitions.start(timeslice);
        transitions.stop();
        assertEquals(Task.Stopping, fsm.getCurrentState());
    }
}
