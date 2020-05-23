package com.yudianxx.springBootDemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WxUser {
    String code;
    String nickName;
    String city;
    String province;
    String avartar;
    String openid;
    String token;
}
