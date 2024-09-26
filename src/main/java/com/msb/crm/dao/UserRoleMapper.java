package com.msb.crm.dao;

import com.msb.crm.base.BaseMapper;
import com.msb.crm.vo.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {

    //根据用户id查询记录
    Integer countUserRoleByUserId(Integer userId);

    //根据用户id删除记录
    Integer deleteUserRoleByUserId(Integer userId);
}