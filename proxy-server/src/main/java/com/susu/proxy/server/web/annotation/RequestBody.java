package com.susu.proxy.server.web.annotation;

import java.lang.annotation.*;

/**
 * <p>Description: 请求参数JSON格式</p>
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestBody {
}
