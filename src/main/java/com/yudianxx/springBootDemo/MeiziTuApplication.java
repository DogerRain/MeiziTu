package com.yudianxx.springBootDemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

//Spring Boot Application的入口点是包含@SpringBootApplication注释的类。
//		该类应具有运行Spring Boot应用程序的主要方法。 @SpringBootApplication注释包括自动配置，
//		组件扫描和Spring Boot配置。如果将@SpringBootApplication批注添加到类中，则无需添加@EnableAutoConfiguration，
//@ComponentScan和@SpringBootConfiguration批注。@SpringBootApplication注释包括所有其他注释。
@SpringBootApplication
//扫描@Scheduled，使用定时任务
@EnableScheduling
@RestController
//@EnableEurekaClient
//使用Hystrix隔离服务
@EnableAsync //开启异步加载没空 即扫描 @Async
//@MapperScan("com.yudianxx.springBootDemo.mapper")
//@EnableAutoConfiguration(exclude = {
//		org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class
//})
//@ImportResource(locations = {"classpath:/mybatis/spring-mybatis-plus.xml"})
@Slf4j
public class MeiziTuApplication implements CommandLineRunner {
    //public class SpringBootDemoApplication extends   SpringBootServletInitializer {
//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//		return application.sources(SpringBootDemoApplication.class);
//	}

    public static void main(String[] args) {
        SpringApplication.run(MeiziTuApplication.class, args);
    }

    //	spring启动
    @Override
    public void run(String... arg0) throws Exception {
        log.info("springboot服务已启动......");
    }
    //@Bean 的话一定是一个对象，不能是一个void
    @Bean(name = "methods")
    public String method1(){
        System.out.println("1111111111111111111111111111111");
        return null;
    }
}

