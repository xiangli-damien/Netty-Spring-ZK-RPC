package com.xiangli.server.server.impl;

import com.xiangli.server.server.RpcServer;
import com.xiangli.server.initializer.NettyServerInitializer;
import com.xiangli.server.provider.ServiceProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/01 11:44
 */
@Component
@Slf4j
public class NettyRpcServer implements RpcServer {
    private ServiceProvider serviceProvider;

    private volatile boolean isRunning = false;

    public NettyRpcServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void start(int port) {
        if (isRunning) {
            log.warn("Server is already running on port " + port);
            return;
        }

        // 检查端口是否已经被占用
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.close();  // 检查完毕后立即关闭，以便 Netty 使用该端口
        } catch (IOException e) {
            log.warn("Port " + port + " is already in use", e);
            return;
        }
        new Thread(() -> {
            log.info("Server: Initializing Netty server, building bossGroup and workGroup...");
            // netty 服务线程组boss负责建立连接， work负责具体的请求
            NioEventLoopGroup bossGroup = new NioEventLoopGroup();
            NioEventLoopGroup workGroup = new NioEventLoopGroup();

            try {
                //启动netty服务器
                log.info("Server: Initializing Netty server, building ServerBootstrap and pipeline...");
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                //初始化
                serverBootstrap.group(bossGroup,workGroup)
                        // 确认使用NioServerSocketChannel来接收新的连接
                        .channel(NioServerSocketChannel.class)
                        //NettyClientInitializer这里 配置netty对消息的处理机制
                        .childHandler(new NettyServerInitializer(serviceProvider));

                //同步堵塞
                log.info("Server: Netty server started successfully, block listening on port " + port);
                ChannelFuture channelFuture=serverBootstrap.bind(port).sync();

                isRunning = true;

                //死循环监听
                channelFuture.channel().closeFuture().sync();
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
                isRunning = false;
            }
        }).start();

    }

    @Override
    public void stop() {
        System.out.println("Netty 服务器正在关闭...");
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
