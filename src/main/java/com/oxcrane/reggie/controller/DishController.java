package com.oxcrane.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oxcrane.reggie.common.R;
import com.oxcrane.reggie.dto.DishDto;
import com.oxcrane.reggie.entity.Category;
import com.oxcrane.reggie.entity.Dish;
import com.oxcrane.reggie.entity.DishFlavor;
import com.oxcrane.reggie.service.CategoryService;
import com.oxcrane.reggie.service.DishFlavorService;
import com.oxcrane.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
//        构造分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);

//        构造条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper();

//        添加分页查询条件
        lambdaQueryWrapper.like(name != null, Dish::getName, name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

//        执行查询
        dishService.page(pageInfo, lambdaQueryWrapper);

//        对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

//            对象拷贝
            BeanUtils.copyProperties(item, dishDto);

//            分类id
            Long categoryId = item.getCategoryId();

//            根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            String categoryName = category.getName();

            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据dishId查询菜品信息和对应的口味信息
     * @param dishId
     * @return
     */
    @GetMapping("/{dishId}")
    public R<DishDto> get(@PathVariable Long dishId) {

        DishDto dishDto = dishService.getByIdWithFlavor(dishId);

        return R.success(dishDto);
    }

    /**
     * 更新菜品信和和对应的口味信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("更新成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {

//        构建条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

//        添加条件
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
//        正在售卖的
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dtoList = list.stream().map((item) ->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            dishDto.setFlavors(dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId,item.getId())));

            dishDto.setCategoryName(categoryService.getOne(new LambdaQueryWrapper<Category>().eq(Category::getId,item.getCategoryId())).getName());

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dtoList);
    }
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//
////        构建条件查询器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//
////        添加条件
//        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
////        正在售卖的
//        queryWrapper.eq(Dish::getStatus, 1);
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }

    /**
     * 停售和起售
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Long status, @RequestParam(value = "ids") List<Long> ids){
        log.info("要更改起售和停售的id{}",ids.toString());
        dishService.updateStatus(status, ids);
        return R.success("操作成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteDish(@RequestParam(value = "ids") List<Long> ids) {
        log.info("要删除的菜品id{}", ids);
        dishService.deleteDish(ids);
        return R.success("删除成功");
    }



}
