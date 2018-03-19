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
package org.onap.vfc.nfvo.emsdriver.taskscheduler;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.onap.vfc.nfvo.emsdriver.collector.alarm.AlarmManager;
import org.onap.vfc.nfvo.emsdriver.configmgr.ConfigurationImp;
import org.onap.vfc.nfvo.emsdriver.configmgr.ConfigurationInterface;

public class AlarmManagerTest {

	private AlarmManager alarmManager;
	private ConfigurationInterface configurationInterface;
	
	
	@Before
	public void setUp() throws Exception {
		configurationInterface = new ConfigurationImp();
		
		alarmManager = new AlarmManager();
		alarmManager.setConfigurationInterface(configurationInterface);
		alarmManager.dispose();
	}

	@Test
	public void test() {
		Thread t = new Thread(alarmManager);
		t.start();
	}

	@Test
	public void testGetConfigurationInteface(){
		ConfigurationInterface confInteface = alarmManager.getConfigurationInterface();
		assertEquals(configurationInterface,confInteface);
	}
}
