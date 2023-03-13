package com.oxcrane.reggie.common;

/**
 * 网页一次HTTP请求占用同一个线程
 * 基于ThreadLocal封装的工具类，用户保存和获取当前登录用户的id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
