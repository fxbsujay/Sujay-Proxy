package com.susu.proxy.server.web.annotation;

import java.lang.annotation.*;

/**
 * <p>Description: Rest风格的Controller</p>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestController {

    String value() default "";

}
