package com.yudianxx.springBootDemo.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yudianxx.springBootDemo.model.User;
import com.yudianxx.springBootDemo.model.image.ImageCollection;
import com.yudianxx.springBootDemo.model.image.Model;
import com.yudianxx.springBootDemo.model.requestVo.MeiziTuPictureRequestVo;
import com.yudianxx.springBootDemo.service.MeiztuPictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
//        log.info("小程序接口调入");
        User user1 = User.builder().age(25).userName("以太网").password("123").build();
        return JSONObject.toJSON(user1);
    }


    @RequestMapping("/getCompleteImages")
    public PageInfo getImagesTest(@RequestBody MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        PageInfo meiziTuPictureResponseVoList = meiztuPictureService.getImagesTest(meiziTuPictureRequestVo);

        return meiziTuPictureResponseVoList;

    }

    @RequestMapping("/getAllMoedel")
    public List findAllmodel(@RequestBody Model model) {
        List<Model> modelList = meiztuPictureService.getAllModels(model);
        return modelList;

    }

    @RequestMapping("/getAllCollection")
    public JSONObject getAllCollection(@RequestBody ImageCollection imageCollection) {
        List<ImageCollection> imageCollectionList = meiztuPictureService.getAllImageCollection(imageCollection);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("imageCollectionList", imageCollectionList);
        return jsonObject;
    }

}
