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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.vfc.nfvo.emsdriver.commons.constant.Constant;
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectVo;
import org.onap.vfc.nfvo.emsdriver.commons.utils.StringUtil;
import org.onap.vfc.nfvo.emsdriver.messagemgr.MessageChannel;
import org.onap.vfc.nfvo.emsdriver.messagemgr.MessageChannelFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


public class AlarmTaskThread extends Thread {
    private static final Log log = LogFactory.getLog(AlarmTaskThread.class);

    private HeartBeat heartBeat = null;

    private boolean isStop = false;
    private CollectVo collectVo = null;
    private int readTimeout = Constant.READ_TIMEOUT_MILLISECOND;
    private int reqId;

    private Socket socket = null;
    private BufferedInputStream is = null;
    private BufferedOutputStream dos = null;

    private MessageChannel alarmChannel;


    public AlarmTaskThread() {
        super();
    }

    public AlarmTaskThread(CollectVo collectVo) {

        this.collectVo = collectVo;
    }

    @Override
    public void run() {
        try {
        alarmChannel = MessageChannelFactory.getMessageChannel(Constant.RESULT_CHANNEL_KEY);
            this.init();
            while (!this.isStop) {
                String body;
		try {
			body = this.receive();
			alarmChannel.put(body);
		} catch (Exception e) {
			log.error("alarmChannel.put Exception: ",e);
			reinit();
		}
            }
        } catch (Exception e) {
            log.error("run Exception:",e);
        }
    }


    public String receive() throws IOException {
	try{
        	Msg msg = null;
	        String retString = null;

		while (retString == null && !this.isStop) {
			msg = MessageUtil.readOneMsg(is);
			log.debug("msg = " + msg.toString(true));
			log.info("msg.getMsgType().name = " + msg.getMsgType().name);
			if ("ackLoginAlarm".equalsIgnoreCase(msg.getMsgType().name)) {
				log.debug("receive login ack");
	                boolean suc = this.ackLoginAlarm(msg);
        	        if (suc) {

                	    if (reqId == Integer.MAX_VALUE) 
				reqId=0;

	                    reqId++;
        	            Msg msgheart = MessageUtil.putHeartBeatMsg(reqId);
                	    heartBeat = new HeartBeat(socket, msgheart);
	                    heartBeat.setName("CMCC_JT_HeartBeat");
        	            // start heartBeat
                	    heartBeat.start();
                	}
                retString = null;
            	}

            	if ("ackHeartBeat".equalsIgnoreCase(msg.getMsgType().name)) {
			log.debug("received heartBeat message:" + msg.getBody());
			retString = null;
		}


		if ("realTimeAlarm".equalsIgnoreCase(msg.getMsgType().name)) {
			log.debug("received alarm message");
			retString = msg.getBody();
		}

		if (retString == null) {
			Thread.sleep(100);
		}
		}//while
        return retString;

	}catch(Exception e){
		log.error("receive Error: ",e);
		throw new IOException("receive Error: ",e);
	}
}

    public void init() throws IOException{
        isStop = false;
        //host
        String host = collectVo.getIP();
        //port
        String port = collectVo.getPort();
        //user
        String user = collectVo.getUser();
        //password
        String password = collectVo.getPassword();

	try{
		if((collectVo.getRead_timeout()).trim().length()>0) 
			this.readTimeout = Integer.parseInt(collectVo.getRead_timeout());

	} catch (NumberFormatException e) {
		log.error("Unable to parse read_timout: ",e);
		throw new NumberFormatException("Unable to parse read_timout: " + e); 
	}

        log.info("socket connect host=" + host + ", port=" + port);
        try {
            int portInt = Integer.parseInt(port);
            socket = new Socket(host, portInt);

        } catch (UnknownHostException e) {
	    log.error("remote host [" + host + "]connect fail" + StringUtil.getStackTrace(e));
            throw new UnknownHostException("remote host [" + host + "]connect fail" + e);
        } catch (IOException e1) {
            log.error("create socket IOException ", e1);
            throw new SocketException("create socket IOException " + e1);
        }
        try {
            socket.setSoTimeout(this.readTimeout);
            socket.setTcpNoDelay(true);
            socket.setKeepAlive(true);
        } catch (SocketException e) {
            log.error(" SocketException " + StringUtil.getStackTrace(e));
            throw new SocketException(" SocketException " + StringUtil.getStackTrace(e));
        }
        try {
            dos = new BufferedOutputStream(socket.getOutputStream());

            Msg msg = MessageUtil.putLoginMsg(user, password);

            try {
                log.debug("send login message " + msg.toString(false));
                MessageUtil.writeMsg(msg, dos);

            } catch (Exception e) {
                log.error("send login message is fail " + StringUtil.getStackTrace(e));
            }

            is = new BufferedInputStream(socket.getInputStream());

        } catch (SocketException e) {
            log.error("SocketException ",e);
            throw new SocketException("SocketException " + e);
        }
    }

    private boolean ackLoginAlarm(Msg msg) throws IOException {
        boolean ret = false;
	try {
		String loginres = msg.getBody();
		String[] loginbody = loginres.split(";");
		if (loginbody.length > 1) {
			for (String str : loginbody) {
				if (str.contains("=")) {
					String[] paras1 = str.split("=", -1);
					if ("result".equalsIgnoreCase(paras1[0].trim())) {
						if("succ".equalsIgnoreCase(paras1[1].trim())) 
							ret = true; 
						else ret = false;
					}
				}
			}
		} else {
			log.error("login ack body Incorrect formatbody=" + loginres);
		}

	} catch (Exception e) {
            log.error("pocess login ack fail" + StringUtil.getStackTrace(e));
        }
        if (ret) {
            log.info("login sucess receive login ack " + msg.getBody());
        } else {
            log.error("login fail receive login ack  " + msg.getBody());
            this.close();
            this.isStop = true;
	    throw new IOException("pocess login ack fail");
        }
        return ret;
    }

    public void close() {

        if (heartBeat != null) {
            heartBeat.setStop(true);
        }

        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
            log.error("Unable to close BufferedInput Stream",e);
            } finally {
                is = null;
            }
        }

        if (dos != null) {
            try {
                dos.close();
            } catch (IOException e) {
            log.error("Unable to close BufferedOutput Stream",e);
            } finally {
                dos = null;
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            log.error("Unable to close Socket",e);
            } finally {
                socket = null;
            }

        }
    }

    public void reinit() {
        int time = 0;
        close();
        while (!this.isStop) {
            close();
            time++;
            try {
                Thread.sleep(1000L * 30);
                init();
                return;
            } catch (Exception e) {
                log.error("Number [" + time + "]reconnect [" + collectVo.getIP() + "]fail" + e);
            }
        }
    }

    /**
     * @param isStop the isStop to set
     */
    public void setStop(boolean isStop) {
        this.isStop = isStop;
    }

    /**
     * @return the heartBeat
     */
    public HeartBeat getHeartBeat() {
        return heartBeat;
    }


}
