package com.ytx.rpc.internal.api.client;

import com.ytx.rpc.internal.api.client.bean.ApiRequest;
import io.netty.channel.Channel;

import java.lang.reflect.Method;

/**
 * Created by zhangfuming on 2015/1/29 15:35.
 */
public interface IInnerApiProcessor {

    ApiRequest execute(Channel channel, Object obj, Method method, Object[] args) throws Exception;

}
