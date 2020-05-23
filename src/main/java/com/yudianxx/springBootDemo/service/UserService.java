package com.yudianxx.springBootDemo.service;

import com.yudianxx.springBootDemo.model.GeneralToken;
import com.yudianxx.springBootDemo.model.WxUser;
import com.yudianxx.springBootDemo.model.image.ImageUser;

public interface UserService {
    GeneralToken getWxOpeinId(WxUser wxUser);
    void storeWxUserInfo(WxUser wxUser);

    ImageUser getUserinfo(String openId);
}
