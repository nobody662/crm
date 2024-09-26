package com.msb.crm.dao;

import com.msb.crm.base.BaseMapper;
import com.msb.crm.vo.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    //通过角色id查询权限记录
    Integer countPermissionByRoleId(Integer roleId);

    //通过角色id删除权限
    void deletePermissionByRoleId(Integer roleId);

    //查询指定角色已经授权过的资源列表(查询角色拥有的资源id)
    List<Integer> queryRoleHasModuleIdsByRoleId(Integer roleId);

    //通过当前登录用户id查询当前登录用户拥有的资源列表 也就是查询对应资源的授权码
    List<String> queryUserHasRoleHasPermissionByUserId(Integer userId);

    //通过资源id查询权限表中是否存在数据 用于菜单管理删除功能
    Integer countPermissionByModuleId(Integer id);

    //删除指定资源id的权限记录 用于菜单管理的删除功能
    Integer deletePermissionByModuleId(Integer id);
}