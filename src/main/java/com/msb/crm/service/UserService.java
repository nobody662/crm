package com.msb.crm.service;

import com.msb.crm.base.BaseService;
import com.msb.crm.dao.UserMapper;
import com.msb.crm.dao.UserRoleMapper;
import com.msb.crm.model.UserModel;
import com.msb.crm.utils.AssertUtil;
import com.msb.crm.utils.Md5Util;
import com.msb.crm.utils.PhoneUtil;
import com.msb.crm.utils.UserIDBase64;
import com.msb.crm.vo.User;
import com.msb.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User,Integer> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;
    /**
     * 用户登录
     * @param userName
     * @param userPwd
     */
    public UserModel userLogin(String userName,String userPwd){
        //1.判断用户名或者密码非空
        checkUserLogin(userName,userPwd);
        //2.调用UserMapper层的方法判断用户
        User user = userMapper.SelectUserByName(userName);

        //3.判断用户是否不存在 为空抛出异常
        AssertUtil.isTrue(user==null,"用户姓名不存在!");

        //4.判断密码是否正确 不正确抛出异常
        checkUserPwd(userPwd,user.getUserPwd());

        //5.返回结构用户对象
        return buildUserInfo(user);
    }

    /**
     * 检验密码是否正确
     * @param userPwd
     * @param pwd
     */
    private void checkUserPwd(String userPwd,String pwd){
        //1.对客户端传过来的密码进行加密
        userPwd= Md5Util.encode(userPwd);
        //2.比较密码是否相同
         AssertUtil.isTrue(!userPwd.equals(pwd),"用户密码不正确");
    }
    /**
     * 调用工具类检查账号密码是否为空
     * @param userName
     * @param userPwd
     */
    private void checkUserLogin(String userName,String userPwd){
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户姓名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空！");
    }

    public UserModel buildUserInfo(User user){
        UserModel userModel=new UserModel();
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    /**
     * 修改密码操作
     * @param userId
     * @param oldPwd
     * @param newPwd
     * @param repeatPwd 确认密码
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPassword(Integer userId,String oldPwd,String newPwd,String repeatPwd){
       //通过id查询用户 返回用户对象
        User user =userMapper.selectByPrimaryKey(userId);
        //判断用户是否存在
        AssertUtil.isTrue(user==null,"该用户不存在！");

        //参数校验 判断是否为空
        checkPasswordParams(user,oldPwd,newPwd,repeatPwd);

        //设置用户的新密码
        user.setUserPwd(Md5Util.encode(newPwd));

        //调用mapper方法 检验结果
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改密码失败!");
    }

    public void checkPasswordParams(User user,String oldPwd,String newPwd, String repeatPwd){
        //判断密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd),"原始密码不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(newPwd),"新密码不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(repeatPwd),"确认密码不能为空");

        //判断密码是否正确
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPwd)),"原始密码不正确");

        //判断原始密码和新密码是否一致
        AssertUtil.isTrue(user.getUserPwd().equals(Md5Util.encode(newPwd)),"原始密码和新密码相同 请重新输入");

        //判断确认密码和新密码是一致
        AssertUtil.isTrue(!newPwd.equals(repeatPwd),"确认密码和新密码不相同 请重新输入");
    }


    /**
     * 查询所有销售人员
     * @return
     */
    @Transactional
    public List<Map<String,Object>> queryAllSales(){
        return userMapper.queryAllSales();
    }

    /**
     * 用户添加操作
     * 1.参数校验
     *     用户名userName 非空 值唯一
     *     email  非空  格式合法
     *     手机号phone 非空  格式合法
     * 2.默认参数设置
     *     isValid  1
     *     createDate  系统时间
     *     updateDate 系统时间
     *     默认密码设置   123456 md5加密
     * 3.执行添加 判断受影响行数
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public  void addUser(User user){
        /*1.参数校验*/
        checkFormParams(user.getUserName(),user.getEmail(),user.getPhone(),null);
        /*2.默认参数设置*/
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        //设置密码
        user.setUserPwd(Md5Util.encode("123456"));
        /*3.执行添加 判断受影响行数 这是调用base中已有的方法*/
        AssertUtil.isTrue(userMapper.insertSelective(user) < 1,"用户添加失败!");
        /*4.用户角色关联 也就是对UserRole表中的值进行添加*/
        relationUserRole(user.getId(),user.getRoleIds());
    }
//参数校验方法
    private void checkFormParams(String userName, String email, String phone,Integer userId) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空!");
        //唯一性判断
        User user = userMapper.SelectUserByName(userName);
        //如果用户对象为空,则表示用户可用 否则不可添加
        // 当执行更新操作 分两种情况 第一种是修改了用户名且数据库中不存在这个用户名 这种直接跳过判断
        // 第二种是未修改用户名即数据库中本来就有这个数据名  此时通过id判断是否修改的是自己 id不同就抛异常
        AssertUtil.isTrue(user!=null && !(user.getId().equals(userId)),"用户已经存在,请重新输入");

        AssertUtil.isTrue(StringUtils.isBlank(email),"邮箱不能为空!");

        AssertUtil.isTrue(StringUtils.isBlank(phone),"用户手机号不能为空!");
        AssertUtil.isTrue(!(PhoneUtil.isMobile(phone)),"手机号格式非法!");

    }
    //用户角色关联方法
    private void relationUserRole(Integer userId,String roleIds){
        // 通过用户ID查询角色记录
        Integer count = userRoleMapper.countUserRoleByUserId(userId);
        // 判断角色记录是否存在
        if (count > 0) {
            // 如果角色记录存在，则删除用户对应的角色记录
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "用户角色分配失败！");
        }

        // 判断角色ID是否存在，如果存在，则添加该用户对应的角色记录
        if (StringUtils.isNotBlank(roleIds)) {
            // 将用户角色数据批量设置到集合中，执行批量添加
            List<UserRole> userRoleList = new ArrayList<>();
            // 将角色ID字符串转换成数组
            String[] roleIdsArray = roleIds.split(",");
            // 遍历数组，得到对应的用户角色对象，并设置到集合中
            for (String roleId : roleIdsArray) {
                UserRole userRole = new UserRole();
                userRole.setRoleId(Integer.parseInt(roleId));
                userRole.setUserId(userId);
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                // 设置到集合中
                userRoleList.add(userRole);
            }
            // 批量添加用户角色记录 调用的事base中已存在的方法
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList) != userRoleList.size(),  "用户角色分配失败！");
        }
    }

    /**
     * 用户更新
     * @param user
     * 1.参数校验
     *      判断id是否为空
     *     用户名userName 非空 值唯一
     *     email  非空  格式合法
     *     手机号phone 非空  格式合法
     * 2.默认参数设置
     *   updateDate 系统当前时间
     * 3.执行更新 判断受影响行数
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user){
        /*1.参数校验*/
        //id校验
        AssertUtil.isTrue(user.getId()==null,"待更新记录不存在");
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(temp==null,"待更新记录不存在");
        //其他校验
        checkFormParams(user.getUserName(),user.getEmail(),user.getPhone(),user.getId());
        /*2.默认参数设置*/
        user.setUpdateDate(new Date());
        /*3.执行更新 判断受影响行数 这是调用base中已有的方法*/
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)!=1,"用户添加失败!");
        /*4.用户角色关联 也就是对UserRole表中的值进行修改*/
        relationUserRole(user.getId(),user.getRoleIds());
    }

    /**
     * 用户删除
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids) {
        /*删除user表中的数据*/
        //判断id是否为空
        AssertUtil.isTrue(null==ids||ids.length<1,"待删除记录不存在!");
        //执行删除(更新) 操作 判断受影响的行数 直接调用base中的方法
        AssertUtil.isTrue(userMapper.deleteBatch(ids) !=ids.length,"用户数据删除失败!");

        /*删除userRole表中的数据*/

        //遍历用户ID中的数组
        for (Integer userId : ids) {
            // 通过用户ID查询角色记录
            Integer count = userRoleMapper.countUserRoleByUserId(userId);
            // 判断角色记录是否存在
            if (count > 0) {
                // 如果角色记录存在，则删除用户对应的角色记录
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "删除用户角色失败！");
            }
        }
    }

}
