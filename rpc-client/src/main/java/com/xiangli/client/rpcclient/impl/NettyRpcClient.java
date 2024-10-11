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
        // 获取服务类接口名称
        String serviceName = request.getInterfaceName();
        // 通过服务中心获取服务地址，暂定负载均衡策略为一致性哈希
        InetSocketAddress serviceAddress = serviceCenter.serviceDiscovery(serviceName);
        // 如果服务地址为空，返回失败响应
        if (serviceAddress == null) {
            log.error("无法找到可用的服务：{}", serviceName);
            return RpcResponse.fail(request.getRequestId());
        }

        // 通过服务地址获取对应的 Channel
        Channel channel = getChannel(serviceAddress);

        // 如果 Channel 有效并且处于活动状态，发送请求
        if (channel != null && channel.isActive()) {
            // 创建 CompletableFuture 对象，用于获取 RPC 响应
            CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
            // 将请求放入未处理请求的 Map 中 (key 为请求的 ID)
            unprocessedRequests.put(request.getRequestId(), resultFuture);
            // 发送请求并且添加监听器
            channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
                // 如果发送失败
                if (!future.isSuccess()) {
                    // 设置异常信息到 CompletableFuture 中
                    resultFuture.completeExceptionally(future.cause());
                    // 从未处理请求的 Map 中移除请求
                    unprocessedRequests.remove(request.getRequestId());
                    log.error("failed to send message to server: ", future.cause());
                }
            });
            try {
                /*
                    * 从 CompletableFuture 中获取 RPC 响应
                    * 如果在 5 秒内没有获取到响应，返回一个默认的失败响应
                 */
                return resultFuture.get(5, java.util.concurrent.TimeUnit.SECONDS);
            } catch (Exception e) {
                unprocessedRequests.remove(request.getRequestId());
                log.error("failed to get result from CompletableFuture: ", e);
                return RpcResponse.fail(request.getRequestId());
            }
        } else {
            log.error("failed to send message to server, channel is null or inactive");
            return RpcResponse.fail(request.getRequestId());
        }
    }

    private Channel getChannel(InetSocketAddress inetSocketAddress) {
        // 从 ChannelProvider 中获取 Channel
        Channel channel = channelProvider.get(inetSocketAddress);
        log.info("getting channel {} from channel provider", channel);
        if (channel == null) {
            log.info("channel is null, try to connect to server {}", inetSocketAddress);
            channel = connect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
            log.info("set channel to channel {} provider", channel);
        }
        return channel;
    }

    private Channel connect(InetSocketAddress inetSocketAddress) {
        try {
            // 客服端连接服务器，同步等待连接成功
            ChannelFuture future = bootstrap.connect(inetSocketAddress).sync();
            Channel channel = future.channel();
            log.info("successfully connected to server {}", inetSocketAddress);
            return channel;
        } catch (InterruptedException e) {
            log.error("failed to connect to server {}", inetSocketAddress);
            return null;
        }
    }

    // 添加关闭方法，释放资源
    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}