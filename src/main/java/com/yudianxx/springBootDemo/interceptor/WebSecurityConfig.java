package com.yudianxx.springBootDemo.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class WebSecurityConfig extends WebMvcConfigurerAdapter {
    @Bean
    public MyInterceptor getSessionInterceptor() {
        return new MyInterceptor();
    }

    /**
     *  springboot初始化会进入这个方法，这里定义的url规则，Interceptor不会拦截
     * @param registry
     */
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
        log.info(" 拦截器 初始化");
        MyInterceptor sessionInterceptor = new MyInterceptor();
        registry.addInterceptor(sessionInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/userAPI/**", "/css/**", "/js/**", "/img/**", "/mapper/**");

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

    private CorsConfiguration addcorsConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        List<String> list = new ArrayList<>();
        list.add("*");
        corsConfiguration.setAllowedOrigins(list);
    /*
    // 请求常用的三种配置，*代表允许所有，当时你也可以自定义属性（比如header只能带什么，只能是post方式等等）
    */
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        return corsConfiguration;
    }
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", addcorsConfig());
        return new CorsFilter(source);
    }

}