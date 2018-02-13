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
package org.onap.vfc.nfvo.emsdriver.northbound.client;

import org.junit.Before;
import org.junit.Test;

public class NorthMessageMgrTest {

	/*private NorthMessageMgr northMessageMgr;
	private ConfigurationInterface configurationInterface;*/
	
	@Before
	public void setUp() throws Exception {
		/*configurationInterface = new ConfigurationInterface() {
			
			public Properties getProperties() {
				Properties pps = new Properties();
				try {
					pps.load(new FileInputStream("conf/ftpconfig.properties"));
				} catch (Exception e) {
				}
				return pps;
			}
			
			public CollectVo getCollectVoByEmsNameAndType(String emsName, String type) {
				// TODO Auto-generated method stub
				return null;
			}
			
			public List<EMSInfo> getAllEMSInfo() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		northMessageMgr = new NorthMessageMgr();
		northMessageMgr.setConfigurationInterface(configurationInterface);
		northMessageMgr.dispose();
	*/
	}

	@Test
	public void test() {
		/*Thread t = new Thread(northMessageMgr);
		t.start();*/
	}

}
