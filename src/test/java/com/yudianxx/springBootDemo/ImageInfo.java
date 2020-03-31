package com.yudianxx.springBootDemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.yudianxx.springBootDemo.config.redis.ConstantNumber;
import com.yudianxx.springBootDemo.config.redis.RedisKeyPrefix;
import com.yudianxx.springBootDemo.config.redis.RedisUtil;
import com.yudianxx.springBootDemo.constants.HttpRequestUtils;
import com.yudianxx.springBootDemo.mapper.image.ImageHandleMapper;
import com.yudianxx.springBootDemo.model.image.Image;
import com.yudianxx.springBootDemo.model.requestVo.PictureModel;
import com.yudianxx.springBootDemo.model.responseVo.MeiziTuPictureResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ImageInfo {


    @Autowired
    ImageHandleMapper imageHandleMapper;

    @Qualifier("taskExecutor")
    @Autowired
    TaskExecutor taskExecutor;

    @Test
    public void getImageInfoAndInsert() {
        b();
    }

    /**
     * 线程池方式加载进redis
     */
    public void b() {
        long start = System.currentTimeMillis();
        log.info("Start Loading Expression into redis cache...");

        // 查询表达式全量数据
        List<Image> imageList = imageHandleMapper.selectList(new QueryWrapper<Image>().lambda().last("limit 301"));

        // 校验是否有数据需要加载缓存
        if (CollectionUtils.isEmpty(imageList)) {
            log.info("There Is Nothing Need To Cache!");
            return;
        }

        // 需要处理的任务总数
        int pageCount = imageList.size();

        // 每条线程处理的任务数
        int pageSize = 300;

        // 计算需要开启多少条线程
        int threadCount = pageCount % pageSize == ConstantNumber.ZERO ? pageCount / pageSize
                : pageCount / pageSize + ConstantNumber.ONE;

        // 开启threadCount条线程
        for (int pageNumber = 1; pageNumber <= threadCount; pageNumber++) {
            // 计算分页参数
            final int executeNumber = pageSize * pageNumber;
            taskExecutor.execute(() -> executeCache2(imageList, pageCount, pageSize, executeNumber));
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
    private void executeCache2(List<Image> expressionCacheDtoList, int pageCount, int pageSize,
                               int executeNumber) {

        // 校验执行最大数量是否大于总计数
        executeNumber = executeNumber < pageCount ? executeNumber : pageCount;

        // 多线程分页处理
        // 例如Thread-1处理0(含)~300(不含)，Thread-2处理300~600(不含)
        for (int j = executeNumber - pageSize; j < executeNumber; j++) {
            Image image = expressionCacheDtoList.get(j);
            if (image != null) {
                log.info("{}执行线程成功,imageId；{}",Thread.currentThread().getName() ,image.getId());
                String url = image.getImageLink() + "?imageInfo";
                Long id = image.getId();
                try {
                    JSONObject jsonObject = HttpRequestUtils.httpGet(url);

                    Image imageResponse = JSON.toJavaObject(jsonObject, Image.class);

                    int width = Integer.parseInt(imageResponse.getWidth());
                    int height = Integer.parseInt(imageResponse.getHeight());
                    int pictureType = 1;

                    if (width < height) {
                        //竖
                        pictureType = 0;
                    }

                    Image newImage = Image.builder().width(width + "").height(height + "").pictureType(pictureType).build();

                    imageHandleMapper.update(newImage, new UpdateWrapper<Image>().lambda().eq(Image::getId, id));
                } catch (Exception e) {
                    log.error("出错了:{}", id, e);
                }

            } else {
                log.info("cache value is null...");
            }
        }
    }

}


