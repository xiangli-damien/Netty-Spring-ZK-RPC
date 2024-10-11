package com.xiangli.server.ratelimit;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/09 09:39
 */
public interface RateLimit {
    //get access permission
    boolean getToken();
}

