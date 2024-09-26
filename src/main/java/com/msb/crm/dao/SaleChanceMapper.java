package com.msb.crm.dao;

import com.msb.crm.base.BaseMapper;
import com.msb.crm.vo.SaleChance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SaleChanceMapper extends BaseMapper<SaleChance,Integer> {
    /**
     * 多条件查询放入baseMapper中
     */


}