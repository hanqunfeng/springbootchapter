package com.example.exception;

/**
 * <h1>视图方法异常</h1>
 * Created by hanqf on 2020/10/24 15:59.
 */


public class ModelViewException extends RuntimeException{
    /**
     * 将Throwable转换为ModelViewException
    */
    public static ModelViewException transfer(Throwable cause){
        return new ModelViewException(cause);
    }

    public ModelViewException(Throwable cause) {
        super(cause);
    }
}
