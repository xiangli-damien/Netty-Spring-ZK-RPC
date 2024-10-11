package com.xiangli.common.annotation;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/02 16:04
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Target(ElementType.FIELD)  // 只作用于字段
@Retention(RetentionPolicy.RUNTIME)  // 注解在运行时可见
@Component  // 标识为Spring组件
public @interface RemoteInvoke {
}
