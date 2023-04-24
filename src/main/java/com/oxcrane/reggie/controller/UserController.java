package com.oxcrane.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oxcrane.reggie.common.CustomException;
import com.oxcrane.reggie.common.R;
import com.oxcrane.reggie.entity.User;
import com.oxcrane.reggie.service.UserService;
import com.oxcrane.reggie.utils.SMSUtils;
import com.oxcrane.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${sms.signName}")
    private String signName;

    @Value("${sms.templateCode}")
    private String templateCode;


    /**
     * 发送手机验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        log.info("发送验证码的手机号:{}",user.getPhone());
//        获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            //        生成随机4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码为:{}",code);
            //        调用阿里云提供短信服务的api发送短信
            SMSUtils.sendMessage(signName,templateCode,phone,code);
            //        需要将生成的验证码保存起来Session
//            手机号作为key，验证码为code
            session.setAttribute(phone,code);

            return R.success("手机验证发送成功");
        }

        return R.error("短信发送失败");

    }

    /**
     * 移动端登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info("登录接收到的参数{}", map);
//        获取手机号
        String phone = map.get("phone").toString();
//        获取验证码
        String code = map.get("code").toString();
//        从session中获得保存的code
        String realCode = null;
//        为了解决先输入手机号获取验证码，然后输入其他手机号填入正确的code产生的空指针异常
        try {
            realCode = session.getAttribute(phone).toString();
        } catch (Exception e) {
            throw new CustomException("错误的手机号");
        }
//        比对两个code，若相同则则登录成功将user放入session，否则返回错误
        if (StringUtils.equals(code, realCode)) {
//            比对成功，说明登陆成功
//            判断是否为新用户，若为新用户则进行注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
//            查询是否为新用户
            User user = userService.getOne(queryWrapper);
//            是新用户
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
//            用户被禁用
            if (user.getStatus() == 0) {
                return R.error("用户被禁用");
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登陆失败");
    }




}
