package com.lagou.edu.utils;

/**
 * @ClassName StringUtils
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/8 16:11
 **/
public class StringUtils {


    /**
     * 首字母小写返回字符串
     *
     * @param beanName
     * @return
     */
    public static String toLowerCaseFirstOne(String beanName) {
        return beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
    }
}
