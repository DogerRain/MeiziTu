package com.yudianxx.springBootDemo.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.PageInfo;
import com.yudianxx.springBootDemo.config.redis.ConstantNumber;
import com.yudianxx.springBootDemo.config.redis.RedisUtil;
import com.yudianxx.springBootDemo.constants.HttpRequestUtils;
import com.yudianxx.springBootDemo.mapper.image.ImageHandleMapper;
import com.yudianxx.springBootDemo.model.User;
import com.yudianxx.springBootDemo.model.image.Image;
import com.yudianxx.springBootDemo.model.requestVo.MeiziTuPictureRequestVo;
import com.yudianxx.springBootDemo.model.responseVo.MeiziTuPictureResponseVo;
import com.yudianxx.springBootDemo.model.responseVo.RetResponse;
import com.yudianxx.springBootDemo.model.responseVo.RetResult;
import com.yudianxx.springBootDemo.service.MeiztuPictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/meizitu")
@Slf4j
public class MeiZiPictureController {


    @Autowired
    MeiztuPictureService meiztuPictureService;

    @Autowired
    ImageHandleMapper imageHandleMapper;

    @Qualifier("taskExecutor")
    @Autowired
    TaskExecutor taskExecutor;


    @RequestMapping("/test")
//    public Object test(@RequestBody User user) {
    public Object test() {
        log.info("小程序接口调入测试");
        User user1 = User.builder().age(25).userName("以太网").password("123").build();

        Set<String> keys = RedisUtil.getKeys("Key_1000*");

        for (String key : keys) {
            log.info(key);
        }

        Map map = RedisUtil.getKeysValues("Key_1000*");
        log.info("map:{}", map);
        return JSONObject.toJSON(user1);
    }

    /**
     * @param
     * @return
     * @Desc 随机返回5个collection的精选图片
     */
    @RequestMapping("/getBannerImages")
    public RetResult getBannerImages() {

        return RetResponse.makeOKRsp();
    }

    /**
     * @param meiziTuPictureRequestVo
     * @return
     * @Desc 根据model id ， collection id ,category id  筛选获取所有图片信息，分页
     */
    @RequestMapping("/getCompleteImagesByPage")
    public RetResult getCompleteImagesByPages(@RequestBody MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        //防止恶意输入击穿数据库
        if (meiziTuPictureRequestVo != null &&
                meiziTuPictureRequestVo.getImageId() == null &&
                meiziTuPictureRequestVo.getCollectionId() == null &&
                meiziTuPictureRequestVo.getModelId() == null) {
            return RetResponse.makeErrRsp("参数为空");
        }
        PageInfo meiziTuPictureResponseVoList = meiztuPictureService.getCompleteImagesByPages(meiziTuPictureRequestVo);
        return RetResponse.makeOKRsp(meiziTuPictureResponseVoList);

    }

    /**
     * @param meiziTuPictureRequestVo
     * @return 获取所有model，分页
     */

    @RequestMapping("/getAllMoedels")
    public RetResult getAllMoedels(@RequestBody MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        PageInfo modelList = meiztuPictureService.getAllModels(meiziTuPictureRequestVo);
        return RetResponse.makeOKRsp(modelList);
    }

    /**
     * 获取所有的合集
     *
     * @param meiziTuPictureRequestVo
     * @return
     */
    @RequestMapping("/getAllCollection")
    public RetResult getAllCollection(@RequestBody MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        PageInfo imageCollectionList = meiztuPictureService.getAllImageCollections(meiziTuPictureRequestVo);
        return RetResponse.makeOKRsp(imageCollectionList);
    }


    /**
     * 随机返回10张图片
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/getRandomPictures")
    public RetResult getRandomPictures() throws Exception {
        List<MeiziTuPictureResponseVo> meiziTuPictureResponseVoList = new ArrayList<>();
        meiziTuPictureResponseVoList = meiztuPictureService.getRandomPictures();
        return RetResponse.makeOKRsp(meiziTuPictureResponseVoList);
    }

    @RequestMapping("/getModelHomeBackgroundInfo")
    public RetResult getModelHomeBackgroundInfo(@RequestBody MeiziTuPictureRequestVo meiziTuPictureRequestVo) throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (meiziTuPictureRequestVo.getModelId() == null) {
            return RetResponse.makeErrRsp("参数为空");
        }
        try {
            map = meiztuPictureService.getModelHomeBackgroundInfo(meiziTuPictureRequestVo.getModelId());

        } catch (Exception e) {
            RetResponse.makeErrRsp("内部错误");
        }
        return RetResponse.makeOKRsp(map);
    }

    @RequestMapping("/getModelImagesRank")
    public RetResult getModelImagesRank(@RequestBody MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        return RetResponse.makeOKRsp(meiztuPictureService.getModelImagesRank(meiziTuPictureRequestVo));
    }


    @RequestMapping("/getImageInfoByThread")
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
        List<Image> imageList = imageHandleMapper.selectList(new QueryWrapper<Image>().lambda().last("limit 153"));

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

            int executeNumber = pageSize * pageNumber;

            int finalPageNumber = pageNumber;

            taskExecutor.execute(() -> executeCache3(imageList, finalPageNumber,pageCount, pageSize, executeNumber));
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
                log.info("{}执行线程成功,imageId:{}", Thread.currentThread().getName(), image.getId());
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

                    Image newImage = Image.builder().width(width + "").height(height + "").pictureType(10).build();

                    imageHandleMapper.update(newImage, new UpdateWrapper<Image>().lambda().eq(Image::getId, id));
                } catch (Exception e) {
                    log.error("出错了:{}", id, e);
                }

            } else {
                log.info("cache value is null...");
            }
        }
    }

    private void executeCache3(List<Image> expressionCacheDtoList, int pageNumber, int pageCount, int pageSize,
                               int executeNumber) {

//        当前执行thread
        int threadCount = pageCount % pageSize == ConstantNumber.ZERO ? pageCount / pageSize
                : pageCount / pageSize + ConstantNumber.ONE;

        int startNum = executeNumber - pageSize;
        if (threadCount == 1) {
            startNum = 0;
        }
        //末页
        if (threadCount == pageNumber) {
            executeNumber = pageCount ;
        }

        for (int j = startNum; j < executeNumber; j++) {
            Image image = expressionCacheDtoList.get(j);
            log.info("{}执行线程成功,imageId:{}", Thread.currentThread().getName(), image.getId());
        }


    }


}
