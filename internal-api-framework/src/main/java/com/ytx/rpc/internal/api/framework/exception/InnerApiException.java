package com.ytx.rpc.internal.api.framework.exception;

/**
 * Created by zhangfuming on 2015/1/29 14:48.
 */
public class InnerApiException extends Exception {

    public InnerApiException(String msg){
        super(msg);
    }

    public InnerApiException(String msg, Throwable throwable){
        super(msg, throwable);
    }

}
