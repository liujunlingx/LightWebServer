package com.light.mvc.annotations;

import com.light.http.HttpMethod;

import java.lang.annotation.*;

/**
 * Created on 2018/4/23.
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebPath {

    String[] value() default {};

    HttpMethod[] method() default {};

    String[] headers() default {};

    String[] params() default {};
}
