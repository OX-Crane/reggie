package com.oxcrane.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oxcrane.reggie.dto.DishDto;
import com.oxcrane.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //    新增菜品，同时插入到菜品和菜品口味两张表：dish、dish_flavor
    public void saveWithFlavor(DishDto dishDto);

//    根据dishId查询菜品信息和对应的口味信息
    public DishDto getByIdWithFlavor(Long DishId);

//    更新菜品信和和对应的口味信息
    public void updateWithFlavor(DishDto dishDto);

//    停售菜品 修改dish表status字段
    public void updateStatus(Long status, List<Long> ids);

//    删除菜品
    public void deleteDish(List<Long> ids);
}
