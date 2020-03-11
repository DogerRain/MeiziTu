package com.yudianxx.springBootDemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yudianxx.springBootDemo.mapper.image.CollectionMapper;
import com.yudianxx.springBootDemo.mapper.image.ImageCategoryMapper;
import com.yudianxx.springBootDemo.mapper.image.ImageHandleMapper;
import com.yudianxx.springBootDemo.mapper.image.ModelMapper;
import com.yudianxx.springBootDemo.model.image.Category;
import com.yudianxx.springBootDemo.model.image.Image;
import com.yudianxx.springBootDemo.model.image.ImageCollection;
import com.yudianxx.springBootDemo.model.image.Model;
import com.yudianxx.springBootDemo.model.requestVo.MeiziTuPictureRequestVo;
import com.yudianxx.springBootDemo.model.responseVo.MeiziTuPictureResponseVo;
import com.yudianxx.springBootDemo.service.MeiztuPictureService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huangyongwen
 * @date 2020/3/10
 * @Description
 */

@Service
public class MeiziTuPictureServiceImpl implements MeiztuPictureService {


    @Autowired
    ImageHandleMapper imageHandleMapper;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ImageCategoryMapper imageCategoryMapper;

    @Autowired
    CollectionMapper collectionMapper;


    public PageInfo getImagesTest(MeiziTuPictureRequestVo meiziTuPictureRequestVo) {

        List<MeiziTuPictureResponseVo> meiziTuPictureResponseVoList = imageHandleMapper.getCompletePicture(meiziTuPictureRequestVo);


        //分页
        PageHelper.startPage(meiziTuPictureRequestVo.getPageNum(), meiziTuPictureRequestVo.getPageSize());
        PageInfo pageInfo = new PageInfo(meiziTuPictureResponseVoList);

        return pageInfo;


    }


    public PageInfo getAllModels(MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        PageHelper.startPage(meiziTuPictureRequestVo.getPageNum(), meiziTuPictureRequestVo.getPageSize());

        Image image = new Image();

        if (StringUtils.isNotBlank(meiziTuPictureRequestVo.getModelName())) {
//            image.setName(meiziTuPictureRequestVo.getModelName());
        }
        List<Image> list = imageHandleMapper.selectList(new QueryWrapper<Image>());
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    @Override
    public List<Model> getAllModels(Model model) {
        QueryWrapper<Model> modelQueryWrapper = new QueryWrapper<>();
        if (model != null && StringUtils.isNotBlank(model.getName())) {
            modelQueryWrapper.lambda().eq(Model::getName, model.getName());
        }
        List<Model> modelList = modelMapper.selectList(modelQueryWrapper);
        return modelList;
    }

    @Override
    public List<Category> getAllCategorys(Category category) {
        //分类暂时没有
        return null;
    }

    @Override
    public List<Image> getAllImages(Image image) {
        return null;
    }

    @Override
    public List<ImageCollection> getAllImageCollection(ImageCollection imageCollection) {
        QueryWrapper<ImageCollection> collectionQueryWrapper = new QueryWrapper<>();
        if (imageCollection != null && imageCollection.getId() != null) {
            collectionQueryWrapper.lambda().eq(ImageCollection::getId, imageCollection.getId());
        }

        List<ImageCollection> imageCollectionList = collectionMapper.selectList(collectionQueryWrapper);

        return imageCollectionList;
    }


}
