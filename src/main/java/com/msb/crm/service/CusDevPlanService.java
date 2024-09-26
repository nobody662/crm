package com.msb.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.msb.crm.base.BaseService;
import com.msb.crm.dao.CusDevPlanMapper;
import com.msb.crm.dao.SaleChanceMapper;
import com.msb.crm.query.CusDevPlanQuery;
import com.msb.crm.utils.AssertUtil;
import com.msb.crm.vo.CusDevPlan;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class  CusDevPlanService extends BaseService<CusDevPlan,Integer> {


    @Resource
    private CusDevPlanMapper cusDevPlanMapper;

    @Resource
    private SaleChanceMapper saleChanceMapper;
    /**
     * 分页查询
     * @param cusDevPlanQuery
     * @return
     */

    public Map<String,Object> queryCusDevPlansByParams(CusDevPlanQuery cusDevPlanQuery){
        Map<String,Object> map=new HashMap<String,Object>();
        //开启分页
        PageHelper.startPage(cusDevPlanQuery.getPage(),cusDevPlanQuery.getLimit());
        //得到分页对象
        PageInfo<CusDevPlan> pageInfo=new PageInfo<>(cusDevPlanMapper.selectByParams(cusDevPlanQuery));
        //设置mapper对象
        map.put("code",0);
        map.put("msg","");
        map.put("count",pageInfo.getTotal());
        //设置分页好的列表
        map.put("data",pageInfo.getList());
        return  map;
    }


    /**
     * 添加客户开发计划
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCusDevPlan(CusDevPlan cusDevPlan){
/**
         * 1.参数校验
         *    机会id 非空 记录必须存在
         *    计划项内容非空
         *    计划项时间非空
         * 2. 参数默认值
         *    is_valid  1
         *    createDate 系统时间
         *    updateDate  系统时间
         * 3.执行添加 判断结果*/

       /*1。参数校验*/
        checkParams(cusDevPlan);
        /*2.设置默认值*/
        cusDevPlan.setIsValid(1);
        cusDevPlan.setCreateDate(new Date());
        cusDevPlan.setUpdateDate(new Date());
        /*3.执行添加 判断结果*/
        AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan) !=1,"计划项记录添加失败!");
    }


    private void checkParams(CusDevPlan cusDevPlan) {
        //非空判断
        Integer sid = cusDevPlan.getSaleChanceId();
        AssertUtil.isTrue(null==sid||saleChanceMapper.selectByPrimaryKey(sid)==null,"营销机会id不能为空");
        //计划向内容 非空
        AssertUtil.isTrue(StringUtils.isBlank(cusDevPlan.getPlanItem()),"计划项内容不能为空!");
        //计划时间 非空
        AssertUtil.isTrue(null==cusDevPlan.getPlanDate(),"计划事件不能为空!");
    }


    /**
     * 更新客户开发计划
     * @param cusDevPlan
     *  * 1.参数校验
     *          *    id 记录必须存在
     *          *    机会id 非空 记录必须存在
     *          *    计划项内容非空
     *          *    计划项时间非空
     *          * 2. 参数默认值
     *          *    updateDate  系统时间
     *          * 3.执行更新 判断结果*/
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCusDevPlan(CusDevPlan cusDevPlan){

        /*1。参数校验*/
        AssertUtil.isTrue(null==cusDevPlan.getId()||cusDevPlanMapper.selectByPrimaryKey(cusDevPlan.getId())==null,"待更新数据id不存在！");
        /*2.设置默认值*/
        //修改更新时间
        cusDevPlan.setUpdateDate(new Date());
        /*3.执行添加 判断结果 调用base中已存在的方法*/
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)!=1,"计划项更新失败!");
    }

    /**
     * 删除计划项
     * @param id
     * 1.参数校验
     *    id 记录必须存在
     *    机会id 非空 记录必须存在
     *
     * 2. 修改isValid属性值
     *
     * 3.执行更新 判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCusDevPlan(Integer id){
        /*1.判断id是否为空*/
        AssertUtil.isTrue( null==id,"待删除的记录不存在!");
        //通过id查询对象
        CusDevPlan cusDevPlan= cusDevPlanMapper.selectByPrimaryKey(id);
        /*2. 修改isValid属性值 也就是删除操作 更新修改时间*/
        cusDevPlan.setIsValid(0);
        cusDevPlan.setUpdateDate(new Date());
        /*3.执行更新 判断结果*/
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)!=1,"计划项数据删除失败!");
    }


}
