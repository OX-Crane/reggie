package com.oxcrane.reggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果类，服务端最终返回的结果最终会封装成此类
 */
@Data
public class R<T> {
//    编码：1为成功，0为失败
    private Integer code;
//    错误提示信息
    private String msg;
//    数据
    private T data;
//    动态数据
    private Map map = new HashMap<>();

    public static <T> R<T> success(T object){
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg){
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value){
        this.map.put(key,value);
        return this;
    }
}
