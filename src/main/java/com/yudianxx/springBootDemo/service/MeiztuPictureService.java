package com.yudianxx.springBootDemo.service;

import com.github.pagehelper.PageInfo;
import com.yudianxx.springBootDemo.model.image.Category;
import com.yudianxx.springBootDemo.model.image.Image;
import com.yudianxx.springBootDemo.model.image.ImageCollection;
import com.yudianxx.springBootDemo.model.image.Model;
import com.yudianxx.springBootDemo.model.requestVo.MeiziTuPictureRequestVo;
import com.yudianxx.springBootDemo.model.responseVo.MeiziTuPictureResponseVo;

import java.util.List;

/**
 * @author huangyongwen
 * @date 2020/3/10
 * @Description
 */
public interface MeiztuPictureService {
    PageInfo getAllModels(MeiziTuPictureRequestVo meiziTuPictureRequestVo);


    PageInfo getCompleteImages(MeiziTuPictureRequestVo meiziTuPictureRequestVo);

    List<Model> getAllModels(Model model);


    List<Category> getAllCategorys(Category category);

    List<Image> getAllImages(Image image);


    List<ImageCollection> getAllImageCollection(ImageCollection imageCollection);


    List<MeiziTuPictureResponseVo> getRandomPictures();

    Model testTransactional() throws Exception;

    void a(Model model);

    void b(Model model);

    void c(Model model);

}
