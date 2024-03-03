/**
 * @Author：乐
 * @Package：com.sky.service.impl
 * @Project：sky-take-out
 * @name：SermealServiceImpl
 * @Date：2024/3/3 0003  12:49
 * @Filename：SermealServiceImpl
 */
package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;


    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Transactional
    public void insert(SetmealDTO setmealDTO) {
        //先存套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
            //新增套餐的状态都为停售
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.insert(setmeal);

        //在存套餐关联的菜品
            //获取套餐id
        Long setmealId = setmeal.getId();
            //将套餐id放入关联的菜品id中
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        setmealDishMapper.insertBatch(setmealDishes);

    }


    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }


    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    public SetmealVO getById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
        //查询套餐数据
        Setmeal setmeal= setmealMapper.getById(id);
        //查询套餐关联的菜品数据
        List<SetmealDish>  setmealDishes= setmealDishMapper.getBySetmealId(id);

        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    /**
     * 批量删除套餐及其关联的菜品
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //取出要删除的套餐id
        for (Long id : ids) {
            //判断该套餐是否为停售--起售中的菜品不能删除
//            setmealMapper


            //删除套餐
           setmealMapper.delete(id);

            //删除套餐关联的菜品
            setmealDishMapper.delete(id);
        }

    }
}
