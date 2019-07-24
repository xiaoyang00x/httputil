package com.httputil.apitest.util;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by yangyu on 2019/3/20.
 */
public class JSchemaAssertUtil {
    private static final Logger log = LoggerFactory.getLogger(JSchemaAssertUtil.class);

    public JSchemaAssertUtil() {
    }

    public void assertJsonResult(String response, String filepath) {
        try {
            InputStream e = this.getClass().getResourceAsStream(filepath);
            System.out.println("当前类所在包路径下获取资源文件：" + this.getClass().getResourceAsStream(filepath));
            JSONObject target = JsonUtil.getJson(e);
            JSONObject actualResp = new JSONObject(response);
            JSONAssert.assertEquals(target, actualResp, true);
            log.info("当前Json校验的源数据：{}", actualResp);
        } catch (JSONException var6) {
            log.error("返回结果Json schema校验异常：{}", var6);
        }

    }

    public static void assertJsonSchema(String json, String filepath) {
        MatcherAssert.assertThat(json, JsonSchemaValidator.matchesJsonSchemaInClasspath(filepath));
    }

    public static void assertJsonSchema(Response response, String filepath) {
        MatcherAssert.assertThat(response.asString(), JsonSchemaValidator.matchesJsonSchemaInClasspath(filepath));
    }
}
