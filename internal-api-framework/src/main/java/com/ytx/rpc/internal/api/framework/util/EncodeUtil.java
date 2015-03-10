package com.ytx.rpc.internal.api.framework.util;

import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.CharSet;
import org.apache.commons.lang3.CharSetUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;

/**
 * Created by zhangfuming on 2015/1/30 18:34.
 */
public class EncodeUtil {

    private static final Charset DEFAULT_CHARSET = CharsetUtil.UTF_8;

    public static byte[] toBytes(String msg){
        return toBytes(msg,DEFAULT_CHARSET);
    }

    public static byte[] toBytes(String msg,Charset charset){
        return msg.getBytes(charset);
    }

    public static byte[] toBytes(Object ob){
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(ob);
            return byteArrayOutputStream.toByteArray();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static Object toObject(byte[] in){
        try{
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(in);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return objectInputStream.readObject();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
