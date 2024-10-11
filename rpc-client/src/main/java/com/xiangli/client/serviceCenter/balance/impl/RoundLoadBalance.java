package com.xiangli.client.serviceCenter.balance.impl;

import com.xiangli.client.serviceCenter.balance.LoadBalance;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/07 23:41
 */
@Slf4j
public class RoundLoadBalance implements LoadBalance {
    private int choose=-1;
    @Override
    public String balance(List<String> addressList) {
        choose++;
        choose=choose%addressList.size();
        log.info("Load balance choose server: " + choose);
        return addressList.get(choose);
    }
    public void addNode(String node) {};
    public void delNode(String node){};
}
