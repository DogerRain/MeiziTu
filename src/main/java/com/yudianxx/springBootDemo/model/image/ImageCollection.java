package com.yudianxx.springBootDemo.model.image;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Data
@TableName("t_collection")
@AllArgsConstructor
@NoArgsConstructor
public class ImageCollection implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
}
