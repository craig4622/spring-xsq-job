package com.lagou.edu.annotation;

import java.lang.annotation.*;

/**
 * @ClassName Controller
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/9 10:55
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {

    //引用id 若为空
    String value() default "";

    String url();
}
