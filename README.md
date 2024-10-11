# Netty-ZK-RPC
A lightweight RPC framework built with Netty and ZooKeeper for efficient service registration, discovery, and high-performance communication in distributed systems.

# 功能点
- 基于 Netty 高性能网络通信框架
- 自定义消息协议结构，消息编码/解码器和序列化器，解决TCP粘包拆包问题
- 实现多种序列化算法（JDK、JSON、PROTOSTUFF）
- 基于 Zookeeper 实现服务注册与发现
- 在客户端实现服务本地缓存监听与动态更新
- 集成 Spring 实现JDK和CGLIB动态代理，实现基于自定义注解的服务调用
- 为客户端提供三种负载均衡算法（轮询、随机、一致性哈希）
- 使用Guava-Retry框架对白名单幂等性服务提供超时重试机制
- 实现长连接与心跳检测机制
- 客户端实现Channel多路复用和异步调用机制，服务端设置线程池提高并发性能
- 服务端实现故障/限流降级