package com.xiangli.client.rpcclient;

import com.xiangli.common.message.RpcRequest;
import com.xiangli.common.message.RpcResponse;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/01 10:10
 */
public interface RpcClient {

    RpcResponse sendRequest(RpcRequest request);
}
