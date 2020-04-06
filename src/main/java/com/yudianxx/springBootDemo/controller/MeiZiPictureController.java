package com.yudianxx.springBootDemo.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.PageInfo;
import com.yudianxx.springBootDemo.config.redis.RedisUtil;
import com.yudianxx.springBootDemo.constants.ConstantUtils;
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
        long start = System.currentTimeMillis();
        log.info("Start Loading Expression into redis cache...");

        // 查询表达式全量数据
//        List<Image> imageList = imageHandleMapper.selectList(new QueryWrapper<Image>().lambda().last("limit 8"));
        List<Image> imageList = imageHandleMapper.selectList(new QueryWrapper<Image>());

        // 校验是否有数据需要加载缓存
        if (CollectionUtils.isEmpty(imageList)) {
            log.info("list为空，退出");
            return;
        }

        // 需要处理的任务总数
        int pageCount = imageList.size();

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
            //如果是末页， 153/100 =1 ,2个任务数， 末页id 只需要到 152
            if (threadCount == pageNumber) {
                executeNumber = pageCount;
            }
            //末页
            int finalNumber = executeNumber;
            taskExecutor.execute(() -> executeCache(imageList, startNum, finalNumber));
        }

        // 计算完成任务消耗时间
        double cost = (System.currentTimeMillis() - start) / 1000.000;
        log.info("Started Loading Expression Cache in {}", cost + " seconds");
    }


    private void executeCache(List<Image> expressionCacheDtoList, int startNum, int finalNumber) {
        for (int j = startNum; j < finalNumber; j++) {
            Image image = expressionCacheDtoList.get(j);
            if (image != null) {
                log.info("当前线程名称：{}，执行线程成功,imageId；{}",Thread.currentThread().getName() ,image.getId());
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
        log.info("线程：{},执行任务数：{}", Thread.currentThread().getName(), finalNumber - startNum);
    }


}
