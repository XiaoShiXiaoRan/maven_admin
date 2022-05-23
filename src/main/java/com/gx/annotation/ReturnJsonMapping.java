package com.gx.annotation;


import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.annotation.*;

@Target({ElementType.METHOD})//指定主键可以使用的位置
@Retention(RetentionPolicy.RUNTIME)
@Documented
//指定@RequestMapping功能 指定了 返回json格式
@RequestMapping(
        produces = "application/json;charset=utf-8"
)
@ResponseBody
//@interface 声明注解的关键字 类似于class,interface
public @interface ReturnJsonMapping {
    @AliasFor(
            annotation = RequestMapping.class
    )
    String name() default "";

    @AliasFor(
            annotation = RequestMapping.class
    )
    String[] value() default {};

    @AliasFor(
            annotation = RequestMapping.class
    )
    String[] path() default {};

    @AliasFor(
            annotation = RequestMapping.class
    )
    String[] params() default {};

    @AliasFor(
            annotation = RequestMapping.class
    )
    String[] headers() default {};

    @AliasFor(
            annotation = RequestMapping.class
    )
    String[] consumes() default {};

    @AliasFor(
            annotation = RequestMapping.class
    )
    RequestMethod[] method() default {};
}
