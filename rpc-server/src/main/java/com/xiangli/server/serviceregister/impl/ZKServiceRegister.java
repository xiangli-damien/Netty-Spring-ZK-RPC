package com.xiangli.server.serviceregister.impl;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/03 15:01
 */
import com.xiangli.server.serviceregister.ServiceRegister;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component
public class ZKServiceRegister implements ServiceRegister {
    // client offer by curator. CuratorFramework是Curator的核心类，用于连接ZooKeeper服务器。
    private CuratorFramework client;

    // root path of zookeeper
    private static final String ROOT_PATH = "MyRPC";

    // initialize zookeeper client and connect to zookeeper server
    public ZKServiceRegister() {
        /*
        * ExponentialBackoffRetry是一种重试策略，
        * 重试时间间隔会随着重试次数的增加而增加，
        * 重试时间间隔的计算公式为baseSleepTimeMs * Math.max(1, random.nextInt(1 << (retryCount + 1)))。
         */
        RetryPolicy policy = new ExponentialBackoffRetry(1000, 3);
        /*
        * CuratorFrameworkFactory is a factory class that provides a fluent API style to create a CuratorFramework instance.
        * zookeeper's address is fixed, both service provider and consumer need to connect to it.
        * sessionTimeoutMs is related to tickTime in zoo.cfg
        * sessionTimeoutMs is the session timeout value, which is the time that the client can maintain the connection with the server.
        * zk will readjust the final timeout value according to minSessionTimeout and maxSessionTimeout.
        * They are 2 times and 20 times of tickTime by default.
         */
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(policy)
                .namespace(ROOT_PATH)
                .build();
        // 异步连接，curator客户端与zookeeper服务端建立连接，不会阻塞
        this.client.start();
        log.info("zookeeper connection successful");
    }

    /*
    * @Description: register service
    * @Param: [serviceName, serviceAddress]
    * @return: void
    * @function: register service with service name and address
     */
    @Override
    public void register(String serviceName, InetSocketAddress serviceAddress) {
        try {
            // path address, one / represents one node
            // / + serviceName + / + serviceAddress(hostName:port)
            String path = "/" + serviceName + "/" + getServiceAddress(serviceAddress);
            // check if already create root path for the service, if not, create it
            // withMode(persistent): create a persistent node for serviceName
            // when the service provider is offline, only the address is deleted, not the service name
            if (client.checkExists().forPath(path) == null) {
                if (client.checkExists().forPath("/" + serviceName) == null) {
                    client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/" + serviceName);
                }

                client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
            } else {
                log.warn("Node already exists for service: " + serviceName + " at " + getServiceAddress(serviceAddress));
            }
            log.info("Service: [{}] register success", serviceName);
        } catch (Exception e) {
            log.error("register service fail");
            e.printStackTrace();
        }
    }

    private String getServiceAddress(InetSocketAddress serviceAddress) {
        return serviceAddress.getHostName() + ":" + serviceAddress.getPort();
    }
}
