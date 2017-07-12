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
package org.openo.nfvo.emsdriver.serviceregister;


import java.util.ArrayList;
import java.util.List;

import org.openo.nfvo.emsdriver.commons.constant.Constant;
import org.openo.nfvo.emsdriver.northbound.client.HttpClientUtil;
import org.openo.nfvo.emsdriver.serviceregister.model.MsbRegisterVo;
import org.openo.nfvo.emsdriver.serviceregister.model.ServiceNodeVo;

import com.alibaba.fastjson.JSON;

public class MsbRestServiceProxy {

    public static String registerService(MsbRegisterVo registerInfo){
    	String url = MsbConfiguration.getMsbAddress()+Constant.MSBAPIROOTDOMAIN;
    	String registerObj = JSON.toJSONString(registerInfo);
    	
    	String registerResponse = HttpClientUtil.doPost(url, registerObj, Constant.ENCODING_UTF8);
        return registerResponse;
    }

    public static void unRegiserService(String serviceName,String version,String ip,String port){
        String url = MsbConfiguration.getMsbAddress()+Constant.MSBAPIROOTDOMAIN+"/"+serviceName+"/version/"+version+"/nodes/"+ip+"/"+port;
        HttpClientUtil.doDelete(url, Constant.ENCODING_UTF8);
    }
    
    public static List<String> queryService(String serviceName,String version){
    	List<String> ipList = new ArrayList<String>();
    	String url = MsbConfiguration.getMsbAddress()+Constant.MSBAPIROOTDOMAIN+"/"+serviceName+"/version/"+version;
    	String response = HttpClientUtil.doGet(url, Constant.ENCODING_UTF8);
    	if(!response.equals("")){
        	MsbRegisterVo msbRegisterVo = JSON.parseObject(response,MsbRegisterVo.class);
    		List<ServiceNodeVo> nodeList = msbRegisterVo.getNodes();
    		
    		for(ServiceNodeVo node :nodeList){
    			String ip = node.getIp();
    			ipList.add(ip);
    		}
    	}
		return ipList;
		
    }

}
