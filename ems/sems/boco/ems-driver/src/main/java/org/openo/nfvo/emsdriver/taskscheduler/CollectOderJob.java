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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openo.nfvo.emsdriver.commons.constant.Constant;
import org.openo.nfvo.emsdriver.commons.model.CollectMsg;
import org.openo.nfvo.emsdriver.commons.model.CollectVo;
import org.openo.nfvo.emsdriver.commons.utils.DriverThread;
import org.openo.nfvo.emsdriver.messagemgr.MessageChannel;
import org.openo.nfvo.emsdriver.messagemgr.MessageChannelFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class CollectOderJob  implements Job {
	
	private Log log = LogFactory.getLog(DriverThread.class);
	private MessageChannel collectChannel = MessageChannelFactory.getMessageChannel(Constant.COLLECT_CHANNEL_KEY);
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		CollectVo  collectVo = (CollectVo)context.getJobDetail().getJobDataMap().get("collectVo");
		if(collectVo != null){
			CollectMsg collectMsg = new CollectMsg();
			collectMsg.setEmsName(collectVo.getEmsName());
			collectMsg.setId(System.nanoTime());
			collectMsg.setType(collectVo.getType());
			
			try {
				collectChannel.put(collectMsg);
			} catch (InterruptedException e) {
				log.error("collectChannel.put is error ",e);
			}
		}else{
			log.error("collectVo is null, collectMsg is not created! ");
		}
		
		
	}

}
