package com.xiangli.client.servicecenter;

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
public class ZKServiceCenter implements ServiceCenter{

    private CuratorFramework client;

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
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            // get all the children nodes of the service name(list of host:port)
            List<String> addresses = client.getChildren().forPath("/" + serviceName);
            // return the first one
            // Introducing the load balancing in the future.
            String address = addresses.get(0);
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
