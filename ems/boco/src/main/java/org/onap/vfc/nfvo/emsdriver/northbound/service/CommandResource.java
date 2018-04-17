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
package org.onap.vfc.nfvo.emsdriver.northbound.service;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/ems-driver")
@Produces(MediaType.APPLICATION_JSON)
@Api(tags = {"ems-driver TestResource"})
public class CommandResource {

    private Logger log = LoggerFactory.getLogger(CommandResource.class);

    @GET
    @Timed
    @ApiOperation(value = "get ems-driver Info")
    public String executeCommand(@QueryParam("command") String command) {
	log.info("receiver command = " + command);
        return command;
    }

}
