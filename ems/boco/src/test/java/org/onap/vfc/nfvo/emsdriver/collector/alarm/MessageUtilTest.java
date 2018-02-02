/*
 * Copyright 2017 BOCO Corporation.  CMCC Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.vfc.nfvo.emsdriver.collector.alarm;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class MessageUtilTest {

    @Test
    public void putLoginFtp() {
        Msg msg = MessageUtil.putLoginFtp("user", "passwd");

        assertNotNull(msg);
    }

    @Test
    public void putSyncMsg() {
        Msg msg = MessageUtil.putSyncMsg(1, 10);

        assertNotNull(msg);
    }

    @Test
    public void reqSyncAlarmFile() {
        Msg msg = MessageUtil.reqSyncAlarmFile(1, "2017-10-7", "2017-10-7");

        assertNotNull(msg);
    }

    @Test
    public void reqSyncAlarmFileByAlarmSeq() {
        Msg msg = MessageUtil.reqSyncAlarmFileByAlarmSeq(1, 12);

        assertNotNull(msg);
    }

    @Test
    public void reqSyncAlarmFileByTime() {
        Msg msg = MessageUtil.reqSyncAlarmFileByTime(1, "2017-10-7", "2017-10-7");

        assertNotNull(msg);
    }

    @Test
    public void closeConnAlarmMsg() {
        Msg msg = MessageUtil.closeConnAlarmMsg();

        assertNotNull(msg);
    }


}
