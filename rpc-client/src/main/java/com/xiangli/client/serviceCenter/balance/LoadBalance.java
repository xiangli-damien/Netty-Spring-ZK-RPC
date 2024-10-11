package com.xiangli.client.serviceCenter.balance;

import java.util.List;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/07 23:31
 */
public interface LoadBalance {
    // balance: 给服务地址列表，根据不同的负载均衡策略选择一个
    String balance(List<String> addressList);

    // addNode: 添加节点
    void addNode(String node);

    // delNode: 删除节点
    void delNode(String node);
}
