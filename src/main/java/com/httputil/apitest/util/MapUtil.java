package com.httputil.apitest.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by yangyu on 2019/3/21.
 */
public class MapUtil {

    public MapUtil() {
    }

    public static void depthOne(Map map) {
        Iterator it = map.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            if(entry.getValue() instanceof Map) {
                try {
                    String ex = (new ObjectMapper()).writeValueAsString(entry.getValue());
                    map.put(entry.getKey(), ex);
                } catch (Exception var4) {
                    var4.printStackTrace();
                }
            }
        }

    }
}
