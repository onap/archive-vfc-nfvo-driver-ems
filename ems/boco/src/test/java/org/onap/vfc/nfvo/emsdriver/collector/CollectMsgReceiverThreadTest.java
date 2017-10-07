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
package org.onap.vfc.nfvo.emsdriver.collector;

import org.junit.Before;
import org.junit.Test;
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectMsg;

public class CollectMsgReceiverThreadTest {

	CollectMsgReceiverThread col = null;
	@Before
	public void setUp() {
		col = new CollectMsgReceiverThread();
	}
	
	
	@Test
	public void dispose() {
		new Thread(){
			public void run() {
				try {
					Thread.sleep(10);
					col.setRun(false);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		col.setRun(true);
		col.dispose();
		col.getTaskService().stopTask();
		col.getThread_max_num();
		col.setThread_max_num(1);
		
	}
}
