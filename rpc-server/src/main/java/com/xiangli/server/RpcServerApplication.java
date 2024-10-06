package com.xiangli.server;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/02 22:58
 */
import com.xiangli.server.serviceregister.impl.ZKServiceRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.InetSocketAddress;

@Configuration
@Slf4j
@ComponentScan(basePackages = "com.xiangli")  // 扫描 com.xiangli 包及其子包中的 Spring 组件
public class RpcServerApplication {

    public static void main(String[] args) {
        // 启动 Spring 容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RpcServerApplication.class);
//        String[] beanNames = context.getBeanDefinitionNames();
//        log.info("以下是 Spring 容器中初始化的 Bean:");
//        for (String beanName : beanNames) {
//            log.info("初始化 Bean: " + beanName + " 类型: " + context.getBean(beanName).getClass().getName());
//        }
        // 从 Spring 容器中获取 ZKServiceRegister 实例
        ZKServiceRegister zkServiceRegister = context.getBean(ZKServiceRegister.class);
        zkServiceRegister.register("com.xiangli.server.serviceimpl.UserServiceImpl", new InetSocketAddress("localhost", 8181));

        // 优雅关闭 Spring 容器
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            context.close();
            log.info("Spring context closed.");
        }));
    }
}