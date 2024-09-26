package com.msb.crm.dao;

import com.msb.crm.base.BaseMapper;
import com.msb.crm.vo.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleMapper extends BaseMapper<Role,Integer> {

    //查询所有角色列表 但只要id和roleName
    List <Map<String,Object>> queryAllRoles(Integer userId);

    Role selectByRoleName(String roleName);

    Role queryRoleByRoleName(String roleName);
}