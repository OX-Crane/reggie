package com.oxcrane.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
//Servlet（控制器）、Filter（过滤器）、Listener（监听器）可以直接通过
// @WebServlet、@WebFilter、@WebListener注解自动注册到Spring容器中，无需其他代码。
@ServletComponentScan
//开启事务管理
@EnableTransactionManagement
public class ReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动....");
    }


}
