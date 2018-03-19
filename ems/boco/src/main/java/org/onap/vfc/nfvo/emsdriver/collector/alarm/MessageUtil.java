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
        String body = String.format(Msg.reqLoginAlarm, user, passwd, "msg");
        return new Msg(body, MsgType.reqLoginAlarm);

    }

    public static Msg putLoginFtp(String user, String passwd) {
        String body = String.format(Msg.reqLoginAlarm, user, passwd, "ftp");
	return new Msg(body, MsgType.reqLoginAlarm);

    }

    public static Msg putSyncMsg(int reqId, int alarmSeq) {
        String body = String.format(Msg.syncAlarmMessageMsg, reqId, alarmSeq);
        return new Msg(body, MsgType.reqSyncAlarmMsg);

    }

    public static Msg putHeartBeatMsg(int reqId) {
        String body = String.format(Msg.reqHeartBeat, reqId);
        return new Msg(body, MsgType.reqHeartBeat);

    }

    public static Msg reqSyncAlarmFile(int reqId, String startTime, String endTime) {
        String body = String.format(Msg.syncActiveAlarmFileMsg, reqId, startTime, endTime);
        return new Msg(body, MsgType.reqSyncAlarmFile);
    }

    public static Msg reqSyncAlarmFileByAlarmSeq(int reqId, int alarmSeq) {
        String body = String.format(Msg.syncAlarmMessageByalarmSeq, reqId, alarmSeq);
        return new Msg(body, MsgType.reqSyncAlarmFile);
    }

    public static Msg reqSyncAlarmFileByTime(int reqId, String startTime, String endTime) {
        String body = String.format(Msg.syncAlarmFileMsg, reqId, startTime, endTime);
        return new Msg(body, MsgType.reqSyncAlarmFile);
    }

    public static Msg closeConnAlarmMsg() {
        String body = String.format(Msg.disconnectMsg);
        return new Msg(body, MsgType.closeConnAlarm);
    }

    public static Msg readOneMsg(BufferedInputStream is) throws IOException {
        byte[] inputB = new byte[9];

        Msg msg = new Msg();
        try( 
            DataInputStream dis = new DataInputStream(is);
            ByteArrayInputStream bais = new ByteArrayInputStream(inputB);
            DataInputStream ois = new DataInputStream(bais)){
            dis.readFully(inputB);
            short startSign = ois.readShort();
            if (startSign != Msg.StartSign) {
                throw new Exception("start sign is [" + Msg.StartSign
                        + "],not is [" + startSign + "]");
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
            throw new IOException("readOneMsg",e);
        } 

        return msg;
    }

    public static void writeMsg(Msg msg, BufferedOutputStream dout) throws IOException {
        try( 
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream(9);
            DataOutputStream oos = new DataOutputStream(byteOutStream)){
            oos.writeShort(Msg.StartSign);
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
