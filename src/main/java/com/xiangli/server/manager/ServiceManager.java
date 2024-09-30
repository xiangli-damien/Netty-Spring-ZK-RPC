package com.xiangli.server.manager;

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

public class ServiceManager {
    //集合中存放服务的实例
    private Map<String, Object> interfaceManager;

    public ServiceManager() {
        this.interfaceManager = new HashMap<>();
    }

    //本地注册服务
    public void registerServiceInterface(Object service) {
        String serviceName = service.getClass().getInterfaces()[0].getName();
        interfaceManager.put(serviceName, service);
    }

    //获取服务实例
    public Object getService(String interfaceName) {
        return interfaceManager.get(interfaceName);
    }
}
