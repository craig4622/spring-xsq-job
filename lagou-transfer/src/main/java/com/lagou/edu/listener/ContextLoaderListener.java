package com.lagou.edu.listener;

import com.lagou.edu.context.IOCContainer;
import com.lagou.edu.factory.BeanFactoryRegister;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @ClassName MyListener
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/8 15:33
 **/
public class ContextLoaderListener implements ServletContextListener {


    /**
     * 根据监听器在项目启动的时候获取注解的类
     *
     * @param servletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //初始化容器
        IOCContainer iocContainer = new IOCContainer();
        //实例化变量,添加事务
        BeanFactoryRegister beanFactoryRegister = new BeanFactoryRegister(iocContainer, servletContextEvent);
        beanFactoryRegister.init();

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

}
