package com.yudianxx.springBootDemo.mapper.image;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yudianxx.springBootDemo.model.image.Image;
import com.yudianxx.springBootDemo.model.requestVo.MeiziTuPictureRequestVo;
import com.yudianxx.springBootDemo.model.requestVo.PictureModel;
import com.yudianxx.springBootDemo.model.responseVo.MeiziTuPictureResponseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ImageHandleMapper extends BaseMapper<Image> {


    List<MeiziTuPictureResponseVo> getCompletePicture(MeiziTuPictureRequestVo meiziTuPictureRequestVo);

    List<MeiziTuPictureResponseVo> getCompletesImagesByCondition(PictureModel pictureModel);

    List<MeiziTuPictureResponseVo> getBannerPictures(MeiziTuPictureRequestVo meiziTuPictureRequestVo);


    List<MeiziTuPictureResponseVo> getRandomPictureByModelId(long modelId, int count);


    List<Long> getOneRandomPicturesIdByModeId(@Param("modelId")long modelId, @Param("pictureType") int pictureType);
//    Integer getOneRandomPicturesIdByModeId( long modelId, int pictureType);

    MeiziTuPictureResponseVo getModelHomeBackgroundInfo(String modelId);

    List<MeiziTuPictureResponseVo> getModelHomeBackgroundInfoOfCollection(String modelId);


}
