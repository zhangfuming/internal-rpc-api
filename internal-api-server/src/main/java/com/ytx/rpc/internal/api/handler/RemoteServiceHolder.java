package com.ytx.rpc.internal.api.handler;

import com.ytx.rpc.internal.api.framework.annotation.RemoteService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangfuming on 2015/2/9 10:40.
 */
public class RemoteServiceHolder {

    private static volatile Map<String,Object> remoteServices = new ConcurrentHashMap<String, Object>();

    private RemoteServiceHolder(){
        throw new RuntimeException("RemoteServiceHolder can not instance!");
    }

    public static void register(String name,Object service){
        remoteServices.put(name,service);
    }

    public static <T> T get(String name,Class<T> clazz){
        return (T) remoteServices.get(name);
    }

    public static Object get(String name){
        return remoteServices.get(name);
    }

}