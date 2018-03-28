/**
 * Copyright 2017 BOCO Corporation 
 * Copyright 2018 CMCC Technologies Co. Ltd.
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.annotations.VisibleForTesting;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class ConfigurationManager extends DriverThread {
    public static final String CONFIG_PROPERTIES_LOCATION = Constant.SYS_CFG + "config.properties";
    protected static final Log logger = LogFactory.getLog(ConfigurationManager.class);
    /**
     * ESM Cache
     */
    @VisibleForTesting
    static Map<String, EMSInfo> emsInfoCache = new ConcurrentHashMap<>();
    @VisibleForTesting
    static Properties properties = null;
    private static Map<String, CrontabVo> emsCrontab = new ConcurrentHashMap<>();
    private static List<String> emsIdList = new ArrayList<>();

    static{
    	 File file = new File(CONFIG_PROPERTIES_LOCATION);
         if (!file.exists() || !file.isFile()) {
         	logger.error("cacheFilePath " + CONFIG_PROPERTIES_LOCATION + " not exist or is not File");
         }else{
			try {
				InputStream in = new FileInputStream(file);
	        	 properties = new Properties();
	             properties.load(in);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("read [" + file.getAbsolutePath() + "]Exception :", e);
			}

         }
    }
    
    public static synchronized List<EMSInfo> getAllEMSInfos() {
        List<EMSInfo> list = new ArrayList<>();
        for (EMSInfo emsinfo : emsInfoCache.values()) {
            list.add(emsinfo);
        }
        return list;
    }

    public static synchronized EMSInfo getEMSInfoByName(String emsName) {
        return emsInfoCache.get(emsName);
    }

    public static synchronized Properties getProperties() {
        return properties;
    }

    @Override
    public void dispose() {
    	Map<String, CrontabVo> emsMap = readCorntab();
    	emsCrontab.putAll(emsMap);
    	new ReceiveSource().start();
        
    }

    public Map<String, CrontabVo> readCorntab() {
        String path = Constant.SYS_CFG + "crontab.xml";
        File cfg = new File(path);
        logger.debug("start loading " + path);
        if (!cfg.exists() || !cfg.isFile()) {
        	logger.debug("not exists " + path);
            return null;
        }
        Map<String, CrontabVo> tmpcache = new HashMap<>();

        try (
            InputStream is = new FileInputStream(cfg)){
            Document doc = XmlUtil.getDocument(is);

            Element root = doc.getRootElement();

            @SuppressWarnings("unchecked")
            List<Element> children = root.getChildren();

            for (Iterator<Element> it = children.iterator(); it.hasNext(); ) {
                CrontabVo crontabVo = new CrontabVo();
                Element child = it.next();
                String type = child.getAttributeValue("type");
                if (StringUtil.isBank(type)) {
                    continue;
                }
                crontabVo.setType(type);
                if (Constant.COLLECT_TYPE_ALARM.equalsIgnoreCase(type)) {
                    boolean iscollect = Boolean.parseBoolean(child.getAttributeValue("iscollect"));
                    if (iscollect) {
                        crontabVo.setIscollect(iscollect);
                    } else {
                        continue;
                    }

                    crontabVo.setReadTimeout(child.getChildText("readtimeout"));
                } else {
                    String crontab = child.getAttributeValue("crontab");
                    if (!StringUtil.isBank(type) && !StringUtil.isBank(crontab)) {
                        crontabVo.setCrontab(crontab);
                    } else {
                        continue;
                    }
                    crontabVo.setMatch(child.getChildText("match"));
                    crontabVo.setGranularity(child.getChildText("granularity"));
                }
                tmpcache.put(type.toUpperCase(), crontabVo);
            }

        } catch (Exception e) {
        	logger.error("load crontab.xml is error " + StringUtil.getStackTrace(e));
        } 
        return tmpcache;
    }

    class ReceiveSource extends Thread {
        long timeStamp = System.currentTimeMillis();
	
 	@Override
        public void run() {
            while (true) {

                try {
                    if (System.currentTimeMillis() - timeStamp > Constant.ONEMINUTE) {
                        timeStamp = System.currentTimeMillis();
                        logger.debug("ReceiveSource run");
                    }
                    //get emsId list
                    List<String> emsIds = this.getEmsIdList();
                    if (!emsIds.isEmpty()) {
                        emsIdList.clear();
                        emsIdList.addAll(emsIds);
                    }

                    for (String emsId : emsIdList) {
                        //get emsInfo by emsId
                        Map<String, EMSInfo> emsInfoMap = this.getEmsInfo(emsId);

                        emsInfoCache.putAll(emsInfoMap);
                    }
                    if (emsInfoCache.size() > 0) {
                        Thread.sleep(30 * 60 * 1000L);
                    } else {
                        Thread.sleep(60 * 1000L);
                    }
                } catch (Exception e) {
                    try {
                        Thread.sleep(60 * 1000L);
                    } catch (Exception e1) {
                    	logger.error("InterruptedException :" + StringUtil.getStackTrace(e1));
                    }
                    logger.error("ReceiveSource exception", e);
                }
            }
        }

        private Map<String, EMSInfo> getEmsInfo(String emsId) {
            Map<String, EMSInfo> tmpcache = new ConcurrentHashMap<>();
            String msbAddress = properties.getProperty("msbAddress");
            String emstUrl = properties.getProperty("esr_emsUrl");
            //set emsId to url
            emstUrl = String.format(emstUrl, emsId);
            String getemstUrl = "http://" + msbAddress + emstUrl;
            String emsResult = HttpClientUtil.doGet(getemstUrl, Constant.ENCODING_UTF8);
            logger.debug(getemstUrl + " result=" + emsResult);
            JSONObject reagobj = JSONObject.parseObject(emsResult);

            JSONObject esrSystemInfoList = reagobj.getJSONObject("esr-system-info-list");
            JSONArray esrSystemInfo = esrSystemInfoList.getJSONArray("esr-system-info");
            Iterator<Object> it = esrSystemInfo.iterator();
            EMSInfo emsInfo = new EMSInfo();
            emsInfo.setName(emsId);
            tmpcache.put(emsId, emsInfo);
            String ipAddressConstant = "ip-address";
            String userNameConstant = "user-name";
            String passwordConstant = "password";
            String portConstant = "port";
            while (it.hasNext()) {
                Object obj = it.next();
                JSONObject collect = (JSONObject) obj;
                String systemType = (String) collect.get("system-type");
                CollectVo collectVo = new CollectVo();
                if (Constant.COLLECT_TYPE_CM.equalsIgnoreCase(systemType)) {
                    CrontabVo crontabVo = emsCrontab.get(systemType);
                    if (crontabVo != null) {
                        collectVo.setType(systemType);
                        collectVo.setCrontab(crontabVo.getCrontab());
                        collectVo.setIP(collect.getString(ipAddressConstant));
                        collectVo.setPort(collect.getString(portConstant));
                        collectVo.setUser(collect.getString(userNameConstant));
                        collectVo.setPassword(collect.getString(passwordConstant));

                        collectVo.setRemotepath(collect.getString("remote-path"));
                        collectVo.setMatch(crontabVo.getMatch());
                        collectVo.setPassive(collect.getString("passive"));
                        collectVo.setGranularity(crontabVo.getGranularity());
                    } else {
                    	logger.error("emsCrontab.get(systemType) result crontabVo is null systemType=[" + systemType + "] emsCrontabMap=" + emsCrontab);
                    }


                } else if (Constant.COLLECT_TYPE_PM.equalsIgnoreCase(systemType)) {
                    CrontabVo crontabVo = emsCrontab.get(systemType);
                    if (crontabVo != null) {
                        collectVo.setType(systemType);
                        collectVo.setCrontab(crontabVo.getCrontab());
                        collectVo.setIP(collect.getString(ipAddressConstant));
                        collectVo.setPort(collect.getString(portConstant));
                        collectVo.setUser(collect.getString(userNameConstant));
                        collectVo.setPassword(collect.getString(passwordConstant));

                        collectVo.setRemotepath(collect.getString("remote-path"));
                        collectVo.setMatch(crontabVo.getMatch());
                        collectVo.setPassive(collect.getString("passive"));
                        collectVo.setGranularity(crontabVo.getGranularity());
                    } else {
                    	logger.error("emsCrontab.get(systemType) result crontabVo is null systemType=[" + systemType + "]");
                    }
                } else if (Constant.COLLECT_TYPE_ALARM.equalsIgnoreCase(systemType)) {
                    CrontabVo crontabVo = emsCrontab.get(systemType);
                    if (crontabVo != null) {
                        collectVo.setIscollect(crontabVo.isIscollect());
                        collectVo.setType(systemType);
                        collectVo.setIP(collect.getString(ipAddressConstant));
                        collectVo.setPort(collect.getString(portConstant));
                        collectVo.setUser(collect.getString(userNameConstant));
                        collectVo.setPassword(collect.getString(passwordConstant));
                        collectVo.setReadTimeout(crontabVo.getReadTimeout());
                    } else {
                    	logger.error("emsCrontab.get(systemType) result crontabVo is null systemType=[" + systemType + "]");
                    }


                } else {
                	logger.error("ems system-type =" + systemType + " ");
                }

                emsInfo.putCollectMap(systemType, collectVo);
            }
            return tmpcache;
        }

        private List<String> getEmsIdList() {
            List<String> emsIds = new ArrayList<>();
            //http
            String msbAddress = properties.getProperty("msbAddress");
            String url = properties.getProperty("esr_ems_listUrl");
            String getemsListUrl = "http://" + msbAddress + url;

            String result = HttpClientUtil.doGet(getemsListUrl, Constant.ENCODING_UTF8);
            logger.debug(getemsListUrl + " result=" + result);
            JSONObject reagobj = JSONObject.parseObject(result);
            JSONArray esrEmsIds = reagobj.getJSONArray("esr-ems");
            Iterator<Object> it = esrEmsIds.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                JSONObject emsId = (JSONObject) obj;
                String emsIdStr = (String) emsId.get("ems-id");
                emsIds.add(emsIdStr);
            }
            return emsIds;
        }
    }
}
