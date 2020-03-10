package com.yudianxx.springBootDemo.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yudianxx.springBootDemo.model.PageObject;
import com.yudianxx.springBootDemo.model.User;
import com.yudianxx.springBootDemo.model.requestVo.MeiziTuPictureRequestVo;
import com.yudianxx.springBootDemo.model.responseVo.MeiziTuPictureResponseVo;
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


    @RequestMapping("/getImagesTest")
    public Object getImagesTest(){
        List<MeiziTuPictureResponseVo> meiziTuPictureResponseVoList = 

    }


    @RequestMapping("/getAllMoedel")
    public Object findAllmodel(@RequestBody MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        PageInfo pageInfo = meiztuPictureService.getAllModels(meiziTuPictureRequestVo);
        return pageInfo;
    }

    @RequestMapping("/getAllModelName")
    public JSONObject getModelByName(){
        return null;
    }

}
