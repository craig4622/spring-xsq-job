package com.lagou.edu.annotation;

import java.lang.annotation.*;

/**
 * @ClassName Autowired
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/9 10:55
 **/
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {

    String value();
}
