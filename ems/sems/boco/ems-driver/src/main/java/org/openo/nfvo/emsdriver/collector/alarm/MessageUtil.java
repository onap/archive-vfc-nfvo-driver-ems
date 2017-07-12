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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;



public class MessageUtil
{
	public static String MSG_BODY_ENCODE_CHARSET="UTF-8";
	public static int MSG_BUF_SIZE=8096 ;
	
	public static Msg putLoginMsg(String user,String passwd)
	{
		String body = String.format(Msg.reqLoginAlarm, user,passwd,"msg");
		Msg msg = new Msg(body,MsgType.reqLoginAlarm);
		

		return msg;
		
	}
	public static Msg putLoginFtp(String user,String passwd)
	{
		String body = String.format(Msg.reqLoginAlarm, user,passwd,"ftp");
		Msg msg = new Msg(body,MsgType.reqLoginAlarm);
		

		return msg;
		
	}
	
	public static Msg putSyncMsg(int reqId,int alarmSeq)
	{
		String body = String.format(Msg.syncAlarmMessageMsg, reqId,alarmSeq);
		Msg msg = new Msg(body,MsgType.reqSyncAlarmMsg);
		

		return msg;
		
	}
	
	public static Msg putHeartBeatMsg(int reqId)
	{
		String body = String.format(Msg.reqHeartBeat, reqId);
		Msg msg = new Msg(body,MsgType.reqHeartBeat);
		return msg;
		
	}
	
	public static Msg reqSyncAlarmFile(int reqId, String startTime,String endTime) {
		String body = String.format(Msg.syncActiveAlarmFileMsg, reqId,startTime,endTime);
		Msg msg = new Msg(body,MsgType.reqSyncAlarmFile);
		return msg;
	}
	
	public static Msg reqSyncAlarmFileByAlarmSeq(int reqId, int alarmSeq) {
		String body = String.format(Msg.syncAlarmMessageByalarmSeq, reqId,alarmSeq);
		Msg msg = new Msg(body,MsgType.reqSyncAlarmFile);
		return msg;
	}
	
	public static Msg reqSyncAlarmFileByTime(int reqId, String startTime,String endTime) {
		String body = String.format(Msg.syncAlarmFileMsg, reqId,startTime,endTime);
		Msg msg = new Msg(body,MsgType.reqSyncAlarmFile);
		return msg;
	}
	
	public static Msg closeConnAlarmMsg()
	{
		String body = String.format(Msg.disconnectMsg);
		Msg msg = new Msg(body,MsgType.closeConnAlarm);
		return msg;
	}

	public static Msg readOneMsg(BufferedInputStream is) throws Exception
	{
		byte[] inputB = new byte[9];
		
		ByteArrayInputStream bais = null;
		DataInputStream ois = null;
		
		Msg msg = new Msg();
		try {
			DataInputStream dis = new DataInputStream(is);
			dis.readFully(inputB);
			bais = new ByteArrayInputStream(inputB);
			ois = new DataInputStream(bais);
			short StartSign = ois.readShort();
			if (StartSign != Msg.StartSign) {
				throw new Exception("start sign is [" + Msg.StartSign
						+ "],not is [" + StartSign + "]");
			}
			int msgType = ois.readByte();
			msg.setMsgType(MsgType.getMsgTypeValue(msgType));
			int timeStamp = ois.readInt();
			msg.setTimeStamp(timeStamp);
			int bodylength = ois.readShort();
			msg.setLenOfBody(bodylength);
			byte b[] = new byte[bodylength];
			dis.readFully(b);
			msg.newBodyfromBytes(b);
		} catch (Exception e) {
			throw new Exception(e);
		}finally{
			if(bais != null){
				bais.close();
			}
			if(ois != null){
				ois.close();
			}
		}
		
		return msg;
	}
	
	public static void writeMsg(Msg msg,BufferedOutputStream dout) throws Exception{
		
		ByteArrayOutputStream byteOutStream = null;
		DataOutputStream oos = null;
		try {
			byteOutStream = new ByteArrayOutputStream(9);
			oos = new DataOutputStream(byteOutStream);
			oos.writeShort(Msg.StartSign);
			oos.writeByte(msg.getMsgType().value);
			oos.writeInt(Msg.creatMsgTimeStamp());
			oos.writeShort(msg.getBodyLenNow());
			
			dout.write(byteOutStream.toByteArray());
			
			dout.write(msg.getBodyBytes());
			dout.flush();
		} catch (Exception e) {
			throw new Exception(e);
		}finally{
			if(oos != null){
				oos.close();
			}
			if(byteOutStream != null){
				byteOutStream.close();
			}
		}
		
	}
	
}
