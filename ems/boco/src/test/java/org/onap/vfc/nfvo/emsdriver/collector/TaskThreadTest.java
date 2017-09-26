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
package org.onap.vfc.nfvo.emsdriver.collector;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.onap.vfc.nfvo.emsdriver.collector.TaskThread;

public class TaskThreadTest {
	
	private String gzPath = System.getProperty("user.dir")+"/data/" +"PM-ENB-EUTRANCELLNB-test.csv.gz";
	private TaskThread taskThread;
	private List<File> list = null;
	@Before
    public void setUp() throws IOException {
		taskThread = new TaskThread();
    }
	
	@Test
	public void decompressed(){
		list = taskThread.decompressed(gzPath);
		assertTrue(list.size() > 0);
	}

	@Test
	public void processPMCsv(){
		list = taskThread.decompressed(gzPath);
		for(File file : list){
			boolean re = taskThread.processPMCsv(file);
			assertTrue(re);
		}
	}
}
