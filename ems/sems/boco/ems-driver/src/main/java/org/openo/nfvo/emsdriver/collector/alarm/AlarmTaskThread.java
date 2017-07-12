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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openo.nfvo.emsdriver.commons.constant.Constant;
import org.openo.nfvo.emsdriver.commons.model.CollectVo;
import org.openo.nfvo.emsdriver.commons.utils.StringUtil;
import org.openo.nfvo.emsdriver.messagemgr.MessageChannel;
import org.openo.nfvo.emsdriver.messagemgr.MessageChannelFactory;

import com.alibaba.fastjson.JSONObject;


public class AlarmTaskThread extends Thread{
	public  Log log = LogFactory.getLog(AlarmTaskThread.class);
	
	private HeartBeat heartBeat = null;
	
	private boolean isStop = false;
	private CollectVo collectVo = null;
	private int read_timeout = Constant.READ_TIMEOUT_MILLISECOND;
	private int reqId;
	
	private Socket socket = null;
	private BufferedInputStream is = null;
	private BufferedOutputStream dos = null;
	
	private MessageChannel alarmChannel;
	
	public AlarmTaskThread(CollectVo collectVo) {

		this.collectVo = collectVo;
	}

	public void run() {
		alarmChannel = MessageChannelFactory.getMessageChannel(Constant.ALARM_CHANNEL_KEY);
		try {
			this.init();
			while(!this.isStop){
				String body;
				try {
					body = this.receive();
					String alarm120 = this.build120Alarm(body);
					
					this.send120Alarm(alarm120);
				} catch (Exception e) {
					reinit();
				}
			}
		} catch (Exception e) {
			log.error(StringUtil.getStackTrace(e));
		}
	}
	
	private void send120Alarm(String alarm120) {
		
		try {
			alarmChannel.put(alarm120);
		} catch (InterruptedException e) {
			log.error(StringUtil.getStackTrace(e));
		}
	}

	private String  build120Alarm(String body) {
		StringBuilder content = new StringBuilder(
		"<?xml version='1.0' encoding='iso-8859-1'?>\n")
		.append("<WholeMsg MsgMark='120' Priority='2' FieldNum='5'><FM_ALARM_MSG>\n");
	
	
		JSONObject reagobj = JSONObject.parseObject(body);
		
		Set<String> keys = reagobj.keySet();
		
		for (String key : keys) {
			
			String value = (String)reagobj.get(key);
			content.append("<").append(key).append(">");
		    content.append(value);
		    content.append("</").append(key).append(">\n");
		}
		content.append("</FM_ALARM_MSG></WholeMsg>");

		return content.toString();
		
	}

	public String receive() throws Exception {

		Msg msg =null;
		String retString = null;
		
		while (retString == null && !this.isStop) {
			
			msg = MessageUtil.readOneMsg(is);
			
			if("ackLoginAlarm".equalsIgnoreCase(msg.getMsgType().name)){
				log.debug("receive login ack");
				boolean suc = this.ackLoginAlarm(msg);
				if(suc){
					
					if(reqId == Integer.MAX_VALUE){
						reqId = 0;
					}
					reqId ++;
					Msg  msgheart = MessageUtil.putHeartBeatMsg(reqId);
					heartBeat =  new HeartBeat(socket,msgheart); 
					heartBeat.setName("CMCC_JT_HeartBeat");
					// start heartBeat
					heartBeat.start();
				}
				retString = null;
			}
			
			if("ackHeartBeat".equalsIgnoreCase(msg.getMsgType().name)){
				log.debug("received heartBeat��"+msg.getBody());
				retString = null;
			}
			
			
			
			if("realTimeAlarm".equalsIgnoreCase(msg.getMsgType().name)){
				log.debug("received alarm message");
				retString =  msg.getBody();
			}
		}
		return retString;
	}
	
	public void init() throws Exception {
		isStop = false;
		//host
		String host = collectVo.getIP();
		//port
		String port = collectVo.getPort();
		//user
		String user = collectVo.getUser();
		//password
		String password = collectVo.getPassword();
		
		String read_timeout = collectVo.getRead_timeout();
		if ((read_timeout != null) && (read_timeout.trim().length() > 0)) {
		      try {
		        this.read_timeout = Integer.parseInt(read_timeout);
		      } catch (NumberFormatException e) {
		        log.error(StringUtil.getStackTrace(e));
		      }
		    }
		log.debug("socket connect host=" + host + ", port=" + port);
		try {
			int portInt = Integer.parseInt(port);
			socket = new Socket(host, portInt);
			
		} catch (UnknownHostException e) {
			throw new Exception("remote host [" + host + "]connect fail" + StringUtil.getStackTrace(e));
		} catch (IOException e) {
			throw new Exception("create socket IOException " + StringUtil.getStackTrace(e));
		}
		try {
			socket.setSoTimeout(this.read_timeout);
			socket.setTcpNoDelay(true);
			socket.setKeepAlive(true);
		} catch (SocketException e) {
			throw new Exception(" SocketException " + StringUtil.getStackTrace(e));
		}
		try {
			dos = new BufferedOutputStream(socket.getOutputStream());
			
			Msg  msg = MessageUtil.putLoginMsg(user,password);
			
			try {
				log.debug("send login message "+msg.toString(false));
				MessageUtil.writeMsg(msg,dos);
				
			} catch (Exception e) {
				log.error("send login message is fail "+StringUtil.getStackTrace(e));
			}

			is = new BufferedInputStream(socket.getInputStream());
			
		} catch (SocketException e) {
			throw new Exception(StringUtil.getStackTrace(e));
		}
	}
	
	private boolean ackLoginAlarm(Msg msg) throws Exception {
		
		boolean is_success = false;
		try {
			String loginres = msg.getBody();
			//ackLoginAlarm; result=fail(succ); resDesc=username-error
			String [] loginbody = loginres.split(";");
			if(loginbody.length > 1){
				for(String str :loginbody){
		            if(str.contains("=")){
		            	String [] paras1 = str.split("=",-1);
		            	if("result".equalsIgnoreCase(paras1[0].trim())){
							if("succ".equalsIgnoreCase(paras1[1].trim())){
								is_success = true;
							}else{
								is_success = false;
							}
						}
		            }
				}
			}else {
				log.error("login ack body Incorrect formatbody=" + loginres);
			}
			
			
		} catch (Exception e) {
			log.error("pocess login ack fail"+StringUtil.getStackTrace(e));
		}
		if (is_success) {
			log.info("login sucess receive login ack " + msg.getBody());
		} else {
			log.error("login fail receive login ack  " + msg.getBody());
			this.close();
			this.isStop = true;
			throw new Exception("login fail quit");
		}
		return is_success;
	}

	public void close() {

		if(heartBeat != null){
			heartBeat.setStop(true);
		}
		
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
			} finally {
				is = null;
			}
		}

		if (dos != null) {
			try {
				dos.close();
			} catch (IOException e) {
			} finally {
				dos = null;
			}
		}

		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
			} finally {
				socket = null;
			}

		}
	}
	
	public void  reinit() {
		int time = 0;
		close();
		while(!this.isStop) {
			close();
			time++;
			try {
				Thread.sleep(1000 * 10);
				init();
				return;
			} catch (Exception e) {
				log.error("Number ["+time+"]reconnect ["+collectVo.getIP()+"]fail" );
			}
		}
	}
}
