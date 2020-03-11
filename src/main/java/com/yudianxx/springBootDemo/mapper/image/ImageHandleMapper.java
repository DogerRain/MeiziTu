package com.yudianxx.springBootDemo.mapper.image;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yudianxx.springBootDemo.model.image.Image;
import com.yudianxx.springBootDemo.model.requestVo.MeiziTuPictureRequestVo;
import com.yudianxx.springBootDemo.model.responseVo.MeiziTuPictureResponseVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
@Component
public interface ImageHandleMapper extends BaseMapper<Image> {


    List<MeiziTuPictureResponseVo> getCompletePicture(MeiziTuPictureRequestVo meiziTuPictureRequestVo);


}
