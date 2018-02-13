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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DriverThreadTest {

	private DriverThread driverThread;
	private DriverThread driverThread1;
	@Before
	public void setUp() throws Exception {
		driverThread = new DriverThread() {		
			@Override
			public void dispose() {
				setName("name");
				getName();
				isEnd();
				setEnd(true);
				interrupt();
				stop();
			}
		};
		driverThread1 = new DriverThread() {		
			@Override
			public void dispose() {
				//setName(null);
				getName();
				isRun();
				isEnd();
				setEnd(false);
				//interrupt();
				stop();
			}
		};
	}

	@Test
	public void testDriverThread() {
		
		Thread t = new Thread(driverThread);
		t.start();
		//fail("Not yet implemented");
	}
	/*@Test
	public void testDriverThread1() {
		
		Thread t = new Thread(driverThread1);
		t.start();
		//fail("Not yet implemented");
	}*/

}
