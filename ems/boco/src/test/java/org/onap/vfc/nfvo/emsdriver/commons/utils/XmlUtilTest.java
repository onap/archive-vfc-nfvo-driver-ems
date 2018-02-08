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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


import org.jdom.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class XmlUtilTest {

	private InputStream is;
	
	@Before
	public void setUp() throws Exception {
		is = new FileInputStream("conf/spring.xml");
		//is = new FileInputStream("/opt/gframe/jenkins/workspace/ems0206/logback.xml");
		
	}

	@Test
	public void testGetDocument() throws Exception {
		Document doc = XmlUtil.getDocument(is);
		assertTrue(doc!=null);
	}
	
	@After
    public void setDown() throws IOException {
		is.close();
    }

}
