package org.onap.vfc.nfvo.emsdriver.serviceregister.model;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class MsbRegisterVoTest {

	private MsbRegisterVo msbRegisterVo;

    @Before
    public void setUp() {
    	msbRegisterVo = new MsbRegisterVo();
    }
    
    @Test
    public void testMsbRegisterVo(){
    	msbRegisterVo.setProtocol("REST");
    	msbRegisterVo.setServiceName("serviceName");
    	msbRegisterVo.setUrl("http://");
    	msbRegisterVo.setVersion("version");
    	msbRegisterVo.setVisualRange("vr");
    	ArrayList<ServiceNodeVo> list = new ArrayList<ServiceNodeVo>();
    	list.add(new ServiceNodeVo());
    	msbRegisterVo.setNodes(list);
    	msbRegisterVo.getNodes();
    	msbRegisterVo.getProtocol();
    	msbRegisterVo.getServiceName();
    	msbRegisterVo.getUrl();
    	msbRegisterVo.getVersion();
    	msbRegisterVo.getVisualRange();
    	
    }

}
