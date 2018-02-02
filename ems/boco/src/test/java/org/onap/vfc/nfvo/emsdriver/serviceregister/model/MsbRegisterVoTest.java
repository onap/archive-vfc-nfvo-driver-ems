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
package org.onap.vfc.nfvo.emsdriver.serviceregister.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class MsbRegisterVoTest {

    private MsbRegisterVo msbRegisterVo;

    @Before
    public void setUp() {
        msbRegisterVo = new MsbRegisterVo();
    }

    @Test
    public void testMsbRegisterVo() {
        msbRegisterVo.setProtocol("REST");
        msbRegisterVo.setServiceName("serviceName");
        msbRegisterVo.setUrl("http://");
        msbRegisterVo.setVersion("version");
        msbRegisterVo.setVisualRange("vr");
        ArrayList<ServiceNodeVo> list = new ArrayList<ServiceNodeVo>();
        list.add(new ServiceNodeVo());
        msbRegisterVo.setNodes(list);
        msbRegisterVo.getNodes();
        msbRegisterVo.getProtocol();
        msbRegisterVo.getServiceName();
        msbRegisterVo.getUrl();
        msbRegisterVo.getVersion();
        msbRegisterVo.getVisualRange();

    }

}
