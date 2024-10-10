package com.xiangli.client.netty.handler;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 15:03
 */
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import com.xiangli.common.message.RpcResponse;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * 自定义入站客户端业户逻辑处理器
 */

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<Object> {


    // private ScheduledFuture<?> heartbeatTask;

    /* 客户端连接建立后，定时发送心跳，需要时使用
    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        // 客户端连接建立后，定时发送心跳
//        heartbeatTask = ctx.executor().scheduleAtFixedRate(() -> {
//            log.info("Client: sending heartbeat (PING)");
//            ctx.writeAndFlush("PING");
//        }, 0, 5, TimeUnit.SECONDS); // 每隔5秒发送一次心跳
//    }
     */

    //客户端接收到服务端的数据后调用
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 判断接收到的消息类型
        if (msg instanceof RpcResponse) {
            RpcResponse response = (RpcResponse) msg;
            // 正常的RPC响应处理
            if (response != null) {
                log.info(String.format("Client receive message from server: %s", response));
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
                ctx.channel().attr(key).set(response);
            } else {
                log.error("Response is null");
            }
            log.info("Client: rpcresponse received, closing channel");
            ctx.channel().close(); // 关闭连接
        } else if ("PONG".equals(msg)) {
            // 处理心跳响应
            log.info("Client: received heartbeat response (PONG)");
        } else {
            log.warn("Unknown message type received: " + msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //异常处理
        cause.printStackTrace();
        ctx.close();
    }


    // 检测空闲超时事件，超时触发发送Ping
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("Client: user idle event triggered");
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleEvent = (IdleStateEvent) evt;
            if (idleEvent.state() == IdleState.WRITER_IDLE) {
                log.info("Client: write idle detected, sending heartbeat (PING)");
                ctx.writeAndFlush("PING");
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
