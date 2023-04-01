package com.oxcrane.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }
    /**
     * 处理自定义异常CustomException
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }

    /**
     * 捕捉并处理图片文件失效
     * D:\img\61d20592-b37f-4d72-a864-07ad5bb8f3bb.jpg (系统找不到指定的文件。)
     * @param exception
     * @return
     */
    @ExceptionHandler({FileNotFoundException.class})
    public R<String> fileExceptionHandler(FileNotFoundException exception) {
        if (exception.getMessage().contains("(系统找不到指定的文件。)")) {
            String msg = exception.getMessage();
            String pictureName = msg.substring(msg.lastIndexOf("\\") + 1, msg.lastIndexOf("("));
            log.error("图片文件失效name:{}",pictureName);
            return R.error("图片文件失效");
        }
        return R.error("未知错误");
    }
}
