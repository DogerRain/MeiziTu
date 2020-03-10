package com.yudianxx.springBootDemo.model;

import lombok.Data;

/**
 * @author huangyongwen
 * @date 2020/3/10
 * @Description
 */
@Data
public class PageObject {
    private int pageNum = 1;
    private int pageSize = 10;
}
