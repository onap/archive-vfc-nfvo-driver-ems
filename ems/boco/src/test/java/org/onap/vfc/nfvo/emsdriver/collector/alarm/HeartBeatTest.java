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

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

public class HeartBeatTest {

    private AlarmSocketServer server;
    
    @Before
    public void setUp() throws IOException {
        new Thread() {
            public void run() {
                server = new AlarmSocketServer();
                server.socketServer();
            }
        }.start();

    }	
	
    @Test
	public void testHeartBeatThread(){
		try {
			Socket socket = new Socket("127.0.0.1",12345);
			Msg msg = MessageUtil.putHeartBeatMsg(1);
			HeartBeat heartBeat = new HeartBeat(socket,msg);
			heartBeat.run();
			Thread.sleep(10);
			heartBeat.setStop(true);
			server.stop();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


}
