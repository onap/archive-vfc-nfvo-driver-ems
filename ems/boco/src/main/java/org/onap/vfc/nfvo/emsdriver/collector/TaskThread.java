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
package org.onap.vfc.nfvo.emsdriver.collector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.vfc.nfvo.emsdriver.commons.constant.Constant;
import org.onap.vfc.nfvo.emsdriver.commons.ftp.AFtpRemoteFile;
import org.onap.vfc.nfvo.emsdriver.commons.ftp.FTPInterface;
import org.onap.vfc.nfvo.emsdriver.commons.ftp.FTPSrv;
import org.onap.vfc.nfvo.emsdriver.commons.ftp.RemoteFile;
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectMsg;
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectVo;
import org.onap.vfc.nfvo.emsdriver.commons.utils.*;
import org.onap.vfc.nfvo.emsdriver.configmgr.ConfigurationImp;
import org.onap.vfc.nfvo.emsdriver.configmgr.ConfigurationInterface;
import org.onap.vfc.nfvo.emsdriver.messagemgr.MessageChannel;
import org.onap.vfc.nfvo.emsdriver.messagemgr.MessageChannelFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskThread implements Runnable {

	public Log log = LogFactory.getLog(TaskThread.class);
	public MessageChannel pmResultChannel;
	private MessageChannel cmResultChannel;
	private CollectMsg data;

	private ConfigurationInterface configurationInterface = new ConfigurationImp();

	private String localPath = Constant.SYS_DATA_TEMP;
	private String resultPath = Constant.SYS_DATA_RESULT;

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public TaskThread(CollectMsg data) {
		this.data = data;
	}

	public TaskThread() {
		super();
	}

	@Override
	public void run() {

		cmResultChannel = MessageChannelFactory.getMessageChannel(Constant.COLLECT_RESULT_CHANNEL_KEY);
		pmResultChannel = MessageChannelFactory.getMessageChannel(Constant.COLLECT_RESULT_PM_CHANNEL_KEY);
		try {
			collectMsgHandle(data);
		} catch (Exception e) {
			log.error(" collectMsgHandle", e);
		}
	}

	private void collectMsgHandle(CollectMsg collectMsg) {
		String emsName = collectMsg.getEmsName();
		String type = collectMsg.getType();
		CollectVo collectVo = configurationInterface.getCollectVoByEmsNameAndType(emsName, type);

		// ftp download
		List<String> downloadfiles = this.ftpDownload(collectVo);
		// paser ftp update message send
		for (String fileName : downloadfiles) {
			this.parseFtpAndSendMessage(fileName, collectVo);
		}
	}

	public void parseFtpAndSendMessage(String fileName, CollectVo collectVo) {
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
			if (Constant.COLLECT_TYPE_CM.equalsIgnoreCase(collectVo.getType())) {
				parseResult = processCMXml(tempfile, nename, "CM");
			} else {
				if (unfileName.indexOf(".csv") > 0) {
					parseResult = processPMCsv(tempfile);
				} else {
					parseResult = processPMXml(tempfile);
				}
			}

			if (parseResult) {
				log.info("parser " + tempfile + " sucess");
				tempfile.delete();
			} else {
				log.info("parser " + tempfile + " fail");
			}

		}
	}

	public boolean processPMXml(File file) {
		try (	FileInputStream	fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, Constant.ENCODING_UTF8)){

			XMLInputFactory fac = XMLInputFactory.newInstance();
			XMLStreamReader	reader = fac.createXMLStreamReader(isr);

			boolean fileHeaderStart = false;
			boolean measurementStart = false;
			boolean pmNameFlag = false;
			boolean pmDataFlag = false;
			boolean objectFlag = true;

			int index = -1;
			int nameIndex = -1;
			String currentMea = null;
			String subName = null;
			String localName = null;
			String endLocalName = null;
			String objectType = null;

			LinkedHashMap<String, String> commonNameAndValue = new LinkedHashMap<String, String>();
			LinkedHashMap<String, String> pmDatas = null;
			LinkedHashMap<Integer, String> pmNames = null;

			int event = -1;
			while (reader.hasNext()) {
				try {
					event = reader.next();

					switch (event) {
					case XMLStreamConstants.START_ELEMENT:
						localName = reader.getLocalName();
						if ("FileHeader".equalsIgnoreCase(localName)) {
							fileHeaderStart = true;
						}
						if (fileHeaderStart) {
							if (!"FileHeader".equalsIgnoreCase(localName)) {
								commonNameAndValue.put(localName, reader.getElementText().trim());
							}

						}
						if ("Measurements".equalsIgnoreCase(localName)) {
							// a new Measurement starts
							measurementStart = true;
						}
						if (measurementStart) {
							// measurement handler
							if ("ObjectType".equalsIgnoreCase(localName)) {
								objectType = reader.getElementText().trim();
								commonNameAndValue.put("ObjectType", objectType);
							}
							if ("PmName".equalsIgnoreCase(localName)) {
								pmNameFlag = true;
								pmNames = new LinkedHashMap<Integer, String>();

							}
							if (pmNameFlag) {
								// pmname handler, add columnNames
								if ("N".equalsIgnoreCase(localName)) {
									nameIndex = Integer.parseInt(getXMLAttribute(reader, "i"));
									String text = reader.getElementText().trim();
									pmNames.put(nameIndex, text);
								}
							}
							if ("PmData".equalsIgnoreCase(localName)) {
								pmDataFlag = true;
								pmDatas = new LinkedHashMap<String, String>();
							}

							if (pmDataFlag) {
								// pmdata handler
								if ("Object".equalsIgnoreCase(localName)) {
									objectFlag = true;
									int n = reader.getAttributeCount();
									for (int i = 0; i < n; i++) {
										String name = reader.getAttributeLocalName(i);
										commonNameAndValue.put(name, reader.getAttributeValue(i));
									}
								}
								if (objectFlag) {

									// add columnValues
									if ("V".equalsIgnoreCase(localName)) {
										String indexStr = getXMLAttribute(reader, "i");
										if (indexStr == null) {
											log.error("ERROR: illegal value index");
											continue;
										}
										index = Integer.parseInt(indexStr);
										String name = pmNames.get(index);
										if (name == null) {
											log.error("illegal data: valueIndex=" + index);
											continue;
										}

										String value = reader.getElementText().trim();
										pmDatas.put(name, value);
									}
									if ("CV".equalsIgnoreCase(localName)) {

										String indexStr = getXMLAttribute(reader, "i");
										if (indexStr == null) {
											log.error("ERROR: illegal value index");
											continue;
										}
										index = Integer.parseInt(indexStr);

										currentMea = pmNames.get(index);
										if (currentMea == null) {
											log.error("illegal data: valueIndex=" + index);
											continue;
										}
									}

									if ("SN".equalsIgnoreCase(localName)) {
										subName = reader.getElementText().trim();

									}
									if ("SV".equalsIgnoreCase(localName)) {
										String subValue = reader.getElementText().trim();
										// pmDatas.put(currentMea+subName,
										// subValue);
										pmDatas.put(subName, subValue);
									}
								}
							}

						}

						break;
					case XMLStreamConstants.CHARACTERS:
						// ...
						break;
					case XMLStreamConstants.END_ELEMENT:
						// ...
						endLocalName = reader.getLocalName();
						if ("Object".equalsIgnoreCase(endLocalName)) {
							objectFlag = false;
							pmDatas.putAll(commonNameAndValue);
							try {
								pmResultChannel.put(pmDatas);

							} catch (InterruptedException e) {
								pmResultChannel.clear();	
								log.error("collectResultChannel.put(resultMap) error ", e);
							}
							// System.out.println(pmDatas);
							// pmDatas.clear();
						}
						if (endLocalName.equalsIgnoreCase("PmData")) {
							pmDataFlag = false;
						}

						if (endLocalName.equalsIgnoreCase("PmName")) {
							pmNameFlag = false;
						}
						if (endLocalName.equalsIgnoreCase("Measurements")) {
							// a measurement over
							measurementStart = false;
						}

						if ("FileHeader".equalsIgnoreCase(endLocalName)) {
							fileHeaderStart = false;
						}
						break;
					}
				} catch (Exception e) {
					log.error("", e);
					event = reader.next();
				}
			}
		reader.close();
		} catch (Exception e) {
			log.error("processPMXml is Exception ", e);
			return false;
		} 
		return true;
	}

	private String getXMLAttribute(XMLStreamReader reader, String obj) {
		String res = null;
		if (obj == null || reader == null) {
			return res;
		}
		int n = reader.getAttributeCount();
		for (int i = 0; i < n; i++) {
			String name = reader.getAttributeLocalName(i);
			if (obj.equalsIgnoreCase(name)) {
				res = reader.getAttributeValue(i);
			}
		}
		return res;
	}

	public boolean processPMCsv(File tempfile) {

		List<String> columnNames = new ArrayList<String>();
		List<String> commonValues = new ArrayList<String>();
		try (FileInputStream brs = new FileInputStream(tempfile);
				InputStreamReader isr = new InputStreamReader(brs, Constant.ENCODING_UTF8);
				BufferedReader br = new BufferedReader(isr)) {

			// common field
			String commonField = br.readLine();
			String[] fields = commonField.split("\\|", -1);
			for (String com : fields) {
				String[] comNameAndValue = com.split("=", 2);
				columnNames.add(comNameAndValue[0].trim());
				commonValues.add(comNameAndValue[1]);
			}
			// column names
			String columnName = br.readLine();
			String[] names = columnName.split("\\|", -1);
			for (String name : names) {
				columnNames.add(name);
			}

			String valueLine = "";
			List<String> valuelist = new ArrayList<String>();

			while ((valueLine = br.readLine()) != null) {
				if (valueLine.trim().equals("")) {
					continue;
				}
				// countNum ++;
				String[] values = valueLine.split("\\|", -1);

				valuelist.addAll(commonValues);
				for (String value : values) {
					valuelist.add(value);
				}
				// this.appendLine(valuelist, bos);
				// resultMap
				HashMap<String, String> resultMap = this.resultMap(columnNames, valuelist);
				try {
					pmResultChannel.put(resultMap);
				} catch (InterruptedException e) {
					pmResultChannel.clear();	
					log.error("collectResultChannel.put(resultMap) error ", e);
				}
				valuelist.clear();
			}
		} catch (IOException e) {
			log.error("processPMCsv is fail ", e);
			return false;
		}
		return true;

	}

	private HashMap<String, String> resultMap(List<String> columnNames, List<String> valuelist) {

		HashMap<String, String> resultMap = new HashMap<String, String>();
		if (columnNames.size() == valuelist.size()) {
			for (int i = 0; i < columnNames.size(); i++) {
				resultMap.put(columnNames.get(i), valuelist.get(i));
			}
		}

		return resultMap;

	}

	private boolean processCMXml(File tempfile, String nename, String type) {

		String csvpath = localPath + nename + "/" + type + "/";
		File csvpathfile = new File(csvpath);
		if (!csvpathfile.exists()) {
			csvpathfile.mkdirs();
		}
		String csvFileName = nename + dateFormat.format(new Date()) + System.nanoTime();
		String csvpathAndFileName = csvpath + csvFileName + ".csv";
		try( 	FileOutputStream fos = new FileOutputStream(csvpathAndFileName, false);
			BufferedOutputStream	bos = new BufferedOutputStream(fos, 10240)){

			boolean FieldNameFlag = false;
			boolean FieldValueFlag = false;
			// line num
			int countNum = 0;
			String xmlPathAndFileName = null;
			String localName = null;
			String endLocalName = null;
			String rmUID = null;
			int index = -1;
			ArrayList<String> names = new ArrayList<String>();// colname
			LinkedHashMap<String, String> nameAndValue = new LinkedHashMap<String, String>();

			try(	FileInputStream	fis = new FileInputStream(tempfile);
				InputStreamReader isr = new InputStreamReader(fis, Constant.ENCODING_UTF8)){

				XMLInputFactory fac = XMLInputFactory.newInstance();
				XMLStreamReader	reader = fac.createXMLStreamReader(isr);
				int event = -1;
				boolean setcolum = true;
				while (reader.hasNext()) {
					try {
						event = reader.next();
						switch (event) {
							case XMLStreamConstants.START_ELEMENT:
								localName = reader.getLocalName();
								if ("FieldName".equalsIgnoreCase(localName)) {
									FieldNameFlag = true;
								}
								if (FieldNameFlag) {
									if ("N".equalsIgnoreCase(localName)) {
										String colName = reader.getElementText().trim();
										names.add(colName);
									}
								}
								if ("FieldValue".equalsIgnoreCase(localName)) {
									FieldValueFlag = true;

								}
								if (FieldValueFlag) {
									if (setcolum) {
										xmlPathAndFileName = this.setColumnNames(nename, names, type);
										setcolum = false;
									}

									if ("Object".equalsIgnoreCase(localName)) {
										int ac = reader.getAttributeCount();
										for (int i = 0; i < ac; i++) {
											if ("rmUID".equalsIgnoreCase(reader.getAttributeLocalName(i))) {
												rmUID = reader.getAttributeValue(i).trim();
											}
										}
										nameAndValue.put("rmUID", rmUID);
									}
									if ("V".equalsIgnoreCase(localName)) {
										index = Integer.parseInt(reader.getAttributeValue(0)) - 1;
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

								if ("FieldName".equalsIgnoreCase(endLocalName)) {
									FieldNameFlag = false;
								}
								if ("FieldValue".equalsIgnoreCase(endLocalName)) {
									FieldValueFlag = false;
								}
								if ("Object".equalsIgnoreCase(endLocalName)) {
									countNum++;
									this.appendLine(nameAndValue, bos);
									nameAndValue.clear();
								}
								break;
						}
					} catch (Exception e) {
						log.error("" + StringUtil.getStackTrace(e));
						event = reader.next();
					}
				}

				String[] fileKeys = this.createZipFile(csvpathAndFileName, xmlPathAndFileName, nename);
				// ftp store
				Properties ftpPro = configurationInterface.getProperties();
				String ip = ftpPro.getProperty("ftp_ip");
				String port = ftpPro.getProperty("ftp_port");
				String ftp_user = ftpPro.getProperty("ftp_user");
				String ftp_password = ftpPro.getProperty("ftp_password");

				String ftp_passive = ftpPro.getProperty("ftp_passive");
				String ftp_type = ftpPro.getProperty("ftp_type");
				String remoteFile = ftpPro.getProperty("ftp_remote_path");
				this.ftpStore(fileKeys, ip, port, ftp_user, ftp_password, ftp_passive, ftp_type, remoteFile);
				// create Message
				String message = this.createMessage(fileKeys[1], ftp_user, ftp_password, ip, port, countNum, nename);

				// set message
				this.setMessage(message);

			reader.close();
			} catch (Exception e) {
				log.error("" + StringUtil.getStackTrace(e));
				return false;
			}

		} catch (FileNotFoundException e1) {
			log.error("FileNotFoundException " + StringUtil.getStackTrace(e1));
			return false;
		}catch (Exception e) {
                                log.error("" + StringUtil.getStackTrace(e));
				return false;
		}
		return true;
	}

	private void setMessage(String message) {

		try {
			cmResultChannel.put(message);
		} catch (Exception e) {
			log.error("collectResultChannel.put(message) is error " + StringUtil.getStackTrace(e));
		}
	}

	public String createMessage(String zipName, String user, String pwd, String ip, String port, int countNum,
			String nename) {

		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<FILE_DATA_READY_UL xmlns:xsi=\" http://www.w3.org/2001/XMLSchema-instance\">"
				+ "<Header SessionID=\"");
		strBuffer.append("");
		strBuffer.append("\" LicenceID=\"");
		strBuffer.append("");
		strBuffer.append("\" SystemID=\"");
		strBuffer.append("");
		strBuffer.append("\" Time=\"");
		strBuffer.append(dateFormat2.format(new Date()));
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
		ftpClient = new FTPSrv();
		// login
		try {
			ftpClient.login(ip, Integer.parseInt(port), ftp_user, ftp_password, "GBK",
					Boolean.parseBoolean(ftp_passive), 5 * 60 * 1000);
		} catch (Exception e) {
			log.error("login fail,ip=[" + ip + "] port=[" + port + "] user=[" + ftp_user + /*"]pwd=[" + ftp_password + */"]"
					+ StringUtil.getStackTrace(e));
			return;
		}
		// ftpClient.store(zipFilePath, remoteFile);
		log.debug("store  [" + zipFilePath + "]to[" + remoteFile + "]");

		FileUtils.deleteQuietly(new File(zipFilePath));

	}

	private String[] createZipFile(String csvpathAndFileName, String xmlPathAndFileName, String nename)
			throws IOException {

		String zipPath = resultPath + nename + dateFormat.format(new Date()) + "_" + System.nanoTime();

		File destDir = new File(zipPath);
		destDir.mkdirs();

		try {
			FileUtils.copyFileToDirectory(new File(csvpathAndFileName), destDir);
			FileUtils.copyFileToDirectory(new File(xmlPathAndFileName), destDir);
		} catch (IOException e) {
			throw e;
			// flow should end here in case of exception
		}

		String destFilePath = zipPath + ".zip";
		try {
			Zip zip = new Zip(destDir.getAbsolutePath(), destFilePath);
			zip.setCompressLevel(9);
			zip.compress();

			FileUtils.deleteDirectory(destDir);
		} catch (IOException e) {
			log.error("zip.compress() is fail " + StringUtil.getStackTrace(e));
		}
		return new String[] { destFilePath, zipPath + ".zip" };
	}

	private String setColumnNames(String nename, List<String> names, String type) {
		// write xml
		String xmlpath = localPath + nename + "/" + type + "/";
		File xmlpathfile = new File(xmlpath);
		if (!xmlpathfile.exists()) {
			xmlpathfile.mkdirs();
		}
		String xmlFileName = nename + dateFormat.format(new Date()) + System.nanoTime();
		String fieldLine = "";
		for (int i = 0; i < names.size(); i++) {
			String field = "\t<Field>\r\n" + "\t\t<FieldNo>" + i + "</FieldNo>\r\n" + "\t\t<FieldName>" + names.get(i)
					+ "</FieldName>\r\n" + "\t\t<FieldType>" + names.get(i) + "</FieldType>\r\n"
					+ "\t\t<FieldNameOther>" + names.get(i) + "</FieldNameOther>\r\n" + "\t</Field>\r\n";
			fieldLine = fieldLine + field;
		}

		String str = "<?xml version=\"1.0\" encoding=\"gbk\"?>\r\n" + "<xml>\r\n" + "<FILE_STRUCTURE>\r\n" + fieldLine
				+ "</FILE_STRUCTURE>\r\n" + "</xml>\r\n";
		String xmlPathAndFileName = xmlpath + xmlFileName + ".xml";
		try {
			this.writeDetail(xmlPathAndFileName, str);
		} catch (Exception e) {
			log.error("writeDetail is fail ,xmlFileName=" + xmlFileName + StringUtil.getStackTrace(e));
		}

		return xmlPathAndFileName;
	}

	private void writeDetail(String detailFileName, String str) throws IOException {

		try (OutputStream readOut = new FileOutputStream(new File(detailFileName), false);
				OutputStreamWriter writer = new OutputStreamWriter(readOut)) {
			writer.write(str);
			writer.flush();
		} catch (IOException e) {
			throw e;
		}
	}

	private void appendLine(LinkedHashMap<String, String> nameAndValue, BufferedOutputStream bos) {
		StringBuilder lineDatas = new StringBuilder();

		for (String key : nameAndValue.keySet()) {
			lineDatas.append(nameAndValue.get(key)).append("|");
		}
		try {
			bos.write(lineDatas.toString().getBytes());
			bos.write("\n".getBytes());
		} catch (IOException e) {
			log.error("appendLine error " + StringUtil.getStackTrace(e));
		}
	}

	// private void appendLine(List<String> values,BufferedOutputStream bos) {
	// StringBuilder lineDatas = new StringBuilder();
	//
	// for (String value : values) {
	// lineDatas.append(value).append("|");
	// }
	// try {
	// bos.write(lineDatas.toString().getBytes());
	// bos.write("\n".getBytes());
	// } catch (IOException e) {
	// log.error("appendLine error "+StringUtil.getStackTrace(e));
	// }
	// }

	public List<File> decompressed(String fileName) {
		List<File> filelist = new ArrayList<File>();

		if (fileName.indexOf(".gz") > 1) {
			try {
				File decompressFile = deGz(fileName);
				filelist.add(decompressFile);
				new File(fileName).delete();
			} catch (IOException e) {
				log.error("decompressed is fail " + StringUtil.getStackTrace(e));
			}
		} else if (fileName.indexOf(".zip") > 1) {
			try {
				File[] files = deZip(new File(fileName));
				new File(fileName).delete();
				for (File temp : files) {
					filelist.add(temp);
				}
			} catch (Exception e) {
				log.error("decompressed is fail " + StringUtil.getStackTrace(e));
			}
		} else {
			filelist.add(new File(fileName));
		}

		return filelist;
	}

	private File deGz(String gzFileName) throws IOException {
		Gunzip gunzip = new Gunzip();
		String orgFile = gzFileName.replace(".gz", "");
		gunzip.unCompress(gzFileName, orgFile);
		return new File(orgFile);
	}

	public File[] deZip(File file) throws IOException {

		String regx = "(.*).zip";
		Pattern p = Pattern.compile(regx);
		Matcher m = p.matcher(file.getName());
		if (m.find()) {
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
		// IP
		String ip = collectVo.getIP();
		// port
		String port = collectVo.getPort();
		// user
		String user = collectVo.getUser();
		// password
		String password = collectVo.getPassword();
		// isPassiveMode
		String passivemode = collectVo.getPassive();

		FTPInterface ftpClient = new FTPSrv();

		// login
		try {
			log.info("ftp login ,ip=[" + ip + "] port=[" + port + "] user=[" + user + /*"]password=[" + password +*/ "]");
			ftpClient.login(ip, Integer.parseInt(port), user, password, "GBK", Boolean.parseBoolean(passivemode),
					5 * 60 * 1000);
		} catch (Exception e) {
			log.error("login fail,ip=[" + ip + "] port=[" + port + "] user=[" + user + /*"]password=[" + password +*/ "]"
					+ StringUtil.getStackTrace(e));
			return fileList;
		}

		// download
		String dir = collectVo.getRemotepath();
		List<String> searchExprList = new ArrayList<String>();
		String[] FPath = dir.split(";");
		for (int i = 0; i < FPath.length; i++) {
			int oldSize = searchExprList.size();
			String conpath = FPath[i] + collectVo.getMatch();
			Hashtable<String, String> varMap = new Hashtable<String, String>();
			long collectPeriod = 900;
			try {
				collectPeriod = Long.parseLong(collectVo.getGranularity()) * 60;
				log.info("collectPeriod =[" + collectPeriod + "]");
			} catch (NumberFormatException e) {
				//e.printStackTrace();
				log.error("NumberFormatException" ,e);
			}
			long[] d = DateUtil.getScanScope(new Date(), collectPeriod);
			searchExprList.add(VarExprParser.replaceVar(conpath, d[0], d[1]));

			varMap.clear();
			varMap = null;
			log.info("[" + conpath + "] result[" + (searchExprList.size() - oldSize) + "] path");
			conpath = null;
		}
		String nowdir = null;
		try {
			nowdir = ftpClient.pwd();
			searchExprList = getPathNoRegular(searchExprList, ftpClient);
		} catch (Exception e1) {
			log.error(" collect fail ", e1);
			return fileList;
		}
		List<AFtpRemoteFile> remoteFiles = new ArrayList<AFtpRemoteFile>();
		for (String expr : searchExprList) {
			ftpClient.chdir(nowdir);
			String keys[] = parseExprKeys(expr);
			String ftpRegular = keys[1];
			String ftpDir = keys[0];

			boolean cdsucess = ftpClient.chdir(ftpDir);
			if (cdsucess) {
				AFtpRemoteFile[] arf = (AFtpRemoteFile[]) ftpClient.list();
				log.info(" list [" + ftpDir + "] result[" + (arf == null ? "null" : arf.length) + "] files");
				// filter

				rfileFilter(remoteFiles, arf, ftpRegular);

				keys = null;
				ftpRegular = ftpDir = null;

				for (AFtpRemoteFile ftpRemoteFile : remoteFiles) {
					if (!new File(localPath).exists()) {
						try {
							new File(localPath).mkdir();
						} catch (Exception e) {
							log.error("create localPath is fail localPath=" + localPath + " "
									+ StringUtil.getStackTrace(e));
						}
					}

					if (!new File(localPath).exists()) {
						new File(localPath).mkdirs();
					}

					String localFileName = localPath + ftpRemoteFile.getFileName();
					File loaclFile = new File(localFileName);
					if (loaclFile.exists()) {
						loaclFile.delete();
					}

					boolean flag = ftpClient.downloadFile(ftpRemoteFile.getAbsFileName(), localFileName);

					if (flag) {
						fileList.add(localFileName);
					} else {
						log.error("download file fail fileName=" + ftpRemoteFile.getAbsFileName());
					}
				}

			} else {
				log.error("cd dir is faill dir =[" + ftpDir + "]");
			}
		}

		return fileList;
	}

	private void rfileFilter(List<AFtpRemoteFile> fileContainer, AFtpRemoteFile[] arfs, String ftpRegular) {
		if (ftpRegular != null && ftpRegular.length() > 0) {
			Pattern pattern = null;
			try {
				pattern = Pattern.compile(ftpRegular, Pattern.CASE_INSENSITIVE);
			} catch (Exception e) {
				log.info("[" + ftpRegular + "]Pattern.compile exception:" + e.getMessage());
				// should rethrow exception or return from here
			}
			int hisSize = fileContainer.size();
			for (int j = 0; arfs != null && j < arfs.length; j++) {
				String fileName = parseFileName(arfs[j].getFileName());
				Matcher matcher = null;
				if (pattern != null)
					matcher = pattern.matcher(fileName);
				else {
					// define the flow when pattern is null
				}

				if (matcher.find())
					fileContainer.add(arfs[j]);
			}
			log.info("[" + ftpRegular + "]filter[" + (fileContainer.size() - hisSize) + "]filse");
			pattern = null;
		} else {
			for (int j = 0; arfs != null && j < arfs.length; j++)
				fileContainer.add(arfs[j]);
		}

	}

	private String parseFileName(String fileName) {
		int idx = fileName.lastIndexOf("/");
		if (idx == -1)
			return fileName;
		return fileName.substring(idx + 1, fileName.length());
	}

	private String[] parseExprKeys(String source) {

		if (source.indexOf(";") > -1) {
			source = source.substring(0, source.indexOf(";"));
		}
		if (source.endsWith("/"))
			return new String[] { source, "" };

		int idx = source.lastIndexOf("/");
		String[] dirkeys = new String[2];
		dirkeys[0] = source.substring(0, idx + 1);
		dirkeys[1] = source.substring(idx + 1, source.length());
		return dirkeys;
	}

	public List<String> getPathNoRegular(List<String> searchExprList, FTPInterface ftpCache) throws Exception {
		boolean isregular = false;
		List<String> regularList = new ArrayList<String>();
		for (String regular : searchExprList) {
			Pattern lpattern = null;
			try {
				lpattern = Pattern.compile("(.*/)<([^/]+)>(/.*)");
			} catch (Exception e) {
				log.error("[" + regular + "]compile fails:" + e.getMessage());
			//	e.printStackTrace();
			}
			Matcher matcher = null;
			if (lpattern != null)
				matcher = lpattern.matcher(regular);
			else {
				// define flow in case lpattern is null
			}

			if (matcher.find()) {
				isregular = true;
				String parpath = matcher.group(1);
				try {
					boolean isin = ftpCache.chdir(parpath);
					if (isin) {
						log.info("cd dir [" + parpath + "] sucess");
					} else {
						log.error("cd dir [" + parpath + "] fail");
					}
				} catch (Exception e) {
					log.error(" cd dir [" + parpath + "]fail", e);
					throw e;
				}
				RemoteFile[] remotef = ftpCache.list();
				for (RemoteFile aremote : remotef) {
					if (aremote.isDirectory() && aremote.getFileName().matches(matcher.group(2))) {
						regularList.add(matcher.group(1) + aremote.getFileName() + matcher.group(3));
					}
				}
			} else {
				regularList.add(regular);
			}
		}
		if (isregular == true) {
			getPathNoRegular(regularList, ftpCache);
		}
		return regularList;
	}

}
