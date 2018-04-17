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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.onap.vfc.nfvo.emsdriver.commons.constant.Constant;

import java.io.BufferedOutputStream;
import java.net.Socket;

public class HeartBeat extends Thread {
    private static final Logger log = LoggerFactory.getLogger(HeartBeat.class);
    private BufferedOutputStream out = null;
    private Socket socket = null;
    private Msg heartStr;
    private boolean stop = false;

    public HeartBeat(Socket socket, Msg heatMessage) {
        this.socket = socket;
        this.heartStr = heatMessage;
    }

    public boolean isStop() {
        return this.stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {
        log.info("HeartBeat start heartStr:" + heartStr.toString(false));
        this.stop = false;
        try {
            while (!this.isStop()) {
                out = new BufferedOutputStream(socket.getOutputStream());
                MessageUtil.writeMsg(heartStr, out);
                log.info("send HeartBeat heartStr:" + heartStr.toString(false));
                Thread.sleep(Constant.ONEMINUTE);
            }
        } catch (Exception e) {
            log.error("send HeartBeat fail ", e);
        }
        log.info("HeartBeat thread stop");
    }


}
