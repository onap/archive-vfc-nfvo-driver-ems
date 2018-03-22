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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectVo;

public class AlarmTaskThreadTest {

	private AlarmTaskThread taskThread;
	private AlarmSocketServer server;
	private Thread severThread;

	@Before
	public void setUp() throws IOException {
		severThread = new Thread(new Runnable(){
			public void run() {
				server = new AlarmSocketServer();
				server.socketServer();
			}
		});
		severThread.start();
		CollectVo collectVo = new CollectVo();
		collectVo.setIP("127.0.0.1");
		collectVo.setPort("12345");
		collectVo.setUser("user");
		collectVo.setPassword("12345");
		collectVo.setReadTimeout("10000");
		taskThread = new AlarmTaskThread(collectVo);
	}

	@Test
	public void build120Alarm() {
		String alarm = "{\"alarmSeq\":495,\"alarmTitle\":\"LTE cell outage\",\"alarmStatus\":1,\"alarmType\":\"processingErrorAlarm\"}";
		try {
			new Thread() {
				public void run() {
					try {
						Thread.sleep(3000);
						server.stop();
						taskThread.setStop(true);
						taskThread.close();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
			taskThread.init();
			taskThread.receive();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(alarm);
	}

	@Test
	public void runAlarmTaskThread() {
			taskThread.start();
			//Thread.sleep(3000);
			server.stop();
			severThread.stop();
			taskThread.setStop(true);
			taskThread.close();
	}

}
