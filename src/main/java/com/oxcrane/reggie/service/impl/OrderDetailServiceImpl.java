package com.oxcrane.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oxcrane.reggie.entity.OrderDetail;
import com.oxcrane.reggie.mapper.OrderDetailMapper;
import com.oxcrane.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
