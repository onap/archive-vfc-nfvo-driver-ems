/*
 * Copyright 2017 BOCO Corporation. CMCC Technologies Co., Ltd
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
package org.onap.vfc.nfvo.emsdriver.collector.alarm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.onap.vfc.nfvo.emsdriver.commons.constant.Constant;
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectVo;
import org.onap.vfc.nfvo.emsdriver.commons.model.EMSInfo;
import org.onap.vfc.nfvo.emsdriver.commons.utils.DriverThread;
import org.onap.vfc.nfvo.emsdriver.configmgr.ConfigurationInterface;
import org.onap.vfc.nfvo.emsdriver.configmgr.ConfigurationManager;

import java.util.ArrayList;
import java.util.List;

public class AlarmManager extends DriverThread {
	protected static final Logger logger = LoggerFactory.getLogger(AlarmManager.class);
	private ConfigurationInterface configurationInterface;

	@Override
	public void dispose() {
		logger.debug("AlarmManager is start");
		// get alarm CONFIG_PROPERTIES_LOCATION
		List<EMSInfo> emsInfos = configurationInterface.getAllEMSInfo();
		while (isRun() && emsInfos.isEmpty()) {
			emsInfos = configurationInterface.getAllEMSInfo();
			if (emsInfos.isEmpty()) {
				try {
					Thread.sleep(1000);
					logger.debug("The configuration properties from "
							+ ConfigurationManager.CONFIG_PROPERTIES_LOCATION
							+ " is not load");
				} catch (Exception e) {
					logger.error("Exception", e);
				}
			}
		}
		List<CollectVo> collectVos = new ArrayList<>();
		for (EMSInfo emsInfo : emsInfos) {
			// alarm
			CollectVo collectVo = emsInfo.getCollectVoByType(Constant.COLLECT_TYPE_ALARM);
			if (collectVo != null) {
				collectVo.setEmsName(emsInfo.getName());
				collectVos.add(collectVo);
			} else {
				logger.error("emsInfo.getCollectVoByType(EMS_RESOUCE) result CollectVo = null emsInfo ="
						+ emsInfo);
			}
		}

		for (CollectVo collectVo : collectVos) {
			AlarmTaskThread alarm = new AlarmTaskThread(collectVo);
			alarm.setName(collectVo.getIP() + collectVo.getPort());
			alarm.start();
			logger.info("AlarmTaskThread is start");
		}

	}

	/**
	 * @return the configurationInterface
	 */
	public ConfigurationInterface getConfigurationInterface() {
		return configurationInterface;
	}

	/**
	 * @param configurationInterface  the configurationInterface to set
	 */
	public void setConfigurationInterface(
			ConfigurationInterface configurationInterface) {
		this.configurationInterface = configurationInterface;
	}

}
