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

public enum MsgType {

    REQ_LOGIN_ALARM("reqLoginAlarm", 1, "all"),
    ACK_LOGIN_ALARM("ackLoginAlarm", 2, "all"),
    REQ_SYNC_ALARM_MSG("reqSyncAlarmMsg", 3, "msg"),
    ACK_SYNC_ALARM_MSG("ackSyncAlarmMsg", 4, "msg"),
    REQ_SYNC_ALARM_FILE("reqSyncAlarmFile", 5, "file"),
    ACK_SYNC_ALARM_FILE("ackSyncAlarmFile", 6, "file"),
    ACK_SYNC_ALARM_FILE_RESULT("ackSyncAlarmFileResult", 7, "file"),
    REQ_HEARTBEAT("reqHeartBeat", 8, "all"),
    ACK_HEARTBEAT("ackHeartBeat", 9, "all"),
    CLOSE_CONN_ALARM("closeConnAlarm", 10, "all"),
    REALTIME_ALARM("realTimeAlarm", 0, "all"),
    UNDEFINED("undefined", -1, "all");

    public int value = -1;
    public String name;
    public String type;

    MsgType(String inName, int inValue, String inType) {
        this.name = inName;
        this.value = inValue;
        this.type = inType;
    }

    public static MsgType getMsgTypeValue(int msgTypeValue) {

        for (MsgType msgType : MsgType.values()) {
            if (msgType.value == msgTypeValue) {
                return msgType;
            }
        }
        return UNDEFINED;
    }

    public static MsgType getMsgTypeName(String msgTypeName) {

        for (MsgType msgType : MsgType.values()) {
            if (msgType.name.equalsIgnoreCase(msgTypeName)) {
                return msgType;
            }
        }
        return UNDEFINED;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
