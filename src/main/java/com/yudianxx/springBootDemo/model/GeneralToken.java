package com.yudianxx.springBootDemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralToken {
    private String expires_in; //成功有效时间
    private String access_token;  // 普通Token
    private String errcode; //失败ID
    private String errmsg; //失败消息

    private String hints; //提示
    private String openid;
    private String session_key;
    private String unionid;

    private String token;






}
