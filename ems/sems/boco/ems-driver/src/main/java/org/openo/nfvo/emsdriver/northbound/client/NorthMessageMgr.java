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
package org.openo.nfvo.emsdriver.northbound.client;

import java.util.Properties;

import org.openo.nfvo.emsdriver.commons.constant.Constant;
import org.openo.nfvo.emsdriver.commons.utils.DriverThread;
import org.openo.nfvo.emsdriver.configmgr.ConfigurationInterface;
import org.openo.nfvo.emsdriver.messagemgr.MessageChannel;
import org.openo.nfvo.emsdriver.messagemgr.MessageChannelFactory;

public class NorthMessageMgr extends DriverThread{

	private MessageChannel alarmChannel = MessageChannelFactory.getMessageChannel(Constant.ALARM_CHANNEL_KEY);
	private MessageChannel collectResultChannel = MessageChannelFactory.getMessageChannel(Constant.COLLECT_RESULT_CHANNEL_KEY);
	private ConfigurationInterface configurationInterface ;
	
	private boolean threadStop = false;
	
	@Override
	public void dispose() {
		//
		new AlarmMessageRecv().start();
		
		new ResultMessageRecv().start();
	}
	
	
	class AlarmMessageRecv extends Thread{
		long timeStamp = System.currentTimeMillis();
		
		public void run() {
			while(!threadStop){
				
				try {
					if(System.currentTimeMillis() - timeStamp > Constant.ONEMINUTE){
						timeStamp = System.currentTimeMillis();
						
						log.debug("ALARM_CHANNEL Msg size :"+alarmChannel.size());
					}
					
					Object obj = alarmChannel.poll();
					if(obj == null){
						continue;
					}
					if(obj instanceof String){
						//http
						Properties properties = configurationInterface.getProperties();
						String msbAddress = properties.getProperty("msbAddress");
						String url = properties.getProperty("alarm");
						String postUrl = "http://"+msbAddress+url;
						
						HttpClientUtil.doPost(postUrl, (String)obj, Constant.ENCODING_UTF8);
					}
					
				} catch (Exception e) {
					log.error("AlarmMessageRecv exception",e);
				}
			}
		}
	}

	class ResultMessageRecv extends Thread{
		long timeStamp = System.currentTimeMillis();
		
		public void run() {
			while(!threadStop){
				
				try {
					if(System.currentTimeMillis() - timeStamp > Constant.ONEMINUTE){
						timeStamp = System.currentTimeMillis();
						
						log.debug("COLLECT_RESULT_CHANNEL Msg size :"+collectResultChannel.size());
					}
					
					Object obj = collectResultChannel.poll();
					if(obj == null){
						continue;
					}
					if(obj instanceof String){
						//http
						Properties properties = configurationInterface.getProperties();
						String msbAddress = properties.getProperty("msbAddress");
						String url = properties.getProperty("dataNotifyUrl");
						String postUrl = "http://"+msbAddress+url;
						HttpClientUtil.doPost(postUrl, (String)obj, Constant.ENCODING_UTF8);
					}
					
				} catch (Exception e) {
					log.error("AlarmMessageRecv exception",e);
				}
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
