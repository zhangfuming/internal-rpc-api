package com.ytx.rpc.internal.api.client.cache;


import com.ytx.rpc.internal.api.client.bean.ApiRequest;
import com.ytx.rpc.internal.api.framework.config.SoRpcProperties;
import com.ytx.rpc.internal.api.framework.constant.InternalConstant;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangfuming on 2015/1/30 13:14.
 */
public class RequestCache {

    private static final long WAIT_TIME_OUT_MILLISECONDS = SoRpcProperties.getLong(InternalConstant.SO_PRO.REQUEST_MAX_WAIT_TIME, 15000L);

    private static final long PERIOD = SoRpcProperties.getLong(InternalConstant.SO_PRO.CLEAN_THREAD_PERIOD,5000L);

    private Map<String,ApiRequest> cacheRequestMap = new ConcurrentHashMap<String, ApiRequest>();

    private static final RequestCache cache = new RequestCache();

    private AtomicBoolean started = new AtomicBoolean(false);

    private RequestCache(){}

    public static void put(String messageId,ApiRequest request){
        if(cache.started.compareAndSet(false,true)){
            new CacheCleaner().startCleaner(cache,PERIOD,WAIT_TIME_OUT_MILLISECONDS);
        }
        cache.cacheRequestMap.put(messageId,request);
    }

    public static ApiRequest get(String messageId){
        return cache.cacheRequestMap.get(messageId);
    }

    public static ApiRequest remove(String messageId){
       return cache.cacheRequestMap.remove(messageId);
    }
    public static ApiRequest remove(ApiRequest request){
       return cache.cacheRequestMap.remove(request.getMessageId());
    }

    static class CacheCleaner{

        private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1,new CacheCleanThreadFactory());

        public void startCleaner(final RequestCache _cache,final long period,final long waitTimeOutMilliseconds){
            executorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    long currentMarkTimes = new Date().getTime();
                    for(ApiRequest request : _cache.cacheRequestMap.values()){
                        if(currentMarkTimes - request.getRequestTime() > waitTimeOutMilliseconds){
                            if(request.isDone()){
                                RequestCache.remove(request); //cache clean
                            }else{
                                request.timeout();
                            }
                        }
                    }
                }
            },period,period, TimeUnit.MILLISECONDS);
        }
    }

    static class CacheCleanThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        CacheCleanThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null)? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "so-reqclean-pool-" + poolNumber.getAndIncrement() + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}
