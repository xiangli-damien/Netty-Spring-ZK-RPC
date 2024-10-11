package com.xiangli.client.transport.unprocessed;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/10 12:12
 */
import com.xiangli.common.message.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class UnprocessedRequests {

    private static final Map<String, CompletableFuture<RpcResponse>> unprocessedRequests = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        unprocessedRequests.put(requestId, future);
    }

    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = unprocessedRequests.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            log.warn("No matching future for requestId: {}", rpcResponse.getRequestId());
        }
    }

    public void remove(String requestId) {
        unprocessedRequests.remove(requestId);
    }
}