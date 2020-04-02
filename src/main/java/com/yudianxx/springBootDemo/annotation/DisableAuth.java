package com.yudianxx.springBootDemo.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author huangyongwen
 * @date 2020/4/2
 * @Description 非鉴权注解，Controller使用此注解，过滤器将不拦截
 */

//四种元注解：@Retention @Target @Documented @Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface DisableAuth {

}


