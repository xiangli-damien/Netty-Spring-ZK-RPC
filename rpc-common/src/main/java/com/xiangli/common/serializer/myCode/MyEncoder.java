package com.xiangli.common.serializer.myCode;

import com.xiangli.common.message.MessageType;
import com.xiangli.common.message.RpcRequest;
import com.xiangli.common.message.RpcResponse;
import com.xiangli.common.serializer.mySerializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/06 16:15
 */
@AllArgsConstructor
@Slf4j
public class MyEncoder extends MessageToByteEncoder {
    private Serializer serializer;

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        log.info("start encoding message : " + msg.getClass());
        //1.写入消息类型
        if (msg instanceof RpcRequest) {
            log.info("Encoded RpcRequest" + msg);
            out.writeShort(MessageType.REQUEST.getCode());
            encodeMessage(out, msg);
        } else if (msg instanceof RpcResponse) {
            out.writeShort(MessageType.RESPONSE.getCode());
            log.info("Encoded RpcResponse" + msg);
            encodeMessage(out, msg);
        } else if ("PING".equals(msg)) {
            log.info("Encoded PING");
            out.writeShort(MessageType.HEARTBEAT_REQUEST.getCode());  // 写入心跳请求类型
        } else if ("PONG".equals(msg)) {
            log.info("Encoded PONG");
            out.writeShort(MessageType.HEARTBEAT_RESPONSE.getCode());  // 写入心跳响应类型
        } else {
            log.error("Unsupported message type: " + msg.getClass());
        }

    }


    // 通用的消息编码方法，用于处理 RpcRequest 和 RpcResponse
    private void encodeMessage(ByteBuf out, Object msg) throws Exception {
        // 2. 写入序列化器类型
        out.writeShort(serializer.getType());
        log.info("Encoded serializer type: {}", serializer.getType());

        // 3. 根据消息类型调用不同的序列化方法
        byte[] serializeBytes = serializer.serialize(msg);
        log.info("Encoded bytes: {}", serializeBytes);

        // 4. 写入序列化后的数据长度
        out.writeInt(serializeBytes.length);

        // 5. 写入序列化后的数据
        out.writeBytes(serializeBytes);
        log.info("Encoded message: {} with length {}", msg.getClass().getSimpleName(), serializeBytes.length);
    }

}
