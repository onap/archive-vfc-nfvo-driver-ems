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

public enum MsgType {
	
	reqLoginAlarm("reqLoginAlarm",1,"all"),
	ackLoginAlarm("ackLoginAlarm",2,"all"),
	reqSyncAlarmMsg("reqSyncAlarmMsg",3,"msg"),
	ackSyncAlarmMsg("ackSyncAlarmMsg",4,"msg"),
	reqSyncAlarmFile("reqSyncAlarmFile",5,"file"),
	ackSyncAlarmFile("ackSyncAlarmFile",6,"file"),
	ackSyncAlarmFileResult("ackSyncAlarmFileResult",7,"file"),
	reqHeartBeat("reqHeartBeat",8,"all"),
	ackHeartBeat("ackHeartBeat",9,"all"),
	closeConnAlarm("closeConnAlarm",10,"all"),
	realTimeAlarm("realTimeAlarm",0,"all"),
	undefined("undefined",-1,"all");
	
	public int value = -1;
	public String name = null;
	public String type = null;
	
	MsgType(String name,int value,String type){this.name = name;this.value = value;this.type = type;}
	
	public static MsgType getMsgTypeValue(int msgTypeValue){
		
		for(MsgType msgType : MsgType.values()){
			if(msgType.value == msgTypeValue){
				return msgType;
			}
		}
		return undefined;
	}
	
	public static MsgType getMsgTypeName(String msgTypeName){
		
		for(MsgType msgType : MsgType.values()){
			if(msgType.name.toLowerCase().equals(msgTypeName.toLowerCase())){
				return msgType;
			}
		}
		return undefined;
	}
	
	public String toString(){
		return this.name;
	}
	
}
