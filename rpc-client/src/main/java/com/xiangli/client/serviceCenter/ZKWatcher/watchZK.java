package com.xiangli.client.serviceCenter.ZKWatcher;

import com.xiangli.client.cache.serviceCache;
import com.xiangli.common.annotation.Remote;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.springframework.stereotype.Component;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/07 16:43
 */
@Slf4j
public class watchZK {

    // curator 提供的zookeeper客户端
    private CuratorFramework client;
    //本地缓存
    serviceCache cache;

    /*
    * watchzK 构造函数
    * @param client zookeeper客户端
    * @param cache 本地缓存
     */

    public watchZK(CuratorFramework client,serviceCache cache){
        log.info("watchZK is constructed");
        this.client=client;
        this.cache=cache;
    }

    /**
     * watchToUpdate: 监听指定路径下节点的变化（包括节点的创建、更新和删除）,并根据变化情况动态更新客户端的本地缓存
     * @param path 监听的节点路径
     */

    public void watchToUpdate(String path) throws InterruptedException {
        // 构建 CuratorCache 对象,并指定监听的节点路径
        CuratorCache curatorCache = CuratorCache.build(client, "/");
        // 添加监听器
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            /**
             * event: 节点变化事件处理
             * @param type 事件类型
             * @param childData 更新前的节点状态
             * @param childData1 更新后的节点状态
             */
            @Override
            public void event(Type type, ChildData childData, ChildData childData1) {

                // 删除节点时：节点被删除，不存在 更新后节点 ，所以第三个参数为 null
                // 节点创建时没有赋予值 create /curator/app1 只创建节点，在这种情况下，更新前节点的 data 为 null，获取不到更新前节点的数据
                switch (type.name()) {
                    // 创建节点时：节点刚被创建，不存在 更新前节点 ，所以第二个参数为 null
                    // 监听器第一次执行时节点存在也会触发次事件
                    // 将新注册的服务加入到本地缓存中
                    case "NODE_CREATED":
                        String[] pathList= pasrePath(childData1);
                        // 如果节点路径长度小于等于2，说明节点路径不符合要求，直接跳过
                        if(pathList.length<=2) break;
                        else {
                            // 获取服务名和地址
                            String serviceName=pathList[1];
                            String address=pathList[2];
                            //将新注册的服务加入到本地缓存中
                            cache.addServiceToCache(serviceName,address);
                        }
                        break;

                    // 节点更新
                    case "NODE_CHANGED":
                        if (childData.getData() != null) {
                            log.info("data before modification: " + new String(childData.getData()));
                        } else {
                            log.info("The node is assigned for the first time!");
                        }
                        String[] oldPathList=pasrePath(childData);
                        String[] newPathList=pasrePath(childData1);
                        cache.replaceServiceAddress(oldPathList[1],oldPathList[2],newPathList[2]);
                        log.info("data after modification: " + new String(childData1.getData()));
                        break;

                    // 节点删除,节点被删除，不存在 更新后节点 ，所以第三个参数为 null
                    case "NODE_DELETED":

                        String[] pathList_d= pasrePath(childData);
                        if(pathList_d.length<=2) break;
                        else {
                            String serviceName=pathList_d[1];
                            String address=pathList_d[2];
                            //将新注册的服务加入到本地缓存中
                            cache.delete(serviceName,address);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        //开启监听
        curatorCache.start();
    }
    //解析节点对应地址
    public String[] pasrePath(ChildData childData){
        //获取更新的节点的路径
        String path=new String(childData.getPath());
        //按照格式 ，读取
        return path.split("/");
    }
}