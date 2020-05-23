package com.yudianxx.springBootDemo.model.image;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@Data
@TableName("t_user")
@AllArgsConstructor
@NoArgsConstructor
public class ImageUser {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    String nickName;
    String city;
    String province;
    String avartar;
    String openid;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String firstLoginTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;
    private String lastLoginTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    String token;
    Boolean isAuthoriseBefore;
}
