package com.oxcrane.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oxcrane.reggie.common.CustomException;
import com.oxcrane.reggie.dto.SetmealDto;
import com.oxcrane.reggie.entity.Setmeal;
import com.oxcrane.reggie.entity.SetmealDish;
import com.oxcrane.reggie.mapper.SetmealMapper;
import com.oxcrane.reggie.service.SetmealDishService;
import com.oxcrane.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
//    事务的注解
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
//        保存套餐的基本信息，操作setmeal，执行insert操作
//        在执行完save方法后，会更新dto，所以他有id了
        this.save(setmealDto);
//        保存套餐和菜品的关联信息，操作setmeal_dish表，执行insert操作

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时要删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {

//        查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = (int)this.count(queryWrapper);

//        如果不能删除抛出业务异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖中不能删除");
        }
//        可以删除，删除套餐表中数据setmeal
        this.removeByIds(ids);

//        可以删除，再删除关系表中信息
//        delete from setmeal_dish where setmeal_id in (1, 2, 3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
//        删除关系表中的数据——setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);

    }
}
