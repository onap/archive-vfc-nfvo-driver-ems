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
package org.onap.vfc.nfvo.emsdriver;

import io.dropwizard.Configuration;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmsDriverConfiguration  extends Configuration{

	@NotEmpty
    private String msbAddress;
	
	private String autoServiceRegister="true";

	@NotEmpty
    private String defaultName = "EmsDriver-Stranger";

    @JsonProperty
    public String getMsbAddress() {
        return msbAddress;
    }

    @JsonProperty
    public void setMsbAddress(String msbAddress) {
        this.msbAddress = msbAddress;
    }
    
    @JsonProperty
    public String getAutoServiceRegister() {
		return autoServiceRegister;
	}
    
    @JsonProperty
	public void setAutoServiceRegister(String autoServiceRegister) {
		this.autoServiceRegister = autoServiceRegister;
	}

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String name) {
        this.defaultName = name;
    }
}
