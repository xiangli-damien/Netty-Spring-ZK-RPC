package com.xiangli.server.serviceregister;

import java.net.InetSocketAddress;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/03 15:08
 */
public interface ServiceRegister {

    /*
    * @Description: 注册服务
    * @Param: [serviceName, serviceAddress]
    * @return: void
    * @function: register service with service name and address
     */
    void register(String serviceName, InetSocketAddress serviceAddress);
}
