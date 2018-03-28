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
package org.onap.vfc.nfvo.emsdriver.taskscheduler;

import org.onap.vfc.nfvo.emsdriver.commons.constant.Constant;
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectVo;
import org.onap.vfc.nfvo.emsdriver.commons.model.EMSInfo;
import org.onap.vfc.nfvo.emsdriver.commons.utils.DriverThread;
import org.onap.vfc.nfvo.emsdriver.configmgr.ConfigurationInterface;
import org.quartz.Job;

import java.util.ArrayList;
import java.util.List;


public class CollectManager extends DriverThread {

    private ConfigurationInterface configurationInterface;

    public void dispose() {
        if (configurationInterface != null) {
            List<EMSInfo> emsInfos = configurationInterface.getAllEMSInfo();
            while (isRun() && emsInfos.isEmpty()) {

                emsInfos = configurationInterface.getAllEMSInfo();
                if (emsInfos.isEmpty()) {
                    try {
                        Thread.sleep(1000);
		    } catch (Exception e) {
			    log.error("Sleep interrupted",e);
		    }
                }

            }

            List<CollectVo> collectVos = new ArrayList<>();
            for (EMSInfo emsInfo : emsInfos) {
                //cm
                CollectVo collectVoCm = emsInfo.getCollectVoByType(Constant.COLLECT_TYPE_CM);
                if (collectVoCm != null) {
                    collectVoCm.setEmsName(emsInfo.getName());
                    collectVos.add(collectVoCm);
                }

                //pm
                CollectVo collectVoPm = emsInfo.getCollectVoByType(Constant.COLLECT_TYPE_PM);
                if (collectVoPm != null) {
                    collectVoPm.setEmsName(emsInfo.getName());
                    collectVos.add(collectVoPm);
                }

            }
            if (!collectVos.isEmpty()) {
                this.addCollectJob(collectVos);
                log.info("1 addCollectJob is OK ");
            } else {
                log.error("collectVos size is 0");
            }

        } else {
            log.error("configurationInterface = null,check spring.xml");
        }
    }

    private void addCollectJob(List<CollectVo> collectVos) {
        for (CollectVo collectVo : collectVos) {
            try {
                String jobName = collectVo.getEmsName() + "_" + collectVo.getType() + collectVo.getIP();
                String jobClass = CollectOderJob.class.getName();
                String time = collectVo.getCrontab();
                if (time != null && !"".equals(time)) {
                    QuartzManager.addJob(jobName, jobClass, time, collectVo);
                } else {
                    log.error("type =[" + collectVo.getType() + "]ip=[" + collectVo.getIP() + "] crontab is null,check EMSInfo.xml");
                }

            } catch (Exception e) {
                log.error("addJob is error", e);
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
