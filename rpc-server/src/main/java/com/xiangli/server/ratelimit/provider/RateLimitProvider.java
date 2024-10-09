package com.xiangli.server.ratelimit.provider;

import com.xiangli.server.ratelimit.RateLimit;
import com.xiangli.server.ratelimit.impl.TokenBucketRateLimitImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/09 10:04
 */
@Slf4j
@Component
public class RateLimitProvider {

    // rate limit map is used to store the rate limit of each interface
    private Map<String, RateLimit> rateLimitMap = new HashMap<>();

    // get rate limit of the interface
    public RateLimit getRateLimit(String interfaceName){
        // if the rate limit of the interface is not in the map, create a new rate limit implements and put it in the map
        if(!rateLimitMap.containsKey(interfaceName)){
            // create a new rate limit implements
            RateLimit rateLimit=new TokenBucketRateLimitImpl(100,10);
            // put the rate limit in the map
            rateLimitMap.put(interfaceName,rateLimit);
            log.info("Server: Created rate limit for interface: " + interfaceName);
            return rateLimit;
        }
        return rateLimitMap.get(interfaceName);
    }

}
