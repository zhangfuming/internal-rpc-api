package com.ytx.rpc.internal.api.client;

import com.ytx.rpc.internal.api.client.bean.ApiRequest;
import com.ytx.rpc.internal.api.client.cache.RequestCache;
import com.ytx.rpc.internal.api.framework.annotation.RemoteService;
import com.ytx.rpc.internal.api.framework.bean.*;
import com.ytx.rpc.internal.api.framework.exception.InnerApiException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zhangfuming on 2015/1/29 15:37.
 */
@ChannelHandler.Sharable
public class InnerApiProcessorImpl extends MessageToMessageDecoder<Object> implements IInnerApiProcessor{

    private AtomicLong msgIdSeq = new AtomicLong(0);

    public static final Long MAX_MSG_ID = Long.MAX_VALUE - 100;

    private static final InnerApiProcessorImpl apiProcessor = new InnerApiProcessorImpl();

    private InnerApiProcessorImpl(){}

    public static InnerApiProcessorImpl getInstance(){
        return apiProcessor;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if(msg instanceof AckBean){
            final AckBean ackBean = (AckBean)msg;
            String messageId = ackBean.getMessageId();
            final ApiRequest apiRequest = RequestCache.get(messageId);
            apiRequest.finish(ackBean.getMessage());
        }else if(msg instanceof ErrorBean){
            ErrorBean errorBean = (ErrorBean)msg;
            String messageId = errorBean.getMessageId();
            ApiRequest apiRequest = RequestCache.get(messageId);
            apiRequest.error(errorBean.getException());
        }
        ctx.fireChannelRead(msg);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();//TODO
    }

    @Override
    public ApiRequest execute(Channel channel, Object obj, Method method, Object[] args) throws Exception {
        String serverName = this.getServiceName(obj);
        Class<?> returnClass = method.getReturnType();
        RequestBean requestBean = this.packageRequest(method.getName(),args,method.getParameterTypes());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(requestBean);

        ApiRequest apiRequest = new ApiRequest();
        final String messageId = getNextMessageId().toString();
        apiRequest.setMessageId(messageId);
        apiRequest.setSessionId(channel.id().asLongText());
        apiRequest.setReturnClass(returnClass);
        apiRequest.setRequestTime(new Date().getTime());

        RequestCache.put(messageId, apiRequest);

        CommandBean cmd =new CommandBean();
        cmd.setMessage(byteArrayOutputStream.toByteArray());
        cmd.setMessageId(messageId);
        cmd.setServiceName(serverName);

        channel.writeAndFlush(cmd);

        return apiRequest;
    }

    protected String getServiceName(Object service) throws Exception {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for (Class<?> inter : interfaces) {
            if (inter.isAnnotationPresent(RemoteService.class)) {
                return inter.getAnnotation(RemoteService.class).value();
            }
        }
        throw new InnerApiException(service.getClass().getName() + "is not annotation present for RemoteService");
    }

    private RequestBean packageRequest(String method, Object[] params, Class<?>[] paramsClass) throws Exception {
        RequestBean request = new RequestBean();
        request.setMethod(method);
        request.setParams(params);
        request.setParamsClass(paramsClass);
        return request;
    }

    private Long getNextMessageId() {
        long msgId = msgIdSeq.get();
        if (msgId > MAX_MSG_ID) {
            msgIdSeq.compareAndSet(msgId, 0);
        }
        return msgIdSeq.incrementAndGet();
    }

}
