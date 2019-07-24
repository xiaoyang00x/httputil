package com.httputil.apitest.http.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Created by yangyu on 2019/3/20.
 */
public class HttpResponse {
    private String url;
    private int code;
    private String body;
    private Map<String, String> headers;
    private Map<String, String> cookies;

    public HttpResponse() {
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getCookies() {
        return this.cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public String toJson() {
        try {
            return (new ObjectMapper()).writeValueAsString(this);
        } catch (JsonProcessingException var2) {
            return null;
        }
    }
}
