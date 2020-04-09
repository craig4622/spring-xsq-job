package com.lagou.edu.annotation;

import java.lang.annotation.*;

/**
 * @ClassName Component
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/9 10:55
 **/
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

    String value() default "";
}
