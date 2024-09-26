package com.msb.crm.service;

import com.msb.crm.base.BaseService;
import com.msb.crm.dao.PermissionMapper;
import com.msb.crm.vo.Permission;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PermissionService extends BaseService<Permission,Integer> {

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 通过当前登录用户id查询当前登录用户拥有的资源列表 也就是查询对应资源的授权码
     * @param userId
     * @return
     */
    public List<String> queryUserHasRoleHasPermissionByUserId(Integer userId) {
        return permissionMapper.queryUserHasRoleHasPermissionByUserId(userId);
    }
}
