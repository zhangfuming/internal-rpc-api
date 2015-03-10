package com.ytx.rpc.internal.api.framework.constant;

/**
 * Created by zhangfuming on 2015/2/9 16:07.
 */
public class InternalConstant {

    public static final String HEARTBEAT_DATA = "HB>";

    public static final String HEARTBEAT_RESPONSE_DATA = "<HB";

    public interface SO_PRO{
        String HOST = "so.rpc.host";
        String PORT = "so.rpc.port";
        String CONNECT_TIMEOUT = "so.rpc.connect.timeout";
        String CLIENT_THREADS = "so.rpc.client.threads";
        String SO_LINGER = "so.rpc.so_linger";
        String SERVER_THREADS = "so.rpc.server.threads";
        String CHANNEL_IDLE = "so.rpc.channel.idle";
        String REQUEST_MAX_WAIT_TIME = "so.rpc.request.max_wait_time";
        String CLEAN_THREAD_PERIOD = "so.rpc.period.between.every.clean_thread";
        String CLIENT_POOL_SIZE = "so.rpc.client.pool.size";
        String CLIENT_CONNECT_POOL_MAX_WAIT = "so.rpc.client.connect_pool_max_wait";
        String CLIENT_CONNECT_MINEVICTABLEIDLETIMEMILLIS = "so.rpc.minevictableidletimemillis";
    }

}
