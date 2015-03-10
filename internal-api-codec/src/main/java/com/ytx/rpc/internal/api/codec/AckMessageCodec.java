package com.ytx.rpc.internal.api.codec;


import com.ytx.rpc.internal.api.framework.bean.AckBean;
import com.ytx.rpc.internal.api.framework.util.EncodeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * msg -> <err>消息整体长度+消息</err>
 * Created by zhangfuming on 2015/1/30 19:11.
 */
public class AckMessageCodec extends ByteToMessageCodec<AckBean> {

    @Override
    protected void encode(ChannelHandlerContext ctx, AckBean msg, ByteBuf out) throws Exception {
        byte[] start = EncodeUtil.toBytes(AckBean.PACKAGE_START);
        byte[] end = EncodeUtil.toBytes(AckBean.PACKAGE_END);
        byte[] message = EncodeUtil.toBytes(msg);
        out.alloc().buffer(start.length + 4 + message.length + end.length);
        out.writeBytes(start);
        out.writeInt(message.length);
        out.writeBytes(message);
        out.writeBytes(end);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        in.markReaderIndex();
        if(in.readableBytes() < AckBean.PACKAGE_START_LEN){
            in.resetReaderIndex();
            return;
        }
        ByteBuf start = in.readBytes(AckBean.PACKAGE_START_LEN);
        if(!checkStart(start)){
            in.resetReaderIndex();
            in.retain();
            ctx.fireChannelRead(in);
            return;
        }
        int len = in.readInt();
        if(in.readableBytes() < len + AckBean.PACKAGE_END_LEN){
            in.resetReaderIndex();
            return;
        }
        ByteBuf message = in.readBytes(len);
        in.readBytes(AckBean.PACKAGE_END_LEN);
        out.add(EncodeUtil.toObject(message.array()));
    }

    private boolean checkStart(ByteBuf start){
        byte[] b = start.array();
        try {
            return StringUtils.equals(new String(b,"UTF-8"),AckBean.PACKAGE_START);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }



}
