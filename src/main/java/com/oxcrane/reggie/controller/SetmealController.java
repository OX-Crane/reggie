package com.oxcrane.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oxcrane.reggie.common.R;
import com.oxcrane.reggie.dto.SetmealDto;
import com.oxcrane.reggie.entity.Category;
import com.oxcrane.reggie.entity.Setmeal;
import com.oxcrane.reggie.entity.SetmealDish;
import com.oxcrane.reggie.service.CategoryService;
import com.oxcrane.reggie.service.SetmealDishService;
import com.oxcrane.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息:{}",setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

//        构造分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
//        构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
//        添加查询条件
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, queryWrapper);
//        对象拷贝————为什么不需要拷贝records，因为泛型不一样
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");

        List<Setmeal> setMealsRecords = pageInfo.getRecords();
        List<SetmealDto> setMealDtoRecords = setMealsRecords.stream().map((item) ->{

            SetmealDto setmealDto = new SetmealDto();
//            对象拷贝
            BeanUtils.copyProperties(item, setmealDto);

//            分类id
            Long categoryId = item.getCategoryId();
//            查询分类的名称
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();

            setmealDto.setCategoryName(categoryName);

            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setMealDtoRecords);

        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam(value = "ids") List<Long> ids) {
        log.info(ids.toString());
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 更新套餐的起售和停售的状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Long status,@RequestParam(value = "ids") List<Long> ids) {
        log.info("status:{},ids:{}",status,ids);
        setmealService.updateStatus(status, ids);
        return R.success("更新成功");
    }

    /**
     * 回显数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        log.info("回显套餐的id{}",id);
        SetmealDto setmealDto = setmealService.get(id);
        return R.success(setmealDto);
    }

    /**
     * 更新套餐
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        log.info("接收到的数据{}", setmealDto);
        setmealService.updateSetmeal(setmealDto);
        return R.success("更新成功");
    }

    /**
     * 获取套餐信息
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        log.info("套餐信息:{}",setmeal);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> setMeals = setmealService.list(queryWrapper);

        return R.success(setMeals);
    }



}
