package com.msb.crm.dao;

import com.msb.crm.base.BaseMapper;
import com.msb.crm.vo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User,Integer> {

    //查询用户 通过用户名
     User SelectUserByName(String userName);

    //查询所有销售人员
     List <Map<String,Object>> queryAllSales();


}