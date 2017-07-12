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

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.openo.nfvo.emsdriver.commons.utils.StringUtil;


public class FTPSrv implements FTPInterface{
	private  Log log = LogFactory.getLog(FTPSrv.class);
	private FTPClient ftpClient = null;
	

	/**
	 * login FTP
	 * @param host
	 * @param port
	 * @param user
	 * @param pwd
	 * @param encode
	 * @param timeout
	 * @throws Exception
	 */
	public void login(String host, int port, String user, String pwd, String encode, boolean isPassiveMode,int timeout) throws Exception {
		ftpClient = new FTPClient();
		
		FTPClientConfig ftpClientConfig = new FTPClientConfig();
		ftpClientConfig.setServerTimeZoneId(TimeZone.getDefault().getID());
		this.ftpClient.setControlEncoding("GBK");
		this.ftpClient.configure(ftpClientConfig);
		ftpClient.setParserFactory(new ExtendsDefaultFTPFileEntryParserFactory());
		
		if(encode!=null && encode.length()>0){
			ftpClient.setControlEncoding(encode);
		}
		
		ftpClient.connect(host, port);
		int reply = this.ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			this.ftpClient.disconnect();
			return ;
		}
		
		if(!ftpClient.login(user, pwd)){
			throw new Exception("login["+host+"],port["+port+"] fail, please check user and password");
		}
		if(isPassiveMode){
			ftpClient.enterLocalPassiveMode();
		}else{
			ftpClient.enterLocalActiveMode();
		}
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		this.ftpClient.setBufferSize(1024 * 2);
		this.ftpClient.setDataTimeout(3*60 * 1000);
		try{
			this.ftpClient.setSoTimeout(timeout);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * logout
	 */
	public void logout(){
		if(ftpClient != null){
			try {
				ftpClient.logout();
				ftpClient.disconnect();
			}catch(Exception e){
			}
			ftpClient = null;
		}
	}


	public boolean chdir(String dir) {
		boolean sucess = false;
		try {
			if(ftpClient.changeWorkingDirectory(dir)){
				sucess = true;
			}else{
				sucess = false;
			}
		} catch (IOException e) {
			log.error("chdir dir ="+dir+" is error"+StringUtil.getStackTrace(e));
			sucess = false;
		}
		
		return sucess;
	}


	public boolean downloadFile(String remoteFile, String localFile) {
		boolean sucess = false;
		BufferedOutputStream toLfileOutput = null;
		try {
			toLfileOutput = new BufferedOutputStream(new FileOutputStream(localFile));
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			if (ftpClient.retrieveFile(remoteFile, toLfileOutput)){
				sucess = true;
			}else{
				sucess = false;
			}
		} catch (Exception ioe) {
			sucess = false;
			log.error("downloadFile remoteFile ="+remoteFile +" is fail ",ioe);
		} finally {
			if (toLfileOutput != null)
				try {
					toLfileOutput.close();
				} catch (IOException e) {
				}
		}
		
		return sucess;
	}


	public RemoteFile[] list() {
		AFtpRemoteFile[] ftpRemoteFiles = null;
		String currdir = null;
		try {
			currdir = ftpClient.printWorkingDirectory();
			if (currdir.endsWith("/") == false) {
				currdir = currdir + "/";
			}
			FTPFile[] rfileList = null;
			rfileList = ftpClient.listFiles(currdir);
			ftpRemoteFiles = new AFtpRemoteFile[rfileList.length];
			for (int i=0; i<rfileList.length; i++){
				ftpRemoteFiles[i] = new AFtpRemoteFile(rfileList[i], ftpClient, currdir);
			}
		} catch (IOException e) {
			log.error("Ftp list currdir = "+currdir+" is fail "+StringUtil.getStackTrace(e));
		}
		return ftpRemoteFiles;
	}


	public RemoteFile[] list(String dir) {
		return null;
	}


	public boolean store(String localFile, String remoteFile) {
		
		boolean sucess = false;
		FileInputStream lfileInput = null;
		try {
			lfileInput = new FileInputStream(localFile);
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			
			if (ftpClient.storeFile(remoteFile, lfileInput)){
				sucess = true;
			}else{
				sucess = false;
			}
		} catch (Exception ioe) {
			sucess = false;
			log.error("store localFile = "+localFile+" is fail "+StringUtil.getStackTrace(ioe));
		} finally {
			if (lfileInput != null)
				try {
					lfileInput.close();
				} catch (IOException e) {
				}
		}
		return sucess;
	}
	
}

