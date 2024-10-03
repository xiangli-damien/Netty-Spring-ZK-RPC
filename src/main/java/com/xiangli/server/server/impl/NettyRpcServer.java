package com.xiangli.server.server.impl;

import com.xiangli.server.RpcServer;
import com.xiangli.server.initializer.NettyServerInitializer;
import com.xiangli.server.provider.ServiceProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/01 11:44
 */
@Component
@Slf4j
@AllArgsConstructor
public class NettyRpcServer implements RpcServer {
    private ServiceProvider serviceProvider;

    @Override
    public void start(int port) {
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

                //死循环监听
                channelFuture.channel().closeFuture().sync();
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
            }
        }).start();

    }

    @Override
    public void stop() {
        System.out.println("Netty 服务器正在关闭...");
    }
}
