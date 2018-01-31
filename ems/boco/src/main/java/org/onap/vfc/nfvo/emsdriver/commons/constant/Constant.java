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
package org.onap.vfc.nfvo.emsdriver.commons.constant;

import java.io.File;

public class Constant {
	
	public static final String SYS_HOME = System.getenv("RUNHOME")==null?System.getProperty("user.dir"):System.getenv("RUNHOME");
	
	public static final String SYS_CFG = SYS_HOME + File.separator + "conf" + File.separator;
	public static final String SYS_DATA = SYS_HOME + File.separator  + "data" + File.separator;
	public static final String SYS_DATA_TEMP = SYS_DATA + File.separator + "temp" + File.separator;
	public static final String SYS_DATA_RESULT = SYS_DATA + File.separator + "RESULT" + File.separator;
	public static final String COLLECT_TYPE_CM = "EMS_RESOUCE";
	public static String COLLECT_TYPE_PM = "EMS_PERFORMANCE";
	public static String COLLECT_TYPE_ALARM = "EMS_ALARM";
	
	public static String ENCODING_UTF8 = "UTF-8";
	public static String ENCODING_GBK = "GBK";
	
	public static final String COLLECT_CHANNEL_KEY = "COLLECT_CHANNEL_KEY";
	public static final String COLLECT_RESULT_CHANNEL_KEY = "COLLECT_RESULT_CHANNEL_KEY";
	public static final String COLLECT_RESULT_PM_CHANNEL_KEY = "COLLECT_RESULT_PM_CHANNEL_KEY";
	public static final String RESULT_CHANNEL_KEY = "RESULT_CHANNEL_KEY";
	
	
	public static final String MSBAPIROOTDOMAIN = "/api/microservices/v1/services";
	
	//alarm
	public static final int READ_TIMEOUT_MILLISECOND = 180000;
	public static final long  ONEMINUTE = 60000;
	
}
