package com.ytx.rpc.internal.api.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by zhangfuming on 2015/2/10 14:21.
 */
public class SoRpcProperties {

    private static final Properties properties = new Properties();

    private SoRpcProperties(){
        throw new RuntimeException("SoRpcProperties can not instance!");
    }

    static{
        InputStream in = SoRpcProperties.class.getResourceAsStream("/default_so_rpc.properties");
        if(null != in){
            try {
                properties.load(in);
            } catch (IOException e) { }
            finally {
                try {
                    in.close();
                } catch (IOException e) { }
            }
        }
        in = SoRpcProperties.class.getResourceAsStream("/so_rpc.properties");
        if(null != in){
            try {
                properties.load(in);
            } catch (IOException e) { }
            finally {
                try {
                    in.close();
                } catch (IOException e) { }
            }
        }
    }

    public static String getString(String key){
        return properties.getProperty(key);
    }

    public static String getString(String key,String defaultValue){
        String val = properties.getProperty(key);
        if(null == val) return defaultValue;
        return val;
    }

    public static Integer getInt(String key) {
        String s = SoRpcProperties.getString(key);
        if(null == s){
            return null;
        }
        return Integer.parseInt(s);
    }

    public static Long getLong(String key) {
        String s = SoRpcProperties.getString(key);
        if(null == s){
            return null;
        }
        return Long.parseLong(s);
    }

    public static Integer getInt(String key,Integer defaultValue) {
        Integer val = SoRpcProperties.getInt(key);
        if(null == val) return defaultValue;
        return val;
    }

    public static Long getLong(String key,Long defaultValue) {
        Long val = SoRpcProperties.getLong(key);
        if(null == val) return defaultValue;
        return val;
    }

}
