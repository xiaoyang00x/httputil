package com.httputil.apitest.util;

import com.alibaba.fastjson.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by yangyu on 2019/3/20.
 */
public class JsonUtil {
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    public JsonUtil() {
    }

    public static JSONObject getJson(InputStream inputStream) {
        StringBuffer stringBuffer = new StringBuffer();
        String s = null;

        try {
            BufferedReader e = new BufferedReader(new InputStreamReader(inputStream));

            while((s = e.readLine()) != null) {
                stringBuffer.append(s);
            }

            return new JSONObject(stringBuffer.toString());
        } catch (FileNotFoundException var4) {
            log.info("目标jsonschema文件不存在{}", var4);
            return null;
        } catch (JSONException var5) {
            var5.printStackTrace();
            return null;
        } catch (IOException var6) {
            var6.printStackTrace();
            return null;
        }
    }

    public static String getJson(String filepath) {
        StringBuffer stringBuffer = new StringBuffer();
        String s = null;

        try {
            BufferedReader e = new BufferedReader(new FileReader(new File(filepath)));

            while((s = e.readLine()) != null) {
                stringBuffer.append(s);
            }

            return stringBuffer.toString();
        } catch (FileNotFoundException var4) {
            var4.printStackTrace();
            return null;
        } catch (IOException var5) {
            var5.printStackTrace();
            return null;
        }
    }

    /**
     * 获取json串某一字段的值
     *
     * @param response 接口返回的json值转换后
     * @param keys     调用示例 String str = JsonUtils.getJsonValueRec(string,"data.departments.subDepartments.name",0);
     * @param index    需要取列表中第几个的值，一般传0就可以
     * @return 返回String类型的结果值
     */
    public static String getJsonValueRec(String response, String keys, int index) {
        String[] key = keys.split("\\.");
        if (key.length == 1) {
            if (response.startsWith("[")) {
                return parseJsonArray(response, index, key[0]);
            }
            if (response.startsWith("{")) {
                return parseJsonObject(response, key[0]);
            }
        } else if (key.length > 1) {
            if (response.startsWith("[")) {
                response = parseJsonArray(response, index, key[0]);
                keys = keys.substring(key[0].length() + 1);
                return getJsonValueRec(response, keys, index);
            }
            if (response.startsWith("{")) {
                response = parseJsonObject(response, key[0]);
                keys = keys.substring(key[0].length() + 1);
                return getJsonValueRec(response, keys, index);
            }
        }
        return "No corresponding value found";
    }

    private static String parseJsonArray(String response, int index, String key) {
        JSONArray resultArray = com.alibaba.fastjson.JSONObject.parseArray(response);
        return resultArray.getJSONObject(index).getString(key);
    }

    private static String parseJsonObject(String response, String key) {
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(response);
        return jsonObject.get(key).toString();
    }
}