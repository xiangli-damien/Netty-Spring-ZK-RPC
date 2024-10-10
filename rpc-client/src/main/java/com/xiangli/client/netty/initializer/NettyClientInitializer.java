package com.xiangli.client.netty.initializer;

import com.xiangli.client.transport.unprocessed.UnprocessedRequests;
import com.xiangli.common.serializer.myCode.MyDecoder;
import com.xiangli.common.serializer.myCode.MyEncoder;
import com.xiangli.common.serializer.mySerializer.ProtostuffSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import com.xiangli.client.netty.handler.NettyClientHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 15:10
 */


public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    /*
        ChannelInitializer是一个Netty提供的一个抽象类，用于初始化Channel,只在Channel第一次注册到EventLoop上调用
        每个Channel都有一个ChannelPipeline，用于添加和设置ChannelHandler链
     */
    private final UnprocessedRequests unprocessedRequests;

    public NettyClientInitializer(UnprocessedRequests unprocessedRequests) {
        this.unprocessedRequests = unprocessedRequests;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        /*
            * InboundHandler ： 解码器，用于解决TCP粘包、拆包问题

            * @param maxFrameLength 		帧的最大长度（单位为字节，下同）
            * @param lengthFieldOffset 	    长度字段的偏移长度（这里的长度字段就是 消息长度字段）
            * @param lengthFieldLength 	    长度字段的长度
            * @param lengthAdjustment  	    要添加到长度字段值的补偿值
            * @param initialBytesToStrip 	从解码帧中取出的第一个字节数
         */
        pipeline.addLast(
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
        /*
            * OutboundHandler ： LengthFieldPrepender 是一个消息编码器，它可以在消息头部增加消息的长度字段（4个字节）
            * @param lengthFieldLength 	长度字段的长度
         */

        pipeline.addLast(new LengthFieldPrepender(4));

        /*
            * OutboundHandler ： MyEncoder 是一个消息编码器，它可以将对象序列化为字节数组,使用自定义的编码器
         */
        pipeline.addLast(new MyEncoder(new ProtostuffSerializer()));


        /*
            * InboundHandler ： MyDecoder 是一个消息解码器，它可以将字节数组反序列化为对象,使用自定义的解码器
         */
        pipeline.addLast(new MyDecoder());

        /*
            * InboundHandler ： IdleStateHandler 是netty提供的处理空闲状态的处理器
            * @param readerIdleTime 读空闲时间
            * @param writerIdleTime 写空闲时间
            * @param allIdleTime    读写空闲时间
            * @param unit           时间单位
         */
        pipeline.addLast(new IdleStateHandler(0, 120, 0, TimeUnit.SECONDS));

        /*
            * InboundHandler ： NettyClientHandler 是一个自定义的消息处理器，用于处理服务端返回的消息
         */
        pipeline.addLast(new NettyClientHandler(unprocessedRequests));
    }
}

