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
package org.openo.nfvo.emsdriver.collector.alarm;
import java.util.ArrayList;
import java.util.List;

import org.openo.nfvo.emsdriver.commons.constant.Constant;
import org.openo.nfvo.emsdriver.commons.model.CollectVo;
import org.openo.nfvo.emsdriver.commons.model.EMSInfo;
import org.openo.nfvo.emsdriver.commons.utils.DriverThread;
import org.openo.nfvo.emsdriver.configmgr.ConfigurationInterface;

public class AlarmManager  extends DriverThread{

	private ConfigurationInterface configurationInterface;
	
	@Override
	public void dispose() {
		log.debug("AlarmManager is start");
		//get alarm config
		List<EMSInfo> emsInfos = configurationInterface.getAllEMSInfo();
		while(isRun() && emsInfos.size() == 0){
			emsInfos = configurationInterface.getAllEMSInfo();
			if(emsInfos.size() == 0){
				try {
					Thread.sleep(1000);
					log.debug("config is not load");
				} catch (InterruptedException e) {
				}
			}
		}
		List<CollectVo> collectVos = new ArrayList<CollectVo>();
		for(EMSInfo emsInfo : emsInfos){
			//alarm
			CollectVo CollectVo = emsInfo.getCollectVoByType(Constant.COLLECT_TYPE_ALARM);
			if(CollectVo != null){
				CollectVo.setEmsName(emsInfo.getName());
				collectVos.add(CollectVo);
			}
		}
		
		for(CollectVo collectVo : collectVos){
			AlarmTaskThread alarm = new AlarmTaskThread(collectVo);
			alarm.setName(collectVo.getIP()+collectVo.getPort());
			alarm.start();
		}
		
	}

	/**
	 * @return the configurationInterface
	 */
	public ConfigurationInterface getConfigurationInterface() {
		return configurationInterface;
	}

	/**
	 * @param configurationInterface the configurationInterface to set
	 */
	public void setConfigurationInterface(
			ConfigurationInterface configurationInterface) {
		this.configurationInterface = configurationInterface;
	}
	
	

}
