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
package org.onap.vfc.nfvo.emsdriver.commons.model;

/**
 * @author boco
 *
 */
public class CrontabVo {
	
	private String type;
	
	private String crontab;
	private String match;
	private String granularity;
	
	private boolean iscollect = false;
	
	private String read_timeout;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the crontab
	 */
	public String getCrontab() {
		return crontab;
	}

	/**
	 * @param crontab the crontab to set
	 */
	public void setCrontab(String crontab) {
		this.crontab = crontab;
	}

	/**
	 * @return the match
	 */
	public String getMatch() {
		return match;
	}

	/**
	 * @param match the match to set
	 */
	public void setMatch(String match) {
		this.match = match;
	}

	/**
	 * @return the granularity
	 */
	public String getGranularity() {
		return granularity;
	}

	/**
	 * @param granularity the granularity to set
	 */
	public void setGranularity(String granularity) {
		this.granularity = granularity;
	}

	/**
	 * @return the iscollect
	 */
	public boolean isIscollect() {
		return iscollect;
	}

	/**
	 * @param iscollect the iscollect to set
	 */
	public void setIscollect(boolean iscollect) {
		this.iscollect = iscollect;
	}

	/**
	 * @return the read_timeout
	 */
	public String getRead_timeout() {
		return read_timeout;
	}

	/**
	 * @param read_timeout the read_timeout to set
	 */
	public void setRead_timeout(String read_timeout) {
		this.read_timeout = read_timeout;
	}
	
	

	
}
