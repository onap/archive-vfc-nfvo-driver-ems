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

import java.io.*;


public class MessageUtil {
    public static final String MSG_BODY_ENCODE_CHARSET = "UTF-8";
    public static final int MSG_BUF_SIZE = 8096;

    public static Msg putLoginMsg(String user, String passwd) {
        String body = String.format(Msg.REQ_LOGIN_ALARM, user, passwd, "msg");
        return new Msg(body, MsgType.REQ_LOGIN_ALARM);

    }

    public static Msg putLoginFtp(String user, String passwd) {
        String body = String.format(Msg.REQ_LOGIN_ALARM, user, passwd, "ftp");
        return new Msg(body, MsgType.REQ_LOGIN_ALARM);
    }

    public static Msg putSyncMsg(int reqId, int alarmSeq) {
        String body = String.format(Msg.SYNC_ALARM_MESSAGE, reqId, alarmSeq);
        return new Msg(body, MsgType.REQ_SYNC_ALARM_MSG);

    }

    public static Msg putHeartBeatMsg(int reqId) {
        String body = String.format(Msg.REQ_HEARTBEAT, reqId);
        return new Msg(body, MsgType.REQ_HEARTBEAT);

    }

    public static Msg reqSyncAlarmFile(int reqId, String startTime, String endTime) {
        String body = String.format(Msg.SYNC_ACTIVE_ALARM_FILE_MSG, reqId, startTime, endTime);
        return new Msg(body, MsgType.REQ_SYNC_ALARM_FILE);
    }

    public static Msg reqSyncAlarmFileByAlarmSeq(int reqId, int alarmSeq) {
        String body = String.format(Msg.SYNC_ALARM_MESSAGE_BY_ALARM_SEQ, reqId, alarmSeq);
        return new Msg(body, MsgType.REQ_SYNC_ALARM_FILE);
    }

    public static Msg reqSyncAlarmFileByTime(int reqId, String startTime, String endTime) {
        String body = String.format(Msg.SYNC_ALARM_FILE_MSG, reqId, startTime, endTime);
        return new Msg(body, MsgType.REQ_SYNC_ALARM_FILE);
    }

    public static Msg closeConnAlarmMsg() {
        String body = String.format(Msg.DISCONNECT_MSG);
        return new Msg(body, MsgType.CLOSE_CONN_ALARM);
    }

    public static Msg readOneMsg(BufferedInputStream is) throws IOException {
        byte[] inputB = new byte[9];
        Msg msg = new Msg();
        DataInputStream dis = new DataInputStream(is);
        try( 
            ByteArrayInputStream bais = new ByteArrayInputStream(inputB);
            DataInputStream ois = new DataInputStream(bais)){
            dis.readFully(inputB);
            short startSign = ois.readShort();
            if (startSign != Msg.START_SIGN) {
                throw new IOException("start sign is [" + Msg.START_SIGN
                        + "],not is [" + startSign + "]");
            }
            int msgType = ois.readByte();
            msg.setMsgType(MsgType.getMsgTypeValue(msgType));
            int timeStamp = ois.readInt();
            msg.setTimeStamp(timeStamp);
            int bodylength = ois.readShort();
            msg.setLenOfBody(bodylength);
            byte[] b = new byte[bodylength];
            dis.readFully(b);
            msg.newBodyfromBytes(b);
          
        } catch (Exception e) {
            throw new IOException("readOneMsg",e);
        } 
        return msg;
    }

    public static void writeMsg(Msg msg, BufferedOutputStream dout) throws IOException {
        try( 
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream(9);
            DataOutputStream oos = new DataOutputStream(byteOutStream)){
            oos.writeShort(Msg.START_SIGN);
            oos.writeByte(msg.getMsgType().value);
            oos.writeInt(Msg.creatMsgTimeStamp());
            oos.writeShort(msg.getBodyLenNow());
            dout.write(byteOutStream.toByteArray());
            dout.write(msg.getBodyBytes());
            dout.flush();
        } catch (Exception e) {
            throw new IOException("writeMsg",e);
        } 

    }

}
