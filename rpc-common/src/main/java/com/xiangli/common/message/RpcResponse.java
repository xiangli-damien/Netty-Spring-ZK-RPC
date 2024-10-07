package com.xiangli.common.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * @author lixiang
 * @version 1.0
 * @create 2024/09/29 21:35
 */


@Data
// RPC响应, 服务端返回给客户端的响应对象
public class RpcResponse implements Serializable {

    // 状态码 (例如: 200 表示成功，500 表示服务器错误)
    private int code;

    // 状态消息 (用于描述具体状态的信息)
    private String message;

    // 返回的数据对象 (请求成功时的返回数据)
    private Object data;

    //更新：加入传输数据的类型，以便在自定义序列化器中解析
    private Class<?> dataType;

    // 默认构造方法
    public RpcResponse() {}

    /**
     * 带参数的构造方法
     *
     * @param code 状态码
     * @param message 状态信息
     * @param data 返回数据
     */
    public RpcResponse(int code, String message, Object data, Class<?> dataType) {
        this.code = code;
        this.message = message;
        this.data = data;
        if (data != null) {
            this.dataType = data.getClass();
        }
    }



    /**
     * 静态方法用于构建成功的响应
     *
     * @param data 请求成功时返回的数据
     * @return RpcResponse 包含状态码 200 和成功数据
     */
    public static RpcResponse success(Object data) {
        return new RpcResponse(200, "Success", data, data.getClass());
    }

    /**
     * 静态方法用于构建失败的响应
     *
     * @return RpcResponse 包含状态码 500 和默认错误信息
     */
    public static RpcResponse fail() {
        return new RpcResponse(500, "Server Error", null, null);
    }

    /**
     * 静态方法用于构建带有错误消息的失败响应
     *
     * @param errorMessage 错误信息
     * @return RpcResponse 包含状态码 500 和具体的错误信息
     */
    public static RpcResponse fail(String errorMessage) {
        return new RpcResponse(500, errorMessage, null, null);
    }
}