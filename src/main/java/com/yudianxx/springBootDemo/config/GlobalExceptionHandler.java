package com.yudianxx.springBootDemo.config;

import com.yudianxx.springBootDemo.model.responseVo.RetResponse;
import com.yudianxx.springBootDemo.model.responseVo.RetResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author huangyongwen
 * @date 2020/3/13
 * @Description 全局controller异常捕捉
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    RetResult handleException(Exception e) {
        log.error("出错啦：", e);
        return RetResponse.makeErrRsp(RetResponse.FAIL);
    }
}
