package com.xiangli.client.proxy;

import com.xiangli.common.annotation.RemoteInvoke;
import com.xiangli.client.rpcclient.impl.NettyRpcClient;
import com.xiangli.common.message.RpcRequest;
import com.xiangli.common.message.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/02 16:06
 */
@Component
@Slf4j
public class RemoteInvokeProxy implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 在这一步我们将会处理Bean中的每个字段，判断它们是否带有 @RemoteInvoke 注解
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 判断字段是否标注了 @RemoteInvoke 注解
            if (field.isAnnotationPresent(RemoteInvoke.class)) {
                // 设置字段的可访问性为 true，以便能够进行反射操作
                field.setAccessible(true);

                // 这里我们创建动态代理来替换原有的属性
                Enhancer enhancer = new Enhancer();
                // 设置需要动态代理的接口
                enhancer.setInterfaces(new Class[]{field.getType()});
                // 设置回调处理，拦截方法调用
                enhancer.setCallback(new MethodInterceptor() {
                    @Override
                    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                        log.info("Client: calling method " + method.getName() + " of " + field.getType().getName());
                        // 在这里，我们将通过 Netty 客户端发送 RPC 请求
                        RpcRequest request = new RpcRequest(
                                method.getDeclaringClass().getName(),  // 接口类名
                                method.getName(),                      // 方法名
                                args,                                  // 参数
                                method.getParameterTypes()              // 参数类型
                        );
                        log.info("Client: sending RPC request " + request);
                        // 调用 Netty 客户端发送请求
                        RpcResponse response = sendRpcRequest(request);
                        log.info("Client: received RPC response " + response);

                        // 返回服务器的响应结果
                        return response != null ? response.getData() : null;
                    }
                });

                try {
                    // 将动态代理对象赋值给对应的字段
                    field.set(bean, enhancer.create());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 后置处理器的这个方法可以直接返回bean即可
        return bean;
    }

    // 发送RPC请求的方法，这里可以调用Netty客户端
    private RpcResponse sendRpcRequest(RpcRequest request) {
        // 实现Netty客户端发送请求并返回响应
        return new NettyRpcClient("localhost", 8181).sendRequest(request);
    }
}