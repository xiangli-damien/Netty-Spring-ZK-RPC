package com.xiangli.common.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/06 18:17
 */
@Getter
@AllArgsConstructor
public enum MessageType {
    // REQUEST(0) 语法的含义是调用了MessageType的构造函数，传入0，然后将返回值赋给REQUEST
    REQUEST(0),RESPONSE(1);
    // code是一个私有变量，只能通过get方法获取
    private int code;
}
