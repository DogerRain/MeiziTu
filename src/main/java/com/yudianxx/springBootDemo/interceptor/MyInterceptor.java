package com.yudianxx.springBootDemo.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yudianxx.springBootDemo.annotation.DisableAuth;
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
        String token = request.getHeader("set-token");

        //使用注解 放行逻辑
        HandlerMethod method = (HandlerMethod) handler;
        DisableAuth auth = method.getMethod().getAnnotation(DisableAuth.class);
        if (auth != null) {
            return true;
        }
        if (token == null){
            returnJson(response, RetResponse.makeRsp(400, "没有权限访问"));
            return false;

        }

        if (token != "1"){
            returnJson(response, RetResponse.makeRsp(401, "token无效"));
            return false;
        }

        return true;
    }

    private void returnJson(HttpServletResponse response, Object object) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        JSONObject jsonObj = new JSONObject();
        try {
            writer = response.getWriter();
            jsonObj =(JSONObject) JSON.toJSON(object);
        } catch (IOException e) {
            jsonObj =(JSONObject) JSON.toJSON(RetResponse.makeUnKnowRsp());
            log.error("response error", e);
        } finally {
            writer.print(jsonObj);
            if (writer != null)
                writer.close();
        }
    }
}
