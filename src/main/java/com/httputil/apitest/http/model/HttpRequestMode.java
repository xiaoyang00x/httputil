package com.httputil.apitest.http.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Created by yangyu on 2019/3/20.
 */
public class HttpRequestMode {
    private String host;
    private String uri;
    private String method;
    private Map<String, ?> headers;
    private Map<String, ?> cookies;
    private Map<String, ?> pathParams;
    private Map<String, ?> params;
    private Map<String, ?> queryParams;
    private Map<String, ?> formParams;
    private String file;
    private Map body;
    private String userAgent;
    private Map retryConditions;
    private String contentType;

    public HttpRequestMode() {
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Map getBody() {
        return this.body;
    }

    public void setBody(Map body) {
        this.body = body;
    }

    public Map getRetryConditions() {
        return this.retryConditions;
    }

    public void setRetryConditions(Map retryConditions) {
        this.retryConditions = retryConditions;
    }

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map headers) {
        this.headers = headers;
    }

    public Map getCookies() {
        return this.cookies;
    }

    public void setCookies(Map cookies) {
        this.cookies = cookies;
    }

    public Map getPathParams() {
        return this.pathParams;
    }

    public void setPathParams(Map pathParams) {
        this.pathParams = pathParams;
    }

    public Map getParams() {
        return this.params;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public Map getQueryParams() {
        return this.queryParams;
    }

    public void setQueryParams(Map queryParams) {
        this.queryParams = queryParams;
    }

    public Map getFormParams() {
        return this.formParams;
    }

    public void setFormParams(Map formParams) {
        this.formParams = formParams;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String toJson() {
        try {
            return (new ObjectMapper()).writeValueAsString(this);
        } catch (JsonProcessingException var2) {
            return null;
        }
    }
}
