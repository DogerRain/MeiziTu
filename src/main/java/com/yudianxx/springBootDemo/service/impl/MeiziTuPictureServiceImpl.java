package com.yudianxx.springBootDemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yudianxx.springBootDemo.config.redis.RedisKeyPrefix;
import com.yudianxx.springBootDemo.config.redis.RedisUtil;
import com.yudianxx.springBootDemo.constants.ConstantUtils;
import com.yudianxx.springBootDemo.mapper.image.CollectionMapper;
import com.yudianxx.springBootDemo.mapper.image.ImageCategoryMapper;
import com.yudianxx.springBootDemo.mapper.image.ImageHandleMapper;
import com.yudianxx.springBootDemo.mapper.image.ModelMapper;
import com.yudianxx.springBootDemo.model.image.Category;
import com.yudianxx.springBootDemo.model.image.ImageCollection;
import com.yudianxx.springBootDemo.model.image.Model;
import com.yudianxx.springBootDemo.model.requestVo.MeiziTuPictureRequestVo;
import com.yudianxx.springBootDemo.model.requestVo.PictureModel;
import com.yudianxx.springBootDemo.model.responseVo.MeiziTuPictureResponseVo;
import com.yudianxx.springBootDemo.service.MeiztuPictureService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author huangyongwen
 * @date 2020/3/10
 * @Description
 */

@Service
@Slf4j
public class MeiziTuPictureServiceImpl implements MeiztuPictureService {

    @Autowired
    MeiztuPictureService selfMeiztuService;

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


    public PageInfo getCompleteImagesByPages(MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        //redis里面查询，没有再往数据库里面查询

//        String redisValue = RedisUtil.get(RedisKeyPrefix.REDIS_SYSTEM_DICT_KEY + imageId).toString();


        List<MeiziTuPictureResponseVo> meiziTuPictureResponseVoList = imageHandleMapper.getCompletePicture(meiziTuPictureRequestVo);
        //分页
        PageHelper.startPage(meiziTuPictureRequestVo.getPageNum(), meiziTuPictureRequestVo.getPageSize());
        PageInfo pageInfo = new PageInfo(meiziTuPictureResponseVoList);
        return pageInfo;
    }

    public PageInfo getAllModels(MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        PageHelper.startPage(meiziTuPictureRequestVo.getPageNum(), meiziTuPictureRequestVo.getPageSize());
        QueryWrapper<Model> queryWrapper = new QueryWrapper<>();
        if (meiziTuPictureRequestVo != null && meiziTuPictureRequestVo.getModelId() != 0) {
            queryWrapper.lambda().eq(Model::getId, meiziTuPictureRequestVo.getModelId());
        }
        List<Model> list = modelMapper.selectList(queryWrapper);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    @Override
    public List<Category> getAllCategorys(Category category) {
        //分类暂时没有
        return null;
    }

    @Override
    public PageInfo getAllImageCollections(MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        PageHelper.startPage(meiziTuPictureRequestVo.getPageNum(), meiziTuPictureRequestVo.getPageSize());
        QueryWrapper<ImageCollection> collectionQueryWrapper = new QueryWrapper<>();
        if (meiziTuPictureRequestVo != null && meiziTuPictureRequestVo.getCollectionId() != 0) {
            collectionQueryWrapper.lambda().eq(ImageCollection::getId, meiziTuPictureRequestVo.getCollectionId());
        }
        List<ImageCollection> imageCollectionList = collectionMapper.selectList(collectionQueryWrapper);
        PageInfo pageInfo = new PageInfo(imageCollectionList);
        return pageInfo;
    }

    public List<MeiziTuPictureResponseVo> getRandomPictures() {
        List<Model> modelList = getRandomModel(ConstantUtils.MODEL_RANDOM_COUNT);
        List<MeiziTuPictureResponseVo> resultList = new ArrayList<>();
        List<MeiziTuPictureResponseVo> listResponse = getRandomPicturesByModel(1, modelList, resultList);
        return listResponse;
    }

    /**
     * 获取随机Modelid
     *
     * @return
     */
    public List<Model> getRandomModel(int needModelNum) {
        List<Model> modelList = modelMapper.getRandomModelCount(needModelNum);
        return modelList;
    }


    /**
     * @Author: Administrator
     * @Date: 2020/3/25  23:58
     * @Description: 根据modelId随机返回图片
     * @Param:
     * @return:
     */
    public List<MeiziTuPictureResponseVo> getRandomPicturesByModel(int count, List<Model> modelList, List<MeiziTuPictureResponseVo> resultList) {
        MeiziTuPictureResponseVo meiziTuPictureResponseVo;
        for (Model model : modelList) {
            int imageId = imageHandleMapper.getOneRandomPicturesIdByModeId(model.getId());
            log.info("imageId:{}", imageId);
            String redisValue = RedisUtil.get(RedisKeyPrefix.REDIS_SYSTEM_DICT_KEY + imageId).toString();
            log.info("redis 取值 redisValue：{}", redisValue);
            if (StringUtils.isNotBlank(redisValue)) {
                meiziTuPictureResponseVo = RedisUtil.parseJson(redisValue, MeiziTuPictureResponseVo.class);
            } else {
                PictureModel pictureModel = PictureModel.builder().imageId(imageId).build();
                meiziTuPictureResponseVo = imageHandleMapper.getCompletesImagesByCondition(pictureModel).get(0);
            }
            resultList.add(meiziTuPictureResponseVo);
        }
        int needCount = constantUtils.MODEL_RANDOM_COUNT * constantUtils.IMAGES_RANDOM_COUNT - resultList.size();
        if (needCount > 0) {
            log.info("进入递归，正在补全图片，需要补全 {} 张图片", needCount);
            getRandomPicturesByModel(count++, getRandomModel(needCount), resultList);
            if (count >= 3) {
                log.info("超过3次还没补全，退出递归");
                return resultList;
            }
        }
        return resultList;
    }


    public Map<String, Object> getModelHomeBackgroundInfo(int modelId) {
        //1. 背景图
        MeiziTuPictureResponseVo meiziTuPictureResponseVo = imageHandleMapper.getModelHomeBackgroundInfo(modelId);
        //2. 合集情况
        List<MeiziTuPictureResponseVo> meiziTuPictureResponseVoList = imageHandleMapper.getModelHomeBackgroundInfoOfCollection(modelId);

        Map<String, Object> map = new HashMap<>();

        map.put("backgroundInfo", meiziTuPictureResponseVo);

        map.put("collectionInfo", meiziTuPictureResponseVoList);

        return map;
    }


    public PageInfo getModelImagesRank(MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        PageHelper.startPage(meiziTuPictureRequestVo.getPageNum(), meiziTuPictureRequestVo.getPageSize());
        PageInfo pageInfo = new PageInfo(modelMapper.getModelImagesRank());
        return pageInfo;
    }

}
