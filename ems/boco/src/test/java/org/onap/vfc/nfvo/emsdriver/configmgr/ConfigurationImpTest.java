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
package org.onap.vfc.nfvo.emsdriver.configmgr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.onap.vfc.nfvo.emsdriver.commons.constant.Constant;
import org.onap.vfc.nfvo.emsdriver.commons.model.CollectVo;
import org.onap.vfc.nfvo.emsdriver.commons.model.EMSInfo;
import org.onap.vfc.nfvo.emsdriver.commons.utils.StringUtil;
import org.onap.vfc.nfvo.emsdriver.commons.utils.XmlUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConfigurationImpTest {
    //normally an unit test should not log
    protected static Log log = LogFactory.getLog(ConfigurationImpTest.class);
    private ConfigurationManager configurationManager;
    private ConfigurationImp configurationImp;

    public static void readcfg() {
        String path = Constant.SYS_CFG + "EMSInfo.xml";
        File cfg = new File(path);
        log.debug("start loading " + path);
        if (!cfg.exists() || !cfg.isFile()) {
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

            for (Iterator<Element> it = children.iterator(); it.hasNext(); ) {
                EMSInfo emsInfo = new EMSInfo();
                Element child = it.next();
                String name = child.getAttributeValue("name");
                if (StringUtil.isBank(name)) {
                    continue;
                }
                emsInfo.setName(name);

//				tmpcache.put(name, emsInfo);

                @SuppressWarnings("unchecked")
                List<Element> collectList = child.getChildren();
                for (Element collect : collectList) {

                    CollectVo collectVo = new CollectVo();

                    String type = collect.getAttributeValue("type");
                    if ("alarm".equalsIgnoreCase(type)) {
                        boolean iscollect = Boolean.parseBoolean(collect.getAttributeValue("iscollect"));
                        if (iscollect) {
                            collectVo.setIscollect(iscollect);
                        } else {
                            continue;
                        }
                        collectVo.setType(type);
                        collectVo.setIP(collect.getChildText("ip"));
                        collectVo.setPort(collect.getChildText("port"));
                        collectVo.setUser(collect.getChildText("user"));
                        collectVo.setPassword(collect.getChildText("password"));
                        collectVo.setReadTimeout(collect.getChildText("readtimeout"));
                    } else {
                        String crontab = collect.getAttributeValue("crontab");
                        if (!StringUtil.isBank(type) && !StringUtil.isBank(crontab)) {
                            collectVo.setType(type);
                            collectVo.setCrontab(crontab);
                        } else {
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
            ConfigurationManager.emsInfoCache.putAll(tmpcache);

            File file = new File(ConfigurationManager.CONFIG_PROPERTIES_LOCATION);
            if (!file.exists() || !file.isFile()) {
                log.error("cacheFilePath " + ConfigurationManager.CONFIG_PROPERTIES_LOCATION + " not exist or is not File");
                return;
            }
            InputStream in = null;
            try {
                ConfigurationManager.properties = new Properties();
                in = new FileInputStream(file);
                ConfigurationManager.properties.load(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            log.error("load EMSInfo.xml is error " + StringUtil.getStackTrace(e));
        } finally {
            tmpcache.clear();
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch (Exception e2) {
            }
            cfg = null;
        }
    }


    @Before
    public void setUp() throws IOException {
        configurationImp = new ConfigurationImp();
        configurationManager = new ConfigurationManager();
        readcfg();
    }

    @Test
    public void getAllEMSInfo() {

        List<EMSInfo> list = configurationImp.getAllEMSInfo();

        assertTrue(list.size() > 0);
    }

    @Test
    public void getCollectVoByEmsNameAndType() {

        CollectVo collectVo = configurationImp.getCollectVoByEmsNameAndType("1234", "cm");

        assertNotNull(collectVo);
    }

    @Test
    public void getProperties() {

        Properties properties = configurationImp.getProperties();

        assertNotNull(properties);
    }

}
