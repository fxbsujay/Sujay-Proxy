package com.susu.proxy.server.web.annotation;

import java.lang.annotation.*;

/**
 * <p>Description: URL参数</p>
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {

    /**
     * 参数值
     *
     * @return 参数值
     */
    String value() default "";
}
