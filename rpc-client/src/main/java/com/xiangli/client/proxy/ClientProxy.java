package com.xiangli.client.proxy;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 09:41
 */

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import com.xiangli.common.message.RpcRequest;
import com.xiangli.common.message.RpcResponse;
import com.xiangli.client.rpcclient.impl.NettyRpcClient;

public class ClientProxy implements InvocationHandler {

    private final NettyRpcClient rpcClient;

    public ClientProxy(String host, int port) {
        this.rpcClient = new NettyRpcClient();
    }

    // 动态代理，每次代理对象调用方法时，都会经过此方法
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构建 RpcRequest 对象
        RpcRequest request = new RpcRequest(
                method.getDeclaringClass().getName(),  // 接口类名
                method.getName(),                      // 方法名
                args,                                  // 参数
                method.getParameterTypes()              // 参数类型
        );

        // 通过 NettyRpcClient 发送请求，接收响应
        RpcResponse response = rpcClient.sendRequest(request);

        // 返回响应中的数据
        return response != null ? response.getData() : null;
    }

    // 获取动态代理对象
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                this
        );
    }
}
