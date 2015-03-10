package com.internal.test.dummy.beans;

import java.io.Serializable;

/**
 * Created by zhangfuming on 2015/2/9 11:14.
 */
public class AdBean implements Serializable {
    private static final long serialVersionUID = 5743668822431739389L;

    private String adNo;

    private String message;

    private String url;

    public String getAdNo() {
        return adNo;
    }

    public void setAdNo(String adNo) {
        this.adNo = adNo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
