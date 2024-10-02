package com.xiangli;


import com.xiangli.client.proxy.ClientProxy;
import com.xiangli.common.pojo.User;
import com.xiangli.common.service.UserService;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/02 09:51
 */
public class NettyClientTest {
    public static void main(String[] args) {
        // 创建客户端代理
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 8080);
        UserService userService = clientProxy.getProxy(UserService.class);

        // 调用远程服务
        User user = userService.getUserByUserId(1);
        System.out.println("User: " + user.getUserName() + ", ID: " + user.getId() + ", Sex: " + user.getSex());

        User newUser = new User(2, "Lixiang", true);
        Integer newUserId = userService.insertUserId(newUser);
        System.out.println("插入的用户 ID: " + newUserId);
    }
}
