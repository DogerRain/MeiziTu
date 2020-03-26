package com.yudianxx.springBootDemo.model.requestVo;

import com.yudianxx.springBootDemo.model.PageObject;
import lombok.Data;

/**
 * @author huangyongwen
 * @date 2020/3/10
 * @Description
 */
@Data
public class MeiziTuPictureRequestVo extends PageObject {
    Boolean isBanner;
    int imageId;
    int modelId;
    String modelName;
    int categoryId;
    String categoryName;
    int collectionId;
    String collectionName;
}
