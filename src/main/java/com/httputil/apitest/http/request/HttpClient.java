package com.httputil.apitest.http.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient {

    private static Logger logger = LoggerFactory.getLogger(HttpClient.class);

    public static CloseableHttpResponse doHttpPost(String url, String params) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(params);
            post.setEntity(entity);
            response = httpClient.execute(post);
        } catch (Exception e) {
            logger.error("通讯异常，异常信息:[{}]", e);
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    public static CloseableHttpResponse doHttpPostJson(String url, Object obj) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost post = new HttpPost(url);
            String str = JSON.toJSONString(obj);
            StringEntity entity = new StringEntity(str,"UTF-8");
            entity.setContentEncoding("UTF-8");
            post.setHeader("Content-type","application/json");
            post.setEntity(entity);
            response = httpClient.execute(post);
        } catch (Exception e) {
            logger.error("通讯异常，异常信息:[{}]", e);
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

}
