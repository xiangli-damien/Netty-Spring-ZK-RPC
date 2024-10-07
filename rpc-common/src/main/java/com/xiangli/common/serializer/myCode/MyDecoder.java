package com.xiangli.common.serializer.myCode;

import java.util.List;
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
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        //1.读取消息类型
        short messageType = in.readShort();
        log.info("DeCoding-1-messageType: "+messageType);

        // 现在还只支持request与response请求
        if(messageType != MessageType.REQUEST.getCode() &&
                messageType != MessageType.RESPONSE.getCode()){
            log.error("暂不支持此种数据");
            return;
        }
        //2.读取序列化的方式&类型
        short serializerType = in.readShort();
        log.info("Decoding-2-serializerType: "+serializerType);

        Serializer serializer = Serializer.getSerializerByCode(serializerType);
        if(serializer == null)
            throw new RuntimeException("不存在对应的序列化器");

        //3.读取序列化数组长度
        int length = in.readInt();
        log.info("Decoding-3-Data length header: "+length);

        //4.读取序列化数组
        byte[] bytes=new byte[length];
        in.readBytes(bytes);
        Object deserialize= serializer.deserialize(bytes, messageType);
        log.info("Decoding-4-Data deserialized: "+deserialize);
        if (deserialize != null) {
            log.info("Data deserialized successfully : "+deserialize.getClass());
        } else {
            log.error("Data deserialization failed.");
        }

        //5.将反序列化后的对象加入到out中
        out.add(deserialize);
    }

}
