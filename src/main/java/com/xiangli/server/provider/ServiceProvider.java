package com.xiangli.server.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 11:12
 */

/**
 * 本地服务注册器，负责管理所有服务类实例的注册和查找
 * 解耦
 * 避免重复创建对象
 */

public class ServiceProvider {
    //集合中存放服务的实例
    private Map<String, Object> interfaceProvider;

    public ServiceProvider() {
        this.interfaceProvider = new HashMap<>();
    }

    //本地注册服务
    public void registerServiceInterface(Object service) {
        // 获取服务实现类的所有接口
        Class<?>[] interfaces = service.getClass().getInterfaces();

        // 确保至少实现了一个接口
        if (interfaces.length == 0) {
            throw new IllegalArgumentException("The service must implement at least one interface");
        }

        // 遍历接口并注册
        for (Class<?> clazz : interfaces) {
            String serviceName = clazz.getName();  // 获取接口名
            interfaceProvider.put(serviceName, service);  // 注册服务
            System.out.println("Registered service: " + serviceName);
        }
    }

    //获取服务实例
    public Object getService(String interfaceName) {
        return interfaceProvider.get(interfaceName);
    }
}
