package com.xiangli.common.service;

import com.xiangli.common.annotation.Idempotent;
import com.xiangli.common.annotation.Remote;
import com.xiangli.common.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 11:01
 */

@Remote
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Idempotent
    @Override
    public User getUserByUserId(Integer id) {
        log.info("Client: calling getUserByUserId with id = " + id);
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
        log.info("Client: calling insertUserId with user = " + user);
        return user.getId();
    }
}