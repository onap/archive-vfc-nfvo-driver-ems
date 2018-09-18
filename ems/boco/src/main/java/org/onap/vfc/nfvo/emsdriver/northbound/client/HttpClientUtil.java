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

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


/*
 * HttpClient post request
 */
public class HttpClientUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);
    public static final String APPLICATION_JSON = "application/json";

    public static String doPost(String url, String json, String charset) {
        String result = null;
        try( 
            CloseableHttpClient httpClient = HttpClientFactory.getSSLClientFactory()){
            HttpPost httpPost = new HttpPost(url);
            if (null != json) {
                StringEntity s = new StringEntity(json);
                s.setContentEncoding("UTF-8");
                s.setContentType(APPLICATION_JSON); // set contentType
                httpPost.setEntity(s);
            }
            try(CloseableHttpResponse response = httpClient.execute(httpPost)){
                if (null != response) {
                    HttpEntity resEntity = response.getEntity();
                    if (null != resEntity) {
                        result = EntityUtils.toString(resEntity, charset);
                    }
                }
            } catch (Exception e) {
                log.error("httpClient.execute(httpPost) is fail", e);
            }
        } catch (Exception e) {
            log.error("doPost is fail ", e);
        } 
	return result;
    }

    public static String doDelete(String url, String charset) {
        String result = null;
        try (
            CloseableHttpClient httpClient = HttpClientFactory.getSSLClientFactory()){
            HttpDelete httpDelete = new HttpDelete(url);

            try(CloseableHttpResponse response = httpClient.execute(httpDelete)){
                if (null != response) {
                    HttpEntity resEntity = response.getEntity();
                    if (null != resEntity) {
                        result = EntityUtils.toString(resEntity, charset);
                    }
                }
            } catch (Exception e) {
                log.error("doDelete Exception: ", e);
            } 
        } catch (Exception e) {
            log.error("doDelete is fail ", e);
        } 
        return result;
    }

    public static String doGet(String url, String charset) {
        String result = null;
        try (
            CloseableHttpClient httpClient = HttpClients.createDefault()){
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Content-Type", APPLICATION_JSON);
            httpGet.setHeader("Accept", APPLICATION_JSON);
            httpGet.setHeader("X-TransactionId", "111");
            httpGet.setHeader("X-FromAppId", "ems-driver");
            Base64 token = new Base64();
            String authenticationEncoding = new String(token.encode(("AAI:AAI").getBytes()));

            httpGet.setHeader("Authorization", "Basic " + authenticationEncoding);
            log.info("1 doGet sucess url =" + url);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)){
                if (null != response) {
                    HttpEntity resEntity = response.getEntity();
                    if (null != resEntity) {
                        result = EntityUtils.toString(resEntity, charset);
                    }
                }
            } catch (Exception e) {
                log.error("doGet Exception: ", e);
            } 
        } catch (Exception e) {
            log.error("doGet is fail ", e);
        } 
        return result;
    }
}
