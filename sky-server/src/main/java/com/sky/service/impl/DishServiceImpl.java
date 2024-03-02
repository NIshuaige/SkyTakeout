/**
 * @Author：乐
 * @Package：com.sky.service.impl
 * @Project：sky-take-out
 * @name：DishServiceImpl
 * @Date：2024/2/29 0029  22:05
 * @Filename：DishServiceImpl
 */
package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;


    /**
     * 新增菜品及其口味
     * @param dishDTO
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //向菜品表中插入数据
        dishMapper.insert(dish);

        //获取insert语句生成的主键值（菜品id）
        Long dishId = dish.getId();
        //口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        //判断是否有口味
        if (flavors != null && flavors.size() >0){
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
            }

            //有口味，将口味插入口味表中
            //批量插入
           dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 菜品分类查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //读取id
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            //判断该菜品的status是否为起售
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //起售的菜品不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
            //禁售的菜品可以删除
                //判断该菜品是否关联套餐
            List<Long> setmealIds= setmealDishMapper.getSetmealIdsByDishIds(ids);
            if (setmealIds != null && setmealIds.size() > 0){
                    //该菜品关联了套餐不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
            //当前菜品没有关联套餐可以删除
                //删除菜品
            for (Long id : ids) {
                dishMapper.deleteById(id);
                //删除菜品关联的口味
                dishFlavorMapper.deleteByDishId(id);
            }

        }



    /**
     * 修改菜品
     * @param dishDTO
     */
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

//        dishMapper.update(dish);

    }


    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    public DishVO getById(Long id) {
        DishVO dishVO = new DishVO();
        //查询菜品
        Dish dish = dishMapper.getById(id);
        //查询菜品关联的口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.getDishFlavorByDishId(id);

        //将查询到的菜品及其关联的口味放入dishVO
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }
}


