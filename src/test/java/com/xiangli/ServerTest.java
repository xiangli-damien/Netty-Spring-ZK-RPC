package com.xiangli;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 13:35
 */
import com.xiangli.server.manager.ServiceManager;
import com.xiangli.server.server.impl.SimpleRpcServer;
import com.xiangli.server.RpcServer;
import com.xiangli.common.service.UserServiceImpl;

public class ServerTest {
    public static void main(String[] args) {
        // 创建服务管理器
        ServiceManager serviceManager = new ServiceManager();

        // 注册服务实现类
        serviceManager.registerServiceInterface(new UserServiceImpl());

        // 启动服务器
        RpcServer server = new SimpleRpcServer(serviceManager);  // 你也可以使用 ThreadPoolRpcServer
        server.start(9999); // 启动服务器，监听 9999 端口

        // 此时服务器会开始接受客户端的连接请求
        System.out.println("服务器启动成功，监听端口 9999");
    }
}