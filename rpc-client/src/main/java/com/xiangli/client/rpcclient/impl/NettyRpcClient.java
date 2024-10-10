package com.xiangli.client.rpcclient.impl;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/01 10:09
 */

import com.xiangli.client.transport.unprocessed.UnprocessedRequests;
import com.xiangli.client.transport.channel.ChannelProvider;
import com.xiangli.client.netty.initializer.NettyClientInitializer;
import com.xiangli.client.rpcclient.RpcClient;
import com.xiangli.client.serviceCenter.ServiceCenter;
import com.xiangli.common.message.RpcRequest;
import com.xiangli.common.message.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * Netty客户端，用于发送RPC请求并接收响应
 */
@Slf4j
@Component
public class NettyRpcClient implements RpcClient {

    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;

    @Autowired
    private ServiceCenter serviceCenter;

    // Netty 客户端需要的Bootstrap和EventLoopGroup
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;


    public NettyRpcClient(){
        unprocessedRequests = new UnprocessedRequests();
        channelProvider = new ChannelProvider();

        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer(unprocessedRequests));  // 初始化pipeline
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        String serviceName = request.getInterfaceName();
        InetSocketAddress serviceAddress = serviceCenter.serviceDiscovery(serviceName);
        if (serviceAddress == null) {
            log.error("无法找到可用的服务：{}", serviceName);
            return RpcResponse.fail(request.getRequestId());
        }

        Channel channel = getChannel(serviceAddress);
        if (channel != null && channel.isActive()) {
            CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
            unprocessedRequests.put(request.getRequestId(), resultFuture);
            channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    resultFuture.completeExceptionally(future.cause());
                    unprocessedRequests.remove(request.getRequestId());
                    log.error("发送请求失败", future.cause());
                }
            });
            try {
                // 设置超时时间，例如 5 秒
                return resultFuture.get(5, java.util.concurrent.TimeUnit.SECONDS);
            } catch (Exception e) {
                unprocessedRequests.remove(request.getRequestId());
                log.error("获取响应失败", e);
                return RpcResponse.fail(request.getRequestId());
            }
        } else {
            log.error("无法获取有效的 Channel，地址：{}", serviceAddress);
            return RpcResponse.fail(request.getRequestId());
        }
    }

    private Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = connect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    private Channel connect(InetSocketAddress inetSocketAddress) {
        try {
            ChannelFuture future = bootstrap.connect(inetSocketAddress).sync();
            Channel channel = future.channel();
            log.info("成功连接到服务器：{}", inetSocketAddress);
            return channel;
        } catch (InterruptedException e) {
            log.error("连接服务器失败：{}", inetSocketAddress, e);
            return null;
        }
    }

    // 添加关闭方法，释放资源
    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}