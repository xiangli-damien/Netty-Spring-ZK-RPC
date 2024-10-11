package com.xiangli.client.serviceCenter.balance.impl;

import com.xiangli.client.serviceCenter.balance.LoadBalance;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/07 23:39
 */
@Slf4j
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String balance(List<String> addressList) {
        Random random=new Random();
        int choose = random.nextInt(addressList.size());
        log.info("Load balance choose server: " + choose);
        return addressList.get(choose);
    }
    public void addNode(String node){} ;
    public void delNode(String node){};
}
