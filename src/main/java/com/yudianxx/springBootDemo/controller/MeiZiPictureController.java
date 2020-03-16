package com.yudianxx.springBootDemo.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yudianxx.springBootDemo.model.User;
import com.yudianxx.springBootDemo.model.image.ImageCollection;
import com.yudianxx.springBootDemo.model.image.Model;
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

import java.util.ArrayList;
import java.util.List;

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
     * @Desc 根据model id ， collection id ,category id  筛选获取图片信息，分页
     */
    @RequestMapping("/getCompleteImages")
    public RetResult getImagesTest(@RequestBody MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        PageInfo meiziTuPictureResponseVoList = meiztuPictureService.getCompleteImages(meiziTuPictureRequestVo);
        return RetResponse.makeOKRsp(meiziTuPictureResponseVoList);

    }

//    @RequestMapping("/getCompleteImagesTest")
//    public RetResult getImagesTest() {
//        MeiziTuPictureRequestVo meiziTuPictureRequestVo = new MeiziTuPictureRequestVo();
//        PageInfo meiziTuPictureResponseVoList = meiztuPictureService.getImagesTest(meiziTuPictureRequestVo);
//        return RetResponse.makeOKRsp(meiziTuPictureResponseVoList);
//
//    }

    @RequestMapping("/getAllMoedel")
    public RetResult findAllmodel(@RequestBody Model model) {
        List<Model> modelList = meiztuPictureService.getAllModels(model);
        return RetResponse.makeOKRsp(modelList);
    }

    @RequestMapping("/getAllCollection")
    public RetResult getAllCollection(@RequestBody ImageCollection imageCollection) {
        List<ImageCollection> imageCollectionList = meiztuPictureService.getAllImageCollection(imageCollection);
        return RetResponse.makeOKRsp(imageCollectionList);
    }

    @RequestMapping("/getRandomPictures")
    public RetResult getRandomPictures() throws Exception {
        List<MeiziTuPictureResponseVo> meiziTuPictureResponseVoList = new ArrayList<>();
        try {
            meiziTuPictureResponseVoList = meiztuPictureService.getRandomPictures();
        } catch (Exception e) {
            RetResponse.makeErrRsp("内部错误");
        }
        return RetResponse.makeOKRsp(meiziTuPictureResponseVoList);
    }


}
