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
import com.yudianxx.springBootDemo.model.image.Image;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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


    public PageInfo getCompleteImages(MeiziTuPictureRequestVo meiziTuPictureRequestVo) {


        List<MeiziTuPictureResponseVo> meiziTuPictureResponseVoList = imageHandleMapper.getCompletePicture(meiziTuPictureRequestVo);


        //分页
        PageHelper.startPage(meiziTuPictureRequestVo.getPageNum(), meiziTuPictureRequestVo.getPageSize());
        PageInfo pageInfo = new PageInfo(meiziTuPictureResponseVoList);

//        throw new RuntimeException();
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
        if (model != null && model.getId() != null) {
            modelQueryWrapper.lambda().eq(Model::getId, model.getId());
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
        List<Model> modelList = getRandomModel(ConstantUtils.MODEL_RANDOM_COUNT);

        List<MeiziTuPictureResponseVo> resultList = new ArrayList<>();

        List<MeiziTuPictureResponseVo> listResponse = getRandomPicturesByModel(modelList, resultList);


        //不够十张图，补够，但imagesId不能重复
        //需要补全多少：
//        int needCount = constantUtils.MODEL_RANDOM_COUNT * constantUtils.IMAGES_RANDOM_COUNT - listResponse.size();
//        log.info("第一次获取图片大小：{}，需要补全多少张：{}", listResponse.size(), needCount);
//        needPicturesCount(1, needCount, listResponse);
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
    public List<MeiziTuPictureResponseVo> getRandomPicturesByModel(List<Model> modelList, List<MeiziTuPictureResponseVo> resultList) {
//        if ()
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
        return resultList;
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

    /**
     * 事务测试
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Model testTransactional() throws Exception {
        Model model = Model.builder().id(100l).name("事务测试").build();
//        try {
//            a(model);  //内部类调用，事务不起作用，加入a()报错了，插入仍然有效
//            b(model);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            selfMeiztuService.a(model);
            selfMeiztuService.b(model);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            /**
             * 下面这两个报错了selfMeiztuService.a(model);selfMeiztuService.b(model)并不会回滚
             * ;不知道为什么，可能是没有事务
             */
            //  throw new RuntimeException();
            //  int i=1/0;
            selfMeiztuService.c(model);  //这个就能回滚
        } catch (Exception e) {
            //         throw e; //把错误抛出来，@Transactional才能发现
        }
        return model;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void a(Model model) {
        log.info("进入A方法");
        model.setName("事务测试a");
        modelMapper.insert(model);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void b(Model model) {
        log.info("进入B方法");
        model.setName("事务测试b");
        modelMapper.insert(model);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void c(Model model) {
        log.info("进入c方法");
        int i = 1 / 0;
    }
}
