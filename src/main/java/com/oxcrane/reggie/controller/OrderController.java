package com.oxcrane.reggie.controller;

import com.oxcrane.reggie.common.BaseContext;
import com.oxcrane.reggie.common.R;
import com.oxcrane.reggie.entity.AddressBook;
import com.oxcrane.reggie.entity.Orders;
import com.oxcrane.reggie.entity.User;
import com.oxcrane.reggie.service.AddressBookService;
import com.oxcrane.reggie.service.OrderDetailService;
import com.oxcrane.reggie.service.OrdersService;
import com.oxcrane.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private UserService userService;
    /**
     * 用户下单
     * @param orders
     * @return
     */

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单信息{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }
}
