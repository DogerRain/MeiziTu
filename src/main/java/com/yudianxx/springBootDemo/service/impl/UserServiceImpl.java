package com.yudianxx.springBootDemo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yudianxx.springBootDemo.constants.HttpRequestUtils;
import com.yudianxx.springBootDemo.constants.TokenUse;
import com.yudianxx.springBootDemo.mapper.image.ImageUserMapper;
import com.yudianxx.springBootDemo.model.GeneralToken;
import com.yudianxx.springBootDemo.model.WxUser;
import com.yudianxx.springBootDemo.model.image.ImageUser;
import com.yudianxx.springBootDemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    ImageUserMapper imageUserMapper;

    //    public GeneralToken getWWxToken(String appid, String secret) {
    public GeneralToken getWxOpeinId(WxUser wxUser) {
        GeneralToken generalToken = null;
//        String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?" +
//                "grant_type=client_credential" +
//                "&appid=" + TokenUse.WX_LOGIN_APPID +
//                "&secret=" + TokenUse.WX_LOGIN_SECRET;
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + TokenUse.WX_LOGIN_APPID + "&secret=" + TokenUse.WX_LOGIN_SECRET + "&js_code=" + wxUser.getCode() + "&grant_type=authorization_code";
        String openid = "";
        JSONObject jsonObject = HttpRequestUtils.httpGet(url);
        generalToken = JSON.parseObject(JSON.toJSONString(jsonObject), GeneralToken.class);
        return generalToken;
    }

    public void storeWxUserInfo(WxUser wxUser) {
        if (wxUser != null) {
            //把用户信息存储在本地
            ImageUser imageUser = getUserinfo(wxUser.getOpenid());

            ImageUser imageUser1 = ImageUser.builder()
                    .openid(wxUser.getOpenid())
                    .city(wxUser.getCity())
                    .province(wxUser.getProvince())
                    .nickName(wxUser.getNickName())
                    .avartar(wxUser.getAvartar())
                    .token(wxUser.getToken())
                    .isAuthoriseBefore(true)
                    .build();
            if (wxUser.getToken()!=null){
                imageUser1.setFirstLoginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }else {
                imageUser1.setLastLoginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }

            if (imageUser == null) {
                imageUserMapper.insert(imageUser1);
            } else {
                imageUserMapper.update(imageUser1, new QueryWrapper<ImageUser>().lambda().eq(ImageUser::getOpenid, wxUser.getOpenid()));
            }
        }
    }

    public ImageUser getUserinfo(String openId) {
        ImageUser imageUser = imageUserMapper.selectOne(new QueryWrapper<ImageUser>().lambda().eq(ImageUser::getOpenid, openId));
        return imageUser;
    }

}
