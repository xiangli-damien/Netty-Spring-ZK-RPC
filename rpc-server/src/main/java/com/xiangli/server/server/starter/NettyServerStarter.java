package com.xiangli.server.server.starter;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/02 20:55
 */
import com.xiangli.server.server.impl.NettyRpcServer;
import com.xiangli.server.server.portUtil.PortUtil;
import com.xiangli.server.serviceregister.impl.ZKServiceRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

// 继承 ApplicationListener 接口，监听 Spring 容器加载完毕的事件
@Component
@Slf4j
public class NettyServerStarter implements ApplicationListener<ContextRefreshedEvent> {

    private final NettyRpcServer nettyRpcServer;
    private final ZKServiceRegister zkServiceRegister;

    public NettyServerStarter(NettyRpcServer nettyRpcServer , ZKServiceRegister zkServiceRegister) {
        this.nettyRpcServer = nettyRpcServer;
        this.zkServiceRegister = zkServiceRegister;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 在 Spring 容器加载完毕后，启动 Netty 服务器
        for (int i = 0; i < 3; i++) {
            int port = PortUtil.findAvailablePort();  // 查找一个可用的端口
            if (!nettyRpcServer.isRunning()) {
                log.info("Server: Starting Netty server on port " + port);
                nettyRpcServer.start(port);  // 启动 Netty 服务器，监听指定端口
                log.info("Server: Netty server listening on port " + port);
                zkServiceRegister.register("com.xiangli.common.service.UserService", new InetSocketAddress("localhost", port));
            } else {
                log.warn("Netty server is already running");
            }
        }



    }
}
