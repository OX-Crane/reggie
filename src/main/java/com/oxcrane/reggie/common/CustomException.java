package com.oxcrane.reggie.common;

/**
 * 自定义业务异常
 */
public class CustomException extends RuntimeException{

    public CustomException(String message) {
//        调用父类的有参构造器
        super(message);
    }

}
