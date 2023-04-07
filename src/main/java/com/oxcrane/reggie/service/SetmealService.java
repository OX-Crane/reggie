package com.oxcrane.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oxcrane.reggie.dto.SetmealDto;
import com.oxcrane.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时要删除套餐和菜品的关联数据
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /**
     * 更新套餐的起售和停售的状态, 操作setmeal表
     * @param status
     * @param ids
     */
    public void updateStatus(Long status, List<Long> ids);

    /**
     * 获得套餐的数据
     * @param id
     */
    public SetmealDto get(Long id);

    /**
     * 更新套餐菜品
     * @param setmealDto
     */
    public void updateSetmeal(SetmealDto setmealDto);
}
