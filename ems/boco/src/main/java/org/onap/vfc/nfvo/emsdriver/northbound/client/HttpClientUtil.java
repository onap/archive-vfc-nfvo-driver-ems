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
package org.onap.vfc.nfvo.emsdriver.northbound.client;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.apache.commons.codec.binary.Base64;

/*
 * HttpClient post request
 */
public class HttpClientUtil {
	
	private static Log log = LogFactory.getLog(HttpClientUtil.class);
	
    public static String doPost(String url,String json,String charset){
    	CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = HttpClientFactory.getSSLClientFactory();
            httpPost = new HttpPost(url);
            if (null != json) {
				StringEntity s = new StringEntity(json);
				s.setContentEncoding("UTF-8");
				s.setContentType("application/json"); // set contentType
				httpPost.setEntity(s);
			}
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {
				if(response != null){
				    HttpEntity resEntity = response.getEntity();
				    if(resEntity != null){
				        result = EntityUtils.toString(resEntity,charset);
				    }
				}
			} catch (Exception e) {
				log.error("httpClient.execute(httpPost) is fail",e);
			}finally{
				if(response != null){
					response.close();
				}
			}
        }catch(Exception e){
        	log.error("doPost is fail ",e);
        }finally{
        	if(httpClient != null){
        		try {
					httpClient.close();
				} catch (IOException e) {
				}
        	}
        	
		}
        return result;
    }
    
    public static String doDelete(String url ,String charset){
    	CloseableHttpClient httpClient = null;
        HttpDelete httpDelete = null;
        String result = null;
        try{
            httpClient = HttpClientFactory.getSSLClientFactory();
            httpDelete = new HttpDelete(url);
            
            CloseableHttpResponse response = httpClient.execute(httpDelete);
            
            try {
				if(response != null){
				    HttpEntity resEntity = response.getEntity();
				    if(resEntity != null){
				        result = EntityUtils.toString(resEntity,charset);
				    }
				}
			} catch (Exception e) {
				log.error("",e);
			}finally{
				if(response != null){
					response.close();
				}
			}
        }catch(Exception e){
        	log.error("doDelete is fail ",e);
        }finally{
        	if(httpClient != null){
        		try {
					httpClient.close();
				} catch (IOException e) {
				}
			}
        }
        return result;
    }
    
    public static String doGet(String url, String charset){
    	CloseableHttpClient httpClient = null;
        HttpGet httpGet = null;
        String result = null;
        try{
            httpClient = HttpClients.createDefault();
            httpGet = new HttpGet(url);
            Base64 token = new Base64();
            String authenticationEncoding = new String(token.encode(("AAI:AAI").getBytes()));

            httpGet.setHeader("Authorization", "Basic " + authenticationEncoding);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            log.info("doGet sucess url ="+url);
            try {
				if(response != null){
				    HttpEntity resEntity = response.getEntity();
				    if(resEntity != null){
				        result = EntityUtils.toString(resEntity,charset);
				    }
				}
			} catch (Exception e) {
				log.error("",e);
			}finally{
				if(response != null){
					response.close();
				}
			}
        }catch(Exception e){
        	log.error("doGet is fail ",e);
        }finally{
        	if(httpClient != null){
        		try {
					httpClient.close();
				} catch (IOException e) {
				}
			}
        }
        return result;
    }
    
    public static CloseableHttpClient createCloseableHttpClientWithBasicAuth(){  
    	  // 创建HttpClientBuilder  
    	HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
        // 设置BasicAuth  
        CredentialsProvider provider = new BasicCredentialsProvider();  
        // Create the authentication scope  
        AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);  
        // Create credential pair，username and password
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("AAI", "AAI");  
        // Inject the credentials  
        provider.setCredentials(scope, credentials);  
        // Set the default credentials provider  
        httpClientBuilder.setDefaultCredentialsProvider(provider);  
        // HttpClient  
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build(); 
        
        return closeableHttpClient;
    }  
    
}