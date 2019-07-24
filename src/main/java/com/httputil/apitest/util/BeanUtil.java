package com.httputil.apitest.util;

import com.google.gson.Gson;
import com.httputil.apitest.core.ApiTestListener;
import com.httputil.apitest.http.annotation.JsonBody;
import com.httputil.apitest.http.annotation.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created by yangyu on 2019/3/20.
 */
public class BeanUtil {
    private static final Logger log = LoggerFactory.getLogger(ApiTestListener.class);
    private static final String OMIT_REG = "_";

    public BeanUtil() {
    }

    public static <E> List<E> toBeanList(Class<E> cla, List<Map<String, Object>> mapList) {
        ArrayList list = new ArrayList(mapList.size());
        Iterator var3 = mapList.iterator();

        while(var3.hasNext()) {
            Map map = (Map)var3.next();
            Object obj = toBean(cla, map);
            list.add(obj);
        }

        return list;
    }

    public static <E> E toBean(Class<E> cla, Map<String, Object> map) {
        Object obj = null;

        try {
            obj = cla.newInstance();
            if(obj == null) {
                throw new Exception();
            }
        } catch (Exception var14) {
            log.error("类型实例化对象失败,类型:" + cla);
            return null;
        }

        HashMap newmap = new HashMap();
        Iterator ms = map.entrySet().iterator();

        while(ms.hasNext()) {
            Entry en = (Entry)ms.next();
            newmap.put("set" + ((String)en.getKey()).trim().replaceAll("_", "").toLowerCase(), en.getValue());
        }

        Method[] var15 = cla.getMethods();
        Method[] var16 = var15;
        int var6 = var15.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Method method = var16[var7];
            String mname = method.getName().toLowerCase();
            if(mname.startsWith("set")) {
                Class[] clas = method.getParameterTypes();
                String v = null;
                if(newmap.get(mname) != null) {
                    v = newmap.get(mname).toString();
                }

                if(v != null && clas.length == 1) {
                    try {
                        method.invoke(obj, new Object[]{v});
                    } catch (Exception var13) {
                        log.error("属性值装入失败,装入方法：" + cla + "." + method.getName() + ".可装入类型" + clas[0] + ";欲装入值的类型:" + v.getClass());
                    }
                }
            }
        }

        return (E) obj;
    }


    public static <E> E toBeanStrict(Class<E> cla, Map<String, Object> map) {
        Object obj = null;

        try {
            obj = cla.newInstance();
            if(obj == null) {
                throw new Exception();
            }
        } catch (Exception var14) {
            log.error("类型实例化对象失败,类型:" + cla);
            return null;
        }

        HashMap newmap = new HashMap();
        Iterator ms = map.entrySet().iterator();

        while(ms.hasNext()) {
            Entry en = (Entry)ms.next();
            newmap.put("set" + ((String)en.getKey()).trim().replaceAll("_", "").toLowerCase(), en.getValue());
        }

        Method[] var15 = cla.getMethods();
        Method[] var16 = var15;
        int var6 = var15.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Method method = var16[var7];
            String mname = method.getName().toLowerCase();
            if(mname.startsWith("set")) {
                Class[] clas = method.getParameterTypes();
                Object v = newmap.get(mname);
                if(v != null && clas.length == 1) {
                    try {
                        method.invoke(obj, new Object[]{v});
                    } catch (Exception var13) {
                        log.error("属性值装入失败,装入方法：" + cla + "." + method.getName() + ".可装入类型" + clas[0] + ";欲装入值的类型:" + v.getClass());
                    }
                }
            }
        }

        return (E) obj;
    }

    public static Field[] getDeclaredField(Object object) {
        Field[] field = null;
        Class clazz = object.getClass();

        while(clazz != Object.class) {
            try {
                field = clazz.getDeclaredFields();
                return field;
            } catch (Exception var4) {
                clazz = clazz.getSuperclass();
            }
        }

        return null;
    }

    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        if(obj == null) {
            return null;
        } else {
            Class clazz = obj.getClass();
            HashMap map = new HashMap();

            for(Gson gson = new Gson(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                try {
                    Field[] declaredFields = clazz.getDeclaredFields();
                    Field[] var5 = declaredFields;
                    int var6 = declaredFields.length;

                    for(int var7 = 0; var7 < var6; ++var7) {
                        Field field = var5[var7];
                        field.setAccessible(true);
                        String json = null;
                        String key = null;
                        Annotation[] as = field.getAnnotations();
                        if(as != null && as.length > 0) {
                            Annotation[] var12 = as;
                            int var13 = as.length;

                            for(int var14 = 0; var14 < var13; ++var14) {
                                Annotation a = var12[var14];
                                if(a instanceof JsonValue) {
                                    json = gson.toJson(field.get(obj));
                                    key = ((JsonValue)a).key();
                                    break;
                                }

                                if(a instanceof JsonBody) {
                                    json = gson.toJson(field.get(obj));
                                    key = ((JsonBody)a).key();
                                    break;
                                }
                            }
                        }

                        if(json == null) {
                            map.put(field.getName(), field.get(obj));
                        } else {
                            if(key == null || key.equals("")) {
                                key = field.getName();
                            }

                            map.put(key, json);
                        }
                    }
                } catch (Exception var16) {
                    ;
                }
            }

            return map;
        }
    }
}
