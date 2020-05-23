package com.yudianxx.springBootDemo.constants;

/**
 * @author huangyongwen
 * @date 2020/4/2
 * @Description
 */

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUse {
    //过期时间2*24小时
    private static final long overdeuTime = 2 *1440 * 60 * 1000;
    //私钥uuid生成，确定唯一性
    public static final String tokenSecRet = "fde35b32-0f47-46be-ae2a-49bcb7ed7d7f";

//    public static final String PICEA_JWT_KEY = "spring-boot-jwt-key~#^";

    // 请求的网址
    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    // 你的appid
    public static final String WX_LOGIN_APPID = "wxc054eda015470720";
    // 你的密匙
    public static final String WX_LOGIN_SECRET = "643d2467bfb0a8995c72deb58a996e93";

    /**
     * 生成token，用户退出后消失
     *
     * @param userName
     * @param passWord
     * @return
     */
    public static String sign(String userName, String passWord) {
        try {
            //设置过期时间
            Date date = new Date(System.currentTimeMillis() + overdeuTime);
            //token私钥加密
            Algorithm algorithm = Algorithm.HMAC256(tokenSecRet);
            //设置头部信息
            Map<String, Object> requestHender = new HashMap<>(2);
            requestHender.put("type", "JWT");
            requestHender.put("encryption", "HS256");
            long date1 = new Date().getTime();
            //返回带有用户信息的签名
            return JWT.create().withHeader(requestHender)
                    .withClaim("userName", userName)
                    .withClaim("passWord", passWord)
                    .withClaim("Time", date1)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 验证token是否正确
     *
     * @param token
     * @return
     */
    public static boolean tokenVerify(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(tokenSecRet);
            JWTVerifier verifier = JWT.require(algorithm).build();
            //验证
            DecodedJWT decodedJWT = verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取登陆用户token中的用户ID
     *
     * @param token
     * @return
     */
    public static int getUserID(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT.getClaim("userId").asInt();
    }


}