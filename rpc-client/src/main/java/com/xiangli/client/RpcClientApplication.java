package com.xiangli.client;

import com.xiangli.client.cache.serviceCache;
import com.xiangli.client.controller.UserController;
import com.xiangli.client.serviceCenter.impl.ZKServiceCenter;
import com.xiangli.common.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/03 11:09
 */
@Slf4j
public class RpcClientApplication {

    public static void main(String[] args) {
        // 初始化 Spring 容器
        /*
        * ApplicationContext是Spring的核心接口之一，它负责管理Bean的生命周期。它不仅会处理Bean的创建和销毁，还会处理依赖注入、注解处理、AOP等功能。
        * 启动AnnotationConfigApplicationContext后，Spring会扫描指定包下的所有类，找到所有被@Component、@Service、@Repository、@Controller等注解标记的类，并将其实例化为Bean。
        *
         */
        ApplicationContext context = new AnnotationConfigApplicationContext("com.xiangli.client");

        // 获取 ZKServiceCenter 实例，用于服务发现
        ZKServiceCenter serviceCenter = context.getBean(ZKServiceCenter.class);

        serviceCache cache = serviceCenter.getCache();

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

        for (int i = 0; i < 5; i++) {
            // 调用 UserController 的方法，自动通过 RemoteInvokeProxy 进行服务发现和 RPC 调用
            log.info("Client: calling getUserByUserId with id = {}", i);
            System.out.println(userController.getUserByUserId(i));

            User user = new User(i, "User" + i, i % 2 == 0);
            System.out.println(userController.insertUserId(user));

        }

    }
}
