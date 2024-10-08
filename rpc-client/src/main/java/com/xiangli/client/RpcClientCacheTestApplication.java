package com.xiangli.client;

import com.xiangli.client.controller.UserController;
import com.xiangli.client.serviceCenter.ZKServiceCenter;
import com.xiangli.common.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.xiangli.client.cache.serviceCache;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/07 19:24
 */
@Slf4j
public class RpcClientCacheTestApplication {

    public static void main(String[] args) {
        // 初始化 Spring 容器
        ApplicationContext context = new AnnotationConfigApplicationContext("com.xiangli.client");

        // 获取 ZKServiceCenter 实例，用于服务发现
        ZKServiceCenter serviceCenter = context.getBean(ZKServiceCenter.class);

        // 获取本地缓存实例
        serviceCache cache = serviceCenter.getCache();

        // 发现服务的地址
        InetSocketAddress serviceAddress = serviceCenter.serviceDiscovery("com.xiangli.server.serviceimpl.UserServiceImpl");

        // 检查服务发现是否成功
        if (serviceAddress != null) {
            log.info("Discovered service at: {}", serviceAddress);
        } else {
            log.error("Service discovery failed!");
            return;
        }

        // 从缓存中获取服务地址
        List<String> cachedAddresses = cache.getServcieFromCache("com.xiangli.server.serviceimpl.UserServiceImpl");

        // 验证缓存中是否添加了服务地址
        if (cachedAddresses != null && !cachedAddresses.isEmpty()) {
            log.info("Service successfully added to cache: {}", cachedAddresses);
        } else {
            log.error("Service was not added to the cache!");
        }

        // 获取 UserController 的 Bean
        UserController userController = context.getBean(UserController.class);

        // 测试调用远程服务
        log.info("Client: calling getUserByUserId with id = 1");
        System.out.println(userController.getUserByUserId(1));

        System.out.println(userController.insertUserId(new User(2, "test", true)));
    }
}