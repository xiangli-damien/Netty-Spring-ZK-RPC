package com.xiangli.client.serviceCenter;

import java.net.InetSocketAddress;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/03 16:24
 */

public interface ServiceCenter {

    InetSocketAddress serviceDiscovery(String serviceName);

    boolean checkMethodRetry(String serviceName, String methodName);
}
