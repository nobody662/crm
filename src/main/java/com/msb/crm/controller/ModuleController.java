package com.msb.crm.controller;

import com.msb.crm.base.BaseController;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.model.TreeModel;
import com.msb.crm.service.ModuleService;
import com.msb.crm.vo.Module;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {

    @Resource
    private ModuleService moduleService;

    /**
     * 查询所有列表资源 用于授权功能树显示
     * @return
     */
    @GetMapping("queryAllModules")
    @ResponseBody
    public List<TreeModel> queryAllModules(Integer roleId){
        return moduleService.queryAllModules(roleId);
    }

    /**
     * 进入角色管理的授权页面中
     * @return
     */
    @GetMapping("toAddGrantPage")
    public String toAddGrantPage(Integer roleId, HttpServletRequest request){
        //将角色id放到请求域中
        request.setAttribute("roleId",roleId);
        return "role/grant";
    }

    /**
     * 查询菜单管理界面资源数据
     * @return
     */
    @GetMapping("list")
    @ResponseBody
    public Map<String,Object> queryModuleList(){
        Map<String, Object> map = moduleService.queryModuleList();
        return map;
    }

    //跳转到菜单管理页面
    @RequestMapping("index")
    public String index(){
        return  "module/module";
    }

    /**
     * 菜单管理添加功能
     * @param module
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addModule(Module module){
        moduleService.addModule(module);
        return success("添加资源成功!");
    }

    /**
     * 打开菜单管理资源添加页面
     * @param grade 层级
     * @param parentId 父菜单ID
     * @return
     */
    @RequestMapping("addModulePage")
    public String  addModulePage(Integer grade,Integer parentId,HttpServletRequest request){
        //将数据设置到请求域中
        request.setAttribute("grade",grade);
        request.setAttribute("parentId",parentId);
        return "module/add";
    }

    /**
     * 菜单管理界面修改
     * @param module
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateModule(Module module){
        moduleService.updateModule(module);
        return success("修改资源成功!");
    }

    //跳转到修改菜单界面
    @RequestMapping("updateModulePage")
    public String  updateModulePage(Integer id,HttpServletRequest request){
        //将资源对象设置到请求域中
        request.setAttribute("module",moduleService.selectByPrimaryKey(id));
        return "module/update";
    }

    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteModule(Integer id){
        moduleService.deleteModule(id);
        return success("删除资源成功!");
    }
}
