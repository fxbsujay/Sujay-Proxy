package com.susu.proxy.server.web.annotation;

import java.lang.annotation.*;


/**
 * <p>Description: 自动注入</p>
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {

    String value() default "";

}
