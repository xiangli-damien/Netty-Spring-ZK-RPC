package com.xiangli.server.server;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 11:27
 */
public interface RpcServer {
    void start(int port);

    void stop();
}
