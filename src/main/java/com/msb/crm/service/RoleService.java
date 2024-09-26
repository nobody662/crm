package com.msb.crm.service;

import com.github.pagehelper.PageHelper;

import com.github.pagehelper.PageInfo;
import com.msb.crm.base.BaseService;
import com.msb.crm.dao.ModuleMapper;
import com.msb.crm.dao.PermissionMapper;
import com.msb.crm.dao.RoleMapper;
import com.msb.crm.dao.UserRoleMapper;
import com.msb.crm.utils.AssertUtil;
import com.msb.crm.vo.Permission;
import com.msb.crm.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class RoleService extends BaseService<Role,Integer> {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private ModuleMapper moduleMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 查询所有角色
     * @param userId
     * @return
     */
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }


//    public Map<String,Object> queryRolesByParams(RoleQuery roleQuery){
//        Map<String,Object> map=new HashMap<String,Object>();
//        PageHelper.startPage(roleQuery.getPage(),roleQuery.getLimit());
//        PageInfo<Role> pageInfo=new PageInfo<Role>(selectByParams(roleQuery));
//        map.put("code",0);
//        map.put("msg","");
//        map.put("count",pageInfo.getTotal());
//        map.put("data",pageInfo.getList());
//        return  map;
//    }
//
//
    /**
     * 角色添加操作
     * 1.参数校验
     *    角色名 非空 唯一
     * 2.参数默认值设置
     *    isValid
     *    createDate
     *    updateDate
     * 3.执行添加  判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRole(Role role){
        /*1.参数校验*/
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名称不能为空!");
        //通过用户名查询角色是否存在
        AssertUtil.isTrue(null !=roleMapper.selectByRoleName(role.getRoleName()),"该角色已存在!");
        /*2.设置默认值*/
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        /*3.执行添加操作 判断受影响的行数*/
        AssertUtil.isTrue(roleMapper.insertSelective(role)<1,"角色添加失败!");
    }

    /**
     * 行工具栏更新操作
     * 1.参数校验
     *      id 非空 唯一
     *    角色名 非空 唯一
     * 2.参数默认值设置
     *    updateDate
     * 3.执行更新  判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public  void updateRole(Role role){
        /*1.参数校验*/
        //id校验
        AssertUtil.isTrue(null==role.getId(),"待更新记录不存在!");
        AssertUtil.isTrue(null==roleMapper.selectByPrimaryKey(role.getId()),"待更新记录不存在!");

        //用户名校验
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名称不能为空!");
        //数据库查出的对象
        Role temp = roleMapper.selectByRoleName(role.getRoleName());
        //数据库查到的id和前端传过来的id不一致也不能改
        AssertUtil.isTrue(null != temp && temp.getId() !=role.getId() ,"该角色名称已存在!");

        /*2.设置默认值*/
        role.setUpdateDate(new Date());
        /*3.执行添加 判断结果 调用base中已存在的方法*/
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"角色更新失败!");
    }

    /**
     * 删除角色
     * 1.参数校验
     *    非空  记录必须存在
     * 2.查询用户角色表记录
     *     如果存在子表记录  删除子表记录
     * 3.执行角色删除操作 判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRole(Integer roleId){
        /*1.参数校验*/
        //id校验
        AssertUtil.isTrue(null==roleId,"待删除记录不存在!");
        Role role = roleMapper.selectByPrimaryKey(roleId);
        AssertUtil.isTrue(null== role,"待删除记录不存在!");

        /*2.设置默认值*/
        role.setIsValid(0);
        role.setUpdateDate(new Date());
        /*3.执行删除添加 判断结果 调用base中已存在的方法*/
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"角色记录删除失败!");

    }

    /**
     * 角色授权
     * 先清除原有的权限记录 在添加新的权限记录
     * @param roleId
     * @param mids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer roleId, Integer[] mids) {
        //1. 通过角色id查询相应的权限记录
       Integer count = permissionMapper.countPermissionByRoleId(roleId);
        //2。判断权限记录是否存在,存在则删除相应的权限记录
        if(count>0){
            //删除原有权限记录
            permissionMapper.deletePermissionByRoleId(roleId);
        }
        //3.如果没有记录添加授权记录
        if(mids !=null && mids.length>0){
            //定义Permission集合
            List<Permission> permissionList =new ArrayList<>();

            //遍历资源id数组
            for (Integer mid : mids) {
                Permission permission=new Permission();
                permission.setModuleId(mid);
                permission.setRoleId(roleId);
                //权限授权码 通过模块表中查询获得
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());

                //将对象设置到集合中
                permissionList.add(permission);
            }
            //4.执行批量添加操作 判断受影响的行数
            AssertUtil.isTrue(permissionMapper.insertBatch(permissionList)!=permissionList.size(),"角色授权失败") ;
        }
    }
}
