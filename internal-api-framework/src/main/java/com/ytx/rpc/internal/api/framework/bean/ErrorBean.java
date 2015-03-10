package com.ytx.rpc.internal.api.framework.bean;

import com.ytx.rpc.internal.api.framework.exception.InnerApiException;
import com.ytx.rpc.internal.api.framework.util.EncodeUtil;

import java.io.Serializable;

/**
 * Created by zhangfuming on 2015/1/29 15:33.
 */
public class ErrorBean implements Serializable{

    private static final long serialVersionUID = 8425854719085855416L;

    public static final String PACKAGE_START = "ERR>";

    public static final int PACKAGE_START_LEN = EncodeUtil.toBytes(PACKAGE_START).length;

    public static final String PACKAGE_END = "/ERR";

    public static final int PACKAGE_END_LEN =  EncodeUtil.toBytes(PACKAGE_END).length;

    private String messageId;

    private InnerApiException exception;

    @Override
    public String toString(){
        return new StringBuffer()
                .append("messageId=" + messageId)
                .append(",exception="+exception.getMessage())
                .toString();
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public InnerApiException getException() {
        return exception;
    }

    public void setException(InnerApiException exception) {
        this.exception = exception;
    }
}
