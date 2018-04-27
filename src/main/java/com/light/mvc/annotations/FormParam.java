package com.light.mvc.annotations;

import java.lang.annotation.*;

/**
 * Created on 2018/4/26.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormParam {

    String value();

    boolean required() default false;
}
