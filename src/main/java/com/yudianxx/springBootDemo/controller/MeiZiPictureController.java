package com.yudianxx.springBootDemo.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yudianxx.springBootDemo.config.redis.RedisUtil;
import com.yudianxx.springBootDemo.model.User;
import com.yudianxx.springBootDemo.model.requestVo.MeiziTuPictureRequestVo;
import com.yudianxx.springBootDemo.model.responseVo.MeiziTuPictureResponseVo;
import com.yudianxx.springBootDemo.model.responseVo.RetResponse;
import com.yudianxx.springBootDemo.model.responseVo.RetResult;
import com.yudianxx.springBootDemo.service.MeiztuPictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

}
