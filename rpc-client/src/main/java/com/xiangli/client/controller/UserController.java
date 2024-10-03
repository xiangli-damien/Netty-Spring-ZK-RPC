package com.xiangli.client.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.xiangli.common.annotation.RemoteInvoke;
import com.xiangli.common.pojo.User;
import com.xiangli.common.service.UserService;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/02 16:35
 */

@Slf4j
@Component
public class UserController {

    // 自定义注解
    // 告诉Spring需要代理这个对象
    @RemoteInvoke
    private UserService userService;

    public User getUserByUserId(Integer id) {
        // 发起远程调用，获取用户信息
        log.info("Client: calling getUserByUserId with id = " + id);
        return userService.getUserByUserId(id);
    }

    public Integer insertUserId(User user) {
        // 发起远程调用，插入用户信息
        log.info("Client: calling insertUserId with user = " + user);
        return userService.insertUserId(user);
    }


}
