package com.ytx.rpc.internal.api.client;

import com.ytx.rpc.internal.api.client.bean.ApiRequest;
import com.ytx.rpc.internal.api.framework.config.SoRpcProperties;
import com.ytx.rpc.internal.api.framework.constant.InternalConstant;
import com.ytx.rpc.internal.api.framework.exception.InnerApiException;
import io.netty.channel.Channel;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by zhangfuming on 2015/1/29 14:31.
 */
public class InnerApiClient implements IInnerApiClient{

    private static final Logger LOGGER = LoggerFactory.getLogger(InnerApiClient.class);

    private int CONNECTIONS = SoRpcProperties.getInt(InternalConstant.SO_PRO.CLIENT_POOL_SIZE,Runtime.getRuntime().availableProcessors());

    private long CONNECT_POOL_MAX_WAIT = SoRpcProperties.getLong(InternalConstant.SO_PRO.CLIENT_CONNECT_POOL_MAX_WAIT,-1L);

    private long Minevictableidletimemillis = SoRpcProperties.getLong(InternalConstant.SO_PRO.CLIENT_CONNECT_MINEVICTABLEIDLETIMEMILLIS,5000L);

    private ThreadLocal<Channel> channels = new ThreadLocal<Channel>();

    private PoolableObjectFactory<Channel> sessionFactory = new ClientConnectionFactory();

    private GenericObjectPool<Channel> pool;

    private IInnerApiProcessor apiProcessor;

    private static final InnerApiClient apiClient = new InnerApiClient();

    private InnerApiClient(){
        this.apiProcessor = InnerApiProcessorImpl.getInstance();
        init();
    }

    public static InnerApiClient getInstance(){
        return apiClient;
    }

    void init() {
        pool = new GenericObjectPool<Channel>(sessionFactory, CONNECTIONS);
        pool.setTestOnBorrow(true);
        pool.setTestOnReturn(true);
        pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
        pool.setMaxIdle(CONNECTIONS);
        pool.setMaxWait(CONNECT_POOL_MAX_WAIT);
        pool.setMinEvictableIdleTimeMillis(Minevictableidletimemillis);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InnerApiException {
        Channel channel = channels.get();
        if(null != channel && channel.isActive()){
            return transactInvoke(channel,proxy,method,args);
        }else {
            return normalInvoke(proxy, method, args);
        }
    }

    private Object normalInvoke(Object obj,Method method,Object[] args)throws InnerApiException{
        Channel channel = null;
        try{
            channel = pool.borrowObject();
            ApiRequest request = apiProcessor.execute(channel,obj,method,args);
            return request.getResult();
        }catch(Exception e){
            LOGGER.error("normal invoke error" ,e);
            throw new InnerApiException("Connect Error",e);
        }finally {
            if(null != channel){
                try{
                    pool.returnObject(channel);
                }catch (Exception e){}
            }
        }
    }

    private Object transactInvoke(Channel channel,Object obj,Method method,Object[] args)throws InnerApiException{
        try{
            ApiRequest request = apiProcessor.execute(channel,obj,method,args);
            return request.getResult();
        }catch(Exception e){
            throw new InnerApiException("Connect Error",e);
        }
    }

    @Override
    public void beforeInvoke() throws Exception {
        Channel channel = channels.get();
        if(null != channel){
            return ;
        }
        channels.set(pool.borrowObject());
    }

    @Override
    public void afterInvoke() throws Exception {
        Channel channel = channels.get();
        if(null != channel){
            pool.returnObject(channel);
        }
        channels.remove();
    }
}
