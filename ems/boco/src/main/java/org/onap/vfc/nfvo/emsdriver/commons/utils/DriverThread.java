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
package org.onap.vfc.nfvo.emsdriver.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DriverThread implements Runnable {
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private String name = null;
    private Thread t = null;
    private boolean run = false;
    private boolean end = false;

    public synchronized void start() {
        t = new Thread(this);
        t.start();
    }

    public String getName() {
        if (t != null)
            return t.getName();
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (t != null)
            t.setName(name);
    }

    public abstract void dispose();

    public final void run() {
        t = Thread.currentThread();
        if (name != null)
            t.setName(name);

        try {
            dispose();
        } catch (Exception e) {
	    log.error(" printStackTrace :", e);
        }
        this.setEnd(true);

    }

    public boolean stop() {
        this.setRun(false);
        while (!isEnd()) {
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                log.error("Exception :", e);
            }
        }
        return end;
    }

    public void interrupt() {
        if (t != null)
            t.interrupt();
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }
}
