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
package org.openo.nfvo.emsdriver.northbound.client;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

/**
 * HttpClient
  */
public class HttpClientFactory{
	
	
	public static CloseableHttpClient getSSLClientFactory() throws Exception {
         
		SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
             //信任所有
             public boolean isTrusted(X509Certificate[] chain,
                             String authType) throws CertificateException {
                 return true;
             }
         }).build();
         SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
         CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
         
         return httpclient;
	}
	
}
