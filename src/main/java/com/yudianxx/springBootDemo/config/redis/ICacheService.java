package com.yudianxx.springBootDemo.config.redis;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.yudianxx.springBootDemo.mapper.image.ImageHandleMapper;
import com.yudianxx.springBootDemo.model.requestVo.MeiziTuPictureRequestVo;
import com.yudianxx.springBootDemo.model.responseVo.MeiziTuPictureResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huangyongwen
 * @date 2020/3/25
 * @Description
 */
@Service
@Slf4j
public class ICacheService {

    @Autowired
    ImageHandleMapper imageHandleMapper;

    @Qualifier("taskExecutor")
    @Autowired
    TaskExecutor taskExecutor;

    public void initDictCache() {
        log.info("Start Loading normal key_value into redis cache...");
        List<MeiziTuPictureResponseVo> responseVoList = imageHandleMapper.getCompletePicture(new MeiziTuPictureRequestVo());
        if (CollectionUtils.isNotEmpty(responseVoList)) {
            for (MeiziTuPictureResponseVo meiziTuPictureEntity : responseVoList) {
                if (null != meiziTuPictureEntity) {
                    if (StringUtils.isNotBlank(meiziTuPictureEntity.getImageId())
                            && StringUtils.isNotBlank(meiziTuPictureEntity.getTitle())) {
                        RedisUtil.set(RedisKeyPrefix.REDIS_SYSTEM_DICT_KEY + meiziTuPictureEntity.getImageId(),RedisUtil.toJson(meiziTuPictureEntity));

                    }

                }
            }
        }
    }

    /**
     * 线程池方式加载进redis
     */
    public void initExpressionCache() {
        long start = System.currentTimeMillis();
        log.info("Start Loading Expression into redis cache...");

        // 查询表达式全量数据
        List<MeiziTuPictureResponseVo> expressionCacheDtoList = imageHandleMapper.getCompletePicture(new MeiziTuPictureRequestVo());

        // 校验是否有数据需要加载缓存
        if (CollectionUtils.isEmpty(expressionCacheDtoList)) {
            log.info("There Is Nothing Need To Cache!");
            return;
        }

        // 需要处理的任务总数
        int pageCount = expressionCacheDtoList.size();

        // 每条线程处理的任务数
        int pageSize = 300;

        // 计算需要开启多少条线程
        int threadCount = pageCount % pageSize == ConstantNumber.ZERO ? pageCount / pageSize
                : pageCount / pageSize + ConstantNumber.ONE;

        // 开启threadCount条线程
        for (int pageNumber = 1; pageNumber <= threadCount; pageNumber++) {
            // 计算分页参数
            final int executeNumber = pageSize * pageNumber;
            taskExecutor.execute(() -> executeCache(expressionCacheDtoList, pageCount, pageSize, executeNumber));
        }

        // 计算完成任务消耗时间
        double cost = (System.currentTimeMillis() - start) / 1000.000;
        log.info("Started Loading Expression Cache in {}", cost + " seconds");
    }

    /**
     * 将字段表达式刷入REDIS缓存
     *
     * @param expressionCacheDtoList
     * @param pageCount
     * @param pageSize
     * @param executeNumber
     */
    private void executeCache(List<MeiziTuPictureResponseVo> expressionCacheDtoList, int pageCount, int pageSize,
                              int executeNumber) {

        // 校验执行最大数量是否大于总计数
        executeNumber = executeNumber < pageCount ? executeNumber : pageCount;

        // 声明参数
        String expression;
        String columnId;
        String expressionTypeId;
        StringBuilder sb = new StringBuilder();

        // 多线程分页处理
        // 例如Thread-1处理0(含)~300(不含)，Thread-2处理300~600(不含)
        for (int j = executeNumber - pageSize; j < executeNumber; j++) {
            MeiziTuPictureResponseVo expressionCacheDto = expressionCacheDtoList.get(j);
            if (expressionCacheDto != null) {
                // 清空StringBuilder
                sb.delete(ConstantNumber.ZERO, sb.length());

                // 获取字段Value
                expression = expressionCacheDto.getImageId();
                columnId = expressionCacheDto.getTitle();
                expressionTypeId = expressionCacheDto.getModelName();

                // 组织RedisKey
                sb.append(RedisKeyPrefix.REDIS_EXPRESSION_KEY).append(columnId).append(RedisKeyPrefix.UNDERLINE)
                        .append(expressionTypeId);

                // 存入缓存
                RedisUtil.set(sb.toString(), expression);
            } else {
                log.info("cache value is null...");
            }
        }
    }
}
