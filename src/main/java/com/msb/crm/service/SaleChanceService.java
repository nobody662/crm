package com.msb.crm.service;

import com.github.pagehelper.PageHelper;

import com.github.pagehelper.PageInfo;
import com.msb.crm.base.BaseService;
import com.msb.crm.dao.SaleChanceMapper;
import com.msb.crm.enums.DevResult;
import com.msb.crm.enums.StateStatus;
import com.msb.crm.query.SaleChanceQuery;
import com.msb.crm.utils.AssertUtil;
import com.msb.crm.utils.PhoneUtil;

import com.msb.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {

    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 分页查询和多条件查询
     * @param saleChanceQuery
     * @return
     */
    public Map<String,Object> querySaleChancesByParams(SaleChanceQuery saleChanceQuery){

        Map<String,Object> map=new HashMap<String,Object>();

        //开启分页
        PageHelper.startPage(saleChanceQuery.getPage(),saleChanceQuery.getLimit());
        //得到分页对象
        PageInfo<SaleChance> pageInfo=new PageInfo<>(saleChanceMapper.selectByParams(saleChanceQuery));

        //设置mapper对象
        map.put("code",0);
        map.put("msg","");
        map.put("count",pageInfo.getTotal());
        //设置分页好的列表
        map.put("data",pageInfo.getList());

        return  map;
    }

    /**
     * 添加营销机会操作
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSaleChance(SaleChance saleChance){
        /**
         * 1.参数校验
         *      customerName  客户名非空
         *      linkMan  非空
         *      linkPhone  非空 11位手机号
         * 2. 设置相关参数默认值
         *       state 默认未分配   如果选择分配人  state 为已分配状态
         *       assignTime 默认空   如果选择分配人  分配时间为系统当前时间
         *       devResult  默认未开发  如果选择分配人 devResult 为开发中 0-未开发  1-开发中 2-开发成功 3-开发失败
         *       isValid  默认有效(1-有效  0-无效)
         *       createDate  updateDate:默认系统当前时间
         * 3.执行添加 判断添加结果
         */
        //1.参数校验
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
    //2.设置相关默认值
        saleChance.setIsValid(1);
    //系统默认时间
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        //判断是否设置了分配人 如果为空 则没设置
        if(StringUtils.isBlank(saleChance.getAssignMan())){
            //设置分配状态 调用枚举类
            saleChance.setState(StateStatus.UNSTATE.getType());
            //设置开发状态 调用枚举类
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
            //设置指派时间
            saleChance.setAssignTime(new Date());
        }else {
            //不为空则说明有分配人 通用设置默认值
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setDevResult(DevResult.DEVING.getStatus());
            saleChance.setAssignTime(new Date());
        }
        //3.执行添加操作,判断受影响的行数
        AssertUtil.isTrue(insertSelective(saleChance)<1,"机会数据添加失败!");
    }


    private void checkParams(String customerName, String linkMan, String linkPhone) {
        //非空校验
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"客户名不能为空!");

        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"联系人不能为空!");

        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"联系号码不能为空!");

        AssertUtil.isTrue(!(PhoneUtil.isMobile(linkPhone)),"联系号码格式不正确!");
    }


    /**
     * 更新营销机会操作
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance){
        /**
         * 1.参数校验
         *     id 记录必须存在
         *     customerName  客户名非空
         *     linkMan  非空
         *     linkPhone  非空 11位手机号
         * 2.设置相关参数值
         *     updateDate  系统当前时间
         *      assignMan  指派人
         *      原始记录 未分配 修改后 已分配(分配人是否存在)
         *      修改后设置分配人
         *          state   0--->1
         *          assignTime   设置分配时间 系统时间
         *          devResult  0--->1
         *       原始记录  已分配  修改后  未分配
         *       修改后取消设置分配人
         *         state 1-->0
         *         assignTime  null
         *         devResult 1-->0
         *         判断修改前后是否同一个指派人 如果是就不用操作 不是就更新
         *  3.执行更新 判断结果
         */
        /*1.参数校验*/
        //查看id是否为空
        AssertUtil.isTrue(null==saleChance.getId(),"待更新记录不存在!");
        //通过主键查询对象是否存在
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(temp==null,"待更新记录不存在!");
        //参数校验
        checkParams(saleChance.getCustomerName(),saleChance.getLinkMan(), saleChance.getLinkPhone());
        /*2.设置相关参数值*/
        // updateDate 更新时间（变为系统当前时间）
        saleChance.setUpdateDate(new Date());
        // assignMan 指派人
        // 判断原始数据是否存在
        if (StringUtils.isBlank(temp.getAssignMan())) { // 不存在
            // 判断修改后的值是否存在
            if (!StringUtils.isBlank(saleChance.getAssignMan())) { // 修改前为空，修改后有值
                // assignTime 指派时间 -> 设置为系统当前时间
                saleChance.setAssignTime(new Date());
                // 分配状态 1 = 已分配
                saleChance.setState(StateStatus.STATED.getType());
                // 开发状态 1 = 开发中
                saleChance.setDevResult(DevResult.DEVING.getStatus());
            }
        } else { // 存在
            // 判断修改后的值是否存在
            if (StringUtils.isBlank(saleChance.getAssignMan())) { // 修改前有值，修改后为空
                // assignTime 指派时间 -> 设置为 null
                saleChance.setAssignTime(null);
                // 分配状态 0 = 未分配
                saleChance.setState(StateStatus.UNSTATE.getType());
                // 开发状态 0 = 未开发
                saleChance.setDevResult(DevResult.UNDEV.getStatus());
            } else { // 修改前有值，修改后有值，且同一用户
                // 判断修改前后是否是同一用户
                if (!saleChance.getAssignMan().equals(temp.getAssignMan())) {
                    //不同用户 则更新指派时间
                    saleChance.setAssignTime(new Date());
                }else {
                    saleChance.setAssignTime(temp.getAssignTime());
                }
            }
        }
        /*3.执行更新操作*/
        AssertUtil.isTrue(updateByPrimaryKeySelective(saleChance)<1,"营销机会更新失败!");
    }

    /**
     * 删除营销机会操作
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids){
        //判断id是否为空
        AssertUtil.isTrue(null==ids||ids.length<1,"待删除记录不存在!");
        //执行删除(更新) 操作 判断受影响的行数
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids) !=ids.length,"营销机会数据删除失败!");
    }


    /**
     * 更新营销计划的开发状态
     * @param id
     * @param devResult
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChanceDevResult(Integer id, Integer devResult) {
        //判断id是否为空
        AssertUtil.isTrue(null==id,"待更新数据不存在!");
        //通过id查询营销机会数据
        SaleChance saleChance=saleChanceMapper.selectByPrimaryKey(id);
        //判断对象是否为空
        AssertUtil.isTrue(null==saleChance,"待更新数据不存在!");

        //设置开发状态
        saleChance.setDevResult(devResult);

        //执行更新操作 判断受影响的行数
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)!=1,"开发状态更新失败!");
    }
}
