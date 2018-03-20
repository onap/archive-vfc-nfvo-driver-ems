/**
 * Copyright 2017 BOCO Corporation. CMCC Technologies Co., Ltd
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onap.vfc.nfvo.emsdriver.commons.model;

import java.util.HashMap;
import java.util.Map;

public class EMSInfo {

    private String name;


    private Map<String, CollectVo> collectMap = new HashMap<>();

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public CollectVo getCollectVoByType(String type) {
        return this.collectMap.get(type);
    }

    public void putCollectMap(String type, CollectVo collectVo) {

        this.collectMap.put(type, collectVo);
    }

    @Override
    public String toString() {
        return "EMSInfo [name=" + name + ", collectMap=" + collectMap + "]";
    }
}
