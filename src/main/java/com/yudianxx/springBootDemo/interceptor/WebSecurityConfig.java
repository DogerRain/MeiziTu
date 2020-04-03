package com.yudianxx.springBootDemo.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@Slf4j
public class WebSecurityConfig extends WebMvcConfigurerAdapter {
    @Bean
    public MyInterceptor getSessionInterceptor() {
        return new MyInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /*调用我们创建的SessionInterceptor。
         * addPathPatterns("/**)的意思是这个链接下的都要进入到SessionInterceptor里面去执行
         * excludePathPatterns("/login")的意思是login的url可以不用进入到SessionInterceptor中，直接
         * 放过执行。
         *
         * 注意：如果像注释那样写是不可以的。这样等于是创建了多个Interceptor。而不是只有一个Interceptor
         * 所以这里有个大坑，搞了很久才发现问题。
         *
         * */


        //登录不要拦截噢
        log.info("进入自定义拦截器");
        MyInterceptor sessionInterceptor = new MyInterceptor();
        registry.addInterceptor(sessionInterceptor).addPathPatterns("/a/")
                .excludePathPatterns("/userAPI/login", "/css/**", "/js/**", "/img/**", "/mapper/**");

//        registry.addInterceptor(sessionInterceptor).excludePathPatterns("/login");
//        registry.addInterceptor(sessionInterceptor).excludePathPatterns("/verify");

        super.addInterceptors(registry);
    }

    /**
     *  解决跨域的问题
     * @param registry
     */

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")

                .allowedOrigins("*")

                .allowCredentials(true)

                .allowedMethods("GET", "POST", "DELETE", "PUT")

                .maxAge(3600);

    }

}