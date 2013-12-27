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
package com.hellblazer.tron.documentation.test;

/**
 * 
 * @author hhildebrand
 * 
 */
public interface TelephoneFsm {
    TelephoneFsm clockTimer();

    TelephoneFsm depositMoney();

    TelephoneFsm dialingDone(int callType, String areaCode, String exchange,
                             String local);

    TelephoneFsm digit(String digit);

    TelephoneFsm emergency();

    TelephoneFsm invalidDigit();

    TelephoneFsm invalidNumber();

    TelephoneFsm leftOfHook();

    TelephoneFsm lineBusy();

    TelephoneFsm loopTimer();

    TelephoneFsm nycTemp();

    TelephoneFsm offHook();

    TelephoneFsm offHookTimer();

    TelephoneFsm onHook();

    TelephoneFsm ringTimer();

    TelephoneFsm time();
}
