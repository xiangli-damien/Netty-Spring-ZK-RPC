package com.xiangli.client.rpcclient.impl;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/01 10:09
 */

import com.xiangli.client.rpcclient.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import com.xiangli.client.netty.initializer.NettyClientInitializer;
import com.xiangli.common.message.RpcRequest;
import com.xiangli.common.message.RpcResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty客户端，用于发送RPC请求并接收响应
 */
@Slf4j
public class NettyRpcClient implements RpcClient {


    private String host;
    private int port;

    // Netty 客户端需要的Bootstrap和EventLoopGroup
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup eventLoopGroup;

    // 静态块初始化Netty客户端的全局配置
    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());  // 初始化pipeline
    }

    // 构造函数传入host和port
    public NettyRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        try {
            // 建立与服务端的连接
            log.info("Client: connecting to server " + host + ":" + port);
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();

            // 发送RPC请求
            channel.writeAndFlush(request);
            log.info("Client: send request: " + request);

            //sync()阻塞获取结果
            channel.closeFuture().sync();

            // 等待并接收响应
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            RpcResponse response = channel.attr(key).get();
            log.info("Client: receive response: " + response);

            // 关闭连接
            channel.closeFuture().sync();
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 异步请求的示例，便于后续扩展
    public void sendRequestAsync(RpcRequest request) {
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                Channel channel = ((ChannelFuture) future).channel();
                channel.writeAndFlush(request).addListener(writeFuture -> {
                    if (writeFuture.isSuccess()) {
                        System.out.println("Request sent successfully");
                    } else {
                        System.out.println("Failed to send request");
                    }
                });
            } else {
                System.out.println("Failed to connect to server");
            }
        });
    }
}
