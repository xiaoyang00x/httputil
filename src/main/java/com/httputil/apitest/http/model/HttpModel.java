package com.httputil.apitest.http.model;

import io.restassured.http.Method;

/**
 * Created by yangyu on 2019/3/20.
 */
public class HttpModel {
    long HttpID;
    private String schemeHost;
    private String uri;
    private Method method;
    private String charSet;
    private int maxRetryTimes = -1;
    private String hostProperty = "";
    private String retryRequireKey = "";
    private String retryRequireValue = "";
    private boolean isJsonBody = false;

    public HttpModel() {
    }

    public boolean isJsonBody() {
        return this.isJsonBody;
    }

    public void setJsonBody(boolean jsonBody) {
        this.isJsonBody = jsonBody;
    }

    public String getSchemeHost() {
        return this.schemeHost;
    }

    public void setSchemeHost(String schemeHost) {
        this.schemeHost = schemeHost;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getCharSet() {
        return this.charSet;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    public int getMaxRetryTimes() {
        return this.maxRetryTimes;
    }

    public void setMaxRetryTimes(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    public long getHttpID() {
        return this.HttpID;
    }

    public void setHttpID(long httpID) {
        this.HttpID = httpID;
    }

    public String getHostProperty() {
        return this.hostProperty;
    }

    public void setHostProperty(String hostProperty) {
        this.hostProperty = hostProperty;
    }

    public String getRetryRequireKey() {
        return this.retryRequireKey;
    }

    public void setRetryRequireKey(String retryRequireKey) {
        this.retryRequireKey = retryRequireKey;
    }

    public String getRetryRequireValue() {
        return this.retryRequireValue;
    }

    public void setRetryRequireValue(String retryRequireValue) {
        this.retryRequireValue = retryRequireValue;
    }
}
