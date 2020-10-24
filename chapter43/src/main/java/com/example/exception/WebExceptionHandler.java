package com.example.exception;

import com.example.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * <h1>全局异常处理器</h1>
 * Created by hanqf on 2020/10/22 15:27.
 */

@Slf4j
@ControllerAdvice
public class WebExceptionHandler {

    /**
     * 拦截自定义ModelViewException异常
     */
    @ExceptionHandler(ModelViewException.class)
    public ModelAndView modelViewException(HttpServletRequest request, ModelViewException exception){
        exception.printStackTrace();
        log.error(exception.getMessage());
        ModelAndView modelAndView = new ModelAndView("common/error");
        modelAndView.addObject("exception",exception);
        modelAndView.addObject("url",request.getRequestURL());
        return modelAndView;
    }


    /**
     * 拦截自定义CustomException异常
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
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public CommonResponse bindException(BindException exception){
        exception.printStackTrace();
        log.error(exception.getMessage());
        FieldError fieldError = exception.getBindingResult().getFieldError();
        return CommonResponse.error(new CustomException(CustomExceptionType.USER_INPUT_ERROR,fieldError.getDefaultMessage()));
    }

    /**
     * 参数绑定异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public CommonResponse methodArgumentNotValidException(MethodArgumentNotValidException exception){
        exception.printStackTrace();
        log.error(exception.getMessage());
        FieldError fieldError = exception.getBindingResult().getFieldError();
        return CommonResponse.error(new CustomException(CustomExceptionType.USER_INPUT_ERROR,fieldError.getDefaultMessage()));
    }

    /**
     * 参数解析异常，如json数据格式不匹配
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public CommonResponse illegalArgumentException(IllegalArgumentException exception){
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
