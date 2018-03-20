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

import static org.junit.Assert.assertNotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

public class MessageUtilTest {

    @Test
    public void putLoginFtp() {
        Msg msg = MessageUtil.putLoginFtp("user", "passwd");

        assertNotNull(msg);
    }

    @Test
    public void putSyncMsg() {
        Msg msg = MessageUtil.putSyncMsg(1, 10);

        assertNotNull(msg);
    }

    @Test
    public void reqSyncAlarmFile() {
        Msg msg = MessageUtil.reqSyncAlarmFile(1, "2017-10-7", "2017-10-7");

        assertNotNull(msg);
    }

    @Test
    public void reqSyncAlarmFileByAlarmSeq() {
        Msg msg = MessageUtil.reqSyncAlarmFileByAlarmSeq(1, 12);

        assertNotNull(msg);
    }

    @Test
    public void reqSyncAlarmFileByTime() {
        Msg msg = MessageUtil.reqSyncAlarmFileByTime(1, "2017-10-7", "2017-10-7");

        assertNotNull(msg);
    }

    @Test
    public void closeConnAlarmMsg() {
        Msg msg = MessageUtil.closeConnAlarmMsg();

        assertNotNull(msg);
    }
    
    @Test
    public void putloginMsg() {
    	String user = "test";
    	String passwd = "test";
    	Msg msg = MessageUtil.putLoginMsg(user, passwd);
    	assertNotNull(msg);	
    }

    @Test
    public void putHeartBeatMsg() {
    	Msg msg = MessageUtil.putHeartBeatMsg(1);
    	assertNotNull(msg);	
    }
    
    @Test
    public void readOneMsg(){
    	try {
    	String fileName = System.getProperty("user.dir") + "/data/" + "bbb.txt";
    	FileInputStream fis = new FileInputStream(fileName);
        BufferedInputStream bis=new BufferedInputStream(fis);
    	Msg msg = MessageUtil.readOneMsg(bis);
    	assertNotNull(msg);	    	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		   System.out.println("File is not found");
       } catch (IOException e){
    	   System.out.println("There is IOException");
       }
    }
    
    
    
    @Test
    public void writeMsg(){
        try {
    	String msgBody = "Heartbeat";
    	Msg msg = new Msg(msgBody,MsgType.ACK_HEARTBEAT);
    	String fileName = System.getProperty("user.dir") + "/data/" + "aaa.txt";
    	FileOutputStream fos=new FileOutputStream(fileName);
        BufferedOutputStream bos=new BufferedOutputStream(fos);
        MessageUtil.writeMsg(msg, bos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("There is IOException");
		}
        
    }
}
