package com.xiangli.common.message;

import java.io.Serializable;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/29 21:35
 */

// RPC请求, 客户端发送给服务端的请求对象
public class RpcRequest implements Serializable {

    // 服务类名，客户端只知道接口
    private String interfaceName;
    // 调用的方法名
    private String methodName;
    // 参数列表
    private Object[] params;
    // 参数类型
    private Class<?>[] paramsType;

    public RpcRequest() {}

    public RpcRequest(String interfaceName, String methodName, Object[] params, Class<?>[] paramsType) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.params = params;
        this.paramsType = paramsType;
    }

    // Getters and Setters
    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Class<?>[] getParamsType() {
        return paramsType;
    }

    public void setParamsType(Class<?>[] paramsType) {
        this.paramsType = paramsType;
    }
}
