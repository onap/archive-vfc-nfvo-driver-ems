package org.onap.vfc.nfvo.emsdriver.serviceregister;

import org.junit.Before;
import org.junit.Test;
import org.onap.vfc.nfvo.emsdriver.serviceregister.model.MsbRegisterVo;

public class MsbRestServiceProxyTest {

	@Before
	public void upSet(){
		MsbConfiguration.setMsbAddress("");
	}
	@Test
	public void testRegisterService(){
		MsbRegisterVo registerInfo = new MsbRegisterVo();
		registerInfo.setServiceName("ems-driver");
		registerInfo.setUrl("/api/emsdriver/v1");
		String registerResponse = MsbRestServiceProxy.registerService(registerInfo);
		System.out.println(registerResponse);
	}
}
