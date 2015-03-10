package com.ytx.rpc.internal.api.codec;

import com.ytx.rpc.internal.api.framework.constant.InternalConstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangfuming on 2015/2/9 16:12.
 */
public class HeartBeatServerDecode extends ByteToMessageDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatServerDecode.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
        int len = InternalConstant.HEARTBEAT_RESPONSE_DATA.getBytes(CharsetUtil.UTF_8).length;
        in.markReaderIndex();
        if(in.readableBytes() < len){
            in.resetReaderIndex();
            return;
        }
        if(StringUtils.equals(InternalConstant.HEARTBEAT_RESPONSE_DATA,new String(in.readBytes(len).array(),CharsetUtil.UTF_8))){
            LOGGER.debug("client live-> %s",ctx.channel().remoteAddress());
            return;
        }else{
            in.resetReaderIndex();
            in.retain();
            ctx.fireChannelRead(in);
        }
    }

}
