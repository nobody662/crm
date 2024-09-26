package com.msb.crm.annoation;

import java.lang.annotation.*;

/**
 * 自定义注解
 */
@Target({ElementType.METHOD})  // 该注解将作用在方法上
@Retention(RetentionPolicy.RUNTIME) // 运行时保留
@Documented
public @interface RequiredPermission {
    //权限码
    String code() default "";
}
