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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.vfc.nfvo.emsdriver.commons.utils.Gunzip;

public class GunzipTest {
	private String csvPath = System.getProperty("user.dir")+"/data/" +"PM-ENB-EUTRANCELLNB-test.csv";
	private String gzPath = System.getProperty("user.dir")+"/data/" +"PM-ENB-EUTRANCELLNB-test.csv.gz";
	private Gunzip gunzip = null;
	private String gunzipfile;
	
	
	@Before
    public void setUp() throws IOException {
		gunzip = new Gunzip();
		Gzip gzip = new Gzip();
		gzip.compress(csvPath, gzPath);
    }
	
	@Test
	public void deCompress() throws IOException{
		gunzipfile = gzPath.replace(".gz", "file");
		gunzip.unCompress(gzPath, gunzipfile);
		assertTrue(new File(gunzipfile).length() > 0);
		
	}
	
	@After
    public void setDown() throws IOException {
		new File(gunzipfile).delete();
		new File(gzPath).delete();
		
    }
}
