package com.msb.crm.controller;


import com.msb.crm.base.BaseController;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.query.RoleQuery;
import com.msb.crm.service.RoleService;
import com.msb.crm.vo.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {

    @Resource
    private RoleService roleService;

    //跳转到视图页面
    @RequestMapping("index")
    public String index(){
        return "role/role";
    }

    /**
     * 查询所有角色列表用于下拉框
     * @return
     */
    @PostMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleService.queryAllRoles(userId);
    }

    /**
     * 分页查询和多条件查询
     * @param roleQuery
     * @return
     */
    @GetMapping("list")
    @ResponseBody
    public Map<String,Object> selectByParams(RoleQuery roleQuery){
        return roleService.queryByParamsForTable(roleQuery);
    }

    /**
     * 头部工具栏目角色添加
     * @param role
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addRole(Role role){
        roleService.addRole(role);
        return success("角色记录添加成功");
    }

    /**
     * 行工具栏角色修改
     * @param role
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateRole(Role role){
        roleService.updateRole(role);
        return success("角色记录更新成功");
    }

    //跳转到添加或者更新页面
    @RequestMapping("addOrUpdateRolePage")
    public  String addOrUpdateRolePage(Integer id, HttpServletRequest request){
        //如果id不为空 则表示修改操作 通过角色id查询角色记录 存到请求域中
        if(id !=null){
            Role role = roleService.selectByPrimaryKey(id);
            //设置到请求域中
            request.setAttribute("role",role);
        }
        return "role/add_update";
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer id){
        roleService.deleteRole(id);
        return success("角色记录删除成功");
    }

    /**
     * 角色授权
     * @param roleId 角色id
     * @param mids  被选中复选框或者单选框的id
     * @return
     */
    @PostMapping ("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer roleId, Integer[] mids){
        roleService.addGrant(roleId,mids);
        return success("角色授权成功");
    }
}
