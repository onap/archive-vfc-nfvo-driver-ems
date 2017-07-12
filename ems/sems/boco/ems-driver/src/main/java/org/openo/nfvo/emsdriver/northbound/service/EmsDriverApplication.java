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
package org.openo.nfvo.emsdriver.northbound.service;

import io.dropwizard.Application;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openo.nfvo.emsdriver.serviceregister.MsbConfiguration;
import org.openo.nfvo.emsdriver.serviceregister.MsbRestServiceProxy;
import org.openo.nfvo.emsdriver.serviceregister.model.MsbRegisterVo;
import org.openo.nfvo.emsdriver.serviceregister.model.ServiceNodeVo;

public class EmsDriverApplication extends Application<EmsDriverConfiguration> {
	
	protected static Log log = LogFactory.getLog(EmsDriverApplication.class);
	
	public static void main(String[] args) throws Exception {
        new EmsDriverApplication().run(args);
    }

    @Override
    public String getName() {
        return "ems-driver";
    }

    @Override
    public void initialize(Bootstrap<EmsDriverConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(EmsDriverConfiguration configuration,Environment environment) {
    	// register CommandResource
    	environment.jersey().register(new CommandResource());
    	
    	
    	MsbConfiguration.setMsbAddress(configuration.getMsbAddress());
    	//MSB register
    	this.msbRegisteEmsDriverService(configuration);
    }

	private void msbRegisteEmsDriverService(EmsDriverConfiguration configuration) {
		DefaultServerFactory defaultServerFactory = (DefaultServerFactory)configuration.getServerFactory();
    	HttpConnectorFactory connector = (HttpConnectorFactory)defaultServerFactory.getAdminConnectors().get(0);
		MsbRegisterVo registerVo = new MsbRegisterVo();
		ServiceNodeVo serviceNode = new ServiceNodeVo();
		String ip = "";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.error("Unable to get host ip: " + e.getMessage());
		}
		if(ip.equals("")){
			ip = connector.getBindHost();
		}
		serviceNode.setIp(ip);
		serviceNode.setPort(String.valueOf(connector.getPort()));
		serviceNode.setTtl(0);
		
		List<ServiceNodeVo> nodeList =  new ArrayList<ServiceNodeVo>();
		nodeList.add(serviceNode);
		registerVo.setServiceName("emsdriver");
		registerVo.setUrl("/openoapi/emsdriver/v1");
		registerVo.setNodes(nodeList);
		
		MsbRestServiceProxy.registerService(registerVo);
		log.info("register monitor-umc service to msb finished.");
		
	}
}
