package com.light.mvc.annotations;

import java.lang.annotation.*;

/**
 * Created on 2018/4/23.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryParam {

    String value();

    boolean required() default false;
}
