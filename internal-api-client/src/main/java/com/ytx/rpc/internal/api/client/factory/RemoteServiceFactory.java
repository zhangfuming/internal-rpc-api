package com.ytx.rpc.internal.api.client.factory;


import com.ytx.rpc.internal.api.client.InnerApiClient;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangfuming on 2015/1/29 19:29.
 */
public class RemoteServiceFactory {

    private static volatile Map<String,Object> remoteServiceProxyMap = new ConcurrentHashMap<String,Object>();

    public static <T> T createRemoteService(Class<T> serverClass){
        try{
            String serviceName = serverClass.getName();
            if(!remoteServiceProxyMap.containsKey(serviceName)){
                synchronized (remoteServiceProxyMap){
                    if(!remoteServiceProxyMap.containsKey(serviceName)){
                        remoteServiceProxyMap.put(serviceName, Proxy.newProxyInstance(serverClass.getClassLoader(), new Class<?>[]{serverClass}, InnerApiClient.getInstance()));
                    }
                }
            }
            return (T) remoteServiceProxyMap.get(serviceName);
        }catch(Exception e){
            throw new RuntimeException("create remote service failed!",e);
        }
    }

}