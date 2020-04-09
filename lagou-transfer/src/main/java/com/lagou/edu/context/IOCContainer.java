package com.lagou.edu.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName IOCContainer
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/9 10:30
 **/
public class IOCContainer {
    /**
     * 存储对象容器
     */
    private static Map<String, Object> singleObject = new ConcurrentHashMap<>(256);


    public Map<String, Object> getSingleObject() {
        return singleObject;
    }


    /**
     * 判断是否含有该key
     *
     * @param beanid
     * @return
     */
    public boolean isContains(String beanid) {
        return singleObject.containsKey(beanid);
    }

    /**
     * 封装参数
     *
     * @param beanid
     * @param o
     */
    public void packObject(String beanid, Object o) {
        singleObject.put(beanid, o);
    }


    /**
     * 根据id获取实例化对象
     *
     * @param id
     * @return
     */

    public static Object getBean(String id) {
        return singleObject.get(id);
    }


}
