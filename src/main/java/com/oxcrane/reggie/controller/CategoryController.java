package com.oxcrane.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oxcrane.reggie.common.R;
import com.oxcrane.reggie.entity.Category;
import com.oxcrane.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("要添加的分类为：category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 修改菜品
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("要修改的菜品为：category{}",category);
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(String ids) {
        log.info("要删除的菜品id为{}",ids);
        Long id = Long.valueOf(ids);
        categoryService.remove(id);
//        categoryService.removeById(Long.valueOf(ids));
        return R.success("删除成功");
    }

    /**
     * 分页查询
     *
     * Page<T> pageInfo = new Page<>(page, pageSize);
     * LambdaQueryWrapper<T> lambdaQueryWrapper = new LambdaQueryWrapper<>();
     * 添加分页条件
     * lambdaQueryWrapper.like(StringUtils.isNotEmpty(参数),T::getxx(),xx);
     * lambdaQueryWrapper.orderByDesc(参数)
     * 查询
     * categoryService.page(pageInfo,lambdaQueryWrapper);
     * return R.success(pageInfo);
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        log.info("菜品分页,当前页码{},页面大小{}",page,pageSize);
//        构造分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
//        构造条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort);
//        执行查询
        categoryService.page(pageInfo,lambdaQueryWrapper);

        return R.success(pageInfo);
    }

    @GetMapping("list")
    public R<List<Category>> list(Category category) {
//        条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        添加条件
        lambdaQueryWrapper.eq(category.getType() != null, Category::getType, category.getType());
//        添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lambdaQueryWrapper);

        return R.success(list);
    }



}
