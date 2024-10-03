package com.xiangli;

import com.xiangli.common.service.UserService;
import com.xiangli.common.service.UserServiceImpl;
import com.xiangli.server.provider.ServiceProvider;
import com.xiangli.server.server.impl.NettyRpcServer;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/02 09:50
 */
public class NettyServerTest {
    public static void main(String[] args) {
        // 创建服务提供者，并注册服务
        ServiceProvider serviceProvider = new ServiceProvider();
        UserService userService = new UserServiceImpl();
        serviceProvider.registerService(userService);

        // 启动 Netty 服务端
        NettyRpcServer server = new NettyRpcServer(serviceProvider);
        server.start(8080);  // 监听 8080 端口
    }
}
