package com.httputil.apitest.util;

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
}