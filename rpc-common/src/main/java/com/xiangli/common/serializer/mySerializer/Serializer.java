package com.xiangli.common.serializer.mySerializer;

import java.io.IOException;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/06 16:15
 */
public interface Serializer {
    /*
    * 把对象序列化成字节数组
    * @param obj 对象
     */
    byte[] serialize(Object obj) throws IOException;

    /*
    * 从字节数组反序列化成消息
    * @param bytes 字节数组
    * @param messageType 消息类型
     */
    Object deserialize(byte[] bytes, int messageType) throws IOException;

    /*
    * 返回序列化器的类型
    * @return 序列化器的类型 0代表java自带序列化方式，1代表json序列化方式
     */
    int getType();


    /*
    * 根据序列化器的类型码获取序列化器
    * @param code 序列化器的类型码
    * @return 序列化器
     */
    static Serializer getSerializerByCode(int code){
        switch (code){
            case 0:
                return new ObjectSerializer(); // java 自带序列化方式
            case 1:
                return new JsonSerializer(); // json序列化方式
            case 2:
                return new ProtostuffSerializer(); // protostuff序列化方式
            default:
                return null;
        }
    }
}
