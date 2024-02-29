package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行功能字段自动填充处理
 */
@Target(ElementType.METHOD)     //指明了修饰的这个注解的使用范围，即被描述的注解可以用在哪里。
@Retention(RetentionPolicy.RUNTIME)//指明修饰的注解的生存周期，即会保留到哪个阶段。RUNTIME： 运行级别保留，编译后的class文件中存在，在jvm运行时保留，可以被反射调用。
public @interface AutoFill {
    //数据库操作类型：UPDATE INSERT
    OperationType value();
}
