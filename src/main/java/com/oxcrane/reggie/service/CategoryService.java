package com.oxcrane.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oxcrane.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
