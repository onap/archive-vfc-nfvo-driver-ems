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

import java.io.BufferedOutputStream;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openo.nfvo.emsdriver.commons.constant.Constant;

public class HeartBeat extends Thread{
	public  Log log = LogFactory.getLog(HeartBeat.class);
	private BufferedOutputStream out = null;
	private Socket socket=null;
	private Msg heartStr ;
	private boolean stop = false;
	public boolean isStop(){
		return this.stop;
	}
	public void setStop(boolean stop){
		this.stop = stop;
	}
	
	public HeartBeat( Socket socket,Msg heatMessage){
		this.socket=socket;	
		this.heartStr = heatMessage;
	}
	
	public void run(){
		log.debug("HeartBeat start heartStr:"+heartStr.toString(false));
		this.stop=false;
		try {
			while(!this.isStop()){
				out = new BufferedOutputStream(socket.getOutputStream());
				MessageUtil.writeMsg(heartStr,out);
				log.debug("send HeartBeat heartStr:"+heartStr.toString(false));
				Thread.sleep(Constant.ONEMINUTE);
			}
		} catch (Exception e) {
			log.error("send HeartBeat fail ",e);
		} 
		log.debug("HeartBeat thread stop");
	}
	

}
