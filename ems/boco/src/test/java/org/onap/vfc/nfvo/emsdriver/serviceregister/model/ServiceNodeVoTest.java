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
package org.onap.vfc.nfvo.emsdriver.serviceregister.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ServiceNodeVoTest {
	
	private ServiceNodeVo serviceNodeVo;

    @Before
    public void setUp() {
    	serviceNodeVo = new ServiceNodeVo();
    }
    
    @Test
    public void testServiceNodeVo(){
    	serviceNodeVo.setIp("127.0.0.1");
    	serviceNodeVo.setPort("100");
    	serviceNodeVo.setTtl(1);
    	assertNotNull(serviceNodeVo.getIp());
    	assertNotNull(serviceNodeVo.getPort());
    	assertNotNull(serviceNodeVo.getTtl());
    }

}
