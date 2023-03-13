package com.oxcrane.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oxcrane.reggie.common.CustomException;
import com.oxcrane.reggie.entity.Category;
import com.oxcrane.reggie.entity.Dish;
import com.oxcrane.reggie.entity.Setmeal;
import com.oxcrane.reggie.mapper.CategoryMapper;
import com.oxcrane.reggie.service.CategoryService;
import com.oxcrane.reggie.service.DishService;
import com.oxcrane.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类,删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
//        构造条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        添加查询条件，根据id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);

        long count1 = dishService.count(dishLambdaQueryWrapper);
//        查询当前分类是否关联了菜品，如果关联了菜品，抛出业务异常
        if (count1 > 0) {
//            已经关联了菜品，抛出业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
//        构造条件构造器
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        添加查询条件，根据id进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        long count2 = setmealService.count(setmealLambdaQueryWrapper);
//        查询当前分类是否关联了套餐，如果关联了菜品，抛出业务异常
        if (count2 > 0) {
//            已经关联的套餐，抛出业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

//        正常删除分类
        super.removeById(id);
    }
}
