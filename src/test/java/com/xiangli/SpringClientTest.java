package com.xiangli;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/02 23:03
 */
import com.xiangli.client.controller.UserController;
import com.xiangli.common.pojo.User;
import com.xiangli.server.RpcServerApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.junit.Assert.assertNotNull;

@Slf4j
public class SpringClientTest {

    @Test
    public void testGetUserById() {
        // 启动 Spring 容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RpcServerApplication.class);

        // 获取客户端控制器
        UserController clientController = context.getBean(UserController.class);

        // 测试获取用户
        log.info("ClientController: requesting getting user by user id");
        User user = clientController.getUserByUserId(1);
        log.info("ClientController: get user successfully : " + user);
        assertNotNull(user);


        context.close();
    }
}