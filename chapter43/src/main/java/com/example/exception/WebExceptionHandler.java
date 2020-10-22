package com.example.exception;

import com.example.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <h1>全局异常处理器</h1>
 * Created by hanqf on 2020/10/22 15:27.
 */

@Slf4j
@ControllerAdvice
public class WebExceptionHandler {

    /**
     * 拦截自定义异常
    */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public CommonResponse customerException(CustomException exception){
        exception.printStackTrace();
        log.error(exception.getMessage());
        return CommonResponse.error(exception);
    }

    /**
     * 参数绑定异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public CommonResponse methodArgumentNotValidException(MethodArgumentNotValidException exception){
        exception.printStackTrace();
        log.error(exception.getMessage());
        return CommonResponse.error(new CustomException(CustomExceptionType.USER_INPUT_ERROR,exception.getMessage()));
    }

    /**
     * 拦截其它异常，基本上就是运行时异常
    */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonResponse exception(Exception exception){
        exception.printStackTrace();
        log.error(exception.getMessage());
        return CommonResponse.error(new CustomException(CustomExceptionType.OTHER_ERROR));
    }
}
