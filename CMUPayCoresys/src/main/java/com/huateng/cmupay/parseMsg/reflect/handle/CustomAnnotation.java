package com.huateng.cmupay.parseMsg.reflect.handle;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.FIELD })
// 用于字段
@Retention(RetentionPolicy.RUNTIME)
// 在运行时加载到Annotation到JVM中
public @interface CustomAnnotation {
	boolean type() default false;// 定义一个具有默认值的Class型成员:true为自定义类类型,false为java常用的数据
	
	char power() default '1';// 0 ：不拼接，1：为空不拼接，2：为空拼接

	String path(); // 定义个字符串,元素路径
   
}