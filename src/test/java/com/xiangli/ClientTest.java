package com.xiangli;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 13:36
 */
import com.xiangli.client.proxy.ClientProxy;
import com.xiangli.common.pojo.User;
import com.xiangli.common.service.UserService;

public class ClientTest {
    public static void main(String[] args) {
        // 创建 ClientProxy 对象，指定服务端的 IP 和端口
        ClientProxy clientProxy = new ClientProxy("127.0.0.1", 9999);

        // 通过 ClientProxy 获取动态代理对象
        UserService userService = clientProxy.getProxy(UserService.class);

        // 调用代理对象的方法
        User user = userService.getUserByUserId(1);
        System.out.println("从服务端得到的用户: " + user);

        // 插入一个用户并获取返回的 ID
        User newUser = new User(2, "Lixiang", true);
        Integer newUserId = userService.insertUserId(newUser);
        System.out.println("插入的用户 ID: " + newUserId);
    }
}