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
package org.openo.nfvo.emsdriver.collector;

import org.openo.nfvo.emsdriver.commons.constant.Constant;
import org.openo.nfvo.emsdriver.commons.model.CollectMsg;
import org.openo.nfvo.emsdriver.commons.utils.DriverThread;
import org.openo.nfvo.emsdriver.messagemgr.MessageChannel;
import org.openo.nfvo.emsdriver.messagemgr.MessageChannelFactory;

public class CollectMsgReceiverThread extends DriverThread{

	private long timeStamp = System.currentTimeMillis();
	
	private MessageChannel collectChannel;
	
	private TaskThreadService taskService;
	
	private int thread_max_num = 100;
	
	
	
	@Override
	public void dispose() {
		collectChannel =  MessageChannelFactory.getMessageChannel(Constant.COLLECT_CHANNEL_KEY);
		
		taskService = TaskThreadService.getInstance(thread_max_num);
		taskService.start();
		
		while (isRun()) {
			
			try {
				if(System.currentTimeMillis() - timeStamp > Constant.ONEMINUTE){
					timeStamp = System.currentTimeMillis();
					
					log.debug("COLLECT_CHANNEL Msg size :"+collectChannel.size());
				}
				
				Object obj = collectChannel.poll();
				if(obj == null){
					continue;
				}
				if(obj != null && obj instanceof CollectMsg){
					CollectMsg collectMsg = (CollectMsg)obj;
					taskService.add(collectMsg);
					log.debug("receive a CollectMsg id = "+collectMsg.getId());
				}else{
					log.error("receive Objcet not CollectMsg "+obj);
				}
				
			} catch (Exception e) {
				log.error("dispatch alarm exception",e);
				
			}
		}
		
	}



	/**
	 * @return the thread_max_num
	 */
	public int getThread_max_num() {
		return thread_max_num;
	}



	/**
	 * @param thread_max_num the thread_max_num to set
	 */
	public void setThread_max_num(int thread_max_num) {
		this.thread_max_num = thread_max_num;
	}

	
}
