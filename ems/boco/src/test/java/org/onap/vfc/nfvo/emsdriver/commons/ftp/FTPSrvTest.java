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
package org.onap.vfc.nfvo.emsdriver.commons.ftp;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

public class FTPSrvTest {

	private FTPSrv fTPSrv;
	
	@Before
	public void setUp() throws Exception {
		FakeFtpServer fakeFtpServer = new FakeFtpServer();  
		fakeFtpServer.setServerControlPort(10089);  
		// 创建服务器 添加用户  
		fakeFtpServer.addUserAccount(new UserAccount("123", "123", "/"));  
		// 建立文件系统  
		FileSystem fileSystem = new UnixFakeFileSystem();  
		fileSystem.add(new DirectoryEntry("/"));  
		fileSystem.add(new FileEntry("/data", "/ftp/aaa"));  
		fileSystem.add(new FileEntry("/data/License.txt", "/ftp/aaa/License.txt"));
		fakeFtpServer.setFileSystem(fileSystem);  
		fakeFtpServer.start();  
		
		fTPSrv = new FTPSrv();
	}

	@Test
	public void testTrue() throws Exception {
		fTPSrv.login("127.0.0.1", 10089, "123", "123", "utf-8", true, 30000);
		String pwd = fTPSrv.pwd();
		boolean isDir = fTPSrv.chdir("/ftp");
		fTPSrv.downloadFile("/ftp/aaa/License.txt", "data/bbb.txt");
		fTPSrv.list();
		fTPSrv.logout();
		assertNotNull(pwd);
		assertTrue(!isDir);
	}
	
	@Test
	public void testFalse() throws Exception {
		fTPSrv.login("127.0.0.1", 10089, "123", "123", "utf-8", false, 30000);
		String pwd = fTPSrv.pwd();
		boolean isDir = fTPSrv.chdir("/ftp");
		fTPSrv.downloadFile("/ftp/aaa/License.txt", "data/bbb.txt");
		fTPSrv.list();
		fTPSrv.logout();
		assertNotNull(pwd);
		assertTrue(!isDir);
	}

}
