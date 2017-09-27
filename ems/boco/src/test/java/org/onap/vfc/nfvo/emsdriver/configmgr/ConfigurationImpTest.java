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
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectVo;
import org.onap.vfc.nfvo.emsdriver.commons.model.EMSInfo;
import org.onap.vfc.nfvo.emsdriver.configmgr.ConfigurationImp;
import org.onap.vfc.nfvo.emsdriver.configmgr.ConfigurationManager;

public class ConfigurationImpTest {

	
	private ConfigurationManager configurationManager;
	private ConfigurationImp configurationImp;
	@Before
    public void setUp() throws IOException {
		configurationImp = new ConfigurationImp();
		configurationManager = new ConfigurationManager();
		configurationManager.readcfg();
    }
	
	@Test
	public void getAllEMSInfo() {
		
		List<EMSInfo> list = configurationImp.getAllEMSInfo();
		
		assertTrue(list.size() >0);
	}
	
	@Test
	public void getCollectVoByEmsNameAndType() {
		
		CollectVo collectVo = configurationImp.getCollectVoByEmsNameAndType("1234","cm");
		
		assertNotNull(collectVo);
	}
	
	@Test
	public void getProperties() {
		
		Properties properties = configurationImp.getProperties();
		
		assertNotNull(properties);
	}
	
}
