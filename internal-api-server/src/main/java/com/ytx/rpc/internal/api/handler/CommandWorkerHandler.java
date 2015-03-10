package com.ytx.rpc.internal.api.handler;

import com.ytx.rpc.internal.api.framework.annotation.RemoteService;
import com.ytx.rpc.internal.api.framework.bean.AckBean;
import com.ytx.rpc.internal.api.framework.bean.CommandBean;
import com.ytx.rpc.internal.api.framework.bean.ErrorBean;
import com.ytx.rpc.internal.api.framework.bean.RequestBean;
import com.ytx.rpc.internal.api.framework.exception.InnerApiException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangfuming on 2015/1/30 16:40.
 */
@ChannelHandler.Sharable
public class CommandWorkerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandWorkerHandler.class);

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        CommandBean commandBean = (CommandBean)msg;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(commandBean.getMessage());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        RequestBean requestBean = (RequestBean)objectInputStream.readObject();

        Object service = RemoteServiceHolder.get(commandBean.getServiceName());
        Method action = service.getClass().getMethod(requestBean.getMethod(), requestBean.getParamsClass());//仅仅获取public方法
        try{
            Object result = action.invoke(service,requestBean.getParams());
            AckBean ackBean = new AckBean();
            ackBean.setMessageId(commandBean.getMessageId());
            ackBean.setMessage(result);
            ctx.channel().writeAndFlush(ackBean);
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("send ack message {}",ackBean);
            }
        }catch(Exception e){
            LOGGER.error("Inner server error",e);
            ErrorBean errorBean = new ErrorBean();
            errorBean.setMessageId(commandBean.getMessageId());
            errorBean.setException(new InnerApiException("Inner server error",e));
            ctx.channel().writeAndFlush(errorBean);
        }
    }

}