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
package org.onap.vfc.nfvo.emsdriver.commons.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class AFtpRemoteFile implements RemoteFile{
	protected FTPClient ftpClient = null;
	protected FTPFile ftpFile = null;
	protected String currDir = null;
	
	public AFtpRemoteFile(FTPFile rfile, FTPClient ftpClient, String currDir) 
		throws IOException {
		this.ftpClient = ftpClient;
		this.ftpFile = rfile;
		this.currDir = currDir;
	}
	
	
	public String getFileName() {
		return ftpFile.getName();
	}
	
	public String getAbsFileName() {
		return currDir.concat(getFileName());
	}
}