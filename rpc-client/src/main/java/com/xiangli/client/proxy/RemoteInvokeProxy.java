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
                // Enhancer 是 CGLIB 提供的一个字节码增强器, 可以用来为没有实现接口的类创建代理
                Enhancer enhancer = new Enhancer();
                // 设置需要动态代理的接口,setInterfaces() 方法的参数是一个 Class 数组，表示需要动态代理的接口
                enhancer.setInterfaces(new Class[]{field.getType()});
                // 设置回调处理，拦截方法调用
                enhancer.setCallback(new MethodInterceptor() {
                    /**
                     * @param o 代理对象
                     * @param method 被代理对象的方法
                     * @param args 方法参数
                     * @param methodProxy 代理方法
                     * @return
                     * @throws Throwable
                     */
                    @Override
                    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                        log.info("Client: calling method " + method.getName() + " of " + field.getType().getName());

                        // 创建RPC请求对象, 封装了请求的接口名、方法名、参数、参数类型,还有请求的唯一标识（通过无参构造）
                        RpcRequest request = new RpcRequest(
                                method.getDeclaringClass().getName(),
                                method.getName(),
                                args,
                                method.getParameterTypes()
                        );

                        // 获取调用服务类的接口名和方法名
                        String serviceName = request.getInterfaceName();
                        String methodName = request.getMethodName();

                        // 客户端重试机制校验（细化到方法级别）----幂等性校验
                        if (serviceCenter.checkMethodRetry(serviceName, methodName)) {
                            log.info("Method " + methodName + " of service " + serviceName + " is idempotent, will retry if needed.");

                            // 使用 GuavaRetry 进行消息发送
                            GuavaRetry guavaRetry = new GuavaRetry(rpcClient);
                            RpcResponse response = guavaRetry.sendServiceWithRetry(request);
                            log.info("Client: received RPC response with guava retry mechanics {}", response);
                            return response != null ? response.getData() : null;

                        }
                        // 如果不是幂等方法，直接发送请求
                        else {
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
}