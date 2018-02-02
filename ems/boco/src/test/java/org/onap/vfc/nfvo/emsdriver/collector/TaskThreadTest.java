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
package org.onap.vfc.nfvo.emsdriver.collector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.vfc.nfvo.emsdriver.commons.constant.Constant;
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectVo;
import org.onap.vfc.nfvo.emsdriver.commons.utils.Gzip;
import org.onap.vfc.nfvo.emsdriver.messagemgr.MessageChannelFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TaskThreadTest {
    private String csvPath = System.getProperty("user.dir") + "/data/" + "PM-ENB-EUTRANCELLNB-test.csv";
    private String xmlPath = System.getProperty("user.dir") + "/data/" + "PM-test.xml";
    private String gzPath = System.getProperty("user.dir") + "/data/" + "PM-ENB-EUTRANCELLNB-testa.csv.gz";
    private TaskThread taskThread;
    private List<File> list = null;

    @Before
    public void setUp() throws IOException {
        taskThread = new TaskThread();
        taskThread.pmResultChannel = MessageChannelFactory.getMessageChannel(Constant.COLLECT_RESULT_PM_CHANNEL_KEY);
        Gzip gzip = new Gzip();
        gzip.compress(csvPath, gzPath);
    }

    @Test
    public void decompressed() {
        list = taskThread.decompressed(gzPath);
        assertTrue(list.size() > 0);
        new File(gzPath).delete();
    }

    @Test
    public void processPMCsv() {
        list = taskThread.decompressed(gzPath);
        for (File file : list) {
            boolean re = taskThread.processPMCsv(file);
            assertTrue(re);
        }
    }

    @Test
    public void processPMXml() {
        File file = new File(xmlPath);
        boolean re = taskThread.processPMXml(file);
        assertTrue(re);
        System.out.println(taskThread.pmResultChannel.size());
    }

    @Test
    public void parseFtpAndSendMessage() {
        CollectVo collectVo = new CollectVo();
        collectVo.setType("ems-p");
        taskThread.parseFtpAndSendMessage(gzPath, collectVo);
    }

    @Test
    public void createMessage() {
        CollectVo collectVo = new CollectVo();
        collectVo.setType("ems-p");
        taskThread.createMessage("zipName", "user", "pwd", "ip", "port", 122, "nename");
    }

    @After
    public void setDown() throws IOException {
        new File(gzPath).delete();
        new File(gzPath.replace(".gz", "")).delete();
    }
}
