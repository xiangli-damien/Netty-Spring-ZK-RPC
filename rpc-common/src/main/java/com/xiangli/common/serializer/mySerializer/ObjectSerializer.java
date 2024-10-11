package com.xiangli.common.serializer.mySerializer;

import java.io.*;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/06 16:15
 * Java原生序列化器，用于序列化和反序列化
 */
public class ObjectSerializer implements Serializer{
    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes = null;
        // 创建一个字节数组输出流，用于存放序列化后的对象
        // bos和oos的区别是bos是字节数组输出流，oos是对象输出流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // 创建一个对象输出流，用于将对象序列化成字节数组
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj); // 序列化对象
            oos.flush(); // 刷新数据流
            bytes = bos.toByteArray(); // 获取序列化后的字节数组
            oos.close(); // 关闭流
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    // 反序列化
    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public int getType() {
        return 0; // 0代表java自带序列化方式
    }
}
