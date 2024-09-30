package com.xiangli.server.server.work;

import com.xiangli.common.message.RpcRequest;
import com.xiangli.common.message.RpcResponse;
import com.xiangli.server.manager.ServiceManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 11:29
 */


// 工作线程，用于处理客户端的请求
public class WorkThread implements Runnable {
    private final Socket socket;
    private final ServiceManager serviceManager;

    public WorkThread(Socket socket, ServiceManager serviceManager) {
        this.socket = socket;
        this.serviceManager = serviceManager;
    }

    @Override
    public void run() {
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            // 读取客户端传来的请求
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            System.out.println("收到客户端请求：" + rpcRequest);

            // 根据请求获取服务，并调用对应的方法
            RpcResponse rpcResponse = handleRequest(rpcRequest);

            // 将响应发送回客户端
            oos.writeObject(rpcResponse);
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("处理客户端请求时出错：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 处理 RPC 请求，调用相应的服务方法
     */
    private RpcResponse handleRequest(RpcRequest rpcRequest) {
        // 获取服务名（接口名）
        String interfaceName = rpcRequest.getInterfaceName();
        // 从 ServiceManager 中获取对应的服务实例
        Object service = serviceManager.getService(interfaceName);

        if (service == null) {
            return RpcResponse.fail("未找到对应的服务：" + interfaceName);
        }

        try {
            // 获取方法名和参数类型
            String methodName = rpcRequest.getMethodName();
            Class<?>[] paramTypes = rpcRequest.getParamsType();

            // 通过反射获取服务中的方法
            Method method = service.getClass().getMethod(methodName, paramTypes);

            // 调用该方法并获取返回值
            Object result = method.invoke(service, rpcRequest.getParams());

            // 返回成功的响应
            return RpcResponse.success(result);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("调用服务方法出错：" + e.getMessage());
            e.printStackTrace();
            return RpcResponse.fail("服务调用出错：" + e.getMessage());
        }
    }
}
