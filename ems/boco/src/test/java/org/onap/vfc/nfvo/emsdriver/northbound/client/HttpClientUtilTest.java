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
package org.onap.vfc.nfvo.emsdriver.northbound.client;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.junit.MockServerRule;
import org.mockserver.model.Header;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;

public class HttpClientUtilTest {

	@Rule
	public MockServerRule server = new MockServerRule(this, 10086);

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testDoGet() {
		MockServerClient mockClient = new MockServerClient("localhost", 10086);
		String expected = "{ message: 'incorrect username and password combination' }";
		mockClient.when(request().withPath("/hello/John").withMethod("GET")
		// .withHeader(new Header(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN))
		// .withQueryStringParameter(new Parameter("my-token", "12345"))
		).respond(response().withStatusCode(200).withBody(expected));

		String responseText = HttpClientUtil.doGet("http://localhost:10086/hello/John", "utf-8");
		assertThat(responseText, equalTo(expected));

	}

	@Test
	public void testDoPost() {
		MockServerClient mockClient = new MockServerClient("localhost", 10086);
		String expected = "You have logged in successfully.";
		mockClient
				.when(request().withPath("/hello/John").withMethod("POST")
						//.withHeader(new Header(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN))
						// .withQueryStringParameter(new Parameter("my-token", "12345"))
						.withBody("username=foo&password=123456"))
				.respond(response().withStatusCode(200).withBody(expected));
		
		String responseText = HttpClientUtil.doPost("http://localhost:10086/hello/John", "username=foo&password=123456", "utf-8");
		//"{\"username\":\"foo\",\"password\":\"123456\"}"
		// 验证输出是否是正确
		assertThat(responseText, equalTo(expected));

	}
	
	@Test
	public void testDoDelete() {
		MockServerClient mockClient = new MockServerClient("localhost", 10086);
		String expected = "{ message: 'incorrect username and password combination' }";
		mockClient.when(request().withPath("/hello/John").withMethod("DELETE")
		// .withHeader(new Header(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN))
		// .withQueryStringParameter(new Parameter("my-token", "12345"))
		).respond(response().withStatusCode(200).withBody(expected));

		String responseText = HttpClientUtil.doDelete("http://localhost:10086/hello/John", "utf-8");
		assertThat(responseText, equalTo(expected));

	}

}
