package com.ytx.rpc.internal.api.client;

import com.ytx.rpc.internal.api.codec.*;
import com.ytx.rpc.internal.api.framework.config.SoRpcProperties;
import com.ytx.rpc.internal.api.framework.constant.InternalConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import org.apache.commons.pool.BasePoolableObjectFactory;

import java.net.InetSocketAddress;

/**
 * Created by zhangfuming on 2015/1/29 13:58.
 */
public class ClientConnectionFactory extends BasePoolableObjectFactory<Channel> {

    static final AttributeKey<String> id = AttributeKey.valueOf("__ID__");

    private static final String DEFAULT_SERVER_HOST = SoRpcProperties.getString(InternalConstant.SO_PRO.HOST);

    private static final int DEFAULT_SERVER_PORT =  SoRpcProperties.getInt(InternalConstant.SO_PRO.PORT);

    private ChannelFuture createConnection(String host,int port,String clientId){
        Bootstrap client = new Bootstrap();
        NioEventLoopGroup clientGroup = new NioEventLoopGroup(SoRpcProperties.getInt(InternalConstant.SO_PRO.CLIENT_THREADS,Runtime.getRuntime().availableProcessors() * 2));
        client.group(clientGroup).channel(NioSocketChannel.class).handler(new ChannelInitializerImpl());
        client.option(ChannelOption.SO_KEEPALIVE, true).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, SoRpcProperties.getInt(InternalConstant.SO_PRO.CONNECT_TIMEOUT,5000));
        client.attr(id,clientId);
        return client.connect(new InetSocketAddress(host, port));
    }

    private ChannelFuture createConnection(String host,int port){
        return createConnection(host,port,"default-client-id");
    }

    private ChannelFuture createConnection(){
        return createConnection(DEFAULT_SERVER_HOST,DEFAULT_SERVER_PORT);
    }

    @Override
    public Channel makeObject() throws Exception {
        ChannelFuture future = this.createConnection().awaitUninterruptibly();
        return future.channel();
    }

    @Override
    public boolean validateObject(Channel obj) {
        return obj.isActive();
    }

    @Override
    public void destroyObject(Channel obj) throws Exception {
        obj.close().syncUninterruptibly();
    }

    static final class ChannelInitializerImpl extends ChannelInitializer<Channel> {
        private static final LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);
        @Override
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new HeartBeatClientDecode());
            pipeline.addLast(new ErrorMessageCodec());
            pipeline.addLast(new AckMessageCodec());
            pipeline.addLast(InnerApiProcessorImpl.getInstance());
            pipeline.addLast(loggingHandler);
            pipeline.addLast(new CommandMessageCodec());
        }
    }


}
