package com.xiangli.server.ratelimit.impl;

import com.xiangli.server.ratelimit.RateLimit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/09 09:40
 */
@Slf4j
public class TokenBucketRateLimitImpl implements RateLimit {

    // Token generation rate
    private static int RATE;

    // Bucket capacity
    private static int CAPACITY;

    // Current bucket capacity
    private volatile int curCapcity;

    // Timestamp
    private volatile long timeStamp = System.currentTimeMillis();

    public TokenBucketRateLimitImpl(int rate, int capacity) {
        RATE = rate;
        CAPACITY = capacity;
        curCapcity = capacity;
    }

    @Override
    public synchronized boolean getToken() {
        // If there is still remaining in the current bucket, return directly
        if (curCapcity > 0) {
            log.info("Server: TokenBucketRateLimitImpl getToken curCapcity: " + curCapcity);
            curCapcity--;
            return true;
        }

        // If the bucket is empty
        long current = System.currentTimeMillis();
        // If the time since the last request is greater than RATE
        if (current - timeStamp >= RATE) {
            log.info("Server: TokenBucketRateLimitImpl getToken current-timeStamp: " + (current - timeStamp));
            // Calculate the tokens generated during this time interval, if >2, add the capacity of the bucket (calculated tokens-1)
            if ((current - timeStamp) / RATE >= 2) {
                curCapcity += (int) (current - timeStamp) / RATE - 1;
            }
            // Keep the bucket token capacity <=10
            if (curCapcity > CAPACITY) {
                curCapcity = CAPACITY;
            }
            // Refresh the timestamp for this request
            timeStamp = current;
            return true;
        }
        // Cannot get, return false
        return false;
    }
}
