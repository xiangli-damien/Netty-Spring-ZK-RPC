package com.xiangli.common.annotation;

/**
 * @author lixiang
 * @version 1.0
 * @create 2024/10/02 16:05
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

@Target(ElementType.TYPE)  // 作用于类
@Retention(RetentionPolicy.RUNTIME)  // 注解在运行时可见
@Component  // 标识为Spring组件
public @interface Remote {
    String value() default "";  // 可以选择指定服务名称
}