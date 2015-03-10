package com.ytx.rpc.internal.api.spring;

import com.ytx.rpc.internal.api.framework.annotation.RemoteService;
import com.ytx.rpc.internal.api.handler.RemoteServiceHolder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.rmi.Remote;
import java.util.Map;

/**
 * Created by zhangfuming on 2015/2/3 15:40.
 */
@Service
public class RegisterRpcCommandService implements ApplicationContextAware, ApplicationListener {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RegisterRpcCommandService.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof ContextRefreshedEvent){
            Map<String,Object> services = applicationContext.getBeansWithAnnotation(Service.class);
            if(null != services){
                for(Object service : services.values()){
                    Class<?> serviceClass = service.getClass();
                    Class<?>[] interfaces = serviceClass.getInterfaces();
                    for(Class<?> c : interfaces){
                        if(c.isAnnotationPresent(RemoteService.class)){
                            RemoteService remote = c.getAnnotation(RemoteService.class);
                            RemoteServiceHolder.register(remote.value(),service);
                        }
                    }
                }
            }
        }
    }
}
