package com.yudianxx.springBootDemo.controller;

import com.yudianxx.springBootDemo.constants.TokenUse;
import com.yudianxx.springBootDemo.mapper.image.ImageUserMapper;
import com.yudianxx.springBootDemo.model.GeneralToken;
import com.yudianxx.springBootDemo.model.WxUser;
import com.yudianxx.springBootDemo.model.image.ImageUser;
import com.yudianxx.springBootDemo.model.responseVo.RetResponse;
import com.yudianxx.springBootDemo.model.responseVo.RetResult;
import com.yudianxx.springBootDemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author huangyongwen
 * @date 2020/4/2
 * @Description
 */
@RestController
@RequestMapping("/userAPI")
@Slf4j
public class LoginController {

    @Autowired
    UserService userService;

    @Autowired
    ImageUserMapper imageUserMapper;


    /**
     * 登陆认证
     *
     * @param wxUser
     * @return
     */
    @RequestMapping(value = "/getUserInfo")
    public RetResult login(@RequestBody WxUser wxUser) {
        ImageUser imageUser = userService.getUserinfo(wxUser.getOpenid());
        if (imageUser.getNickName() != null && imageUser.getAvartar() != null) {
            imageUser.setIsAuthoriseBefore(true);
        }
        return RetResponse.makeOKRsp(imageUser);
    }


    /**
     * 小程序code换取oppenId
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/init")
    public RetResult authorizeLogin(@RequestBody WxUser wxUser) {

        GeneralToken generalToken = new GeneralToken();
        if (wxUser != null && StringUtils.isNotBlank(wxUser.getCode())) {
            //获取openId
            generalToken = userService.getWxOpeinId(wxUser);

            if (StringUtils.isNotBlank(generalToken.getErrmsg())) {
                log.error("微信服务器返回错误");
                return RetResponse.makeErrRsp(generalToken.getErrmsg());
            }
            userService.storeWxUserInfo(wxUser);
        }
        log.info("获取openid成功：{}", generalToken.getOpenid());
        //返回openid和token给前端
        String token = TokenUse.sign("", generalToken.getOpenid());
        generalToken.setToken(token);
        return RetResponse.makeOKRsp(generalToken);
    }


    /**
     * 小程序授权获取用户信息
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/authorizeGetUserInfo")
    public RetResult authorizeGetUserInfo(@RequestBody WxUser wxUser) {
//        判断是用户之前是否授权过
        //授权过，数据库查询，更新token返回给用户
     /*   ImageUser imageUser = imageUserMapper.selectOne(new QueryWrapper<ImageUser>().lambda().eq(ImageUser::getOpenid, wxUser.getOpenid()));
        if (imageUser != null) {
            return RetResponse.makeOKRsp(GeneralToken.builder().token(TokenUse.sign(wxUser.getNickName(), imageUser.getOpenid())));
        }*/
        userService.storeWxUserInfo(wxUser);
        //返回一个token给前端
//        Map<String,String>  map  = new HashMap<>();
//        map.put("token",token);
        //把用户信息放在redis
        return RetResponse.makeOKRsp("授权成功了");
    }


}
