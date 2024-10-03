package com.xiangli.server.provider;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/02 16:17
 */
import com.xiangli.common.annotation.Remote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 负责扫描所有带有 @Remote 注解的服务类，并自动将其注册到 ServiceProvider 中。
 */
@Component
@Slf4j
public class RpcServiceRegistrar implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;

    public RpcServiceRegistrar(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 如果类上有 @Remote 注解，则将其注册为 RPC 服务
        if (bean.getClass().isAnnotationPresent(Remote.class)) {
            log.info("Server: register service " + bean.getClass().getInterfaces()[0].getCanonicalName());
            serviceProvider.registerService(bean);  // 只需要传递服务实例
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}