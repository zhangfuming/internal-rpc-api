package com.ytx.rpc.internal.api.framework.bean;

import com.ytx.rpc.internal.api.framework.util.EncodeUtil;

import java.io.Serializable;

/**
 * Created by zhangfuming on 2015/1/29 15:08.
 */
public class CommandBean implements Serializable{

    private static final long serialVersionUID = -4817574993026924172L;

    public static final String PACKAGE_START = "CMD>";

    public static final int PACKAGE_START_LEN = EncodeUtil.toBytes(PACKAGE_START).length;

    public static final String PACKAGE_END = "/CMD";

    public static final int PACKAGE_END_LEN =  EncodeUtil.toBytes(PACKAGE_END).length;

    private byte[] message;

    private String messageId;

    private String serviceName;

    @Override
    public String toString(){
        return new StringBuffer()
                .append("messageId=" + messageId)
                .append(",message=" + message)
                .append(",serviceName=" + serviceName)
                .toString();
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
