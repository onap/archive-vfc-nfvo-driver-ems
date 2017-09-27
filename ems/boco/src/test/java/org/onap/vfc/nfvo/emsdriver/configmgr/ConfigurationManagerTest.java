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
package org.onap.vfc.nfvo.emsdriver.configmgr;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.onap.vfc.nfvo.emsdriver.commons.model.EMSInfo;
import org.onap.vfc.nfvo.emsdriver.configmgr.ConfigurationManager;

public class ConfigurationManagerTest {

	private ConfigurationManager configurationManager;
	@Before
    public void setUp() throws IOException {
		configurationManager = new ConfigurationManager();
		configurationManager.readcfg();
    }
	
	@Test
	public void getAllEMSInfos() {
		
		List<EMSInfo> list = ConfigurationManager.getAllEMSInfos();
		
		assertTrue(list.size() >0);
	}
	
	@Test
	public void getEMSInfoByName() {
		
		EMSInfo eMSInfo = ConfigurationManager.getEMSInfoByName("1234");
		
		assertNotNull(eMSInfo);
	}
	
	@Test
	public void getProperties() {
		
		Properties properties = ConfigurationManager.getProperties();
		
		assertNotNull(properties);
	}
	
	
}
