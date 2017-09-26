package org.onap.vfc.nfvo.emsdriver.serviceregister;

import org.junit.Test;

public class MsbConfigurationTest {
	
	@Test
	public void testSetMsbAddress(){
		MsbConfiguration.setMsbAddress("aaa/bnn");
	}
	
	@Test
	public void testGetMsbAddress(){
		MsbConfiguration.getMsbAddress();
	}

}
