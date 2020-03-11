package com.yudianxx.springBootDemo.model.image;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("t_model")
@Builder
@AllArgsConstructor
public class Model implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
}
