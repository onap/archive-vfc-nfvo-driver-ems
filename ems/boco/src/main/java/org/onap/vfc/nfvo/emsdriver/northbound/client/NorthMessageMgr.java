/**
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
package org.onap.vfc.nfvo.emsdriver.northbound.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Level;
import org.onap.vfc.nfvo.emsdriver.commons.constant.Constant;
import org.onap.vfc.nfvo.emsdriver.commons.utils.DriverThread;
import org.onap.vfc.nfvo.emsdriver.configmgr.ConfigurationInterface;
import org.onap.vfc.nfvo.emsdriver.messagemgr.MessageChannel;
import org.onap.vfc.nfvo.emsdriver.messagemgr.MessageChannelFactory;

import com.alibaba.fastjson.JSONObject;

import evel_javalibrary.att.com.AgentMain;
import evel_javalibrary.att.com.EvelFault;
import evel_javalibrary.att.com.EvelFault.EVEL_SEVERITIES;
import evel_javalibrary.att.com.EvelFault.EVEL_SOURCE_TYPES;
import evel_javalibrary.att.com.EvelFault.EVEL_VF_STATUSES;
import evel_javalibrary.att.com.EvelHeader;
import evel_javalibrary.att.com.EvelScalingMeasurement;

public class NorthMessageMgr extends DriverThread{

	private MessageChannel alarmChannel = MessageChannelFactory.getMessageChannel(Constant.RESULT_CHANNEL_KEY);
	private MessageChannel collectChannel = MessageChannelFactory.getMessageChannel(Constant.COLLECT_CHANNEL_KEY);
	private MessageChannel collectResultChannel = MessageChannelFactory.getMessageChannel(Constant.COLLECT_RESULT_CHANNEL_KEY);
	private ConfigurationInterface configurationInterface ;
	
	private boolean threadStop = false;
	private Level level = Level.DEBUG;
	
	@Override
	public void dispose() {
		Properties properties = configurationInterface.getProperties();
		String event_api_url = properties.getProperty("event_api_url");
		String port = properties.getProperty("port");
		String path = properties.getProperty("path");
		String topic = properties.getProperty("topic");
		String username = properties.getProperty("username");
		String password = properties.getProperty("password");
		String levelStr = properties.getProperty("level");
		if("debug".equals(levelStr)){
			level = Level.DEBUG;
		}else{
			level = Level.INFO;
		}
		
		//login north 
		event_api_url = "http://"+event_api_url;
		try{
			AgentMain.evel_initialize(event_api_url, Integer.parseInt(port),
					path,topic,
					username,
					password,
					level);
		} catch( Exception e ){
			log.error("AgentMain.evel_initialize fail ",e);
		}
		
		new HeatBeatTread().start();
		//
		new AlarmMessageRecv().start();
		
		new ResultMessageRecv().start();
		
		new CollectMessageRecv().start();
	}
	
	
	class HeatBeatTread extends Thread{
		
		public void run() {
			
			while(!threadStop){
				
				try {
					EvelHeader header  = EvelHeader.evel_new_heartbeat("Hearbeat_EMS","EMS-driver");
		            header.evel_nfnamingcode_set("EMS-driver");
		            header.evel_nfcnamingcode_set("EMS-driver");
		            AgentMain.evel_post_event(header);
		            log.debug("HeatBeat send!");
		            try {
		                Thread.sleep(60*1000);
		            } catch( Exception e ){
		                 e.printStackTrace();
		            }
				} catch (Exception e) {
					log.error("HeatBeatTread exception",e);
				}
			}
		}
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
						String result = (String)obj;
						JSONObject reagobj = JSONObject.parseObject(result);
						
						EvelFault evelFault = this.resultEvelFault(reagobj);
						
						//send
						AgentMain.evel_post_event(evelFault);
					}else{
						log.error("AlarmMessageRecv receive Object = "+obj);
					}
					
				} catch (Exception e) {
					log.error("AlarmMessageRecv exception",e);
				}
			}
		}

		private EvelFault resultEvelFault(JSONObject reagobj) {
			
			String eventName = null;
			EvelHeader.PRIORITIES pri = null;
			EVEL_SEVERITIES severity = null;
			EVEL_VF_STATUSES status = null;
			String alarmStatus = reagobj.getString("alarmStatus");
			String origSeverity = reagobj.getString("origSeverity");
			if("0".equals(alarmStatus)){
				status = EVEL_VF_STATUSES.EVEL_VF_STATUS_IDLE;
				eventName = "Fault_"+reagobj.getString("neType")+"_"+reagobj.getString("alarmTitle")+"Cleared";
				
				if("1".equals(origSeverity)){
					severity = EVEL_SEVERITIES.EVEL_SEVERITY_CRITICAL;
					pri = EvelHeader.PRIORITIES.EVEL_PRIORITY_HIGH;
				}else if("2".equals(origSeverity)){
					severity = EVEL_SEVERITIES.EVEL_SEVERITY_MAJOR;
					pri = EvelHeader.PRIORITIES.EVEL_PRIORITY_MEDIUM;
				}else if("3".equals(origSeverity)){
					severity = EVEL_SEVERITIES.EVEL_SEVERITY_MINOR;
					pri = EvelHeader.PRIORITIES.EVEL_PRIORITY_NORMAL;
				}else if("4".equals(origSeverity)){
					severity = EVEL_SEVERITIES.EVEL_SEVERITY_WARNING;
					pri = EvelHeader.PRIORITIES.EVEL_PRIORITY_LOW;
					
				}
			}else{
				status = EVEL_VF_STATUSES.EVEL_VF_STATUS_ACTIVE;
				eventName = "Fault_"+reagobj.getString("neType")+"_"+reagobj.getString("alarmTitle");
				pri = EvelHeader.PRIORITIES.EVEL_PRIORITY_NORMAL;
				severity = EVEL_SEVERITIES.EVEL_SEVERITY_NORMAL;
			}
			
			String evnId = reagobj.getString("alarmId");
			String alarmCondition = reagobj.getString("specificProblem");
			
			String specificProblem = reagobj.getString("specificProblem");
			
			EvelFault flt  = new EvelFault(eventName, evnId,alarmCondition, 
					specificProblem,pri,severity,
					EVEL_SOURCE_TYPES.EVEL_SOURCE_VIRTUAL_NETWORK_FUNCTION,
					status);
			 flt.evel_nfcnamingcode_set("");
			 flt.evel_nfnamingcode_set("");
			 flt.evel_header_type_set("applicationVnf");
			 String eventTime = reagobj.getString("eventTime");
			 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 Date eventTimeD = new Date();
			 try {
				eventTimeD = format.parse(eventTime);
			 } catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
			 flt.evel_start_epoch_set(eventTimeD.getTime());
			 flt.evel_last_epoch_set(eventTimeD.getTime());
			 
			 flt.evel_fault_category_set(reagobj.getString("alarmType"));
			 flt.evel_fault_interface_set(reagobj.getString("objectName"));
			 String neUID = reagobj.getString("neUID");
			 flt.evel_reporting_entity_id_set(neUID.substring(0,9));//
			 flt.evel_reporting_entity_name_set(neUID.substring(0,9));
			 flt.evel_header_set_sourceid(true, reagobj.getString("neName"));
			 flt.evel_header_set_source_name(reagobj.getString("objectName"));
			 
			 flt.evel_header_set_priority(pri);
			 for(String key : reagobj.keySet()){
				 flt.evel_fault_addl_info_add(key, reagobj.getString(key));
			 }
             
             return flt;
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
					log.error("ResultMessageRecv exception",e);
				}
			}
		}
	}
	
	class CollectMessageRecv extends Thread{
		long timeStamp = System.currentTimeMillis();
		
		public void run() {
			while(!threadStop){
				
				try {
					if(System.currentTimeMillis() - timeStamp > Constant.ONEMINUTE){
						timeStamp = System.currentTimeMillis();
						
						log.debug("COLLECT_CHANNEL_KEY Msg size :"+collectChannel.size());
					}
					
					Object obj = collectChannel.poll();
					if(obj == null){
						continue;
					}
					if(obj instanceof Map){
						@SuppressWarnings("unchecked")
						Map<String,String> reMap = (Map<String,String>)obj;
						EvelScalingMeasurement evelScalingMeasurement = this.resultEvelScalingMeasurement(reMap);
						
						//send
						AgentMain.evel_post_event(evelScalingMeasurement);
						
					}else{
						log.error("CollectMessageRecv receive Object = "+obj);
					}
					
				} catch (Exception e) {
					log.error("CollectMessageRecv exception",e);
				}
			}
		}

		private EvelScalingMeasurement resultEvelScalingMeasurement(Map<String,String> reMap) {
			String evname = "Mfvs_"+reMap.get("ElementType")+reMap.get("ObjectType");
			String evid = reMap.get("startTime")+reMap.get("ObjectType")+reMap.get("rmUID");
			int Period = Integer.parseInt(reMap.get("Period")!=null?reMap.get("Period"):"15");
			EvelScalingMeasurement sm  = new EvelScalingMeasurement(Period,evname, evid);

  			for(String key : reMap.keySet()){
  				sm.evel_measurement_custom_measurement_add(reMap.get("ElementType"), key, reMap.get(key));
  				
  			}
  			
  			sm.evel_nfcnamingcode_set("");
  			sm.evel_nfnamingcode_set("");
  			sm.evel_header_type_set("applicationVnf");
  			String rmUID = reMap.get("rmUID");
  			sm.evel_reporting_entity_id_set(rmUID.substring(0,9));//
  			String Dn = reMap.get("Dn");
  			sm.evel_reporting_entity_name_set(Dn.substring(0, Dn != null && Dn.indexOf(";")>0?Dn.indexOf(";"):Dn.length()));
  			sm.evel_header_set_sourceid(true, reMap.get("rmUID"));
  			sm.evel_header_set_source_name(reMap.get("rmUID"));
			 
  			sm.evel_header_set_priority(EvelHeader.PRIORITIES.EVEL_PRIORITY_NORMAL);
           return sm;
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
