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

import java.text.ParseException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class DateUtilTest {

	private Date fireTime;

	@Before
	public void setUp() throws Exception {
		fireTime = new Date();
	}

	@Test
	public void testGetScanScope() {
		long[] scanScope = DateUtil.getScanScope(fireTime, 3600);
		long start = scanScope[0];
		long end = scanScope[1];
		
		long[] scanScope1 = DateUtil.getScanScope(fireTime, 3000);
		long start1 = scanScope1[0];
		long end1 = scanScope1[1];
		
		long[] scanScope2 = DateUtil.getScanScope(fireTime, 24 * 60 * 60);
		long start2 = scanScope2[0];
		long end2 = scanScope2[1];
		
		long[] scanScope3 = DateUtil.getScanScope(fireTime, 4200);
		long start3 = scanScope3[0];
		long end3 = scanScope3[1];
		
		assertNotNull(start);
		assertNotNull(end);
		assertNotNull(start1);
		assertNotNull(end1);
		assertNotNull(start2);
		assertNotNull(end2);
		assertNotNull(start3);
		assertNotNull(end3);
	}
	
	@Test
	public void testTimeString() {
		String timeString = DateUtil.getTimeString(null);
		String timeString1 = DateUtil.getTimeString("20180206T");
		String timeString2 = DateUtil.getTimeString("20180206T+12");
		assertNotNull(timeString);
		assertNotNull(timeString1);
		assertNotNull(timeString2);
	}
	
	@Test
	public void testAddTime() throws Exception {
		String addTime = DateUtil.addTime("2018-02-06 11:47:00","2");
		System.out.println(addTime);
		assertNotNull(addTime);
	}

}
