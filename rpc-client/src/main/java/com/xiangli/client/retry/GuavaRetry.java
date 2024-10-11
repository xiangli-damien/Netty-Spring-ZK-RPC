package com.xiangli.client.retry;

import com.github.rholder.retry.*;
import com.xiangli.client.rpcclient.RpcClient;
import com.xiangli.common.message.RpcRequest;
import com.xiangli.common.message.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/08 20:53
 */
@Slf4j
public class GuavaRetry {
    private RpcClient rpcClient;

    public GuavaRetry(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public RpcResponse sendServiceWithRetry(RpcRequest request) {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                // 无论出现什么异常，都进行重试
                .retryIfException()
                // 返回结果为 500 时进行重试
                .retryIfResult(response -> response == null || response.getCode() == 500)
                // 重试等待策略：等待 2 秒后再进行重试
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                // 重试停止策略：重试达到 3 次
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("Retrying... Attempt number: {}", attempt.getAttemptNumber());
                    }
                })
                .build();
        try {
            return retryer.call(() -> rpcClient.sendRequest(request));
        } catch (Exception e) {
            log.error("RPC request failed after retries", e);
            return RpcResponse.fail(request.getRequestId(), "RPC request failed after retries: " + e.getMessage());
        }
    }
}