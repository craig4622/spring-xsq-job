package com.lagou.edu.factory;

import com.lagou.edu.context.IOCContainer;

import javax.servlet.ServletContextEvent;

/**
 * @ClassName BeanFactoryRegister
 * @Description TODO
 * @Author xsq
 * @Date 2020/4/9 10:35
 **/
public class BeanFactoryRegister {

    private IOCContainer iOCContainer;

    private ServletContextEvent servletContextEvent;

    public BeanFactoryRegister(IOCContainer iOCContainer, ServletContextEvent servletContextEvent) {
        this.iOCContainer = iOCContainer;
        this.servletContextEvent = servletContextEvent;
    }

    public void init() {
        AnnotationBeanFactory annotationBeanFactory = new AnnotationBeanFactory(iOCContainer, servletContextEvent);
        //初始化实例对象
        annotationBeanFactory.buildInstance();
        //进行Autowired注入
        annotationBeanFactory.injectionProperty();
        //进行事务处理
        annotationBeanFactory.initTransaction();
    }
}
