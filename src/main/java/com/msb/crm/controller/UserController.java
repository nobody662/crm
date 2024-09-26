package com.msb.crm.controller;

import com.msb.crm.base.BaseController;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.exceptions.ParamsException;
import com.msb.crm.model.UserModel;
import com.msb.crm.query.UserQuery;
import com.msb.crm.service.UserService;
import com.msb.crm.utils.LoginUserUtil;
import com.msb.crm.vo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    /**
     * 用户登录
     * @param userName
     * @param userPwd
     * @return
     */
    @PostMapping("login")
    @ResponseBody
    public ResultInfo userLogin(String userName,String userPwd){
        ResultInfo resultInfo=new ResultInfo();
        //调用service层
        UserModel userModel = userService.userLogin(userName, userPwd);

        //设置resultInfo result返回值 返回值给客户端
        resultInfo.setResult(userModel);

        return resultInfo;
    }

    /**
     * 用户修改密码
     * @return
     */
    @PostMapping("updatePwd")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request,String oldPwd,String newPwd, String repeatPwd){
        ResultInfo resultInfo=new ResultInfo();
        //获得cookie中的userId
        Integer userId= LoginUserUtil.releaseUserIdFromCookie(request);
        //调用service层修改密码方法
        userService.updateUserPassword(userId,oldPwd,newPwd,repeatPwd);

        return resultInfo;
    }

    /**
     * 跳转到修改用户密码界面
     * @return
     */
    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){

        return  "user/password";
    }



    /**
     * 查询所有销售人员
     * @return
     */
    @GetMapping("queryAllSales")
    @ResponseBody
    public List<Map<String,Object>> queryAllSales(){
        return userService.queryAllSales();
    }


    /**
     * 分页多条件查询用户列表
     * @param userQuery
     * @return
     */
    @GetMapping("list")
    @ResponseBody
    public Map<String,Object> selectByParms(UserQuery userQuery){
        //直接调用baseService中的方法 所以service中继承baseService就不要写了
        return userService.queryByParamsForTable(userQuery);
    }

    //跳转到用户列表页面视图
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addUser(User user){
        userService.addUser(user);
        return success("用户添加成功!");
    }

    /**
     * 打开添加或者修改页面
     * @return
     */
    @RequestMapping("addOrUpdateUserPage")
    public String addOrUpdateUserPage(Integer id,HttpServletRequest request){
        //判断id是否为空 如果不为 则表示更新操作
        if (id!=null){
            User user = userService.selectByPrimaryKey(id);
            //将数据射到请求域中
            request.setAttribute("userInfo",user);
        }
        return "user/add_update";
    }

    /**
     * 更新用户
     * @param user
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success("用户更新成功!");
    }

    /**
     * 删除操作和批量删除
     * @param ids
     * @return
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        userService.deleteSaleChance(ids);
        return success("用户删除成功!");
    }
}
