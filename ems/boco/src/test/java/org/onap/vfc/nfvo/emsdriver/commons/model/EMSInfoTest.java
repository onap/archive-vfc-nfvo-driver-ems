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
package org.onap.vfc.nfvo.emsdriver.commons.model;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectVo;
import org.onap.vfc.nfvo.emsdriver.commons.model.EMSInfo;

public class EMSInfoTest {
	
	private EMSInfo EMSInfo;

    @Before
    public void setUp() {
    	EMSInfo = new EMSInfo();
    }

    @Test
    public void testEMSInfo() {
    	EMSInfo.setName("emsName");
    	Map<String,CollectVo> collectMap = new HashMap<String,CollectVo>();
    	CollectVo collectVo = new CollectVo();
    	collectVo.setEmsName("emsName");
    	collectMap.put("pm", collectVo);
    	EMSInfo.putCollectMap("pm", collectVo);
    	
        assertNotNull(EMSInfo.toString());
    }
}
