package com.yudianxx.springBootDemo.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yudianxx.springBootDemo.annotation.DisableAuth;
import com.yudianxx.springBootDemo.constants.TokenUse;
import com.yudianxx.springBootDemo.model.responseVo.RetResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author huangyongwen
 * @date 2020/4/2
 * @Description
 */
@Slf4j
public class MyInterceptor implements HandlerInterceptor {


    //WebSecurityConfig配置的拦截URL都会进这里

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("拦截成功");


        String token = request.getHeader("token");

        //使用注解 放行逻辑
        HandlerMethod method = (HandlerMethod) handler;
        DisableAuth auth = method.getMethod().getAnnotation(DisableAuth.class);
        if (auth != null) {
            return true;
        }
        if (token.equals("meizitu")){
            return true;
        }


        if (token == null) {
            returnJson(response, RetResponse.makeRsp(400, "没有权限访问"));
            return false;

        }

        //解密token
        try {
            DecodedJWT decodeToken = JWT.decode(token);
            decodeToken.getSubject();
            decodeToken.getExpiresAt();
            log.info(decodeToken.getClaim("userName").asString());
            log.info(decodeToken.getClaim("passWord").asString());
//            decodeToken.getClaim("roles").asList(String.class).stream().forEach(record -> {
//                System.out.println("----roles=" + record);
//            });
        } catch (JWTDecodeException j) {
            returnJson(response, RetResponse.makeRsp(401, "token认证失败"));
            return false;
        }

//      验证token

        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(TokenUse.tokenSecRet)).build();
//            如果不需要解密，验证不出错就没问题
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            returnJson(response, RetResponse.makeRsp(401, "token认证失败"));
            return false;
        }
        return true;
    }

    private void returnJson(HttpServletResponse response, Object object) throws Exception {
        PrintWriter writer = null;
//        response.addHeader("Access-Control-Allow-Origin", "*");
//        response.addHeader("Access-Control-Allow-Credentials", "true");
//        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, HEAD");
//        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
//        response.addHeader("Access-Control-Max-Age", "3600");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        JSONObject jsonObj = new JSONObject();
        try {
            writer = response.getWriter();
            jsonObj = (JSONObject) JSON.toJSON(object);
        } catch (IOException e) {
            jsonObj = (JSONObject) JSON.toJSON(RetResponse.makeUnKnowRsp());
            log.error("response error", e);
        } finally {
            writer.print(jsonObj);
            if (writer != null)
                writer.close();
        }
    }
}
