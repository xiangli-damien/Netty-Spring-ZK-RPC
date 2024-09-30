package com.xiangli.server.server.impl;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 11:25
 */

import com.xiangli.server.RpcServer;
import com.xiangli.server.manager.ServiceManager;
import com.xiangli.server.server.work.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 12:00
 */
public class SimpleRpcServer implements RpcServer {
    private final ServiceManager serviceManager;

    // 构造函数，接收服务管理器
    public SimpleRpcServer(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @Override
    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务器启动，正在监听端口：" + port);

            // 持续监听客户端连接
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("客户端已连接：" + socket.getInetAddress());

                // 创建一个新线程来处理客户端请求
                new Thread(new WorkThread(socket, serviceManager)).start();
            }
        } catch (IOException e) {
            System.err.println("服务器启动失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        // 停止服务器逻辑可以在这里实现
        System.out.println("服务器已停止");
    }
}
