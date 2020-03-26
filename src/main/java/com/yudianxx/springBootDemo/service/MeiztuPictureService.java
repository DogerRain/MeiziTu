package com.yudianxx.springBootDemo.service;

import com.github.pagehelper.PageInfo;
import com.yudianxx.springBootDemo.model.image.Category;
import com.yudianxx.springBootDemo.model.requestVo.MeiziTuPictureRequestVo;
import com.yudianxx.springBootDemo.model.responseVo.MeiziTuPictureResponseVo;

import java.util.List;
import java.util.Map;

/**
 * @author huangyongwen
 * @date 2020/3/10
 * @Description
 */
public interface MeiztuPictureService {

    PageInfo getCompleteImagesByPages(MeiziTuPictureRequestVo meiziTuPictureRequestVo);

    PageInfo getAllModels(MeiziTuPictureRequestVo meiziTuPictureRequestVo);

    List<Category> getAllCategorys(Category category);

    PageInfo getAllImageCollections(MeiziTuPictureRequestVo meiziTuPictureRequestVo);

    List<MeiziTuPictureResponseVo> getRandomPictures();

    Map<String, Object> getModelHomeBackgroundInfo(String modelId);

    PageInfo getModelImagesRank(MeiziTuPictureRequestVo meiziTuPictureRequestVo);

}
