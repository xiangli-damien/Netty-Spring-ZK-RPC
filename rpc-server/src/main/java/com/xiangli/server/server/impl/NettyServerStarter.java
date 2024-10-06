package com.xiangli.server.server.impl;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/02 20:55
 */
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
// 继承 ApplicationListener 接口，监听 Spring 容器加载完毕的事件
@Component
@Slf4j
public class NettyServerStarter implements ApplicationListener<ContextRefreshedEvent> {

    private final NettyRpcServer nettyRpcServer;

    public NettyServerStarter(NettyRpcServer nettyRpcServer) {
        this.nettyRpcServer = nettyRpcServer;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 在 Spring 容器加载完毕后，启动 Netty 服务器
        if (!nettyRpcServer.isRunning()) {
            log.info("Server: Starting Netty server");
            nettyRpcServer.start(8181);  // 启动 Netty 服务器，监听 8181 端口
            log.info("Server: Netty server listening on port 8181");
        } else {
            log.warn("Netty server is already running");
        }
    }
}
