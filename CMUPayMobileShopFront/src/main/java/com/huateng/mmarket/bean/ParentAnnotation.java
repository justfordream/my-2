package com.huateng.mmarket.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target( { ElementType.FIELD })
//用于字段
@Retention(RetentionPolicy.RUNTIME)
//在运行时加载到Annotation到JVM中

public @interface ParentAnnotation {

}
