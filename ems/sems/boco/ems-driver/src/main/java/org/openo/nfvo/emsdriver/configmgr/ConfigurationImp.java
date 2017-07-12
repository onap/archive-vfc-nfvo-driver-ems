/**
 * Copyright 2017 BOCO Corporation.
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
package org.openo.nfvo.emsdriver.configmgr;

import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openo.nfvo.emsdriver.commons.model.CollectVo;
import org.openo.nfvo.emsdriver.commons.model.EMSInfo;


public class ConfigurationImp implements ConfigurationInterface{
	
	private  Log log = LogFactory.getLog(ConfigurationImp.class);
	
	@Override
	public List<EMSInfo> getAllEMSInfo() {
		List<EMSInfo> emsInfos = ConfigurationManager.getAllEMSInfos();
		return emsInfos;
	}
	
	@Override
	public CollectVo getCollectVoByEmsNameAndType(String emsName, String type) {
		CollectVo collectVo = null;
		
		EMSInfo emsInfo = ConfigurationManager.getEMSInfoByName(emsName);
		if(emsInfo != null){
			collectVo = emsInfo.getCollectVoByType(type);
		}else{
			log.error("ConfigurationManager.getEMSInfoByName return null");
		}
		return collectVo;
	}

	@Override
	public Properties getProperties() {
		Properties p = ConfigurationManager.getProperties();
		return p;
	}

	
}
