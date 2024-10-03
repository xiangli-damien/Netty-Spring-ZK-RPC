package com.xiangli.client.netty.handler;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 15:03
 */
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import com.xiangli.common.message.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 自定义入站客户端业户逻辑处理器
 */

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    //客户端接收到服务端的数据后调用
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        //接收到response, 给channel设计别名，让sendRequest里读取response
        if (response != null) {
            // 读取服务端的响应
            log.info(String.format("Client receive message from server: %s", response));
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            // 给channel设置属性
            ctx.channel().attr(key).set(response);
        } else {
            log.error("Response is null");
        }
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //异常处理
        cause.printStackTrace();
        ctx.close();
    }
}
