package com.xiangli.server.netty.initializer;

import com.xiangli.common.serializer.myCode.MyDecoder;
import com.xiangli.common.serializer.myCode.MyEncoder;
import com.xiangli.common.serializer.mySerializer.ProtostuffSerializer;
import com.xiangli.server.netty.handler.NettyRpcServerHandler;
import com.xiangli.server.provider.ServiceProvider;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.AllArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/01 11:23
 */
@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceProvider serviceManager;
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //消息格式 【长度】【消息体】，解决沾包问题
        pipeline.addLast(
                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
        //计算当前待发送消息的长度，写入到前4个字节中
        pipeline.addLast(new LengthFieldPrepender(4));

        //使用自定义的编/解码器
        pipeline.addLast(new MyEncoder(new ProtostuffSerializer()));

        //使用了自定义的解码器
        pipeline.addLast(new MyDecoder());

        /*
            * IdleStateHandler 是netty提供的处理空闲状态的处理器，
            * @param readerIdleTime 读空闲时间
            * @param writerIdleTime 写空闲时间
            * @param allIdleTime    读写空闲时间
            * @param unit           时间单位
         */
        pipeline.addLast(new IdleStateHandler(40, 0, 0, TimeUnit.SECONDS));

        pipeline.addLast(new NettyRpcServerHandler(serviceManager));
    }
}

