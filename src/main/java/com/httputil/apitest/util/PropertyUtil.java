package com.httputil.apitest.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by yangyu on 2019/3/20.
 */
public class PropertyUtil {
    private static Log log = LogFactory.getLog(PropertyUtil.class);
    public static final String DEFAULT_PROPERTY_FILE = "config.properties";
    private static final String ADD_PROPERTY_PREFIX = "add.property.file.";
    private static final String PROPERTY_EXTENSION = ".properties";
    private static TreeMap<String, String> props = new TreeMap();
    private static Set<String> files = new HashSet();

    public PropertyUtil() {
    }

    private static void load(String name) {
        StringBuilder key = new StringBuilder();
        Properties p = readPropertyFile(name);
        Iterator i = p.entrySet().iterator();

        while(i.hasNext()) {
            Map.Entry addfile = (Map.Entry)i.next();
            props.put((String)addfile.getKey(), (String)addfile.getValue());
        }

        if(p != null) {
            int var6 = 1;

            while(true) {
                key.setLength(0);
                key.append("add.property.file.");
                key.append(var6);
                String var7 = p.getProperty(key.toString());
                if(var7 == null) {
                    break;
                }

                String path = getPropertiesPath(name, var7);
                addPropertyFile(path);
                ++var6;
            }
        }

    }

    private static Properties readPropertyFile(String name) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        if(is == null) {
            is = PropertyUtil.class.getResourceAsStream("/" + name);
        }

        Properties p = new Properties();

        try {
            p.load(is);
            files.add(name);
        } catch (NullPointerException var12) {
            System.err.println("!!! PANIC: Cannot load " + name + " !!!");
            System.err.println(var12.getStackTrace());
        } catch (IOException var13) {
            System.err.println("!!! PANIC: Cannot load " + name + " !!!");
            System.err.println(var13.getStackTrace());
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
            } catch (IOException var11) {
                log.error("", var11);
            }

        }

        return p;
    }

    private static void overrideProperties() {
        Enumeration enumeration = Collections.enumeration(props.keySet());

        while(enumeration.hasMoreElements()) {
            String name = (String)enumeration.nextElement();
            String value = System.getProperty(name);
            if(value != null) {
                props.put(name, value);
            }
        }

    }

    public static void addPropertyFile(String name) {
        if(!name.endsWith(".properties")) {
            StringBuilder nameBuf = new StringBuilder();
            nameBuf.append(name);
            nameBuf.append(".properties");
            name = nameBuf.toString();
        }

        if(!files.contains(name)) {
            load(name);
        }

    }

    public static String getProperty(String key) {
        String result = (String)props.get(key);
        if(result != null && result.equals("@" + key)) {
            return result;
        } else if(result != null && result.startsWith("@@")) {
            return result.substring(1);
        } else {
            if(result != null && result.startsWith("@")) {
                result = getProperty(result.substring(1));
            }

            return result;
        }
    }

    public static String getProperty(String key, String defaultValue) {
        String result = (String)props.get(key);
        return result == null?defaultValue:result;
    }

    public static Enumeration<String> getPropertyNames() {
        return Collections.enumeration(props.keySet());
    }

    public static Enumeration<String> getPropertyNames(String keyPrefix) {
        SortedMap map = props.tailMap(keyPrefix);
        Iterator iter = map.keySet().iterator();

        String name;
        do {
            if(!iter.hasNext()) {
                return Collections.enumeration(map.keySet());
            }

            name = (String)iter.next();
        } while(name.startsWith(keyPrefix));

        return Collections.enumeration(props.subMap(keyPrefix, name).keySet());
    }

    public static Set<String> getPropertiesValues(String propertyName, String keyPrefix) {
        Properties localProps = loadProperties(propertyName);
        if(localProps == null) {
            return null;
        } else {
            Enumeration keyEnum = getPropertyNames(localProps, keyPrefix);
            return keyEnum == null?null:getPropertiesValues(localProps, keyEnum);
        }
    }

    public static Enumeration<String> getPropertyNames(Properties localProps, String keyPrefix) {
        if(localProps != null && keyPrefix != null) {
            ArrayList matchedNames = new ArrayList();
            Enumeration propNames = localProps.propertyNames();

            while(propNames.hasMoreElements()) {
                String name = (String)propNames.nextElement();
                if(name.startsWith(keyPrefix)) {
                    matchedNames.add(name);
                }
            }

            return Collections.enumeration(matchedNames);
        } else {
            return null;
        }
    }

    public static Set<String> getPropertiesValues(Properties localProps, Enumeration<String> propertyNames) {
        if(localProps != null && propertyNames != null) {
            HashSet retSet = new HashSet();

            while(propertyNames.hasMoreElements()) {
                retSet.add(localProps.getProperty((String)propertyNames.nextElement()));
            }

            return retSet;
        } else {
            return null;
        }
    }
    public static Properties loadProperties(String propertyName) {
        if(propertyName != null && !"".equals(propertyName)) {
            Properties retProps = new Properties();
            StringBuilder resourceName = new StringBuilder();
            resourceName.append(propertyName);
            resourceName.append(".properties");
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName.toString());
            if(is == null) {
                is = PropertyUtil.class.getResourceAsStream("/" + propertyName + ".properties");
            }

            try {
                retProps.load(is);
            } catch (NullPointerException var15) {
                log.warn("*** Can not find property-file [" + propertyName + ".properties] ***", var15);
                retProps = null;
            } catch (IOException var16) {
                log.error("", var16);
                retProps = null;
            } finally {
                try {
                    if(is != null) {
                        is.close();
                    }
                } catch (IOException var14) {
                    log.error("", var14);
                    retProps = null;
                }

            }

            return retProps;
        } else {
            return null;
        }
    }

    private static String getPropertiesPath(String resource, String addFile) {
        File file = new File(resource);
        String dir = file.getParent();
        StringBuilder retBuf;
        if(dir != null) {
            retBuf = new StringBuilder();
            retBuf.setLength(0);
            retBuf.append(dir);
            retBuf.append(File.separator);
            dir = retBuf.toString();
        } else {
            dir = "";
        }

        retBuf = new StringBuilder();
        retBuf.setLength(0);
        retBuf.append(dir);
        retBuf.append(addFile);
        return retBuf.toString();
    }

    static {
        StringBuilder key = new StringBuilder();
        load("config.properties");
        if(props != null) {
            int i = 1;

            while(true) {
                key.setLength(0);
                key.append("add.property.file.");
                key.append(i);
                String path = getProperty(key.toString());
                if(path == null) {
                    break;
                }

                addPropertyFile(path);
                ++i;
            }
        }

        overrideProperties();
    }
}
