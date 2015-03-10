package com.ytx.rpc.internal.api.framework.bean;

import com.ytx.rpc.internal.api.framework.util.EncodeUtil;

import java.io.Serializable;

/**
 * Created by zhangfuming on 2015/1/29 15:33.
 */
public class AckBean implements Serializable{

    private static final long serialVersionUID = -7590705205451982996L;

    public static final String PACKAGE_START = "ACK>";

    public static final int PACKAGE_START_LEN = EncodeUtil.toBytes(PACKAGE_START).length;

    public static final String PACKAGE_END = "/ACK";

    public static final int PACKAGE_END_LEN =  EncodeUtil.toBytes(PACKAGE_END).length;

    private String messageId;

    private Object message;

    @Override
    public String toString(){
        return new StringBuffer()
                .append("messageId="+messageId)
                .append(",message="+message)
                .toString();
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
