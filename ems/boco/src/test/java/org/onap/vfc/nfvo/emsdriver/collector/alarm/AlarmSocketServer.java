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
package org.onap.vfc.nfvo.emsdriver.collector.alarm;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class AlarmSocketServer {

	boolean stop = false;
	InputStreamReader isr = null;
	BufferedOutputStream dos = null;
	Socket socket = null;
	ServerSocket server = null;
	public void socketServer(){
		
		try {
			server = new ServerSocket(12345);
			socket = server.accept();
			
			socket.setSoTimeout(3*1000);
			socket.setTcpNoDelay(true);
			socket.setKeepAlive(true);
			InputStream in = socket.getInputStream();
			isr = new InputStreamReader(in);
			dos = new BufferedOutputStream(socket.getOutputStream());
			while(!stop){
				int len = 0;
				char b[] = new char[1024 * 8];
				try{
					len = isr.read(b, 0, b.length);
				}catch(SocketTimeoutException e){
					e.printStackTrace();
					break;
				}
				if (len > 0) {
					String restlu = new String(b, 0, len);
					if(restlu.contains("reqLoginAlarm")){
						Msg msg = new Msg("ackLoginAlarm;result=succ;resDesc= ",MsgType.ackLoginAlarm);
						
						MessageUtil.writeMsg(msg,dos);
					}
					if(restlu.contains("reqHeartBeat")){
						Msg msg = new Msg("ackHeartBeat;result=succ;resDesc= ",MsgType.ackHeartBeat);
						
						MessageUtil.writeMsg(msg,dos);
					}
					Msg msg = new Msg("realTimeAlarm;result=succ;resDesc= ",MsgType.realTimeAlarm);
					
					MessageUtil.writeMsg(msg,dos);
					
					
			
				}else{
					System.out.println("len:" +len);
					Thread.sleep(10);
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void stop(){
		try {
			stop = true;
			if(isr != null){
				isr.close();
			}
			if(dos != null){
				dos.close();
			}
			if(socket != null){
				socket.close();
			}
			if(server != null){
				server.close();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
