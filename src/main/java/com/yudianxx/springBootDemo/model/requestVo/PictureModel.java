package com.yudianxx.springBootDemo.model.requestVo;

import lombok.Builder;
import lombok.Data;

/**
 * @author huangyongwen
 * @date 2020/3/10
 * @Description
 */
@Data
@Builder
public class PictureModel  {
    Boolean isBanner;
    int imageId;
    String modelId;
    String modelName;
    String categoryId;
    String categoryName;
    String collectionId;
    String collectionName;
}
