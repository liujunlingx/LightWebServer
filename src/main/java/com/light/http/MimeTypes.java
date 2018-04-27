package com.light.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created on 2018/4/23.
 */
@Slf4j
public class MimeTypes {

    private static final HashMap<String,String> mimeMap = new HashMap<>();

    public static void main(String[] args) {
        System.out.println(ClassLoader.getSystemClassLoader().getResourceAsStream("mime.properties") == null);
    }

    static{
        try {
            Properties properties = new Properties();
            properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream("mime.properties"));
            properties.entrySet().forEach(entry->{
                Object key = entry.getKey();
                Object value = entry.getValue();
                mimeMap.put((String)key,(String)value);
            });
        } catch (IOException e) {
            log.error("Failed to load src/http/mime.properties");
        } catch (NullPointerException e){
            log.error("test");
        }
    }

    //返回response时，要标明response的Content-Type
    public static String getContentType(String path){
        int i = path.lastIndexOf(".");
        if(i == -1 || i == path.length() - 1) return "text/plain";
        String suffix = path.substring(i+1,path.length());
        return mimeMap.getOrDefault(suffix,"text/plain");
    }

}
