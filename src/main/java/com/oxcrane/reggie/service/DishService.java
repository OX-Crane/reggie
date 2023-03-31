package com.oxcrane.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oxcrane.reggie.dto.DishDto;
import com.oxcrane.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    //    新增菜品，同时插入到菜品和菜品口味两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

//    根据dishId查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long DishId);

//    更新菜品信和和对应的口味信息
    public void updateWithFlavor(DishDto dishDto);
}
