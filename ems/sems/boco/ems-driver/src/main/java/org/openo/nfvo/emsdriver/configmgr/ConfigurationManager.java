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
package org.openo.nfvo.emsdriver.configmgr;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.jdom.Document;
import org.jdom.Element;
import org.openo.nfvo.emsdriver.commons.constant.Constant;
import org.openo.nfvo.emsdriver.commons.model.CollectVo;
import org.openo.nfvo.emsdriver.commons.model.EMSInfo;
import org.openo.nfvo.emsdriver.commons.utils.DriverThread;
import org.openo.nfvo.emsdriver.commons.utils.StringUtil;
import org.openo.nfvo.emsdriver.commons.utils.XmlUtil;


public class ConfigurationManager extends DriverThread{

	/**
	 * ESM Cache
	 */
	private static Map<String, EMSInfo> emsInfoCache = new ConcurrentHashMap<String, EMSInfo>();
	
	private static Properties properties = null;
	
	private final static String  ftpconfig = Constant.SYS_CFG + "ftpconfig.properties";
	
	@Override
	@SuppressWarnings("unchecked")
	public void dispose() {
		String path = Constant.SYS_CFG + "EMSInfo.xml";
		File cfg = new File(path);
		this.log.debug("start loading " + path);
	    if(!cfg.exists() || !cfg.isFile()){
	    	this.log.debug("not exists " + path);
	    	return;
	    }
	    
	    InputStream is = null;
	    Map<String, EMSInfo> tmpcache = new HashMap<String, EMSInfo>();
	    
	    try {
			is = new FileInputStream(cfg);
			Document doc = XmlUtil.getDocument(is);
			
			Element root = doc.getRootElement();
			
			List<Element> children = root.getChildren();
			
			for(Iterator<Element> it = children.iterator();it.hasNext();){
				EMSInfo emsInfo = new EMSInfo();
				Element child = it.next();
				String name = child.getAttributeValue("name");
				if(StringUtil.isBank(name)){
					continue;
				}
				emsInfo.setName(name);
				
				tmpcache.put(name, emsInfo);
				
				List<Element> collectList = child.getChildren();
				for(Element collect : collectList){
					
					CollectVo collectVo = new CollectVo();
					
					String type = collect.getAttributeValue("type");
					if("alarm".equalsIgnoreCase(type)){
						boolean iscollect =  Boolean.parseBoolean(collect.getAttributeValue("iscollect"));
						if(iscollect){
							collectVo.setIscollect(iscollect);
						}else{
							continue;
						}
						collectVo.setType(type);
						collectVo.setIP(collect.getChildText("ip"));
						collectVo.setPort(collect.getChildText("port"));
						collectVo.setUser(collect.getChildText("user"));
						collectVo.setPassword(collect.getChildText("password"));
						collectVo.setRead_timeout(collect.getChildText("readtimeout"));
					}else{
						String crontab = collect.getAttributeValue("crontab");
						if(!StringUtil.isBank(type) && !StringUtil.isBank(crontab)){
							collectVo.setType(type);
							collectVo.setCrontab(crontab);
						}else{
							continue;
						}
						collectVo.setIP(collect.getChildText("ip"));
						collectVo.setPort(collect.getChildText("port"));
						collectVo.setUser(collect.getChildText("user"));
						collectVo.setPassword(collect.getChildText("password"));
						collectVo.setRemotepath(collect.getChildText("remotepath"));
						collectVo.setMatch(collect.getChildText("match"));
						collectVo.setPassive(collect.getChildText("passive"));
						collectVo.setFtptype(collect.getChildText("ftptype"));
						collectVo.setGranularity(collect.getChildText("granularity"));
					}
				
					emsInfo.putCollectMap(type, collectVo);
				}
				tmpcache.put(name, emsInfo);
			}
			emsInfoCache.putAll(tmpcache);
			
		} catch (Exception e) {
			log.error("load EMSInfo.xml is error "+StringUtil.getStackTrace(e));
		}finally{
			tmpcache.clear();
			try {
				if(is != null){
					is.close();
					is = null;
				}
			} catch (Exception e2) {
			}
			cfg = null;
		}
		
		
		//this.log.debug("start loading " + cacheFilePath);
		File file = new File(ftpconfig);
	    if(!file.exists() || !file.isFile()){
	    	this.log.error("cacheFilePath " + ftpconfig+"not exist or is not File");
	    	return;
	    }
	    InputStream in  = null;
		try{
			properties = new Properties();
	        in = new FileInputStream(file);
	        properties.load(in);
	       
		}catch(Exception e) {
			log.error("read ["+file.getAbsolutePath()+"]Exception :",e);
		}finally {
			if(in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}
	}
	
	public static synchronized List<EMSInfo> getAllEMSInfos(){
		List<EMSInfo> list = new ArrayList<EMSInfo>();
		for(EMSInfo emsinfo :emsInfoCache.values()){
			list.add(emsinfo);
		}
		return list;
	}
	
	public static synchronized EMSInfo getEMSInfoByName(String emsName){
		EMSInfo emsInfo= emsInfoCache.get(emsName);
		return emsInfo;
	}
	
	public  static synchronized Properties getProperties() {
		return properties;
	}
	

}
