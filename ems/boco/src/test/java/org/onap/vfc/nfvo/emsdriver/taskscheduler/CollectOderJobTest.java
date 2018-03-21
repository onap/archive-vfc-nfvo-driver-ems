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

import org.junit.Test;
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectVo;
import org.quartz.Job;

public class CollectOderJobTest {
	
	@Test
	public void executeCollectOderJob(){
		CollectVo collectVo = new CollectVo();
		collectVo.setEmsName("zteEms");
		collectVo.setType("ems-p");
		collectVo.setIP("127.0.0.1");
		collectVo.setCrontab("*/5 * * * * ?");
		
		  String jobName = collectVo.getEmsName() + "_" + collectVo.getType() + collectVo.getIP();
          Job job = new CollectOderJob();
          String jobClass = job.getClass().getName();
          String time = collectVo.getCrontab();
          if (time != null && !"".equals(time)) {
              QuartzManager.addJob(jobName, jobClass, time, collectVo);
              try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
	              QuartzManager.removeJob(jobName);				
			}
          } else {
              System.out.println("type =[" + collectVo.getType() + "]ip=[" + collectVo.getIP() + "] crontab is null");
          }
		
	}

	
}
