package com.yudianxx.springBootDemo.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author huangyongwen
 * @date 2020/3/25
 * @Description
 */
@Component
@Slf4j
public class InitCache implements ApplicationRunner {

    @Autowired
    private ICacheService cache;

    @Value("${meizitu.needInitCache}")
    private int needInitCacheFlag;

    @Override
    @Async
    public void run(ApplicationArguments arg) throws Exception {

        if (needInitCacheFlag == 1) {
            // 加载字典表缓存
//        cache.initDictCache();
            // 加载表达式缓存
            cache.initExpressionCache();
        }

    }
}
