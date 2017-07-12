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
package org.openo.nfvo.emsdriver.commons.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtil {

	public static String getStackTrace(Throwable t){
		
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			pw.flush();
			sw.flush();
			return sw.getBuffer().toString();
		} catch (Exception e) {
			
		}finally{
			try {
				if(pw != null) pw.close();
				if(sw != null) sw.close();
			} catch (Exception e2) {
				
			}
		}
		return null;
	}

	public static String addSlash(String dirName) {
		if (dirName.endsWith(File.separator))
			return dirName;
		return dirName + File.separator;
	}
	
	public static  boolean isBank(String str){
		
		if(str == null || str.trim().length() == 0){
			
			return true;
		}
		return false;
	}
}
