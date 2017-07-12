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
package org.openo.nfvo.emsdriver.taskscheduler;

import java.util.ArrayList;
import java.util.List;

import org.openo.nfvo.emsdriver.commons.constant.Constant;
import org.openo.nfvo.emsdriver.commons.model.CollectVo;
import org.openo.nfvo.emsdriver.commons.model.EMSInfo;
import org.openo.nfvo.emsdriver.commons.utils.DriverThread;
import org.openo.nfvo.emsdriver.configmgr.ConfigurationInterface;
import org.quartz.Job;


public class CollectManager  extends DriverThread{

	private ConfigurationInterface configurationInterface;
	public void dispose() {
		if(configurationInterface != null){
			List<EMSInfo> emsInfos = configurationInterface.getAllEMSInfo();
			while(isRun() && emsInfos.size() == 0){
				
				emsInfos = configurationInterface.getAllEMSInfo();
				if(emsInfos.size() == 0){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
				
			}
			
			List<CollectVo> collectVos = new ArrayList<CollectVo>();
			for(EMSInfo emsInfo : emsInfos){
				//cm
				CollectVo CollectVoCm = emsInfo.getCollectVoByType(Constant.COLLECT_TYPE_CM);
				if(CollectVoCm != null){
					CollectVoCm.setEmsName(emsInfo.getName());
					collectVos.add(CollectVoCm);
				}
				
				//pm
				CollectVo CollectVoPm = emsInfo.getCollectVoByType(Constant.COLLECT_TYPE_PM);
				if(CollectVoPm != null){
					CollectVoPm.setEmsName(emsInfo.getName());
					collectVos.add(CollectVoPm);
				}
				
			}
			this.addCollectJob(collectVos);
			log.info("addCollectJob is OK ");
		}else{
			log.error("configurationInterface = null,check spring.xml");
		}
	}
	
	private void addCollectJob(List<CollectVo> collectVos) {
		for(CollectVo collectVo : collectVos){
			try {
				String jobName = collectVo.getEmsName()+"_"+collectVo.getType()+collectVo.getIP();
				Job job = new CollectOderJob();
				String jobClass = job.getClass().getName();
				String time = collectVo.getCrontab();
				if(time != null && !"".equals(time)){
					QuartzManager.addJob(jobName, jobClass, time,collectVo);
				}else{
					log.error("type =["+collectVo.getType()+"]ip=["+collectVo.getIP()+"] crontab is null,check EMSInfo.xml");
				}
			
			} catch (Exception e) {
				log.error("addJob is error",e);
			}
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
