package com.xiangli.server;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/02 22:58
 */
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Configuration
@ComponentScan(basePackages = "com.xiangli")  // 扫描 com.xiangli 包及其子包中的 Spring 组件
public class RpcServerApplication {

    public static void main(String[] args) {
        // 启动 Spring 容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RpcServerApplication.class);

        // 优雅关闭 Spring 容器
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            context.close();
            System.out.println("Spring 容器关闭...");
        }));
    }
}