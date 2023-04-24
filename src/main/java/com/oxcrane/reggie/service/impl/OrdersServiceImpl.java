package com.oxcrane.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oxcrane.reggie.common.BaseContext;
import com.oxcrane.reggie.entity.*;
import com.oxcrane.reggie.mapper.OrdersMapper;
import com.oxcrane.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.ws.soap.Addressing;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     */
    @Transactional
    @Override
    public void submit(Orders orders) {
//        获取用户id
        Long userId = BaseContext.getCurrentId();
        orders.setUserId(userId);
//        查询收货人、手机号、地址
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        String consignee = addressBook.getConsignee();
        String phone = addressBook.getPhone();
        String detail = addressBook.getDetail();
        orders.setConsignee(consignee);
        orders.setPhone(phone);
        orders.setAddress((addressBook.getProvinceName() == null? "": addressBook.getProvinceCode())
                +(addressBook.getCityName() == null? "": addressBook.getCityCode())
                +(addressBook.getDistrictName() == null? "": addressBook.getDistrictCode())
                +detail);
//        查询用户名
        User user = userService.getById(orders.getUserId());
        String userName = user.getName();
        orders.setUserName(userName);
//        查询当前用户的购物车
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
//        设置时间
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
//        生成订单号
        long number = IdWorker.getId();
        orders.setNumber(String.valueOf(number));
//        向订单表插入一条数据
        orders.setStatus(2);
//        计算总金额和
        AtomicInteger amount = new AtomicInteger(0);
        for (ShoppingCart item:shoppingCarts) {
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
        }
        orders.setAmount(new BigDecimal(amount.get()));
        this.save(orders);
        log.info("插入完成的订单信息{}",orders);

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) ->{
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orders.getId());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            return orderDetail;
        }).collect(Collectors.toList());
//        向订单明细表插入数据（多条）
        orderDetailService.saveBatch(orderDetails);
//        下单成功，清空购物车
        shoppingCartService.remove(queryWrapper);
    }
}
