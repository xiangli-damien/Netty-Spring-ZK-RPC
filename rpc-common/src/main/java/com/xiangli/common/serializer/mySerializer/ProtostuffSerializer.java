package com.xiangli.common.serializer.mySerializer;

import com.xiangli.common.message.MessageType;
import com.xiangli.common.message.RpcRequest;
import com.xiangli.common.message.RpcResponse;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/07 13:41
 */
public class ProtostuffSerializer implements Serializer {

    // 提前分配好 Buffer，避免每次进行序列化都需要重新分配 buffer 内存空间
    private final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    @Override
    public byte[] serialize(Object obj) throws IOException {
        try {
            Schema schema = RuntimeSchema.getSchema(obj.getClass());
            return ProtostuffIOUtil.toByteArray(obj, schema, BUFFER);
        } catch (Exception e) {
            throw new IOException("Protostuff serialize failed.", e);
        } finally {
            // 重置 buffer
            BUFFER.clear();
        }
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) throws IOException {
        try {
            Object obj = null;
            // 根据 messageType 确定反序列化的对象类型
            if (messageType == MessageType.REQUEST.getCode()) {
                // 如果是请求，使用 RpcRequest
                Schema<RpcRequest> schema = RuntimeSchema.getSchema(RpcRequest.class);
                RpcRequest request = schema.newMessage();
                ProtostuffIOUtil.mergeFrom(bytes, request, schema);
                obj = request;
            } else if (messageType == MessageType.RESPONSE.getCode()) {
                // 如果是响应，使用 RpcResponse
                Schema<RpcResponse> schema = RuntimeSchema.getSchema(RpcResponse.class);
                RpcResponse response = schema.newMessage();
                ProtostuffIOUtil.mergeFrom(bytes, response, schema);
                obj = response;
            }
            return obj;
        } catch (Exception e) {
            throw new IOException("Protostuff deserialize failed.", e);
        }
    }

    @Override
    public int getType() {
        return 2; // 设定为 2 表示使用 Protostuff 序列化
    }
}