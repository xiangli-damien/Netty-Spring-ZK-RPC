package com.xiangli.client.serviceCenter.impl;

import com.xiangli.client.cache.serviceCache;
import com.xiangli.client.serviceCenter.ServiceCenter;
import com.xiangli.client.serviceCenter.ZKWatcher.watchZK;
import com.xiangli.client.serviceCenter.balance.impl.ConsistentHashBalance;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.stereotype.Component;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/03 16:25
 */
@Slf4j
@Component
@Data
public class ZKServiceCenter implements ServiceCenter {

    private CuratorFramework client;

    private serviceCache cache;

    private static final String ROOT_PATH = "MyRPC";

    public ZKServiceCenter(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        this.client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .namespace(ROOT_PATH)
                .sessionTimeoutMs(40000)
                .retryPolicy(retryPolicy)
                .build();

        this.client.start();
        log.info("zookeeper connection successful");
        // 初始化本地缓存
        cache = new serviceCache();
        log.info("Local cache initialized");
        // 初始化watcher
        watchZK watchZK = new watchZK(client, cache);
        try {
            watchZK.watchToUpdate(ROOT_PATH);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("Failed to start the watch service");
        }

        // 主动加载当前已有的服务到缓存
        try {
            List<String> services = client.getChildren().forPath("/");
            for (String serviceName : services) {
                List<String> addresses = client.getChildren().forPath("/" + serviceName);
                for (String address : addresses) {
                    cache.addServiceToCache(serviceName, address);
                }
            }
        } catch (Exception e) {
            log.error("Failed to initialize cache with existing services", e);
        }

    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {

        try {
            // get all the children nodes of the service name(list of host:port) from cache
            List<String> addressList = cache.getServcieFromCache(serviceName);
            log.info("Service discovery from cache: [{}]", addressList);
            // if the cache is empty, get the children nodes from zookeeper
            if (addressList == null) {
                addressList = client.getChildren().forPath("/" + serviceName);
                log.info("Service discovery from zookeeper: [{}]", addressList);
            }

            if (addressList == null || addressList.isEmpty()) {
                log.error("No addresses found for service: {}", serviceName);
                return null;
            }
            String address = new ConsistentHashBalance().balance(addressList);
            log.info("Service discovery successful: [{}]", address);
            return parseAddress(address);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Service discovery failed");
        }
        return null;
    }

    private InetSocketAddress parseAddress(String address) {
        // split the string IP address to a InetSocketAddress object
        String[] splitAddress = address.split(":");
        // return the InetSocketAddress object
        return new InetSocketAddress(splitAddress[0], Integer.parseInt(splitAddress[1]));
    }
}
