package org.onap.vfc.nfvo.emsdriver.serviceregister.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ServiceNodeVoTest {
	
	private ServiceNodeVo serviceNodeVo;

    @Before
    public void setUp() {
    	serviceNodeVo = new ServiceNodeVo();
    }
    
    @Test
    public void testServiceNodeVo(){
    	serviceNodeVo.setIp("127.0.0.1");
    	serviceNodeVo.setPort("100");
    	serviceNodeVo.setTtl(1);
    	assertNotNull(serviceNodeVo.getIp());
    	assertNotNull(serviceNodeVo.getPort());
    	assertNotNull(serviceNodeVo.getTtl());
    }

}
