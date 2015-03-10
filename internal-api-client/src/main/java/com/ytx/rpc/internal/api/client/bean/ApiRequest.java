package com.ytx.rpc.internal.api.client.bean;

import com.ytx.rpc.internal.api.client.cache.RequestCache;
import com.ytx.rpc.internal.api.framework.exception.InnerApiException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangfuming on 2015/1/29 14:58.
 */
public class ApiRequest {

    private String messageId;

    private String sessionId;

    private String clientId;

    private Class<?> returnClass;

    private AtomicBoolean finished = new AtomicBoolean(false);

    private Object result;

    private InnerApiException exception;

    private long requestTime;

    private final Object lock = new Object();

    public void finish(Object obj){
        if(finished.compareAndSet(false,true)){
            result = obj;
            synchronized (lock){
                lock.notify();
            }
        }
    }

    public Object getResult()throws InnerApiException{
        synchronized (lock){
            try {
                lock.wait();
                if(isDone()) RequestCache.remove(this); //delete from cache
            } catch (InterruptedException e) {
            }
        }
        if(null != exception){
            throw exception;
        }
        return result;
    }

    public void error(InnerApiException exception){
        if(finished.compareAndSet(false,true)){
            this.exception = exception;
            synchronized (lock){
                lock.notify();
            }
        }
    }

    public void timeout(){
        if(finished.compareAndSet(false,true)){
            this.exception = new InnerApiException("Wait server response timeout");
            synchronized (lock){
                lock.notify();
            }
        }
    }

    public boolean isDone(){
        return finished.get();
    }

    @Override
    public String toString(){
        return new StringBuffer()
                .append("messageId=" + messageId)
                .append(",sessionId=" + sessionId)
                .append(",clientId=" + clientId)
                .append(",returnClass=" + returnClass)
                .append(",finished=" + finished.get())
                .append(",result=" + result)
                .append(",exception=" + exception)
                .append(",requestTime=" + requestTime)
                .toString();
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Class<?> getReturnClass() {
        return returnClass;
    }

    public void setReturnClass(Class<?> returnClass) {
        this.returnClass = returnClass;
    }

    public AtomicBoolean getFinished() {
        return finished;
    }

    public void setFinished(AtomicBoolean finished) {
        this.finished = finished;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public InnerApiException getException() {
        return exception;
    }

    public void setException(InnerApiException exception) {
        this.exception = exception;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }
}
