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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.vfc.nfvo.emsdriver.commons.constant.Constant;
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectMsg;
import org.onap.vfc.nfvo.emsdriver.commons.utils.StringUtil;

import java.util.concurrent.*;


public class TaskThreadService extends Thread {

    private final ExecutorService pool;
    public Log log = LogFactory.getLog(TaskThreadService.class);
    private BlockingQueue<CollectMsg> queue = new LinkedBlockingQueue<CollectMsg>();
    private boolean startFlag = true;
    private long timeStamp = System.currentTimeMillis();

    private TaskThreadService(int poolSize) {
        pool = Executors.newFixedThreadPool(poolSize);
    }

    public static TaskThreadService getInstance(int poolSize) {
        return new TaskThreadService(poolSize);
    }

    public void run() { // run the service
        try {
            while (startFlag) {

                try {
                    if (System.currentTimeMillis() - timeStamp > Constant.ONEMINUTE) {
                        timeStamp = System.currentTimeMillis();
                        log.debug("task queue size " + queue.size());
                    }

                    CollectMsg data = receive();
                    if (data == null) {
                        continue;
                    }

                    pool.execute(new TaskThread(data));


                } catch (Exception e) {
                    log.error("collect task process fail!" + StringUtil.getStackTrace(e));
                }

            }

        } catch (Exception ex) {
            log.error("task ThreadService error " + StringUtil.getStackTrace(ex));
            pool.shutdown();
        }
        log.error("Task ThreadService exit");
    }


    public CollectMsg receive() {
        try {
            return queue.poll(100, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("queue.poll is error" + StringUtil.getStackTrace(e));
        }
        return null;
    }


    public void add(CollectMsg data) {
        try {
            queue.put(data);
        } catch (Exception e) {
            log.error("queue.put is error" + StringUtil.getStackTrace(e));
        }
    }


    public int size() {
        return queue.size();
    }

    public void stopTask() {
        startFlag = false;
    }
}	
