package com.yudianxx.springBootDemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.yudianxx.springBootDemo.constants.ConstantUtils;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangyongwen
 * @date 2020/3/10
 * @Description
 */

@Service
@Slf4j
public class MeiziTuPictureServiceImpl implements MeiztuPictureService {

    @Autowired
    ConstantUtils constantUtils;

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

    public List<MeiziTuPictureResponseVo> getRandomPictures() {
        /**
         * 规则，每次取5个模特，每个模特取2张图片
         */
        List<Model> modelList = getRandomModel();

        List<MeiziTuPictureResponseVo> listResponse = new ArrayList<>();

        /**
         * 获取随机妹子的图片
         */
        for (Model model : modelList) {
            List<MeiziTuPictureResponseVo> list = imageHandleMapper.getrandomPictureByCount(model.getId(), constantUtils.IMAGES_RANDOM_COUNT);
            for (MeiziTuPictureResponseVo meiziTuPictureResponseVo : list) {
                listResponse.add(meiziTuPictureResponseVo);
            }
        }
        //不够十张图，补够，但imagesId不能重复
        //需要补全多少：
        int needCount = constantUtils.MODEL_RANDOM_COUNT * constantUtils.IMAGES_RANDOM_COUNT - listResponse.size();
        log.info("第一次获取图片大小：{}，需要补全多少张：{}", listResponse.size(), needCount);
        needPicturesCount(1, needCount, listResponse);
        return listResponse;
    }

    /**
     * 获取随机妹子id
     *
     * @return
     */
    public List<Model> getRandomModel() {
        List<Model> modelList = modelMapper.getRandomModelCount(constantUtils.MODEL_RANDOM_COUNT);
        log.info("随机妹子id：{}", modelList);
        return modelList;
    }

    /**
     * @param j
     * @param needCount
     * @param listResponse
     * @return 该方法返回没有获取过的图片id
     */
    public List<MeiziTuPictureResponseVo> needPicturesCount(int j, int needCount, List<MeiziTuPictureResponseVo> listResponse) {
        if (needCount != 0) {
            List<MeiziTuPictureResponseVo> list = imageHandleMapper.getCompletePictureNotinclude(
                    listResponse, needCount);
            for (MeiziTuPictureResponseVo meiziTuPictureResponseVo : list) {
                listResponse.add(meiziTuPictureResponseVo);
            }
            needCount = constantUtils.MODEL_RANDOM_COUNT * constantUtils.IMAGES_RANDOM_COUNT - listResponse.size();
            log.info("现大小：{}，该次补：{}张，递归补全图片第：{}次", listResponse.size(), needCount, j++);
            if (j >= 3) {
                return listResponse;
            }
            needPicturesCount(j, needCount, listResponse);

        }
        log.info("最后图片数：{}", listResponse.size());
        return listResponse;
    }


}
