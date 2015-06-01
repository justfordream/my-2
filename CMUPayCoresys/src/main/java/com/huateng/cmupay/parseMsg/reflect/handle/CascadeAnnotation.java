package com.huateng.cmupay.parseMsg.reflect.handle;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CascadeAnnotation {
	
	
	String cascadeField() default ""; 
	String cascadePath() default ""; 
	String cascadeMethod() ; 
	String method() ; 
	String constraintClazzPath();
	String constraintClazzName() default ""; ;
	String constraintUniqueMark() default "";
 
}