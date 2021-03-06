package com.yudianxx.springBootDemo.service.impl;

import com.alibaba.fastjson.JSON;
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

import java.util.*;

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

    /**
     * 根据条件返回image信息
     *
     * @param meiziTuPictureRequestVo
     * @return
     */

    public PageInfo getCompleteImagesByPages(MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        //redis里面查询，没有再往数据库里面查询
        String imageId = StringUtils.isBlank(meiziTuPictureRequestVo.getImageId()) ? "*" : meiziTuPictureRequestVo.getImageId() + "";
        String modeId = StringUtils.isBlank(meiziTuPictureRequestVo.getModelId()) ? "*" : meiziTuPictureRequestVo.getModelId() + "";
        String collection = StringUtils.isBlank(meiziTuPictureRequestVo.getCollectionId()) ? "*" : meiziTuPictureRequestVo.getCollectionId() + "";


        String key = imageId + RedisKeyPrefix.REDIS_SPACE + modeId + RedisKeyPrefix.REDIS_SPACE + collection;

        Map map = RedisUtil.getKeysValues(key);
        String redisValue = "";
        List<MeiziTuPictureResponseVo> meiziTuPictureResponseVoList = new ArrayList<>();

        for (Object v : map.values()) {
            redisValue = v.toString();
            if (StringUtils.isNotBlank(redisValue)) {
                MeiziTuPictureResponseVo meiziTuPictureResponseVo = JSON.parseObject(redisValue, MeiziTuPictureResponseVo.class);
                meiziTuPictureResponseVoList.add(meiziTuPictureResponseVo);
            }
        }

        //分页
        PageHelper.startPage(meiziTuPictureRequestVo.getPageNum(), meiziTuPictureRequestVo.getPageSize());


        if (map.size() == 0) {
            meiziTuPictureResponseVoList = imageHandleMapper.getCompletePicture(meiziTuPictureRequestVo);
        }
        PageInfo pageInfo = new PageInfo(meiziTuPictureResponseVoList);
        return pageInfo;

    }

    public PageInfo getAllModels(MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        PageHelper.startPage(meiziTuPictureRequestVo.getPageNum(), meiziTuPictureRequestVo.getPageSize());
        QueryWrapper<Model> queryWrapper = new QueryWrapper<>();
        if (meiziTuPictureRequestVo != null && StringUtils.isNotBlank(meiziTuPictureRequestVo.getModelId())) {
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
        if (meiziTuPictureRequestVo != null && StringUtils.isNotBlank(meiziTuPictureRequestVo.getCollectionId())) {
            collectionQueryWrapper.lambda().eq(ImageCollection::getId, meiziTuPictureRequestVo.getCollectionId());
        }
        List<ImageCollection> imageCollectionList = collectionMapper.selectList(collectionQueryWrapper);
        PageInfo pageInfo = new PageInfo(imageCollectionList);
        return pageInfo;
    }

    /**
     * 10 个modelId，一个modelId一个imageId图片
     *
     * @return
     */
    public List<MeiziTuPictureResponseVo> getRandomPictures() {
        //10个随机modelId
        List<Model> modelList = getRandomModel(ConstantUtils.MODEL_RANDOM_COUNT);
        List<MeiziTuPictureResponseVo> resultList = new ArrayList<>();
        int pictureType = Math.random() > 0.5 ? 1 : 0;
        Map<Integer, Integer> imageIdMap = new HashMap<>();
        List<MeiziTuPictureResponseVo> listResponse = getRandomPicturesByModel(imageIdMap, pictureType, 1, modelList, resultList);
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
    public List<MeiziTuPictureResponseVo> getRandomPicturesByModel(Map<Integer, Integer> imageIdMap, int pictureType, int count, List<Model> modelList, List<MeiziTuPictureResponseVo> resultList) {
        MeiziTuPictureResponseVo meiziTuPictureResponseVo;

        for (Model model : modelList) {
            //选择随机一张图片，但是一定要指定pictureType
//            int imageId = imageHandleMapper.getOneRandomPicturesIdByModeId(model.getId(), pictureType);
            List<Long> listImageId = imageHandleMapper.getOneRandomPicturesIdByModeId(model.getId(), pictureType);

            if (listImageId.size() == 0) {
                continue;
            }

            //产生[ 0,listImageId.size() ) 的下标。
            int index = (int) (Math.random() * listImageId.size());

            int imageId = (int) listImageId.get(index).longValue();

            if (imageId == 0 || imageIdMap.get(imageId) != null) {
                //图片没有，跳出循环
                //已经存在该modelId
                continue;
            }
            imageIdMap.put(imageId, imageId);
            log.info("imageId:{}", imageId);
            Map map = RedisUtil.getKeysValues(imageId + RedisKeyPrefix.REDIS_SPACE + "*");
            String redisValue = "";
            for (Object v : map.values()) {
//                System.out.println("value= " + v);
                redisValue = v.toString();
            }
            log.info("redis 取值 redisValue：{}", redisValue);
            if (StringUtils.isNotBlank(redisValue)) {
                meiziTuPictureResponseVo = RedisUtil.parseJson(redisValue, MeiziTuPictureResponseVo.class);
            } else {
                PictureModel pictureModel = PictureModel.builder().imageId(imageId + "").build();

                meiziTuPictureResponseVo = imageHandleMapper.getCompletesImagesByCondition(pictureModel).get(0);
            }
            resultList.add(meiziTuPictureResponseVo);
        }
        int needCount = constantUtils.MODEL_RANDOM_COUNT * constantUtils.IMAGES_RANDOM_COUNT - resultList.size();
        if (needCount > 0) {
            log.info("进入递归，正在补全图片，需要补全 {} 张图片", needCount);
            //modelId需要重新选取
            getRandomPicturesByModel(imageIdMap, pictureType, count++, getRandomModel(needCount), resultList);
            if (count >= 3) {
                log.info("超过3次还没补全，退出递归");
                return resultList;
            }
        }
        return resultList;
    }


    @Override
    public Map<String, Object> getModelHomeBackgroundInfo(String modelId) {
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


    public List<MeiziTuPictureResponseVo> getBannerPictures(MeiziTuPictureRequestVo meiziTuPictureRequestVo) {
        if (StringUtils.isBlank(meiziTuPictureRequestVo.getPictureType())) {
            meiziTuPictureRequestVo.setPictureType("1");
        }
        return imageHandleMapper.getBannerPictures(meiziTuPictureRequestVo);
    }

}
