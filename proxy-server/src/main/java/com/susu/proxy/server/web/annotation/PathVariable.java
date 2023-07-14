package com.susu.proxy.server.web.annotation;

import java.lang.annotation.*;

/**
 * <p>Description: 路径参数</p>
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable {

    /**
     * 参数值
     *
     * @return 参数值
     */
    String value() default "";

}
