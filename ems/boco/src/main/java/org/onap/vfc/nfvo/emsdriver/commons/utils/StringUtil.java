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
package org.onap.vfc.nfvo.emsdriver.commons.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StringUtil {

    private static final Log log = LogFactory.getLog(StringUtil.class);
    public static String getStackTrace(Throwable t) {

        try( 
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw)){
            t.printStackTrace(pw);
            pw.flush();
            sw.flush();
            return sw.getBuffer().toString();
        } catch (Exception e) {
		log.error("getStackTrace : ",e);
        } 
        return null;
    }

    public static String addSlash(String dirName) {
        if (dirName.endsWith(File.separator))
            return dirName;
        return dirName + File.separator;
    }

    public static boolean isBank(String str) {
	    return (str == null || str.trim().length() == 0); 
    }
}
