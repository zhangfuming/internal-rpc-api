package com.ytx.rpc.internal.api;

import com.ytx.rpc.internal.api.codec.AckMessageCodec;
import com.ytx.rpc.internal.api.codec.CommandMessageCodec;
import com.ytx.rpc.internal.api.codec.ErrorMessageCodec;
import com.ytx.rpc.internal.api.codec.HeartBeatServerDecode;
import com.ytx.rpc.internal.api.framework.config.SoRpcProperties;
import com.ytx.rpc.internal.api.framework.constant.InternalConstant;
import com.ytx.rpc.internal.api.handler.CommandWorkerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhangfuming on 2015/1/30 10:35.
 */
public class InnerApiServer implements IInnerApiServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InnerApiServer.class);

    private static int processorNums = SoRpcProperties.getInt(InternalConstant.SO_PRO.SERVER_THREADS, Runtime.getRuntime().availableProcessors());

    private static final ChannelGroup group = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    private static final IInnerApiServer innerApiServer = new InnerApiServer();

    private static String host = SoRpcProperties.getString(InternalConstant.SO_PRO.HOST);

    private static int port = SoRpcProperties.getInt(InternalConstant.SO_PRO.PORT);

    private InnerApiServer(){
    }

    public static IInnerApiServer getInstance(String host,int port){
        InnerApiServer.host = host;
        InnerApiServer.port = port;
        return innerApiServer;
    }

    public static IInnerApiServer getInstance(){
        return innerApiServer;
    }

    public void start(){
        init();
    }

    private void init() {
        final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        final EventLoopGroup workerGroup = new NioEventLoopGroup(processorNums);
        ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializerImpl());
        server.option(ChannelOption.SO_LINGER, SoRpcProperties.getInt(InternalConstant.SO_PRO.SO_LINGER, 200));
        ChannelFuture f = server.bind(host,port).syncUninterruptibly();
        final Channel channel = f.channel();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (channel != null)
                    channel.close();
                group.close();
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
        f.channel().closeFuture().syncUninterruptibly();
    }

    private static final ChannelRegister channelRegister = new ChannelRegister();
    private static final HeartbeatHandler heartbeatHandler = new HeartbeatHandler();

    static final class ChannelInitializerImpl extends ChannelInitializer<Channel> {
        private static final LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        @Override
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(channelRegister);
            pipeline.addLast(new HeartBeatServerDecode());
            pipeline.addLast(new CommandMessageCodec(),new CommandWorkerHandler());
            pipeline.addLast(loggingHandler);
            pipeline.addLast(new IdleStateHandler(0, 0, SoRpcProperties.getLong(InternalConstant.SO_PRO.CHANNEL_IDLE,3000L), TimeUnit.MILLISECONDS), heartbeatHandler);
            pipeline.addLast(new AckMessageCodec());
            pipeline.addLast(new ErrorMessageCodec());
        }
    }


    @ChannelHandler.Sharable
    static final class ChannelRegister extends ChannelHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            group.add(ctx.channel());
            super.channelActive(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            ctx.channel().disconnect().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        LOGGER.info(future.channel() + " client leave");
                        group.remove(future.channel());
                        future.channel().close();
                    }
                }
            });
            ctx.close();
        }
    }

    @ChannelHandler.Sharable
    static final class HeartbeatHandler extends ChannelHandlerAdapter {

        private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(InternalConstant.HEARTBEAT_DATA, CharsetUtil.UTF_8));

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }

}
