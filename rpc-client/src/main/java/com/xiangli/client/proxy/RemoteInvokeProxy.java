package com.xiangli.client.proxy;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.xiangli.client.retry.GuavaRetry;
import com.xiangli.client.serviceCenter.ServiceCenter;
import com.xiangli.common.annotation.RemoteInvoke;
import com.xiangli.client.rpcclient.impl.NettyRpcClient;
import com.xiangli.common.message.RpcRequest;
import com.xiangli.common.message.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/02 16:06
 */
@Component
@Slf4j
public class RemoteInvokeProxy implements BeanPostProcessor {

    @Autowired
    private ServiceCenter serviceCenter;

    @Autowired
    private NettyRpcClient rpcClient;

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

                        // 创建RPC请求对象
                        RpcRequest request = new RpcRequest(
                                method.getDeclaringClass().getName(),
                                method.getName(),
                                args,
                                method.getParameterTypes()
                        );

                        //
                        String serviceName = request.getInterfaceName();
                        String methodName = request.getMethodName();

                        // 细化到方法级别的幂等性校验
                        if (serviceCenter.checkMethodRetry(serviceName, methodName)) {
                            log.info("Method " + methodName + " of service " + serviceName + " is idempotent, will retry if needed.");

                            // 使用 GuavaRetry
                            GuavaRetry guavaRetry = new GuavaRetry(rpcClient);
                            RpcResponse response = guavaRetry.sendServiceWithRetry(request);
                            log.info("Client: received RPC response after retry {}", response);
                            return response != null ? response.getData() : null;

                        } else {
                            log.info("Method " + methodName + " of service " + serviceName + " is not idempotent. Sending request without retry.");
                            RpcResponse response = rpcClient.sendRequest(request);
                            log.info("Client: received RPC response " + response);
                            return response != null ? response.getData() : null;
                        }
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
        return bean;
    }

//    // 发送RPC请求的方法，动态获取服务地址
//    private RpcResponse sendRpcRequest(RpcRequest request) {
//        String serviceName = request.getInterfaceName();  // 获取接口名
//
//        // 从服务中心获取服务地址
//        InetSocketAddress serviceAddress = serviceCenter.serviceDiscovery(serviceName);
//
//        if (serviceAddress != null) {
//            String host = serviceAddress.getHostName();
//            int port = serviceAddress.getPort();
//            log.info("Client: connecting to server " + host + ":" + port);
//
//            // 使用Netty客户端发送RPC请求
//            return new NettyRpcClient(host, port).sendRequest(request);
//        } else {
//            log.error("No available service found for " + serviceName);
//            return null;
//        }
//    }
}