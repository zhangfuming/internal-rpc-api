package com.ytx.rpc.internal.api.client;

import java.lang.reflect.InvocationHandler;

/**
 * Created by zhangfuming on 2015/1/29 14:40.
 */
public interface IInnerApiClient extends InvocationHandler{

    public void beforeInvoke()throws Exception;

    public void afterInvoke()throws Exception;

}
