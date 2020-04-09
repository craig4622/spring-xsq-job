package com.lagou.edu.factory;

/**
 * @author xsq
 * <p>
 * 工厂类，生产对象（使用反射技术）
 */
public interface BeanFactory {

    /**
     * 根据id获取实例化对象
     *
     * @param id
     * @return
     */
    Object getBean(String id);

}
