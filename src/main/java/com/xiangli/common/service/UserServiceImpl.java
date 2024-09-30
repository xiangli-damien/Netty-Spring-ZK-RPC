package com.xiangli.common.service;

import com.xiangli.common.pojo.User;

import java.util.Random;
import java.util.UUID;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 11:01
 */

public class UserServiceImpl implements UserService {
    @Override
    public User getUserByUserId(Integer id) {
        System.out.println("客户端查询了"+id+"的用户");
        // 模拟从数据库中取用户的行为
        Random random = new Random();
        User user = new User();
        user.setUserName(UUID.randomUUID().toString());
        user.setId(id);
        user.setSex(random.nextBoolean());
        return user;
    }

    @Override
    public Integer insertUserId(User user) {
        System.out.println("插入数据成功: " + user.getUserName());
        return user.getId();
    }
}