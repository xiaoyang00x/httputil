package com.httputil.apitest.http.request;

import com.google.gson.Gson;
import com.httputil.apitest.http.annotation.*;
import com.httputil.apitest.http.model.HttpModel;
import com.httputil.apitest.util.BeanUtil;
import com.httputil.apitest.util.JSchemaAssertUtil;
import com.httputil.apitest.util.PropertyUtil;
import com.httputil.apitest.util.StringUtil;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangyu on 2019/3/20.
 */
public class HttpDriver extends RestAssured {
    static final Logger logger = LoggerFactory.getLogger("RestDriver.class");

    public HttpDriver() {
    }

    public static void addFilter() {
        RestAssured.replaceFiltersWith(new RequestLoggingFilter(), new Filter[]{new ResponseLoggingFilter()});
    }

    public static Response doGet(String path, Object paramObject) throws Exception {
        addFilter();
        if (paramObject == null) {
            return (Response) given().get(path, new Object[0]);
        } else {
            Map map = buildRequest(paramObject);
            Response res = (Response) given().params(map).get(path, new Object[0]);
            return res;
        }
    }

    public static Response doGetWithMap(String path, Map<String, ?> paramMap) throws Exception {
        addFilter();
        return paramMap == null ? (Response) given().get(path, new Object[0]) : (Response) given().params(paramMap).get(path, new Object[0]);
    }

    public static Response doPost(String path, Object paramObject) throws Exception {
        addFilter();
        if (paramObject == null) {
            return (Response) given().post(path, new Object[0]);
        } else {
            Map map = buildRequest(paramObject);
            return (Response) given().params(map).post(path, new Object[0]);
        }
    }

    public static Response doPost(Cookies cookies, String path, Object paramObject) throws Exception {
        addFilter();
        if (paramObject == null) {
            return (Response) given().post(path, new Object[0]);
        } else {
            Map map = buildRequest(paramObject);
            return (Response) given().cookies(cookies).params(map).post(path, new Object[0]);
        }
    }

    public static Response doPostWithMap(String path, Map<String, ?> paramMap) throws Exception {
        addFilter();
        return paramMap == null ? (Response) given().post(path, new Object[0]) : (Response) given().params(paramMap).post(path, new Object[0]);
    }

    public static Response doPut(String path, Object paramObject) throws Exception {
        addFilter();
        if (paramObject == null) {
            return (Response) given().put(path, new Object[0]);
        } else {
            Map map = buildRequest(paramObject);
            return (Response) given().params(map).put(path, new Object[0]);
        }
    }

    public static Response doPutWithMap(String path, Map<String, ?> paramMap) throws Exception {
        addFilter();
        return paramMap == null ? (Response) given().put(path, new Object[0]) : (Response) given().params(paramMap).put(path, new Object[0]);
    }

    public static Response doGet(Object paramObject) throws Exception {
        return doRequest((String) null, (String) null, Method.GET, (Cookies) null, paramObject);
    }

    public static Response doRequest(String hosts, String uris, Method httpMethod, Cookies cookies, Object paramObject) throws Exception {
        addFilter();
        Response io_response = null;
        HttpModel httpModel = readParamFromAnnotations(paramObject);
        Map requestmap = buildHttpRequest(paramObject);
        Map headers_map = (Map) requestmap.get("headers");
        Map cooikes_map = (Map) requestmap.get("cooikes");
        Map querys_map = (Map) requestmap.get("querys");
        Map params_map = (Map) requestmap.get("params");
        Map form_map = (Map) requestmap.get("formparams");
        Map uri_map = (Map) requestmap.get("uriparams");
        Map body_map = (Map) requestmap.get("body");
        String jsonbody = (String) body_map.get("jsonbody");
        String filebody_string = (String) body_map.get("filebody");
        File filebody = null;
        if (StringUtils.isNotEmpty(filebody_string)) {
            filebody = new File(filebody_string);
        }

        Map retry_map = (Map) requestmap.get("retry");
        String retry_key = "";
        String retry_value = "";
        String url = "";
        String host = "";
        String uri = "";
        boolean isJsonBody = httpModel.isJsonBody();
        int maxRetryTimes = 1;
        if (httpModel.getSchemeHost() == null) {
            host = PropertyUtil.getProperty("host");
        } else {
            host = httpModel.getSchemeHost();
        }

        if (httpModel.getUri() != null) {
            uri = httpModel.getUri();
        }

        String url1;
        while (uri.contains("{") && uri.contains("}")) {
            url1 = uri.substring(uri.lastIndexOf("{") + 1, uri.lastIndexOf("}"));
            uri = uri.replace("{" + url1 + "}", uri_map.get(url1).toString());
            uri_map.remove(url1);
        }

        if (httpModel.getMaxRetryTimes() != -1 && httpModel.getMaxRetryTimes() >= 1) {
            maxRetryTimes = httpModel.getMaxRetryTimes();
            if (retry_map != null) {
                if (retry_map.containsKey("retryKey")) {
                    retry_key = retry_map.get("retryKey").toString();
                }

                if (retry_map.containsKey("retryValue")) {
                    retry_value = retry_map.get("retryValue").toString();
                }
            }
        }

        if (httpModel.getHostProperty() != "") {
            url1 = httpModel.getHostProperty();
            host = PropertyUtil.getProperty(url1);
        }

        if (httpModel.getMethod() != null && httpMethod == null) {
            httpMethod = httpModel.getMethod();
        }

        if (hosts != "" && hosts != null) {
            host = hosts;
        }

        if (uris != "" && uris != null) {
            uri = uris;
        }

        url = host + uri;
        url1 = null;
        URL var32 = new URL(url);
        if (!headers_map.isEmpty()) {
            if (!headers_map.containsKey("Content-Type")) {
                headers_map.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            }

            if (!headers_map.containsKey("User-Agent")) {
                headers_map.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1)");
            }
        } else {
            headers_map.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            headers_map.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1)");
        }

        if (cookies != null) {
            Iterator filePath = cookies.iterator();

            while (filePath.hasNext()) {
                Cookie e = (Cookie) filePath.next();
                cooikes_map.put(e.getName(), e.getValue());
            }
        }

        for (int var33 = 1; var33 <= maxRetryTimes; ++var33) {
            String var35;
            try {
                if (isJsonBody) {
                    var35 = (new Gson()).toJson(params_map);
                    if (filebody != null) {
                        io_response = (Response) given().headers(headers_map).cookies(cooikes_map).formParams(form_map).queryParams(querys_map).body(var35).body(filebody).request(httpMethod, var32);
                    } else {
                        io_response = (Response) given().headers(headers_map).cookies(cooikes_map).formParams(form_map).queryParams(querys_map).body(var35).request(httpMethod, var32);
                    }
                } else if (jsonbody != null && filebody != null) {
                    io_response = (Response) given().headers(headers_map).cookies(cooikes_map).formParams(form_map).queryParams(querys_map).params(params_map).body(jsonbody).multiPart(filebody).request(httpMethod, var32);
                } else if (jsonbody != null && filebody == null) {
                    io_response = (Response) given().headers(headers_map).cookies(cooikes_map).formParams(form_map).queryParams(querys_map).params(params_map).body(jsonbody).request(httpMethod, var32);
                } else if (jsonbody == null && filebody != null) {
                    io_response = (Response) given().headers(headers_map).cookies(cooikes_map).formParams(form_map).queryParams(querys_map).params(params_map).multiPart(filebody).request(httpMethod, var32);
                } else {
                    io_response = (Response) given().headers(headers_map).cookies(cooikes_map).formParams(form_map).queryParams(querys_map).params(params_map).request(httpMethod, var32);
                }
            } catch (Exception var31) {
                logger.info("response返回异常,第 " + String.valueOf(var33) + " 次retry");
                TimeUnit.SECONDS.sleep(10L);
                continue;
            }

            if (io_response == null) {
                logger.info("response返回为null,第 " + String.valueOf(var33) + " 次retry");
                TimeUnit.SECONDS.sleep(10L);
            } else {
                if (retry_key == null || retry_key == "") {
                    return io_response;
                }

                var35 = io_response.jsonPath().get(retry_key).toString();
                if (var35.equals(retry_value)) {
                    return io_response;
                }

                String retry_msg = String.format("期望 %s 字段返回value:%s,实际返回:%s, 第 %s 次retry", new Object[]{retry_key, retry_value, var35, String.valueOf(var33)});
                logger.info(retry_msg);
                TimeUnit.SECONDS.sleep(10L);
            }
        }

        if (io_response == null) {
            throw new Exception("reponse返回Null");
        } else {
            String var34 = StringUtil.findFile(uri);
            if (!var34.equals("") && var34 != null) {
                try {
                    JSchemaAssertUtil.assertJsonSchema(io_response, var34);
                } catch (Exception var30) {
                    if (!var30.getMessage().equals("Schema to use cannot be null")) {
                        throw new Exception("reponse的jsonschema断言校验失败 %s ", var30);
                    }

                    logger.info("接口" + uri + "期望的jsonschema的模板文件不存在！");
                }

                return io_response;
            } else {
                return io_response;
            }
        }
    }

    public static Response doRequest(Object paramObject) throws Exception {
        return doRequest((String) null, (String) null, (Method) null, (Cookies) null, paramObject);
    }

    public static Response doPost(Object paramObject) throws Exception {
        return doRequest((String) null, (String) null, Method.POST, (Cookies) null, paramObject);
    }

    public static Response doPost(Cookies cookies, Object paramObject) throws Exception {
        return doRequest((String) null, (String) null, Method.POST, cookies, paramObject);
    }

    public static Response doGet(Cookies cookies, Object paramObject) throws Exception {
        return doRequest((String) null, (String) null, Method.GET, cookies, paramObject);
    }

    private static Map<String, Map> buildHttpRequest(Object model) throws Exception {
        HashMap request_map = new HashMap();
        Map map = BeanUtil.objectToMap(model);
        HashMap query_map = new HashMap();
        HashMap header_map = new HashMap();
        HashMap cooike_map = new HashMap();
        HashMap body_map = new HashMap();
        HashMap retry_map = new HashMap();
        HashMap uri_map = new HashMap();
        HashMap form_map = new HashMap();
        body_map.put("jsonbody", (Object) null);
        body_map.put("filebody", (Object) null);
        ArrayList fields = getAllParamModelFields(model.getClass());
        Iterator var11 = fields.iterator();

        while (true) {
            while (var11.hasNext()) {
                Field field = (Field) var11.next();
                Annotation[] fieldAs = field.getAnnotations();
                Annotation[] var14 = fieldAs;
                int var15 = fieldAs.length;

                for (int var16 = 0; var16 < var15; ++var16) {
                    Annotation a = var14[var16];
                    if (a instanceof HttpQuery) {
                        query_map.put(field.getName(), map.get(field.getName()));
                        map.remove(field.getName());
                        break;
                    }

                    if (a instanceof NonHttpParam) {
                        map.remove(field.getName());
                        break;
                    }

                    String origin_key;
                    String value;
                    Object var23;
                    if (a instanceof HttpHeader) {
                        header_map.put(field.getName(), map.get(field.getName()));
                        if (((HttpHeader) a).rmKeys()) {
                            map.remove(field.getName());
                        }

                        if (((HttpHeader) a).replace().matches("\\S+")) {
                            origin_key = field.getName();
                            value = ((HttpHeader) a).replace();
                            var23 = header_map.get(origin_key);
                            header_map.remove(origin_key);
                            header_map.put(value, var23);
                        }
                        break;
                    }

                    if (a instanceof HttpCooike) {
                        cooike_map.put(field.getName(), map.get(field.getName()));
                        if (((HttpCooike) a).rmKeys()) {
                            map.remove(field.getName());
                        }

                        if (((HttpCooike) a).replace().matches("\\S+")) {
                            origin_key = field.getName();
                            value = ((HttpCooike) a).replace();
                            var23 = cooike_map.get(origin_key);
                            cooike_map.remove(origin_key);
                            cooike_map.put(value, var23);
                        }
                        break;
                    }

                    if (a instanceof HttpParam) {
                        origin_key = field.getName();
                        value = ((HttpParam) a).value();
                        var23 = map.get(origin_key);
                        map.remove(origin_key);
                        map.put(value, var23);
                        break;
                    }

                    if (a instanceof JsonBody) {
                        origin_key = field.getName();
                        value = map.get(origin_key).toString();
                        body_map.put("jsonbody", value);
                        map.remove(origin_key);
                    } else if (a instanceof FileBody) {
                        origin_key = field.getName();
                        value = map.get(origin_key).toString();
                        File file = new File(value);
                        if (!file.isFile()) {
                            value = "";
                        }

                        body_map.put("filebody", value);
                        map.remove(origin_key);
                    } else if (a instanceof RetryKey) {
                        origin_key = field.getName();
                        value = map.get(origin_key).toString();
                        retry_map.put("retryKey", value);
                        map.remove(origin_key);
                    } else if (a instanceof RetryValue) {
                        origin_key = field.getName();
                        value = map.get(origin_key).toString();
                        retry_map.put("retryValue", value);
                        map.remove(origin_key);
                    } else {
                        Object var21;
                        if (a instanceof PathVariable) {
                            origin_key = field.getName();
                            var21 = map.get(origin_key);
                            uri_map.put(origin_key, var21);
                            map.remove(origin_key);
                        } else if (a instanceof HttpFormParam) {
                            origin_key = field.getName();
                            var21 = map.get(origin_key);
                            form_map.put(origin_key, var21);
                            map.remove(origin_key);
                        } else if (a instanceof HttpDynFormParam) {
                            origin_key = field.getName();
                            HashMap var22 = (HashMap) map.get(origin_key);
                            form_map.putAll(var22);
                            map.remove(origin_key);
                        }
                    }
                }
            }

            request_map.put("headers", header_map);
            request_map.put("querys", query_map);
            request_map.put("cooikes", cooike_map);
            request_map.put("params", map);
            request_map.put("body", body_map);
            request_map.put("retry", retry_map);
            request_map.put("uriparams", uri_map);
            request_map.put("formparams", form_map);
            return request_map;
        }
    }

    private static Map<String, ?> buildRequest(Object model) throws Exception {
        Map map = BeanUtil.objectToMap(model);
        ArrayList fields = getAllParamModelFields(model.getClass());
        Iterator var3 = fields.iterator();

        while (true) {
            while (var3.hasNext()) {
                Field field = (Field) var3.next();
                Annotation[] fieldAs = field.getAnnotations();
                Annotation[] var6 = fieldAs;
                int var7 = fieldAs.length;

                for (int var8 = 0; var8 < var7; ++var8) {
                    Annotation a = var6[var8];
                    if (a instanceof NonHttpParam) {
                        map.remove(field.getName());
                        break;
                    }
                }
            }

            return map;
        }
    }

    private static HttpModel readParamFromAnnotations(Object model) throws IllegalAccessException {
        HttpModel httpModel = new HttpModel();
        Annotation[] as = model.getClass().getAnnotations();
        if (as != null && as.length > 0) {
            Annotation[] var3 = as;
            int var4 = as.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Annotation a = var3[var5];
                if (a instanceof HttpID) {
                    httpModel.setHttpID(((HttpID) a).value());
                } else {
                    String hostproperty;
                    if (a instanceof HttpSchemeHost) {
                        hostproperty = handleAnnotation(model, ((HttpSchemeHost) a).value());
                        httpModel.setSchemeHost(hostproperty);
                    } else if (a instanceof HttpUri) {
                        hostproperty = handleAnnotation(model, ((HttpUri) a).value());
                        httpModel.setUri(hostproperty);
                    } else if (a instanceof HttpMethod) {
                        Method var8 = ((HttpMethod) a).value();
                        httpModel.setMethod(var8);
                    } else if (a instanceof HttpCharSet) {
                        hostproperty = ((HttpCharSet) a).value();
                        httpModel.setCharSet(hostproperty);
                    } else if (a instanceof HttpMaxRetry) {
                        int var9 = ((HttpMaxRetry) a).value();
                        httpModel.setMaxRetryTimes(var9);
                        httpModel.setRetryRequireKey(((HttpMaxRetry) a).retryKey());
                        httpModel.setRetryRequireValue(((HttpMaxRetry) a).retryVaule());
                    } else if (a instanceof HostProperty) {
                        hostproperty = ((HostProperty) a).value();
                        httpModel.setHostProperty(hostproperty);
                    } else if (a instanceof JsonBody) {
                        httpModel.setJsonBody(true);
                    }
                }
            }
        }

        return httpModel;
    }

    private static String handleAnnotation(Object model, String value) throws IllegalAccessException {
        HashMap paramFieldMap = null;
        if (value.indexOf(123) >= 0) {
            if (paramFieldMap == null) {
                paramFieldMap = new HashMap();
                ArrayList fields = getAllParamModelFields(model.getClass());
                if (fields != null) {
                    logger.info("handleAnnotation fields: " + fields.size());
                    Iterator entry = fields.iterator();

                    while (entry.hasNext()) {
                        Field object = (Field) entry.next();
                        HttpReplace anno = (HttpReplace) object.getAnnotation(HttpReplace.class);
                        if (anno != null) {
                            paramFieldMap.put(anno.value(), object);
                        }
                    }
                }
            }

            if (!paramFieldMap.isEmpty()) {
                Iterator fields1 = paramFieldMap.entrySet().iterator();

                while (fields1.hasNext()) {
                    Map.Entry entry1 = (Map.Entry) fields1.next();
                    Object object1 = ((Field) entry1.getValue()).get(model);
                    if (object1 != null) {
                        value = value.replace("{" + (String) entry1.getKey() + "}", object1.toString());
                    }
                }
            }

            logger.info("handleAnnotation value: " + value);
        }

        return value;
    }

    public static ArrayList<Field> getAllParamModelFields(Class<?> claxx) {
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

}
