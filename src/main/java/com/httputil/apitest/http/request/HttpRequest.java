package com.httputil.apitest.http.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.httputil.apitest.http.annotation.*;
import com.httputil.apitest.http.model.HttpRequestMode;
import com.httputil.apitest.http.model.HttpResponse;
import com.httputil.apitest.util.BeanUtil;
import com.httputil.apitest.util.MapUtil;
import com.httputil.apitest.util.PropertyUtil;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.replaceFiltersWith;


/**
 * Created by yangyu on 2019/3/20.
 */
public class HttpRequest {

    private static Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    public HttpRequest() {
    }

    public static Response doRequest(HttpRequestMode httpRequestMode) throws Exception {
        int retryCount = 0;
        String retryKey = "";
        Object retryNotEqual = null;
        boolean isSetContentType = false;
        String contextType = "application/x-www-form-urlencoded;charset=UTF-8";
        RequestSpecification requestSpecification = given().urlEncodingEnabled(false);
        if (StringUtils.isEmpty(httpRequestMode.getMethod())) {
            throw new AssertionError("请指定请求Method");
        } else {
            if (StringUtils.isNotEmpty(httpRequestMode.getHost())) {
                requestSpecification.baseUri(httpRequestMode.getHost());
            }

            if (StringUtils.isNotEmpty(httpRequestMode.getUri())) {
                requestSpecification.basePath(httpRequestMode.getUri());
            }

            if (MapUtils.isNotEmpty(httpRequestMode.getPathParams())) {
                requestSpecification.pathParams(httpRequestMode.getPathParams());
            }

            if (MapUtils.isNotEmpty(httpRequestMode.getHeaders())) {
                requestSpecification.headers(httpRequestMode.getHeaders());
                if (httpRequestMode.getHeaders().containsKey("Content-Type")) {
                    isSetContentType = true;
                }
            } else {
                HashMap response = new HashMap();
                requestSpecification.headers(response);
            }

            if (StringUtils.isNotEmpty(httpRequestMode.getUserAgent())) {
                requestSpecification.headers("User-Agent", httpRequestMode.getUserAgent(), new Object[0]);
            } else if (!isSetContentType) {
                requestSpecification.headers("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1)", new Object[0]);
            }

            Map var12;
            if (MapUtils.isNotEmpty(httpRequestMode.getParams())) {
                var12 = httpRequestMode.getParams();
                MapUtil.depthOne(var12);
                requestSpecification.params(var12);
            }

            if (MapUtils.isNotEmpty(httpRequestMode.getQueryParams())) {
                requestSpecification.queryParams(httpRequestMode.getQueryParams());
            }

            if (MapUtils.isNotEmpty(httpRequestMode.getFormParams())) {
                var12 = httpRequestMode.getFormParams();
                MapUtil.depthOne(var12);
                requestSpecification.formParams(var12);
            }

            if (StringUtils.isNotEmpty(httpRequestMode.getFile())) {
                requestSpecification.multiPart(new File(httpRequestMode.getFile()));
                contextType = "multipart/form-data";
            }

            if (MapUtils.isNotEmpty(httpRequestMode.getBody())) {
                requestSpecification.body(httpRequestMode.getBody());
                contextType = "application/json";
            }

            if (StringUtils.isNotEmpty(httpRequestMode.getContentType())) {
                requestSpecification.contentType(httpRequestMode.getContentType());
            } else if (!isSetContentType) {
                requestSpecification.contentType(contextType);
            }

            if (MapUtils.isNotEmpty(httpRequestMode.getCookies())) {
                requestSpecification.cookies(httpRequestMode.getCookies());
            }

            // 兼容在线咨询医生端特殊的batman-token字段
            if (MapUtils.isNotEmpty(httpRequestMode.getCookies())) {
                Map httpCookie = new HashMap(5);
                httpCookie.put("batman-token",httpRequestMode.getCookies().get("batman_token"));
                requestSpecification.cookies(httpCookie);
            }

            if (MapUtils.isNotEmpty(httpRequestMode.getRetryConditions())) {
                var12 = httpRequestMode.getRetryConditions();

                try {
                    retryCount = MapUtils.getIntValue(var12, "retryCount");
                } catch (NumberFormatException var10) {
                    logger.warn("retryCount格式错误:{}", Integer.valueOf(retryCount));
                }

                if (StringUtils.isNotEmpty(var12.get("retryKey").toString())) {
                    retryKey = var12.get("retryKey").toString();
                }

                try {
                    retryNotEqual = MapUtils.getObject(var12, "retryNotEqual");
                } catch (NumberFormatException var9) {
                    logger.warn("retryNotEqual:{}", String.valueOf(retryNotEqual));
                }
            }

            Response var13 = null;

            while (retryCount >= 0) {
                try {
                    if (!StringUtils.isNotEmpty(retryKey) || retryNotEqual == null) {
                        var13 = (Response) requestSpecification.request(Method.valueOf(httpRequestMode.getMethod().toUpperCase()));
                        break;
                    }

                    var13 = (Response) requestSpecification.request(Method.valueOf(httpRequestMode.getMethod().toUpperCase()));
                    if (var13 == null) {
                        logger.warn("返回response为null");
                        --retryCount;
                    } else {
                        String ep = var13.jsonPath().getString(retryKey);
                        if (ep.equals(String.valueOf(retryNotEqual))) {
                            break;
                        }

                        logger.warn("因返retryKey:{}对应value:{},与期望:{}不一致，将retry", new Object[]{retryKey, ep, String.valueOf(retryNotEqual)});
                        --retryCount;
                    }
                } catch (Exception var11) {
                    logger.error("request Error:{}", var11.getMessage());
                    --retryCount;
                }
            }

            return var13;
        }
    }

    private static ArrayList<Field> getAllParamModelFields(Class<?> claxx) {
        ArrayList fieldList;
        for (fieldList = new ArrayList(); claxx != null && claxx != Object.class; claxx = claxx.getSuperclass()) {
            Field[] fs = claxx.getDeclaredFields();

            for (int i = 0; i < fs.length; ++i) {
                Field f = fs[i];
                if (!f.isSynthetic()) {
                    fieldList.add(f);
                }
            }
        }

        return fieldList;
    }

    private static void buildRequestParams(Object model, HttpRequestMode httpRequestMode, boolean isJsonBody) throws Exception {
        Map map = BeanUtil.objectToMap(model);
        HashMap query_map = new HashMap();
        HashMap header_map = new HashMap();
        HashMap cooike_map = new HashMap();
        String filepath = "";
        Map retry_map = httpRequestMode.getRetryConditions();
        HashMap pathParams_map = new HashMap();
        HashMap form_map = new HashMap();
        String host_from_field = "";
        String content_type = "";
        ArrayList fields = getAllParamModelFields(model.getClass());
        Iterator uri = fields.iterator();

        while (true) {
            while (uri.hasNext()) {
                Field req_time = (Field) uri.next();
                Annotation[] value = req_time.getAnnotations();
                Annotation[] sign = value;
                int var19 = value.length;

                for (int var20 = 0; var20 < var19; ++var20) {
                    Annotation a = sign[var20];
                    if (a instanceof HttpQuery) {
                        query_map.put(req_time.getName(), map.get(req_time.getName()));
                        map.remove(req_time.getName());
                        break;
                    }

                    if (a instanceof NonHttpParam) {
                        map.remove(req_time.getName());
                        break;
                    }

                    String host_key;
                    String value1;
                    Object var31;
                    if (a instanceof HttpHeader) {
                        header_map.put(req_time.getName(), map.get(req_time.getName()));
                        if (((HttpHeader) a).rmKeys()) {
                            map.remove(req_time.getName());
                        }

                        host_key = ((HttpHeader) a).replace();
                        if (StringUtils.isNotEmpty(host_key)) {
                            value1 = req_time.getName();
                            var31 = header_map.get(value1);
                            header_map.remove(value1);
                            header_map.put(host_key, var31);
                        }
                        break;
                    }

                    if (a instanceof HttpCooike) {
                        cooike_map.put(req_time.getName(), map.get(req_time.getName()));
                        if (((HttpCooike) a).rmKeys()) {
                            map.remove(req_time.getName());
                        }

                        host_key = ((HttpCooike) a).replace();
                        if (StringUtils.isNotEmpty(host_key)) {
                            value1 = req_time.getName();
                            var31 = cooike_map.get(value1);
                            cooike_map.remove(value1);
                            cooike_map.put(host_key, var31);
                        }
                        break;
                    }

                    if (a instanceof HttpParam) {
                        host_key = req_time.getName();
                        value1 = ((HttpParam) a).value();
                        var31 = map.get(host_key);
                        map.remove(host_key);
                        map.put(value1, var31);
                        break;
                    }

                    if (a instanceof FileBody) {
                        host_key = req_time.getName();
                        value1 = map.get(host_key).toString();
                        filepath = HttpRequest.class.getResource(value1).getFile();
                        File file = new File(filepath);
                        if (!file.isFile()) {
                            filepath = "";
                        }

                        map.remove(host_key);
                    } else if (a instanceof RetryKey) {
                        host_key = req_time.getName();
                        value1 = map.get(host_key).toString();
                        retry_map.put("retryKey", value1);
                        map.remove(host_key);
                    } else if (a instanceof RetryValue) {
                        host_key = req_time.getName();
                        value1 = map.get(host_key).toString();
                        retry_map.put("retryNotEqual", value1);
                        map.remove(host_key);
                    } else {
                        Object var30;
                        if (a instanceof PathVariable) {
                            host_key = req_time.getName();
                            var30 = map.get(host_key);
                            pathParams_map.put(host_key, var30);
                            map.remove(host_key);
                        } else if (a instanceof HttpFormParam) {
                            host_key = req_time.getName();
                            var30 = map.get(host_key);
                            form_map.put(host_key, var30);
                            map.remove(host_key);
                        } else if (a instanceof HostValue) {
                            host_key = req_time.getName();
                            value1 = map.get(host_key).toString();
                            if (StringUtils.isNotEmpty(value1)) {
                                host_from_field = value1;
                            }

                            map.remove(host_key);
                        } else if (a instanceof ContentType) {
                            host_key = req_time.getName();
                            value1 = map.get(host_key).toString();
                            if (StringUtils.isNotEmpty(value1)) {
                                content_type = value1;
                            }

                            map.remove(host_key);
                        }
                    }
                }
            }

            if (StringUtils.isNotEmpty(host_from_field)) {
                httpRequestMode.setHost(host_from_field);
            }

            httpRequestMode.setRetryConditions(retry_map);
            httpRequestMode.setQueryParams(query_map);
            httpRequestMode.setHeaders(header_map);
            httpRequestMode.setCookies(cooike_map);
            httpRequestMode.setPathParams(pathParams_map);
            httpRequestMode.setFile(filepath);
            httpRequestMode.setFormParams(form_map);
            httpRequestMode.setContentType(content_type);
            if (isJsonBody) {
                httpRequestMode.setBody(map);
            } else {
                httpRequestMode.setParams(map);
            }

            return;
        }
    }

    public static HttpRequestMode updateRequestFromJsonFile(File src_json) throws Exception {
        HttpRequestMode httpRequestMode = (HttpRequestMode) (new ObjectMapper()).readValue(src_json, HttpRequestMode.class);
        return httpRequestMode;
    }

    public static HttpRequestMode updateRequestFromModel(Object model) throws Exception {
        HttpRequestMode httpRequestMode = new HttpRequestMode();
        String updateUri = PropertyUtil.getProperty("host");
        boolean isJsonBody = false;
        Annotation[] as = model.getClass().getAnnotations();
        if (as != null && as.length > 0) {
            Annotation[] var6 = as;
            int var7 = as.length;

            for (int var8 = 0; var8 < var7; ++var8) {
                Annotation a = var6[var8];
                if (a instanceof HttpSchemeHost) {
                    updateUri = ((HttpSchemeHost) a).value();
                } else {
                    String contentType;
                    if (a instanceof HttpUri) {
                        contentType = ((HttpUri) a).value();
                        httpRequestMode.setUri(contentType);
                    } else if (a instanceof HttpMethod) {
                        Method var12 = ((HttpMethod) a).value();
                        httpRequestMode.setMethod(var12.toString());
                    } else if (a instanceof HttpMaxRetry) {
                        int var13 = ((HttpMaxRetry) a).value();
                        Object retryConditions = httpRequestMode.getRetryConditions();
                        if (retryConditions == null) {
                            retryConditions = new HashMap();
                        }

                        ((Map) retryConditions).put("retryCount", Integer.valueOf(var13));
                        httpRequestMode.setRetryConditions((Map) retryConditions);
                    } else if (a instanceof HostProperty) {
                        contentType = ((HostProperty) a).value();
                        updateUri = PropertyUtil.getProperty(contentType);
                    } else if (a instanceof JsonBody) {
                        isJsonBody = true;
                    } else if (a instanceof ContentType) {
                        contentType = ((ContentType) a).value();
                        httpRequestMode.setContentType(contentType);
                    }
                }
            }
        }

        if (StringUtils.isNotEmpty(updateUri)) {
            httpRequestMode.setHost(updateUri);
        }

        buildRequestParams(model, httpRequestMode, isJsonBody);
        return httpRequestMode;
    }

    public static Response doRequest(Object obj) throws Exception {
        HttpRequestMode httpRequestMode = updateRequestFromModel(obj);
        return doRequest(httpRequestMode);
    }

    public static Response doRequest(String json) throws Exception {
        HttpRequestMode httpRequestMode = (HttpRequestMode) (new ObjectMapper()).readValue(json, HttpRequestMode.class);
        return doRequest(httpRequestMode);
    }

    public static HttpResponse responseTrans(Response response) {
        if (response == null) {
            return null;
        } else {
            HttpResponse httpResponseMode = new HttpResponse();
            httpResponseMode.setCode(response.getStatusCode());
            httpResponseMode.setBody(response.getBody().asString());
            Headers headers = response.getHeaders();
            HashMap headersmap = new HashMap();
            Iterator cookies = headers.iterator();

            while (cookies.hasNext()) {
                Header header = (Header) cookies.next();
                headersmap.put(header.getName(), header.getValue());
            }

            httpResponseMode.setHeaders(headersmap);
            Map cookies1 = response.getCookies();
            httpResponseMode.setCookies(cookies1);
            return httpResponseMode;
        }
    }

    public static HttpResponse handleRequest(HttpRequestMode httpRequestMode) throws Exception {
        Response response = doRequest(httpRequestMode);
        return responseTrans(response);
    }

    public static HttpResponse handleRequest(String json) throws Exception {
        HttpRequestMode httpRequestMode = (HttpRequestMode) (new ObjectMapper()).readValue(json, HttpRequestMode.class);
        Response response = doRequest(httpRequestMode);
        return responseTrans(response);
    }

    public static Response doPost(Object obj) throws Exception {
        HttpRequestMode httpRequestMode = updateRequestFromModel(obj);
        httpRequestMode.setMethod("POST");
        return doRequest(httpRequestMode);
    }

    public static Response doGet(Object obj) throws Exception {
        HttpRequestMode httpRequestMode = updateRequestFromModel(obj);
        httpRequestMode.setMethod("GET");
        return doRequest(httpRequestMode);
    }

    static {
        replaceFiltersWith(new RequestLoggingFilter(), new Filter[]{new ResponseLoggingFilter()});
    }
}
