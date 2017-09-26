/**
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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.onap.vfc.nfvo.emsdriver.collector.alarm.AlarmTaskThread;

public class AlarmTaskThreadTest {

	private AlarmTaskThread taskThread;
	
	@Before
    public void setUp() throws IOException {
		taskThread = new AlarmTaskThread();
    }
	
	@Test
	public void build120Alarm(){
		String alarm = "{\"alarmSeq\":495,\"alarmTitle\":\"LTE cell outage\",\"alarmStatus\":1,\"alarmType\":\"processingErrorAlarm\"}";
//		String al = taskThread.build120Alarm(alarm);
		assertNotNull(alarm);
	}
		
}
