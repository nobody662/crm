package com.msb.crm.controller;


import com.msb.crm.annoation.RequiredPermission;
import com.msb.crm.base.BaseController;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.enums.StateStatus;
import com.msb.crm.query.SaleChanceQuery;
import com.msb.crm.service.SaleChanceService;
import com.msb.crm.service.UserService;
import com.msb.crm.utils.CookieUtil;
import com.msb.crm.utils.LoginUserUtil;
import com.msb.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    /**
     * 分页查询
     * @param flag 如果flag值为1 则表示是客服开发计划 否则是营销数据
     * @param request
     * @param saleChanceQuery
     * @return
     */
    @RequiredPermission(code = "101001")
    @GetMapping("list")
    @ResponseBody
    public Map<String,Object> querySaleChanceByParams(Integer flag, HttpServletRequest request, SaleChanceQuery saleChanceQuery){
        //判断flag的值
        if(flag!=null && flag==1){
            //查询客户开发计划
            //设置分配状态
            saleChanceQuery.setState(StateStatus.STATED.getType());
            //设置分配人(当前用户登录的id)
            //从cookie中获得当前登录用户id
            Integer userId=LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQuery.setAssignMan(userId);
        }
        return saleChanceService.querySaleChancesByParams(saleChanceQuery);
    }
    //进入营销机会界面
    @RequiredPermission(code = "1010")
    @RequestMapping("index")
    public String index(){
        return "saleChance/sale_chance";
    }

    /**
     * 添加操作
     * @param request
     * @param saleChance
     * @return
     */
    @RequiredPermission(code = "101002")
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addSaleChance(HttpServletRequest request, SaleChance saleChance){
        //从cookie中搞活动当前用户的登录名
        String userName = CookieUtil.getCookieValue(request, "userName");
        //设置用户名给营销机会对象
        saleChance.setCreateMan(userName);
        //调用service层的添加方法
        saleChanceService.saveSaleChance(saleChance);
        return success("营销机会数据添加成功");
    }
    //进入营销机会添加页面或更新界面
    @RequestMapping("toSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer saleChanceId,HttpServletRequest request){

        //判断id是否为空
        if (saleChanceId!=null){
            //说明是更新操作 通过id查询数据
            SaleChance saleChance=saleChanceService.selectByPrimaryKey(saleChanceId);
            //请求转发将数据存到作用域
            request.setAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }

    /**
     * 更新操作
     * @param saleChance
     * @return
     */
    @RequiredPermission(code = "101004")
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance( SaleChance saleChance){
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会数据更新成功");
    }

    /**
     * 删除操作
     * @param ids
     * @return
     */
    @RequiredPermission(code = "101003")
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        saleChanceService.deleteSaleChance(ids);
        return success("营销机会删除成功!");
    }

    /**
     * 更新营销机会的开发状态
     * @param id
     * @param devResult
     * @return
     */
    @PostMapping("updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id,Integer devResult){

        saleChanceService.updateSaleChanceDevResult(id,devResult);
        return success("开发状态更新成功");
    }

}
