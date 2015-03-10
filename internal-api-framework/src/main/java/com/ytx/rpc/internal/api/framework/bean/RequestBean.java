package com.ytx.rpc.internal.api.framework.bean;

import java.io.Serializable;

/**
 * Created by zhangfuming on 2015/1/29 15:15.
 */
public class RequestBean implements Serializable{

    private static final long serialVersionUID = 5149232616374520001L;

    private String method;

    private Class<?>[] paramsClass;

    private Object[] params;

    @Override
    public String toString(){
        return new StringBuffer()
                .append("method=" + method)
                .append(",paramsClass=" + paramsClass.getClass())
                .append(",params=" + params)
                .toString();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Class<?>[] getParamsClass() {
        return paramsClass;
    }

    public void setParamsClass(Class<?>[] paramsClass) {
        this.paramsClass = paramsClass;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
