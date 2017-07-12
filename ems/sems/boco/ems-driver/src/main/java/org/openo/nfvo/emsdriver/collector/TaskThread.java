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
package org.openo.nfvo.emsdriver.collector;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openo.nfvo.emsdriver.commons.constant.Constant;
import org.openo.nfvo.emsdriver.commons.ftp.AFtpRemoteFile;
import org.openo.nfvo.emsdriver.commons.ftp.FTPInterface;
import org.openo.nfvo.emsdriver.commons.ftp.FTPSrv;
import org.openo.nfvo.emsdriver.commons.ftp.SFTPSrv;
import org.openo.nfvo.emsdriver.commons.model.CollectMsg;
import org.openo.nfvo.emsdriver.commons.model.CollectVo;
import org.openo.nfvo.emsdriver.commons.utils.StringUtil;
import org.openo.nfvo.emsdriver.commons.utils.UnZip;
import org.openo.nfvo.emsdriver.commons.utils.Zip;
import org.openo.nfvo.emsdriver.configmgr.ConfigurationImp;
import org.openo.nfvo.emsdriver.configmgr.ConfigurationInterface;
import org.openo.nfvo.emsdriver.messagemgr.MessageChannel;
import org.openo.nfvo.emsdriver.messagemgr.MessageChannelFactory;


public class TaskThread implements Runnable{
	
	public  Log log = LogFactory.getLog(TaskThread.class);
	
	private MessageChannel collectResultChannel;
	
	private CollectMsg data;
	
	private ConfigurationInterface configurationInterface = new ConfigurationImp();
	
	private String localPath = Constant.SYS_DATA_TEMP;
	private String resultPath = Constant.SYS_DATA_RESULT;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public TaskThread(CollectMsg data) {
		this.data = data;
	}
	
	@Override
	public void run(){
		
		collectResultChannel = MessageChannelFactory.getMessageChannel(Constant.COLLECT_RESULT_CHANNEL_KEY);
			
		try {
			collectMsgHandle(data);
		} catch (Exception e) {
			log.error("",e);
		}
	}

	private void collectMsgHandle(CollectMsg collectMsg) {
		String emsName = collectMsg.getEmsName();
		String type = collectMsg.getType();
		CollectVo collectVo = configurationInterface.getCollectVoByEmsNameAndType(emsName, type);
		
		//ftp download 
		List<String> downloadfiles = this.ftpDownload(collectVo);
		//paser ftp update message send
		for(String fileName :downloadfiles){
			this.parseFtpAndSendMessage(fileName,collectVo);
		}
	}

	private void parseFtpAndSendMessage(String fileName, CollectVo collectVo) {
		//
		List<File> filelist = decompressed(fileName);
		
		for (File tempfile : filelist) { 
		
			String unfileName = tempfile.getName();
			
			Pattern pa = Pattern.compile(".*-(.*)-\\w{2}-");
			Matcher ma = pa.matcher(unfileName);
			if (!ma.find())
			  continue;
			String nename = ma.group(1);
			boolean parseResult = false;
			if("CM".equalsIgnoreCase(collectVo.getType())){
				parseResult = processCMXml(tempfile, nename,"CM");
			}else{
				parseResult = processPMCsv(tempfile, nename,"PM");
			}
			
			if (parseResult){
				log.info("parser "+tempfile+" sucess");
			}else {
				log.info("parser "+tempfile+" fail");
			}
			
		}
	}
	
	private boolean processPMCsv(File tempfile, String nename,String type) {
		
		String csvpath = localPath+nename+"/"+type+"/";
		File csvpathfile = new File(csvpath);
		if(!csvpathfile.exists()){
			csvpathfile.mkdirs();
		}
		String csvFileName = nename +dateFormat.format(new Date())+  System.nanoTime();
		String csvpathAndFileName = csvpath+csvFileName;
		BufferedOutputStream  bos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(csvpathAndFileName,false);
			bos = new BufferedOutputStream(fos, 10240);
		} catch (FileNotFoundException e1) {
			log.error("FileNotFoundException "+StringUtil.getStackTrace(e1));
		}
		
		FileInputStream brs = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		List<String> columnNames = new ArrayList<String>();
		List<String> commonValues = new ArrayList<String>();
		try {
			
			brs = new FileInputStream(tempfile);
			isr = new InputStreamReader(brs, Constant.ENCODING_UTF8);
			br = new BufferedReader(isr);
			//common field
			String commonField = br.readLine();
			String[] fields = commonField.split("|",-1);
			for(String com : fields){
				String[] comNameAndValue = com.split("=",2);
				columnNames.add(comNameAndValue[0].trim());
				commonValues.add(comNameAndValue[1]);
			}
			//column names
			String columnName = br.readLine();
			String[] names = columnName.split("|",-1);
			for(String name : names){
				columnNames.add(name);
			}
			
			String xmlPathAndFileName = this.setColumnNames(nename, columnNames,type);
			
			String valueLine = "";
			List<String> valuelist = new ArrayList<String>();
			int countNum = 0 ;
			while (br.readLine() != null) {
				
				if (valueLine.trim().equals("")) {
					continue;
				}
				countNum ++;
				String [] values = valueLine.split("|",-1);
				
				valuelist.addAll(commonValues);
				for(String value : values){
					valuelist.add(value);
				}
				this.appendLine(valuelist, bos);
				
				valuelist.clear();
			}
			
			if(bos != null){
				bos.close();
				bos = null;
			}
			if(fos != null){
				fos.close();
				fos = null;
			}
			
			String[] fileKeys = this.createZipFile(csvpathAndFileName,xmlPathAndFileName,nename);
			//ftp store
			Properties ftpPro = configurationInterface.getProperties();
			String ip = ftpPro.getProperty("ftp_ip");
			String port = ftpPro.getProperty("ftp_port");
			String ftp_user = ftpPro.getProperty("ftp_user");
			String ftp_password = ftpPro.getProperty("ftp_password");
			
			String ftp_passive = ftpPro.getProperty("ftp_passive");
			String ftp_type = ftpPro.getProperty("ftp_type");
			String remoteFile = ftpPro.getProperty("ftp_remote_path");
			this.ftpStore(fileKeys,ip,port,ftp_user,ftp_password,ftp_passive,ftp_type,remoteFile);
			//create Message
			String message = this.createMessage(fileKeys[1], ftp_user, ftp_password, ip,  port, countNum,nename);
			
			//set message
			this.setMessage(message);
		} catch (IOException e) {
			log.error("processPMCsv is fail ",e);
			return false;
		}finally{
			try{
				if (br != null)
					br.close();
				if (isr != null)
					isr.close();
				if (brs != null)
					brs.close();
				if(bos != null){
					bos.close();
				}
				
				if(fos != null){
					fos.close();
				}
			} catch (Exception e){
				log.error(e);
			}
		}
		return true;
		
	}

	private boolean processCMXml(File tempfile, String nename, String type) {
		
		String csvpath = localPath+nename+"/"+type+"/";
		File csvpathfile = new File(csvpath);
		if(!csvpathfile.exists()){
			csvpathfile.mkdirs();
		}
		String csvFileName = nename +dateFormat.format(new Date())+  System.nanoTime();
		String csvpathAndFileName = csvpath+csvFileName+".csv";
		BufferedOutputStream  bos = null;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(csvpathAndFileName,false);
			bos = new BufferedOutputStream(fos, 10240);
		} catch (FileNotFoundException e1) {
			log.error("FileNotFoundException "+StringUtil.getStackTrace(e1));
		}
		
		boolean FieldNameFlag = false;
		boolean FieldValueFlag = false;
		//line num
		int countNum = 0;
		String xmlPathAndFileName = null;
		String localName = null;
		String endLocalName = null;
		String rmUID = null;
		int index = -1;
		ArrayList<String> names = new ArrayList<String>();// colname
		LinkedHashMap<String, String> nameAndValue = new LinkedHashMap<String, String>();

		
		FileInputStream fis = null;
		InputStreamReader isr = null;
		XMLStreamReader reader = null;
		try{
			fis = new FileInputStream(tempfile);
			isr = new InputStreamReader(fis, Constant.ENCODING_UTF8);
			XMLInputFactory fac = XMLInputFactory.newInstance();
			reader = fac.createXMLStreamReader(isr);
			int event = -1;
			boolean setcolum = true;
			while (reader.hasNext()){
				try{
					event = reader.next();
					switch (event){
					case XMLStreamConstants.START_ELEMENT:
						localName = reader.getLocalName();
						if ("FieldName".equalsIgnoreCase(localName)){
							FieldNameFlag = true;
						}
						if (FieldNameFlag){
							if ("N".equalsIgnoreCase(localName)){
								String colName = reader.getElementText().trim();
								names.add(colName);
							}
						}
						if ("FieldValue".equalsIgnoreCase(localName)){
							FieldValueFlag = true;
							
						}
						if (FieldValueFlag){
							if(setcolum){
								xmlPathAndFileName = this.setColumnNames(nename, names,type);
								setcolum = false;
							}
							
							if ("Object".equalsIgnoreCase(localName)){
								int ac = reader.getAttributeCount();
								for (int i = 0; i < ac; i++){
									if ("rmUID".equalsIgnoreCase(reader.getAttributeLocalName(i))){
										rmUID = reader.getAttributeValue(i).trim();
									}
								}
								nameAndValue.put("rmUID", rmUID);
							}
							if ("V".equalsIgnoreCase(localName)) {
								index = Integer.parseInt(reader
										.getAttributeValue(0)) - 1;
								String currentName = names.get(index);
								String v = reader.getElementText().trim();
								nameAndValue.put(currentName, v);
							}
						}
						break;
					case XMLStreamConstants.CHARACTERS:
						break;
					case XMLStreamConstants.END_ELEMENT:
						endLocalName = reader.getLocalName();

						if ("FieldName".equalsIgnoreCase(endLocalName)){
							FieldNameFlag = false;
						}
						if ("FieldValue".equalsIgnoreCase(endLocalName)){
							FieldValueFlag = false;
						}
						if ("Object".equalsIgnoreCase(endLocalName)){
							countNum ++;
							this.appendLine(nameAndValue,bos);
							nameAndValue.clear();
						}
						break;
					}
				} catch (Exception e)
				{
					log.error(""+StringUtil.getStackTrace(e));
					event = reader.next();
				}
			}
			
			
			if(bos != null){
				bos.close();
				bos = null;
			}
			if(fos != null){
				fos.close();
				fos = null;
			}
			
			String[] fileKeys = this.createZipFile(csvpathAndFileName,xmlPathAndFileName,nename);
			//ftp store
			Properties ftpPro = configurationInterface.getProperties();
			String ip = ftpPro.getProperty("ftp_ip");
			String port = ftpPro.getProperty("ftp_port");
			String ftp_user = ftpPro.getProperty("ftp_user");
			String ftp_password = ftpPro.getProperty("ftp_password");
			
			String ftp_passive = ftpPro.getProperty("ftp_passive");
			String ftp_type = ftpPro.getProperty("ftp_type");
			String remoteFile = ftpPro.getProperty("ftp_remote_path");
			this.ftpStore(fileKeys,ip,port,ftp_user,ftp_password,ftp_passive,ftp_type,remoteFile);
			//create Message
			String message = this.createMessage(fileKeys[1], ftp_user, ftp_password, ip,  port, countNum,nename);
			
			//set message
			this.setMessage(message);
		} catch (Exception e){
			log.error(""+StringUtil.getStackTrace(e));
			return false;
		} finally{
			try{
				if (reader != null){
					reader.close();
				}
				if (isr != null){
					isr.close();
				}
				if (fis != null){
					fis.close();
				}
				if(bos != null){
					bos.close();
				}
				
				if(fos != null){
					fos.close();
				}
			} catch (Exception e){
				log.error(e);
			}
		}
		return true;
	}
	
	private void setMessage(String message) {

		try {
			collectResultChannel.put(message);
		} catch (Exception e) {
			log.error("collectResultChannel.put(message) is error "+StringUtil.getStackTrace(e));
		}
	}

	private String createMessage(String zipName,String user,String pwd,String ip, String port,int countNum, String nename) {

		StringBuffer strBuffer = new StringBuffer();
		strBuffer
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<FILE_DATA_READY_UL xmlns:xsi=\" http://www.w3.org/2001/XMLSchema-instance\">"
						+ "<Header SessionID=\"");
		strBuffer.append("");
		strBuffer.append("\" LicenceID=\"");
		strBuffer.append("");
		strBuffer.append("\" SystemID=\"");
		strBuffer.append("");
		strBuffer.append("\" Time=\"");
		strBuffer.append( dateFormat2.format(new Date()));
		strBuffer.append("\" PolicyID=\"");
		strBuffer.append("");
		strBuffer.append("\"/><Body>");
		strBuffer.append("<DataCatalog>");
		strBuffer.append("");
		strBuffer.append("</DataCatalog><GroupID>");
		strBuffer.append(nename);
		strBuffer.append("</GroupID><DataSourceName>");
		strBuffer.append("");
		strBuffer.append("</DataSourceName><InstanceID>");
		strBuffer.append("");
		strBuffer.append("</InstanceID><FileFormat>");
		strBuffer.append("csv");
		strBuffer.append("</FileFormat><CharSet>");
		strBuffer.append("gbk");
		strBuffer.append("</CharSet><FieldSeparator>");
		strBuffer.append("|");
		strBuffer.append("</FieldSeparator><IsCompressed>");
		strBuffer.append("true");
		strBuffer.append("</IsCompressed><StartTime>");
		strBuffer.append(dateFormat2.format(new Date()));
		strBuffer.append("</StartTime><EndTime>");
		strBuffer.append("");
		strBuffer.append("</EndTime><FileList>");
		strBuffer.append(zipName);
		strBuffer.append("</FileList><ConnectionString>");
		strBuffer.append("ftp://" + user + ":" + pwd + "@" + ip + ":" + port);
		strBuffer.append("</ConnectionString>");
		strBuffer.append("<DataCount>");
		strBuffer.append(countNum);
		strBuffer.append("</DataCount>");
		
		strBuffer.append("<FileSize>").append("").append("</FileSize>");
		strBuffer.append("<DataGranularity>").append("").append("</DataGranularity>");

		
		strBuffer.append("</Body></FILE_DATA_READY_UL>");
		return strBuffer.toString();

	}

	private void ftpStore(String[] fileKeys, String ip, String port, String ftp_user, String ftp_password, 
			String ftp_passive, String ftp_type, String remoteFile) {
		String zipFilePath = fileKeys[0];
		
		
		FTPInterface ftpClient;
		if("ftp".equalsIgnoreCase(ftp_type)){
			 ftpClient = new FTPSrv();
		}else{
			 ftpClient = new SFTPSrv();
		}
		
		//login
		try {
			ftpClient.login(ip, Integer.parseInt(port), ftp_user, ftp_password, "GBK", Boolean.parseBoolean(ftp_passive), 5*60*1000);
		} catch (Exception e) {
			log.error("login fail,ip=["+ip+"] port=["+port+"] user=["+ftp_user+"]pwd=["+ftp_password+"]"+StringUtil.getStackTrace(e));
		    return;
		} 
		ftpClient.store(zipFilePath, remoteFile);
		log.debug("store  ["+zipFilePath+"]to["+remoteFile+"]");
								
		FileUtils.deleteQuietly(new File(zipFilePath));
		
		
	}

	private String[] createZipFile(String csvpathAndFileName,String xmlPathAndFileName,String nename) {
		
		String zipPath = resultPath+nename +dateFormat.format(new Date())+"_"+System.nanoTime();
		
		File destDir = new File(zipPath);
		destDir.mkdirs();
		
		try {
			FileUtils.copyFileToDirectory(new File(csvpathAndFileName), destDir);
			FileUtils.copyFileToDirectory(new File(xmlPathAndFileName), destDir);
		} catch (IOException e) {
			
		}
		
		String destFilePath = zipPath + ".zip";
		try {
			Zip zip = new Zip(destDir.getAbsolutePath(), destFilePath);
			zip.setCompressLevel(9);
			zip.compress();

			FileUtils.deleteDirectory(destDir);
		} catch (IOException e) {
			log.error("zip.compress() is fail "+StringUtil.getStackTrace(e));
		}
		return new String[] { destFilePath, zipPath + ".zip"};
	}


	private String setColumnNames(String nename, List<String> names,String type) {
		//write xml
		String xmlpath = localPath+nename +"/"+type+"/";
		File xmlpathfile = new File(xmlpath);
		if(!xmlpathfile.exists()){
			xmlpathfile.mkdirs();
		}
		String xmlFileName = nename +dateFormat.format(new Date())+ System.nanoTime();
		String fieldLine = "";
		for (int i = 0; i < names.size(); i++) {
			String field = "\t<Field>\r\n" + "\t\t<FieldNo>" + i
					+ "</FieldNo>\r\n" + "\t\t<FieldName>"
					+ names.get(i) + "</FieldName>\r\n"
					+ "\t\t<FieldType>" + names.get(i)
					+ "</FieldType>\r\n"
					+ "\t\t<FieldNameOther>" + names.get(i)
					+ "</FieldNameOther>\r\n" +
					"\t</Field>\r\n";
			fieldLine = fieldLine + field;
		}

		String str = "<?xml version=\"1.0\" encoding=\"gbk\"?>\r\n"
				+ "<xml>\r\n" + "<FILE_STRUCTURE>\r\n" + fieldLine
				+ "</FILE_STRUCTURE>\r\n" + "</xml>\r\n";
		String xmlPathAndFileName = xmlpath+xmlFileName+".xml";
		try {
			this.writeDetail(xmlPathAndFileName,str);
		} catch (Exception e) {
			log.error("writeDetail is fail ,xmlFileName="+xmlFileName +StringUtil.getStackTrace(e));
		}
		
		return xmlPathAndFileName;
	}
	
	private void writeDetail(String detailFileName,String str) throws Exception {
		OutputStreamWriter writer = null;
		OutputStream readOut = null;
		try {
			readOut = new FileOutputStream(new File(detailFileName), false);
			writer = new OutputStreamWriter(readOut);
			writer.write(str);
			writer.flush();
		} finally {
			
			if(null != writer){
				writer.close();
			}
			if(readOut != null){
				readOut.close();
			}
			
		}

	}
	

	private void appendLine(LinkedHashMap<String, String> nameAndValue,BufferedOutputStream  bos) {
		StringBuilder lineDatas =  new StringBuilder();
		
		for (String key : nameAndValue.keySet()) {
			lineDatas.append(nameAndValue.get(key)).append("|");
		}
		try {
			bos.write(lineDatas.toString().getBytes());
			bos.write("\n".getBytes());
		} catch (IOException e) {
			log.error("appendLine error "+StringUtil.getStackTrace(e));
		}
	}
	
	private void appendLine(List<String> values,BufferedOutputStream  bos) {
		StringBuilder lineDatas =  new StringBuilder();
		
		for (String value : values) {
			lineDatas.append(value).append("|");
		}
		try {
			bos.write(lineDatas.toString().getBytes());
			bos.write("\n".getBytes());
		} catch (IOException e) {
			log.error("appendLine error "+StringUtil.getStackTrace(e));
		}
	}

	public List<File> decompressed(String fileName){
	    List<File> filelist = new ArrayList<File>();
	
	    if (fileName.indexOf(".gz") > 1)
	    {
//	      decompressFile = deGz(file);
	    } else if (fileName.indexOf(".zip") > 1)
	    {
	    	try {
				File[] files = deZip(new File(fileName));
				for(File temp :files){
					filelist.add(temp);
				}
			} catch (Exception e) {
				log.error("decompressed is fail "+StringUtil.getStackTrace(e));
			}
	    }
	    else {
	    	filelist.add(new File(fileName));
	    }
	
	    return filelist;
	}

	public File[] deZip(File file) throws Exception{
		  
		String regx = "(.*).zip";
	      Pattern p = Pattern.compile(regx);
	      Matcher m = p.matcher(file.getName());
	      if (m.find())
	      {
	        String orgFile = localPath + m.group(1) + "/";
	        UnZip unzip = new UnZip(file.getAbsolutePath(), orgFile);
            unzip.deCompress();
	        file = new File(orgFile);
	      }
	      File[] files = file.listFiles();
	      
	      return files;
	     
	}

	private List<String> ftpDownload(CollectVo collectVo) {
		
		List<String> fileList = new ArrayList<String>();
		//IP
		String ip = collectVo.getIP();
		//port
		String port = collectVo.getPort();
		//user
		String user = collectVo.getUser();
		//password
		String password = collectVo.getPassword();
		//isPassiveMode
		String passivemode = collectVo.getPassive();
		
		String ftpType = collectVo.getFtptype();
		FTPInterface ftpClient = null;
		if("ftp".equalsIgnoreCase(ftpType)){
			 ftpClient = new FTPSrv();
		}else{
			 ftpClient = new SFTPSrv();
		}
		
		//login
		try {
			ftpClient.login(ip, Integer.parseInt(port), user, password, "GBK", Boolean.parseBoolean(passivemode), 5*60*1000);
		} catch (Exception e) {
			log.error("login fail,ip=["+ip+"] port=["+port+"] user=["+user+"]password=["+password+"]"+StringUtil.getStackTrace(e));
		    return fileList;
		} 
		
		//download
		String dir = collectVo.getRemotepath();
		boolean cdsucess = ftpClient.chdir(dir);
		if(cdsucess){
			AFtpRemoteFile[] remoteFiles = (AFtpRemoteFile[]) ftpClient.list();
			
			for(AFtpRemoteFile ftpRemoteFile: remoteFiles){
				if(!new File(localPath).exists()){
					try {
						new File(localPath).mkdir();
					} catch (Exception e) {
						log.error("create localPath is fail localPath="+localPath+" "+StringUtil.getStackTrace(e));
					}
				}
				
				if(!new File(localPath).exists()){
					new File(localPath).mkdirs();
				}
				
				String localFileName = localPath + ftpRemoteFile.getFileName();
				File loaclFile = new File(localFileName);
				if (loaclFile.exists()) {
					loaclFile.delete();
				}
				
				boolean flag = ftpClient.downloadFile(ftpRemoteFile.getAbsFileName(), localFileName);
				
				if(flag){
					fileList.add(localFileName);
				}else{
					log.error("download file fail fileName="+ftpRemoteFile.getAbsFileName());
				}
			}
			
		}else{
			log.error("chdir is faill dir =["+dir+"]");
		}
		
		return fileList;
	}
	
	
}