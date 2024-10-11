package com.xiangli.common.serializer.myCode;

import java.util.List;

import com.xiangli.common.message.RpcRequest;
import com.xiangli.common.message.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import com.xiangli.common.serializer.mySerializer.Serializer;
import com.xiangli.common.message.MessageType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/06 16:14
 */
@Slf4j
public class MyDecoder extends ByteToMessageDecoder{

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 1. 读取消息类型
        short messageType = in.readShort();
        log.info("DeCoding-1-messageType: {}", messageType);

        // 2. 根据消息类型处理不同的逻辑
        if (messageType == MessageType.REQUEST.getCode()) {
            log.info("Decoding RpcRequest...");
            decodeRpcMessage(in, messageType, out);  // 处理RpcRequest
        } else if (messageType == MessageType.RESPONSE.getCode()) {
            log.info("Decoding RpcResponse...");
            decodeRpcMessage(in, messageType, out);  // 处理RpcResponse
        } else if (messageType == MessageType.HEARTBEAT_REQUEST.getCode()) {
            // 处理心跳请求 (PING)
            log.info("Received PING heartbeat message.");
            out.add("PING");
        } else if (messageType == MessageType.HEARTBEAT_RESPONSE.getCode()) {
            // 处理心跳响应 (PONG)
            log.info("Received PONG heartbeat message.");
            out.add("PONG");
        } else {
            log.error("Unsupported message type: {}", messageType);
            return;  // 不支持的消息类型，直接返回
        }
    }

    /**
     * 通用的消息解码方法，用于解码 RpcRequest 和 RpcResponse。
     *
     * @param in            ByteBuf 输入流，包含待解码的数据
     * @param messageType   消息类型
     * @param out           解码后的消息对象列表，用于传递给下一个 ChannelHandler
     * @param <T>           消息的具体类型
     * @throws Exception    在解码过程中可能抛出的异常
     */
    private <T> void decodeRpcMessage(ByteBuf in, Short messageType, List<Object> out) throws Exception {
        // 3. 读取序列化器类型
        short serializerType = in.readShort();
        log.info("Decoding-2-serializerType: {}", serializerType);

        Serializer serializer = Serializer.getSerializerByCode(serializerType);
        if (serializer == null) {
            log.error("No matching serializer found for type: {}", serializerType);
            throw new RuntimeException("No matching serializer found.");
        }

        // 4. 读取序列化数据的长度
        int length = in.readInt();
        log.info("Decoding-3-Data length header: {}", length);

        // 5. 读取序列化的字节数据
        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        // 6. 反序列化数据，转换为目标类型
        Object deserialize= serializer.deserialize(bytes, messageType);
        log.info("Decoding-4-Data deserialized: "+deserialize);
        if (deserialize != null) {
            log.info("Data deserialized successfully : "+deserialize.getClass());
        } else {
            log.error("Data deserialization failed.");
        }

        // 7. 将解码后的消息加入输出列表
        out.add(deserialize);
    }

}
