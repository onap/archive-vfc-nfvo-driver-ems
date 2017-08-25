/**
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
import java.io.IOException;

import org.junit.Before;
import org.onap.vfc.nfvo.emsdriver.commons.utils.UnZip;

public class UnZipTest {

	private String zipPath = System.getProperty("user.dir")+"/data/" +"PM-ENB-EUTRANCELLNB-test.csv.gz";
	private String toPath = System.getProperty("user.dir")+"/data/";
	private UnZip unZip = null;
	
	@Before
    public void setUp() throws IOException {
		unZip = new UnZip(zipPath,toPath);
    }
	
	public void deCompress() throws IOException{
		unZip.deCompress();
		
		assertTrue(new File(toPath).listFiles().length > 0);
	}
	
	public static void main(String[] str){
		 System.out.println(System.getProperty("user.dir"));
	}
}
