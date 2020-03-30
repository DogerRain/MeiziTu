package com.yudianxx.springBootDemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yudianxx.springBootDemo.constants.HttpRequestUtils;
import com.yudianxx.springBootDemo.mapper.image.ImageHandleMapper;
import com.yudianxx.springBootDemo.model.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ImageInfo {


    @Autowired
    ImageHandleMapper imageHandleMapper;

    @Test
    public void getImageInfoAndInsert() {
//        6366

    }

    public void a() {
//        List<Image> imageList = imageHandleMapper.selectList(new QueryWrapper<Image>());
        List<Image> imageList = imageHandleMapper.selectList(new QueryWrapper<Image>().lambda().last("limit 1000"));
        int i = 0;
        for (Image image : imageList) {
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
                log.info("正在执行第{}次，width:{},height:{},pictureType:{}", i++, width, height, pictureType);

                Image newImage = Image.builder().width(width + "").height(height + "").pictureType(pictureType).build();

                imageHandleMapper.update(newImage, new UpdateWrapper<Image>().lambda().eq(Image::getId, id));

            } catch (Exception e) {
                log.error("出错了:{}", id, e);
            }

        }
    }

    public void testA(){

    }

    public void testB(){

    }

}

class a extends Thread {
    @Override
    public void run() {

    }
}

class b extends Thread{

}