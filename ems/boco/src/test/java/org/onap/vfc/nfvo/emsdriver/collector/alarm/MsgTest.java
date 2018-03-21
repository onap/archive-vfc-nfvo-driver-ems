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
package org.onap.vfc.nfvo.emsdriver.collector.alarm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.junit.Test;


public class MsgTest {
    
    @Test 
    public void newBodyfromBytesTest() {
    	try {
    		Msg  msg = new Msg("Alarm",MsgType.REALTIME_ALARM);
    		String str = "test";
			msg.newBodyfromBytes(str.getBytes());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
   
    @Test 
    public void setTimestampTest() {
    	Msg  msg = new Msg("Alarm",MsgType.REALTIME_ALARM);
    	Date date = new Date();
    	int secondes = date.getSeconds();
    	msg.setTimeStamp(secondes);
    	int timestamp = msg.getTimeStamp();
    	assertEquals(secondes,timestamp);
    }
    
    @Test
    public void toStringTest(){
    	Msg  msg = new Msg("Alarm",MsgType.REALTIME_ALARM);
    	msg.setBody("NewAlarm");
    	String msgStr = msg.toString();
    	System.out.println("msg to string is "+msgStr);
    	assertNotNull(msgStr);   	
    }
   
}
