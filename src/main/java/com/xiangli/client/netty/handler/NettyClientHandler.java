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

/**
 * 自定义入站客户端业户逻辑处理器
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    //客户端接收到服务端的数据后调用
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        //接收到response, 给channel设计别名，让sendRequest里读取response
        if (response != null) {
            // 读取服务端的响应
            System.out.println("Handler: Client received response: " + response);
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RPCResponse");
            // 给channel设置属性
            ctx.channel().attr(key).set(response);
        } else {
            System.out.println("Received null response");
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
