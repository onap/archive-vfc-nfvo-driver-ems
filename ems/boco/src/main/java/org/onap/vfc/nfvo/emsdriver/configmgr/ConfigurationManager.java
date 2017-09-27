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
package org.onap.vfc.nfvo.emsdriver.configmgr;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.onap.vfc.nfvo.emsdriver.commons.constant.Constant;
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectVo;
import org.onap.vfc.nfvo.emsdriver.commons.model.CrontabVo;
import org.onap.vfc.nfvo.emsdriver.commons.model.EMSInfo;
import org.onap.vfc.nfvo.emsdriver.commons.utils.DriverThread;
import org.onap.vfc.nfvo.emsdriver.commons.utils.StringUtil;
import org.onap.vfc.nfvo.emsdriver.commons.utils.XmlUtil;
import org.onap.vfc.nfvo.emsdriver.northbound.client.HttpClientUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class ConfigurationManager extends DriverThread{
	protected static Log log = LogFactory.getLog(ConfigurationManager.class);
	/**
	 * ESM Cache
	 */
	private static Map<String, EMSInfo> emsInfoCache = new ConcurrentHashMap<String, EMSInfo>();
	private static Map<String, CrontabVo> emsCrontab= new ConcurrentHashMap<String, CrontabVo>();
	private static List<String> emsIdList = new ArrayList<String>();
	private static Properties properties = null;
	
	private final static String  config = Constant.SYS_CFG + "config.properties";
	
	@Override
	public void dispose() {
		
		//this.log.debug("start loading " + cacheFilePath);
		File file = new File(config);
	    if(!file.exists() || !file.isFile()){
	    	log.error("cacheFilePath " + config+" not exist or is not File");
	    	return;
	    }
	    InputStream in  = null;
		try{
			properties = new Properties();
	        in = new FileInputStream(file);
	        properties.load(in);
	        Map<String, CrontabVo> emsMap = readCorntab();
	        
	        emsCrontab.putAll(emsMap);
	        
	        //
			new ReceiveSource().start();
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
	
	private Map<String, CrontabVo> readCorntab() {
		String path = Constant.SYS_CFG + "crontab.xml";
		File cfg = new File(path);
		log.debug("start loading " + path);
	    if(!cfg.exists() || !cfg.isFile()){
	    	log.debug("not exists " + path);
	    	return null;
	    }
	    
	    InputStream is = null;
	    Map<String, CrontabVo> tmpcache = new HashMap<String, CrontabVo>();
	    
	    try {
			is = new FileInputStream(cfg);
			Document doc = XmlUtil.getDocument(is);
			
			Element root = doc.getRootElement();
			
			@SuppressWarnings("unchecked")
			List<Element> children = root.getChildren();
			
			for(Iterator<Element> it = children.iterator();it.hasNext();){
				CrontabVo crontabVo = new CrontabVo();
				Element child = it.next();
				String type = child.getAttributeValue("type");
				if(StringUtil.isBank(type)){
					continue;
				}
				crontabVo.setType(type);
				if("ems-alarm".equalsIgnoreCase(type)){
					boolean iscollect =  Boolean.parseBoolean(child.getAttributeValue("iscollect"));
					if(iscollect){
						crontabVo.setIscollect(iscollect);
					}else{
						continue;
					}
					
					crontabVo.setRead_timeout(child.getChildText("readtimeout"));
				}else{
					String crontab = child.getAttributeValue("crontab");
					if(!StringUtil.isBank(type) && !StringUtil.isBank(crontab)){
						crontabVo.setCrontab(crontab);
					}else{
						continue;
					}
					crontabVo.setMatch(child.getChildText("match"));
					crontabVo.setGranularity(child.getChildText("granularity"));
				}
				tmpcache.put(type, crontabVo);
			}
			
		} catch (Exception e) {
			log.error("load crontab.xml is error "+StringUtil.getStackTrace(e));
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
		return tmpcache;
	}


	public  void readcfg(){
		String path = Constant.SYS_CFG + "EMSInfo.xml";
		File cfg = new File(path);
		log.debug("start loading " + path);
	    if(!cfg.exists() || !cfg.isFile()){
	    	log.debug("not exists " + path);
	    	return;
	    }
	   
        
	    InputStream is = null;
	    Map<String, EMSInfo> tmpcache = new HashMap<String, EMSInfo>();
	    
	    try {
			is = new FileInputStream(cfg);
			Document doc = XmlUtil.getDocument(is);
			
			Element root = doc.getRootElement();
			
			@SuppressWarnings("unchecked")
			List<Element> children = root.getChildren();
			
			for(Iterator<Element> it = children.iterator();it.hasNext();){
				EMSInfo emsInfo = new EMSInfo();
				Element child = it.next();
				String name = child.getAttributeValue("name");
				if(StringUtil.isBank(name)){
					continue;
				}
				emsInfo.setName(name);
				
//				tmpcache.put(name, emsInfo);
				
				@SuppressWarnings("unchecked")
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
			
			File file = new File(config);
		    if(!file.exists() || !file.isFile()){
		    	log.error("cacheFilePath " + config+" not exist or is not File");
		    	return;
		    }
		    InputStream in  = null;
			try{
				properties = new Properties();
		        in = new FileInputStream(file);
		        properties.load(in);
			}catch (Exception e) {
				e.printStackTrace();
			}
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
	
	class ReceiveSource extends Thread{
		long timeStamp = System.currentTimeMillis();
		
		public void run() {
			while(true){
				
				try {
					if(System.currentTimeMillis() - timeStamp > Constant.ONEMINUTE){
						timeStamp = System.currentTimeMillis();
						log.debug("ReceiveSource run");
					}
					//get emsId list
					List<String> emsIds = this.getEmsIdList();
					if(emsIds.size() > 0){
						emsIdList.clear();
						emsIdList.addAll(emsIds);
					}
					
					for(String emsId : emsIdList){
						//get emsInfo by emsId 
						Map<String, EMSInfo> emsInfoMap = this.getEmsInfo(emsId);
						
						emsInfoCache.putAll(emsInfoMap);
					}
					
					
					//
					if(emsInfoCache.size() > 0){
						Thread.sleep(5*60*1000);
					}else{
						Thread.sleep(60*1000);
					}
				} catch (Exception e) {
					log.error("ReceiveSource exception",e);
				}
			}
		}

		private Map<String, EMSInfo> getEmsInfo(String emsId) {
			Map<String, EMSInfo> tmpcache = new ConcurrentHashMap<String, EMSInfo>();
			String msbAddress = properties.getProperty("msbAddress");
			String emstUrl = properties.getProperty("esr_emsUrl");
			//set emsId to url
			String.format(emstUrl, emsId);
			String getemstUrl = "http://"+msbAddress+emstUrl;
			String emsResult = HttpClientUtil.doGet(getemstUrl, Constant.ENCODING_UTF8);
			log.debug(getemstUrl+" result="+emsResult);
			JSONObject reagobj = JSONObject.parseObject(emsResult);
			
			JSONObject  esr_system_info_list = reagobj.getJSONObject("esr-system-info-list");
			JSONArray esr_system_info = esr_system_info_list.getJSONArray("esr-system-info");
			Iterator<Object> it = esr_system_info.iterator();
			EMSInfo emsInfo = new EMSInfo();
			emsInfo.setName(emsId);
			tmpcache.put(emsId, emsInfo);
			while(it.hasNext()){
				Object obj = it.next();
				JSONObject collect = (JSONObject)obj;
				String system_type = (String)collect.get("system-type");
				CollectVo collectVo = new CollectVo();
				if("ems-resource".equalsIgnoreCase(system_type)){
					CrontabVo crontabVo = emsCrontab.get(system_type);
					if(crontabVo != null){
						collectVo.setType(system_type);
						collectVo.setCrontab(crontabVo.getCrontab());
						collectVo.setIP(collect.getString("ip-address"));
						collectVo.setPort(collect.getString("port"));
						collectVo.setUser(collect.getString("user-name"));
						collectVo.setPassword(collect.getString("password"));
					
						collectVo.setRemotepath(collect.getString("remote-path"));
						collectVo.setMatch(crontabVo.getMatch());
						collectVo.setPassive(collect.getString("passive"));
						collectVo.setGranularity(crontabVo.getGranularity());
					}
					
					
				}else if("ems-performance".equalsIgnoreCase(system_type)){
					CrontabVo crontabVo = emsCrontab.get(system_type);
					if(crontabVo != null){
						collectVo.setType(system_type);
						collectVo.setCrontab(crontabVo.getCrontab());
						collectVo.setIP(collect.getString("ip-address"));
						collectVo.setPort(collect.getString("port"));
						collectVo.setUser(collect.getString("user-name"));
						collectVo.setPassword(collect.getString("password"));
					
						collectVo.setRemotepath(collect.getString("remote-path"));
						collectVo.setMatch(crontabVo.getMatch());
						collectVo.setPassive(collect.getString("passive"));
						collectVo.setGranularity(crontabVo.getGranularity());
					}
				}else if("ems-alarm".equalsIgnoreCase(system_type)){
					CrontabVo crontabVo = emsCrontab.get(system_type);
					if(crontabVo != null){
						collectVo.setIscollect(crontabVo.isIscollect());
						collectVo.setType(system_type);
						collectVo.setIP(collect.getString("ip-address"));
						collectVo.setPort(collect.getString("port"));
						collectVo.setUser(collect.getString("user-name"));
						collectVo.setPassword(collect.getString("password"));
						collectVo.setRead_timeout(crontabVo.getRead_timeout());
					}else{
						log.error("emsCrontab.get(system_type) result crontabVo is null" );
					}
					
					
				}else{
					log.error("ems system-type ="+system_type+" ");
				}
				
				emsInfo.putCollectMap(system_type, collectVo);
			}
			return tmpcache;
		}

		private List<String> getEmsIdList() {
			List<String> emsIds = new ArrayList<String>();
			//http
			String msbAddress = properties.getProperty("msbAddress");
			String url = properties.getProperty("esr_ems_listUrl");
			String getemsListUrl = "http://"+msbAddress+url;
			
			String result = HttpClientUtil.doGet(getemsListUrl, Constant.ENCODING_UTF8);
			log.debug(getemsListUrl+" result="+result);
			JSONObject reagobj = JSONObject.parseObject(result);
			JSONArray  esr_emsIds = reagobj.getJSONArray("esr-ems");
			Iterator<Object> it = esr_emsIds.iterator();
			while(it.hasNext()){
				Object obj = it.next();
				JSONObject emsId = (JSONObject)obj;
				String emsIdStr = (String)emsId.get("ems-id");
				emsIds.add(emsIdStr);
			}
			return emsIds;
		}
	}
}
