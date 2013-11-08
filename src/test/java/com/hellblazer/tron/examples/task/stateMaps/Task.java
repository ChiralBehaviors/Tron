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

package com.hellblazer.tron.examples.task.stateMaps;

/**
 * 
 * @author hhildebrand
 * 
 */
public enum Task {
    /**
     * the task is actively doing work. The task is allowed to run for a
     * specified time limit.
     */
    Running,
    /**
     * the task is waiting to run again since it is not yet completed.
     */
    Suspended,
    /**
     * the task has either completed running or externally stopped.
     */
    Stopped,
    /**
     * the uncompleted task is externally prevented from running again. It will
     * stay in this state until either stopped or unblocked.
     */
    Blocked,
    /**
     * the task is cleaning up allocated resources before entering the stop
     * state.
     */
    Stopping,
    /**
     * the task is completely stopped and all associated resources returned. The
     * task may now be safely deleted. This is the FSM end state.
     */
    Deleted;
}
