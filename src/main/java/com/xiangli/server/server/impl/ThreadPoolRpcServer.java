package com.xiangli.server.server.impl;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 13:29
 */
import com.xiangli.server.RpcServer;
import com.xiangli.server.manager.ServiceManager;
import com.xiangli.server.server.work.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolRpcServer implements RpcServer {
    // 线程池，用于管理工作线程
    private final ThreadPoolExecutor threadPool;
    private final ServiceManager serviceManager;

    /**
     * 默认线程池构造函数
     * @param serviceManager 服务管理器，管理已注册的服务
     */
    public ThreadPoolRpcServer(ServiceManager serviceManager) {
        // 创建一个默认配置的线程池
        this.threadPool = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),  // 核心线程数
                1000, // 最大线程数
                60L,  // 空闲线程的存活时间
                TimeUnit.SECONDS,  // 时间单位
                new ArrayBlockingQueue<>(100)  // 队列大小
        );
        this.serviceManager = serviceManager;
    }

    /**
     * 自定义线程池构造函数
     * @param serviceManager 服务管理器
     * @param corePoolSize 核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime 空闲线程存活时间
     * @param unit 存活时间单位
     * @param workQueue 任务队列
     */
    public ThreadPoolRpcServer(ServiceManager serviceManager, int corePoolSize,
                               int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                               BlockingQueue<Runnable> workQueue) {
        this.threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.serviceManager = serviceManager;
    }

    /**
     * 启动服务器
     * @param port 监听的端口号
     */
    @Override
    public void start(int port) {
        System.out.println("线程池RPC服务器启动，监听端口：" + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // 无限循环监听请求
            while (true) {
                Socket socket = serverSocket.accept();  // 阻塞等待客户端连接
                // 使用线程池执行工作线程来处理每个客户端请求
                threadPool.execute(new WorkThread(socket, serviceManager));
            }
        } catch (IOException e) {
            System.err.println("服务器启动或运行时发生错误：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 停止服务器
     */
    @Override
    public void stop() {
        System.out.println("服务器正在关闭...");
        threadPool.shutdown();  // 停止线程池
        System.out.println("服务器已关闭");
    }
}
