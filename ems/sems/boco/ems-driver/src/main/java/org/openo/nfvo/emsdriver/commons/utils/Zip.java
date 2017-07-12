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
package org.openo.nfvo.emsdriver.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Zip {
	protected int compressDirectoryCount = 0; 
	protected int compressFileCount = 0;     
	
	protected int relativeAddrIdx = 0;        
	protected int compressLevel = 6;         
	protected String zipFilePath = null;      
	protected String compressPath = null;    
	
	protected ZipOutputStream zipOutput = null;
	
	/**
	 * 
	 */
	public Zip(String compressPath, String zipFilePath) throws IOException{
		File compressFile = new File(compressPath);
		if (!compressFile.exists())
			throw new IOException("the file or directory '"+compressPath+"' not found!");
		
		this.zipFilePath = zipFilePath;
		this.compressPath = compressFile.getAbsolutePath();

		if (this.zipFilePath == null) {
			StringBuffer zipFilePathBuf = new StringBuffer(this.compressPath);
			int bufLen = zipFilePathBuf.length();
			if (zipFilePathBuf.charAt(bufLen-1) == '/')
				zipFilePathBuf.deleteCharAt(bufLen-1);
			this.zipFilePath = zipFilePathBuf.append(".zip").toString();
		}
		relativeAddrIdx = this.compressPath.lastIndexOf(File.separator)+1;
	}
	
	/**
	 * 
	 */
	public void compress() throws IOException {
		File theFile = new File(zipFilePath);
		
		if (!theFile.exists()) {
			String parentPath = theFile.getParent();
			if (parentPath != null)
				new File(parentPath).mkdirs();
			theFile.createNewFile();
		}
		zipOutput = new ZipOutputStream(new FileOutputStream(zipFilePath));
		zipOutput.setMethod(ZipOutputStream.DEFLATED);
		zipOutput.setLevel(compressLevel);
		compressDirectory(new File(compressPath));
		zipOutput.close();
	}
	
	protected void compressDirectory(File directoryPath) throws IOException {
		if (directoryPath.isFile()) {
			compressFile(directoryPath.getAbsolutePath());
		}else{
			File listFiles[] = directoryPath.listFiles();
			for (int i=0; i<listFiles.length; i++)
				if (listFiles[i].isFile()) {
					compressFile(listFiles[i].getAbsolutePath());
				}else {
					compressDirectoryCount ++;
					compressDirectory(listFiles[i]);
				}
		}
	
	
	}
	protected void compressFile(String absolutePath) throws IOException {
		compressFileCount ++;
		byte byteBuf[] = new byte[2048];
		zipOutput.putNextEntry(new ZipEntry(absolutePath.substring(relativeAddrIdx)));
		
		FileInputStream input= new FileInputStream(absolutePath);
		for (int count=0; (count=input.read(byteBuf,0,byteBuf.length))!=-1;)
			zipOutput.write(byteBuf, 0, count);
		input.close();
		zipOutput.closeEntry();
	}
	
	public void setCompressLevel(int level) {
		compressLevel = level;
	}
}
