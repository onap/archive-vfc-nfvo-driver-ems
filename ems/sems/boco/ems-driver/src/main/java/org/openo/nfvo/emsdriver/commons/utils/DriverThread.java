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
package org.openo.nfvo.emsdriver.commons.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class DriverThread implements Runnable{
	protected Log log = LogFactory.getLog(this.getClass());
	private String name = null;
	private Thread t = null;
	private boolean run = false;
	private boolean end = false;
	
	public synchronized void start() {
		t = new Thread(this);
		t.start();
	}
	public void setName(String name) {
		this.name = name;
		if (t != null)
			t.setName(name);
	}

	public String getName() {
		if (t != null)
			return t.getName();
		return name;
	}
	
	public abstract void dispose();

	final public void run() {
		t = Thread.currentThread();
		if (name != null)
			t.setName(name);

		try {
			dispose();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		this.setEnd(true);
		
	}

	public boolean stop(){
		
		this.setRun(false);
		while(!isEnd()){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				log.error("InterruptedException :"+StringUtil.getStackTrace(e));
			}
		}
		return end;
	}
	
	public void interrupt() {
		if (t != null)
			t.interrupt();
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public boolean isRun() {
		return run;
	}
	public void setEnd(boolean end) {
		this.end = end;
	}
	public boolean isEnd() {
		return end;
	}
}
