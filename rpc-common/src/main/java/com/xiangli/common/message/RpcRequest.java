package com.xiangli.common.message;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/29 21:35
 */
@Data
// RPC请求, 客户端发送给服务端的请求对象
public class RpcRequest implements Serializable {

    // 消息请求ID
    private String requestId;

    // 服务类名，客户端只知道接口
    private String interfaceName;
    // 调用的方法名
    private String methodName;
    // 参数列表
    private Object[] params;
    // 参数类型
    private Class<?>[] paramsType;

    public RpcRequest() {
        this.requestId = UUID.randomUUID().toString();
    }

    // this()调用无参构造函数
    public RpcRequest(String interfaceName, String methodName, Object[] params, Class<?>[] paramsType) {
        this();
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.params = params;
        this.paramsType = paramsType;
    }
}