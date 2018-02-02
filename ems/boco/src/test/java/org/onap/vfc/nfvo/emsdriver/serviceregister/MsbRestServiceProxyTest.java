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
package org.onap.vfc.nfvo.emsdriver.serviceregister;

import org.junit.Before;
import org.junit.Test;
import org.onap.vfc.nfvo.emsdriver.serviceregister.model.MsbRegisterVo;

import java.util.List;

public class MsbRestServiceProxyTest {

    @Before
    public void upSet() {
        MsbConfiguration.setMsbAddress("");
    }

    @Test
    public void testRegisterService() {
        MsbRegisterVo registerInfo = new MsbRegisterVo();
        registerInfo.setServiceName("ems-driver");
        registerInfo.setUrl("/api/emsdriver/v1");
        MsbRestServiceProxy.registerService(registerInfo);
    }

    @Test
    public void testunRegiserService() {
        MsbRestServiceProxy.unRegiserService("emsdriver", "v1", "172.0.0.1", "9999");
    }

    @Test
    public void testqueryService() {
        List<String> queryService = MsbRestServiceProxy.queryService("emsdriver", "v1");
    }
}
