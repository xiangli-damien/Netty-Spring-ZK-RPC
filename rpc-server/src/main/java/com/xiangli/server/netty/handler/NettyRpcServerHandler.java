package com.xiangli.server.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.xiangli.common.message.RpcRequest;
import com.xiangli.common.message.RpcResponse;
import com.xiangli.server.provider.ServiceProvider;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/01 10:55
 */

/*
    * 服务端inbound处理器
    * 从request中读取数据，调用服务端相应服务
 */

@Slf4j
@AllArgsConstructor
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<Object> {
    private ServiceProvider serviceProvider;

    // 定义线程池
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            10,  // 核心线程数
            20,  // 最大线程数
            60L, // 空闲线程存活时间
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000)  // 阻塞队列容量
    );
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("Server: Channel ID: {} ", ctx.channel().id());
        threadPool.submit(() -> {
            try {
                log.info("Server: received message from client" + msg);
                // 判断消息类型
                if (msg instanceof RpcRequest) {
                    RpcRequest request = (RpcRequest) msg;
                    // 正常RPC请求处理
                    RpcResponse response = getResponse(request);
                    log.info("Server: send response to client");
                    ctx.writeAndFlush(response);
                } else if ("PING".equals(msg)) {
                    // 处理心跳请求
                    log.info("Server: received heartbeat (PING), sending PONG");
                    ctx.writeAndFlush("PONG");
                } else {
                    log.warn("Unknown message type received: " + msg);
                }
            } catch (Exception e) {
                log.error("Server: error in processing request", e);
                e.printStackTrace();
                ctx.close();
            } finally {
                // 释放消息
                ReferenceCountUtil.release(msg);
            }
        });

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private RpcResponse getResponse(RpcRequest rpcRequest){
        //得到服务名
        String interfaceName=rpcRequest.getInterfaceName();
        //得到服务端相应服务实现类
        Object service = serviceProvider.getService(interfaceName);
        //反射调用方法
        Method method=null;
        log.info("Server: get response from service");
        try {
            log.info("Server: get method using reflection");
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsType());
            log.info("Server: invoke method");
            Object invoke = method.invoke(service,rpcRequest.getParams());
            return RpcResponse.success(rpcRequest.getRequestId(), invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            log.error("method execution error");
            return RpcResponse.fail(rpcRequest.getRequestId());
        }
    }

//    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("Server: user idle event triggered");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleEvent = (IdleStateEvent) evt;
            if (idleEvent.state() == IdleState.READER_IDLE) {
                log.warn("Server: no read for 10 seconds, closing connection");
                ctx.close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

}

