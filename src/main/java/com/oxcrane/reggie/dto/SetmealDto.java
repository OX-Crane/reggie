package com.oxcrane.reggie.dto;

import com.oxcrane.reggie.entity.Setmeal;
import com.oxcrane.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
