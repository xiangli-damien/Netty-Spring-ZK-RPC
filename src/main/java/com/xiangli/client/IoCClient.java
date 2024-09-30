package com.xiangli.client;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 09:52
 * @function 代理对象进行远程rpc调用，采用传统io
 */

import com.xiangli.common.message.RpcRequest;
import com.xiangli.common.message.RpcResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class IoCClient {

    /**
     * 发送 Rpc 请求并接收 Rpc 响应
     * @param host 服务端的主机地址
     * @param port 服务端的端口号
     * @param request Rpc 请求对象
     * @return Rpc 响应对象
     */
    public static RpcResponse sendRequest(String host, int port, RpcRequest request) {
        RpcResponse response = null;
        // 尝试链接服务端通过Socket(host, port)
        try (Socket socket = new Socket(host, port)) {
            // 创建输出流，并将socket的输出流包裹，意味接下来通过oos写出的数据会被发送到socket连接的端口
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            // 将request对象序列化并且发送
            oos.writeObject(request);
            // 刷新输出流，保险起见避免有数据滞留在缓冲区
            oos.flush();

            // 从服务端读取响应，socket可以进行双向通信，客户端通过output发送，服务端通过input回复
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            // 反序列化字节流成为一个对象，阻塞等待
            response = (RpcResponse) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error occurred while sending request to server: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
}

