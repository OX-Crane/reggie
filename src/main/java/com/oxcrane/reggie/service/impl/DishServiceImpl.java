package com.oxcrane.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oxcrane.reggie.common.CustomException;
import com.oxcrane.reggie.dto.DishDto;
import com.oxcrane.reggie.entity.Dish;
import com.oxcrane.reggie.entity.DishFlavor;
import com.oxcrane.reggie.mapper.DishMapper;
import com.oxcrane.reggie.service.DishFlavorService;
import com.oxcrane.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {

//        保存菜品的基本信息到菜品表dish
        this.save(dishDto);
//        菜品id
        Long dishId = dishDto.getId();

//        菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());


//        保存菜品口味数据到菜品口味表dish_flavor
//        saveBatch()批量保存
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据dishId查询菜品信息和对应的口味信息
     * @param DishId
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long DishId) {
//        查询当前菜品基本信息，从dish表查询
        Dish dish = this.getById(DishId);

        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish, dishDto);
//        查询当前菜品口味信息，从dishFlavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());

        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品信和和对应的口味信息
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {
//        更新dish表基本信息
        this.updateById(dishDto);
//        清理当前菜品对应口味信息--dish_flavor表的delete操作
//        构造条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dishDto.getId() != null, DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(queryWrapper);
//        再添加当前提交过来的口味信息--dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors =  flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 停售和起售菜品 修改dish表status字段
     * @param ids
     */
    @Override
    public void updateStatus(Long status,List<Long> ids) {
//        构造条件生成器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, Dish::getId, ids);

        List<Dish> dishes = this.list(queryWrapper);

        for (Dish i: dishes) {
//        修改菜品的状态
            i.setStatus(status.intValue());
        }
//        保存菜品
        this.updateBatchById(dishes);
    }

    /**
     * 删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteDish(List<Long> ids) {
//        构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(ids != null, Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);

        long count = this.count(queryWrapper);

        if (count > 0) {
            throw new CustomException("菜品正在售卖，请先停止起售");
        }

//        删除菜品的口味
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId,ids);


        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);

        for (DishFlavor i: list) {
            dishFlavorService.removeById(i.getId());
        }

        this.removeBatchByIds(ids);

    }
}
