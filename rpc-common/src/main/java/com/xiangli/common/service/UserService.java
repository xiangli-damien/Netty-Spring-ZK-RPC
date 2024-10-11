package com.xiangli.common.service;

import com.xiangli.common.pojo.User;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/30 10:54
 */
public interface UserService {
    // 客户端通过这个接口调用服务端的实现类
    User getUserByUserId(Integer id);
    //新增一个功能
    Integer insertUserId(User user);

}
