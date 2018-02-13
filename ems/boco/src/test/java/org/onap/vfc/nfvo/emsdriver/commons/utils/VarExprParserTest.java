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

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class VarExprParserTest {

	private Date date;
	private long time;
	
	@Before
	public void setUp() throws Exception {
		date = new Date();
		time = date.getTime();
	}

	@Test
	public void testReplaceVar() {
		String str = VarExprParser.replaceVar("asd", time, time);
		//String str1 = VarExprParser.replaceVar("${s_year}12+45-7h", time, time);
		//String str2 = VarExprParser.replaceVar("${s_mon}12+45-7m,23453", time, time);
		String str3 = VarExprParser.replaceVar("${s_day}", time, time);
		String str4 = VarExprParser.replaceVar("${s_hour}", time, time);
		String str5 = VarExprParser.replaceVar("${s_min}", time, time);
		String str6 = VarExprParser.replaceVar("${e_year}", time, time);
		String str7 = VarExprParser.replaceVar("${e_mon}", time, time);
		String str8 = VarExprParser.replaceVar("${e_day}", time, time);
		String str9 = VarExprParser.replaceVar("${e_hour}", time, time);
		String str0 = VarExprParser.replaceVar("${e_min}", time, time);

		assertNotNull(str);
		//assertNotNull(str1);
		//assertNotNull(str2);
		assertNotNull(str3);
		assertNotNull(str4);
		assertNotNull(str5);
		assertNotNull(str6);
		assertNotNull(str7);
		assertNotNull(str8);
		assertNotNull(str9);
		assertNotNull(str0);
		
	}
	
	@Test
	public void testReplaceTimeVar() {
		String start = VarExprParser.replaceTimeVar("${SCAN_START_TIME}", "start", "stop");
		String stop = VarExprParser.replaceTimeVar("${SCAN_STOP_TIME}", "start", "stop");
		
		assertNotNull(start);
		assertNotNull(stop);
		
	}

}
