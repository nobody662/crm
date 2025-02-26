package com.msb.crm.service;

import com.msb.crm.base.BaseService;
import com.msb.crm.dao.UserRoleMapper;
import com.msb.crm.vo.UserRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserRoleService extends BaseService<UserRole,Integer> {

    @Resource
    private UserRoleMapper userRoleMapper;


}
