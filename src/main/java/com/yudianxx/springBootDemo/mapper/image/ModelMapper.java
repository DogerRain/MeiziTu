package com.yudianxx.springBootDemo.mapper.image;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yudianxx.springBootDemo.model.image.Model;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelMapper extends BaseMapper<Model> {
    List<Model> getRandomModelCount(int count);

}
