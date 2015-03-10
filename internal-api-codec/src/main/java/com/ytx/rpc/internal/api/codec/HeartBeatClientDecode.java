package com.ytx.rpc.internal.api.codec;

import com.ytx.rpc.internal.api.framework.constant.InternalConstant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by zhangfuming on 2015/2/9 16:12.
 */
public class HeartBeatClientDecode extends ByteToMessageDecoder {

    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(InternalConstant.HEARTBEAT_RESPONSE_DATA, CharsetUtil.UTF_8));

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
        int len = InternalConstant.HEARTBEAT_DATA.getBytes( CharsetUtil.UTF_8).length;
        in.markReaderIndex();
        if(in.readableBytes() < len){
            in.resetReaderIndex();
            return;
        }
        if(StringUtils.equals(InternalConstant.HEARTBEAT_DATA,new String(in.readBytes(len).array(),"UTF-8"))){
            ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            return;
        }else{
            in.resetReaderIndex();
            in.retain();
            ctx.fireChannelRead(in);
        }
    }

}
