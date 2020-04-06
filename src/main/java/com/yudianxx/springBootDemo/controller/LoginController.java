package com.yudianxx.springBootDemo.controller;

import com.yudianxx.springBootDemo.constants.TokenUse;
import com.yudianxx.springBootDemo.model.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangyongwen
 * @date 2020/4/2
 * @Description
 */
@RestController
@RequestMapping("/meizitu/Api")
@Slf4j
public class LoginController {

    /**
     * 登陆认证
     * @param userCode      用户名
     * @param password      用户密码
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(String userCode, String password){
//验证用户登陆信息于数据库中的用户信息是否一致
        Image ub = new Image();
        if (ub != null) {
            String token= TokenUse.sign("111",111);
            if (token!=null){
                return token;
            }
        }
        return null;
    }
}
