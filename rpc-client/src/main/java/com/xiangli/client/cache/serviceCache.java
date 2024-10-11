package com.xiangli.client.cache;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/07 17:20
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
public class serviceCache {

    /**
    * cache 用来存储服务名和服务地址的映射关系
    * @key serviceName 服务名
    * @value addressList 服务提供者列表
     */
    private static Map<String, List<String>> cache=new HashMap<>();

    /**
    * addServiceToCache 用来添加服务
    * @param serviceName 服务名
    * @param address 服务地址
     */
    public void addServiceToCache(String serviceName,String address){
        if(cache.containsKey(serviceName)){
            List<String> addressList = cache.get(serviceName);
            addressList.add(address);
            log.info("Add service: " + serviceName + " and address: " + address + " to local cache");
        }else {
            List<String> addressList=new ArrayList<>();
            addressList.add(address);
            cache.put(serviceName,addressList);

        }
    }

    /**
    * replaceServiceAddress 用来修改服务地址
    * @param serviceName 服务名
    * @param oldAddress 旧地址
    * @param newAddress 新地址
     */
    public void replaceServiceAddress(String serviceName,String oldAddress,String newAddress){
        if(cache.containsKey(serviceName)){
            List<String> addressList=cache.get(serviceName);
            addressList.remove(oldAddress);
            addressList.add(newAddress);
        }else {
            log.info("Service does not exist");
        }
    }

    /**
     * getServcieFromCache 用来从缓存中取服务地址
     * @param serviceName 服务名
     * @return 服务地址列表
     */
    public  List<String> getServcieFromCache(String serviceName){
        log.info("Get service address from local cache" + serviceName);
        if(!cache.containsKey(serviceName)) {
            log.info("Service does not exist");
            return null;
        }
        List<String> a=cache.get(serviceName);
        log.info("Service address list: " + a);
        return a;
    }


    /**
     * delete 用来从缓存中删除服务地址
     * @param serviceName 服务名
     * @param address  服务地址
     */
    public void delete(String serviceName,String address){
        List<String> addressList = cache.get(serviceName);
        addressList.remove(address);
        log.info("Delete service: " + serviceName + " and address: " + address + " from local cache");
    }
}
