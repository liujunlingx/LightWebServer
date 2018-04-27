package com.light.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

/**
 * Created on 2018/4/27.
 */
@Slf4j
public class PropertiesUtil {

    private static Properties properties;

    static {
        properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream("settings.properties"));
        } catch (IOException e) {
            log.error("无法读取配置文件settings.properties",e);
            System.exit(1);
        }
    }

    public static String getProperty(String name){
        return properties.getProperty(name);
    }

    public static String getProperty(String name,String defaultValue){
        return getProperty(name) == null ? defaultValue : getProperty(name);
    }
}
