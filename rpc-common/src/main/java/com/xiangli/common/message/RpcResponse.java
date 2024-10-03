package com.xiangli.common.message;

import java.io.Serializable;
/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/29 21:35
 */



// RPC响应, 服务端返回给客户端的响应对象
public class RpcResponse implements Serializable {
    // 状态码
    private int code;
    // 状态信息
    private String message;
    // 具体数据
    private Object data;

    public RpcResponse() {}

    public RpcResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 静态方法构建成功信息
    public static RpcResponse success(Object data) {
        return new RpcResponse(200, "Success", data);
    }

    // 静态方法构建失败信息
    public static RpcResponse fail() {
        return new RpcResponse(500, "Server Error", null);
    }

    // 新增带错误消息的 fail 方法
    public static RpcResponse fail(String errorMessage) {
        return new RpcResponse(500, errorMessage, null);
    }

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
