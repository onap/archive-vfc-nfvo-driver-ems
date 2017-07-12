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

public class SFTPSrv  implements FTPInterface{

	public boolean chdir(String dir) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean downloadFile(String remoteFile, String localFile) {
		// TODO Auto-generated method stub
		return false;
	}

	public RemoteFile[] list() {
		// TODO Auto-generated method stub
		return null;
	}

	public RemoteFile[] list(String dir) {
		// TODO Auto-generated method stub
		return null;
	}

	public void login(String host, int port, String user, String pwd,
			String encode, boolean isPassiveMode, int timeout) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void logout() {
		// TODO Auto-generated method stub
		
	}

	public boolean store(String localFile, String remoteFile) {
		// TODO Auto-generated method stub
		return false;
	}

	
}

