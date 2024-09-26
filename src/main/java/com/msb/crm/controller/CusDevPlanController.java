package com.msb.crm.controller;

import com.msb.crm.base.BaseController;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.query.CusDevPlanQuery;
import com.msb.crm.service.CusDevPlanService;
import com.msb.crm.service.SaleChanceService;
import com.msb.crm.vo.CusDevPlan;
import com.msb.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("cus_dev_plan")
public class CusDevPlanController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    @Resource
    private CusDevPlanService cusDevPlanService;

    /**
     * 页面加载
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "cusDevPlan/cus_dev_plan";
    }

    /**
     * 打开开发和详情弹出框界面
     * @param sid
     * @return
     */
    @RequestMapping("toCusDevPlanDataPage")
    public String toCusDevPlanDataPage(Integer sid, HttpServletRequest request){
        //通过id查询营销机会对象
        SaleChance saleChance=saleChanceService.selectByPrimaryKey(sid);
        //将对象放到请求域中
        request.setAttribute("saleChance",saleChance);
        return "cusDevPlan/cus_dev_plan_data";
    }

    /**
     * 点击开发或者详情按钮显示的分页查询
     * @param cusDevPlanQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCusDevPlansByParams(CusDevPlanQuery cusDevPlanQuery){
        return cusDevPlanService.queryCusDevPlansByParams(cusDevPlanQuery);
    }

    /**
     *添加计划向
     * @param cusDevPlan
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.addCusDevPlan(cusDevPlan);
        return success("计划项数据添加成功");
    }
    /**
     *更新计划向
     * @param cusDevPlan
     * @return
     */

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("计划项数据更新成功");
    }

    /**
     * 跳转到数据添加和更新的页面
     * @param sid 前台传过来的营销机会ID 给添加和更新计划页面获得
     * @param id 前台转过来用于判断是添加还是更新操作
     * @param request
     * @return
     */
    @RequestMapping("addOrUpdateCusDevPlanPage")
    public String addOrUpdateCusDevPlanPage(Integer sid,Integer id,HttpServletRequest request){
        //设置到请求域中 便于add update js页面取到存入隐藏域中
        request.setAttribute("sid",sid);
        request.setAttribute("cusDevPlan",cusDevPlanService.selectByPrimaryKey(id));
        return "cusDevPlan/add_update";
    }

    /**
     *删除计划向
     * @param id
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteCusDevPlan(Integer id){
        cusDevPlanService.deleteCusDevPlan(id);
        return success("计划项数据删除成功");
    }



}
