package com.xiangli.server.provider;

import com.xiangli.server.ratelimit.RateLimit;
import com.xiangli.server.ratelimit.provider.RateLimitProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 11:12
 */

/**
 * 本地服务注册器，负责管理所有服务类实例的注册和查找。
 */
@Component
@Slf4j
public class ServiceProvider {

    // 存储接口名和服务实例的映射关系
    private final Map<String, Object> serviceMap = new HashMap<>();

    @Autowired
    private RateLimitProvider rateLimitProvider;

    /**
     * 注册服务到服务提供者中。
     * 通过服务实例找到它实现的接口并进行注册。
     *
     * @param service 服务的具体实现类。
     */
    public void registerService(Object service) {
        // 获取服务实现类的所有接口
        Class<?>[] interfaces = service.getClass().getInterfaces();

        // 确保至少实现了一个接口
        if (interfaces.length == 0) {
            throw new IllegalArgumentException("The service must implement at least one interface");
        }

        // 遍历接口并注册
        for (Class<?> iface : interfaces) {
            String serviceName = iface.getName();  // 获取接口名
            serviceMap.put(serviceName, service);  // 注册服务
            log.info("Server: Registered service: " + serviceName);
        }
    }

    /**
     * 根据接口名获取服务实例。
     * @param interfaceName 接口的全限定类名。
     * @return 服务实例对象，如果未找到则返回 null。
     */
    public Object getService(String interfaceName) {
        RateLimit rareLimit = rateLimitProvider.getRateLimit(interfaceName);
        if (rareLimit.getToken()){
            log.info("Server: service " + interfaceName + " is available");
            return serviceMap.get(interfaceName);
        } else {
            log.warn("Server: The service " + interfaceName + " is limited by rate limit");
            throw new RuntimeException("The service is limited by rate limit");
        }

    }
}