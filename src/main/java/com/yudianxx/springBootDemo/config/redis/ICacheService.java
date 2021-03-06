package com.yudianxx.springBootDemo.config.redis;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.yudianxx.springBootDemo.constants.ConstantUtils;
import com.yudianxx.springBootDemo.mapper.image.ImageHandleMapper;
import com.yudianxx.springBootDemo.model.requestVo.PictureModel;
import com.yudianxx.springBootDemo.model.responseVo.MeiziTuPictureResponseVo;
import lombok.extern.slf4j.Slf4j;
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
        List<MeiziTuPictureResponseVo> responseVoList = imageHandleMapper.getCompletesImagesByCondition(PictureModel.builder().build());
        if (CollectionUtils.isNotEmpty(responseVoList)) {
            for (MeiziTuPictureResponseVo meiziTuPictureEntity : responseVoList) {
                if (null != meiziTuPictureEntity) {
                    RedisUtil.set(RedisKeyPrefix.REDIS_SYSTEM_DICT_KEY + meiziTuPictureEntity.getImageId(), RedisUtil.toJson(meiziTuPictureEntity));
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
//        List<MeiziTuPictureResponseVo> expressionCacheDtoList = imageHandleMapper.getCompletePicture(new MeiziTuPictureRequestVo());
        List<MeiziTuPictureResponseVo> expressionCacheDtoList = imageHandleMapper.getCompletesImagesByCondition(PictureModel.builder().build());

        // 校验是否有数据需要加载缓存
        if (CollectionUtils.isEmpty(expressionCacheDtoList)) {
            log.info("list为空，退出");
            return;
        }

        // 需要处理的任务总数
        int pageCount = expressionCacheDtoList.size();

        // 每条线程处理的任务数
        int pageSize = ConstantUtils.THREAD_SIZE;

        // 计算需要开启多少条线程
        int threadCount = pageCount % pageSize == ConstantUtils.ZERO ? pageCount / pageSize
                : pageCount / pageSize + ConstantUtils.ONE;
        int executeNumber = 0;
        // 开启threadCount条线程
        for (int pageNumber = 1; pageNumber <= threadCount; pageNumber++) {
            // 计算分页参数

            //一个页执行的任务数，保证不重复
            executeNumber = pageSize * pageNumber;
            //每页 开始 执行序号
            int startNum = executeNumber - pageSize;
            //如果是末页， 153/100 =1 ,共2个任务数， 末页id 只需要到 152
            if (threadCount == pageNumber) {
                executeNumber = pageCount;
            }
            //末页
            int finalNumber = executeNumber;
            taskExecutor.execute(() -> executeCache(expressionCacheDtoList, startNum, finalNumber));
        }

        // 计算完成任务消耗时间
        double cost = (System.currentTimeMillis() - start) / 1000.000;
        log.info("Started Loading Expression Cache in {}", cost + " seconds");
    }

    /**
     * 将字段表达式刷入REDIS缓存
     *
     * @param expressionCacheDtoList
     * @param startNum
     * @param finalNumber
     */
    private void executeCache(List<MeiziTuPictureResponseVo> expressionCacheDtoList, int startNum, int finalNumber) {

        // 多线程分页处理
        // 例如Thread-1处理0(含)~300(不含)，Thread-2处理300~600(不含)
        for (int j = startNum; j < finalNumber; j++) {
            MeiziTuPictureResponseVo expressionCacheDto = expressionCacheDtoList.get(j);
            if (expressionCacheDto != null) {
                // 清空StringBuilder
//                sb.delete(ConstantUtils.ZERO, sb.length());

                // 获取字段Value
                String imageId = expressionCacheDto.getImageId();
                String modelId = expressionCacheDto.getModelId();
                String collectionId = expressionCacheDto.getCollectionId();

                // 组织RedisKey
//                sb.append(RedisKeyPrefix.REDIS_EXPRESSION_KEY).append(columnId).append(RedisKeyPrefix.UNDERLINE)
//                        .append(expressionTypeId);

                //key生成规则
                // imageId+modelId+collectionId
                String keys = imageId + RedisKeyPrefix.REDIS_SPACE + modelId + RedisKeyPrefix.REDIS_SPACE + collectionId;

                // 存入缓存
//                RedisUtil.set(sb.toString(), expression);
                RedisUtil.set(keys, RedisUtil.toJson(expressionCacheDto));
            } else {
                log.info("cache value is null...");
            }
        }
    }
}
