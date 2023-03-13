package com.oxcrane.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.oxcrane.reggie.common.BaseContext;
import com.oxcrane.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

//    路径匹配器，支持通配符
    public static final AntPathMatcher PathMatcher = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        Long threadId = Thread.currentThread().getId();
        log.info("拦截器线程Id为" + threadId);

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        1、获取本次请求的URL
        String requestURI = request.getRequestURI();

        log.info("拦截到请求：{}",requestURI);

//        定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "front/**"
        };
//        2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
//        3、如不需要处理，直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request, response);
            return;
        }
//        4、判断登录状态、如已登陆，则直接放行
        Long employeeId = (Long) request.getSession().getAttribute("employee");
        if (employeeId != null){
            log.info("用户已登陆,用户id:{}",employeeId);
            BaseContext.setCurrentId(employeeId);
            filterChain.doFilter(request, response);
            return;
        }
//        5、如果未登录则返回未登录结果，通过输出流的方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            boolean match = PathMatcher.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
