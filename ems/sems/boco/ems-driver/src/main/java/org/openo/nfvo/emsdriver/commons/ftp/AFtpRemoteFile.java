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
package org.openo.nfvo.emsdriver.commons.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

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
	
	public long getSize() {
		return ftpFile.getSize();
	}
	
	public String getFileName() {
		return ftpFile.getName();
	}
	
	public String getAbsFileName() {
		return currDir.concat(getFileName());
	}
	
	public boolean isDirectory() {
		return ftpFile.isDirectory();
	}
	public boolean isFile() {
		return ftpFile.isFile();
	}
	
	public String getOwner() {
		return ftpFile.getUser();
	}

	public Date getModifyDate() {
		return ftpFile.getTimestamp().getTime();
	}
	public boolean renameTo(String newName) throws IOException {
		return ftpClient.rename(
				currDir.concat(getFileName()), newName);
	}
	public boolean remove() throws IOException {
		return ftpClient.deleteFile(
				currDir.concat(getFileName()));
	}
	
	public InputStream getInputStream() throws IOException {
		return ftpClient.retrieveFileStream(this.getAbsFileName());
	}
	
	public void release() {
		ftpClient = null;
		ftpFile = null;
		currDir = null;
	}
}