package com.ytx.rpc.internal.api.codec;

import com.ytx.rpc.internal.api.framework.bean.ErrorBean;
import com.ytx.rpc.internal.api.framework.util.EncodeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * msg -> <err>消息整体长度+消息</err>
 * Created by zhangfuming on 2015/1/30 19:41.
 */
public class ErrorMessageCodec extends ByteToMessageCodec<ErrorBean> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ErrorBean msg, ByteBuf out) throws Exception {
        byte[] start = EncodeUtil.toBytes(ErrorBean.PACKAGE_START);
        byte[] end = EncodeUtil.toBytes(ErrorBean.PACKAGE_END);
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
        if(in.readableBytes() < ErrorBean.PACKAGE_START_LEN){
            in.resetReaderIndex();
            return;
        }
        ByteBuf start = in.readBytes(ErrorBean.PACKAGE_START_LEN);
        if(!checkStart(start)){
            in.resetReaderIndex();
            in.retain();
            ctx.fireChannelRead(in);
            return;
        }
        int len = in.readInt();
        if(in.readableBytes() < len + ErrorBean.PACKAGE_END_LEN){
            in.resetReaderIndex();
            return;
        }
        ByteBuf message = in.readBytes(len);
        in.readBytes(ErrorBean.PACKAGE_END_LEN);
        out.add(EncodeUtil.toObject(message.array()));
    }

    private boolean checkStart(ByteBuf start){
        byte[] b = start.array();
        try {
            return StringUtils.equals(new String(b,"UTF-8"),ErrorBean.PACKAGE_START);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
    }



}
