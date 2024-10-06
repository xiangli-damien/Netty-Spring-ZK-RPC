package com.xiangli.client.servicecenter;

import java.net.InetSocketAddress;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/03 16:24
 */
public interface ServiceCenter {

    InetSocketAddress serviceDiscovery(String serviceName);
}
