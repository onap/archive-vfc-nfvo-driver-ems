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
package org.openo.nfvo.emsdriver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openo.nfvo.emsdriver.commons.constant.Constant;
import org.openo.nfvo.emsdriver.commons.utils.DriverThread;
import org.openo.nfvo.emsdriver.northbound.service.EmsDriverApplication;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;



public class EMSDriverApp {

	private static Log log = LogFactory.getLog(DriverThread.class);
	public static ApplicationContext context = null;
	static {
		try {
			/** spring bean applicationContext **/
			context = new FileSystemXmlApplicationContext("file:" + Constant.SYS_CFG+ "spring.xml");
			
		} catch (BeansException e) {
			log.error("spring.xml is fail ", e);
			System.exit(1);
		} catch (Exception e) {
			log.error("spring.xml is fail", e);
			System.exit(1);
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] allThreadName = context.getBeanNamesForType(DriverThread.class);

		log.info("worker num :" + allThreadName.length);

		for (String threadName : allThreadName) {
			DriverThread thread = (DriverThread) context.getBean(threadName);
			if (thread == null) {
				log.error(threadName + "Thread start error,system exit");
				System.exit(1);
			}
			thread.setName(threadName);
			thread.start();
		}
		
		try {
			new EmsDriverApplication().run(args);
		} catch (Exception e) {
			log.error("EmsDriverApplication.run is fail", e);
			System.exit(1);
		}
		log.info("the workerThreads start sucess" );
	}

}
