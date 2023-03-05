package com.oxcrane.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oxcrane.reggie.common.R;
import com.oxcrane.reggie.entity.Employee;
import com.oxcrane.reggie.service.EmployeeService;
import com.oxcrane.reggie.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
//        1、将页面提交的密码password进行md5加密
        String password = employee.getPassword();
        password =DigestUtils.md5DigestAsHex(password.getBytes());
//        2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
//        3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("用户不存在");
        }
//        4、密码对比，如果不一致则返回失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }
//        5、查询员工状态，如果是禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0){
            return R.error("账号被禁用");
        }
//        6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
//        清理Session中保存的当前员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


}
