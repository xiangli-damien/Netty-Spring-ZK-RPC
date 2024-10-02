# Netty-ZK-RPC
A lightweight RPC framework built with Netty and ZooKeeper for efficient service registration, discovery, and high-performance communication in distributed systems.

# 计划功能点
- 基于 Netty 高性能网络通信
- 自定义消息编码/解码器和序列化器，解决TCP粘包拆包问题
- 实现多种序列化算法（JDK、JSON、HESSIAN、KRYO、PROTOSTUFF）
- 基于 Zookeeper 实现本地服务注册与发现
- 在客户端实现服务本地缓存监听与动态更新
- 集成 Spring 实现动态代理，实现基于自定义注解的服务调用
- 为客户端提供四种负载均衡算法（轮询、随机、LRU、一致性哈希）
- 使用Guava-Retry框架对白名单幂等性服务提供超时重试机制
- 实现长连接与心跳检测机制
- 服务端实现故障/限流降级
- 服务设置熔断保护机制