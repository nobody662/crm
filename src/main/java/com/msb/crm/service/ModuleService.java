package com.msb.crm.service;

import com.msb.crm.base.BaseService;
import com.msb.crm.dao.ModuleMapper;
import com.msb.crm.dao.PermissionMapper;
import com.msb.crm.model.TreeModel;
import com.msb.crm.utils.AssertUtil;
import com.msb.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService <Module,Integer> {

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private PermissionMapper permissionMapper;
    /**
     * 查询所有资源列表 用于授权树显示
     * @return
     */
    public List<TreeModel> queryAllModules(Integer roleId){
        //查询所有资源列表
        List<TreeModel> treeModelList = moduleMapper.queryAllModules();


        //查询指定角色已经授权过的资源列表(查询角色拥有的资源id)
        List<Integer> permissionIds = permissionMapper.queryRoleHasModuleIdsByRoleId(roleId);
        //判断角色是否拥有资源id
        if(permissionIds !=null && permissionIds.size()>0){
            //选好所有资源列表,判断用户拥有的资源id中是否有匹配的,如有有 则设置checked属性为true
            treeModelList.forEach(treeModel -> {
                //判断角色拥有的资源id是否有当前遍历过的id
                if(permissionIds.contains(treeModel.getId())){
                    //如果包含 说明授权过 则设置checked为true
                    treeModel.setChecked(true);
                }
            });
        }

        return treeModelList;
    }

    /**
     * 查询菜单管理界面资源数据
     * @return
     */
    public Map<String, Object> queryModuleList() {
        Map<String, Object> map = new HashMap<>();

        // 查询资源列表
        List<Module> modelList = moduleMapper.queryModuleList();
        map.put("code", 0); // 状态码
        map.put("msg", " "); // 消息
        map.put("count", modelList.size()); // 总数
        map.put("data", modelList); // 资源列表数据，使用 "data" 作为键

        return map;
    }

    /**
     * 菜单管理界面添加资源
     *  1. 参数校验
     *      模块名称 moduleName
     *          非空，同一层级下模块名称唯一
     *      地址 url
     *          仅二级菜单 (grade=1)，非空且不可重复
     *      父级菜单 parentId
     *          一级菜单 (目录 grade=0)    -1
     *          二级|三级菜单 (菜单|按钮 grade=1 或 2) 非空，父级菜单必须存在
     *      层级 grade
     *          非空， 0|1|2
     *      权限码 optValue
     *          非空，不可重复
     *
     *  2. 设置参数的默认值
     *      是否有效 isValid      1
     *      创建时间 createDate  系统当前时间
     *      修改时间 updateDate  系统当前时间
     *
     *  3. 执行添加操作，判断受影响的行数
     *
     * @param module
     * @return void
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addModule(Module module) {
        // 方法体实现
        /* 1. 参数校验*/
        // 层级 grade 非空, 0|1|2
        Integer grade = module.getGrade();
        AssertUtil.isTrue( null == grade || !(grade == 0 || grade == 1 || grade == 2),  "菜单层级不合法!");

        // 模块名称 moduleName 非空
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"模块名称不能为空!");
        // 模块名称 moduleName 同一层级下模块名称唯一
        AssertUtil.isTrue( null != moduleMapper .queryModuleByGradeAndModuleName(grade, module.getModuleName()),  "同一层级下模块名称重复!");

        // 如果是二级菜单 (grade=1)
        if (grade == 1) {
            // 地址 url 二级菜单 (grade=1) 非空
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),  "URL不能为空!");

            // 地址 url 二级菜单 (grade=1) 且不可重复
            AssertUtil.isTrue( null != moduleMapper.queryModuleByGradeAndUrl(grade, module.getUrl()),  "该层级下地址不可重复!");
        }

        // 父级菜单 parentId 一级菜单 (目录 grade=0) -1
        if (grade == 0) {
            module.setParentId(-1);
        }
        // 父级菜单 parentId 二级|三级菜单 (菜单|按钮 grade=1或2) 非空，父级菜单必须存在
        if (grade != 0) {
            // 非空
            AssertUtil.isTrue(null == module.getParentId(),  "父级菜单不能为空!");
            // 父级菜单必须存在 (校验父级的ID并持久层查询记录)
            AssertUtil.isTrue( null == moduleMapper.selectByPrimaryKey(module.getParentId()),  "请指定有效的上级资源记录!");

            //权限吗 optValue 非空
            AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"权限码不能为空");

            //权限吗 optValue 不可重复
            AssertUtil.isTrue(null!=moduleMapper.queryModuleByOptValue(module.getOptValue()),"权限码重复！");
        }

        /*设置参数的默认值*/
        module.setIsValid((byte)1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());

        /*3.执行添加操作，判断受影响的行数*/
        AssertUtil.isTrue(moduleMapper.insertSelective(module)<1,"添加资源失败");
    }

    /**
     * 菜单管理界面修改资源
     * @param module
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateModule(Module module) {
        // 方法体实现
        /* 1. 参数校验*/
        //id非空,数据存在
        AssertUtil.isTrue(null==module.getId(),"待更新记录不存在！");
        //通过id查询资源对象
        Module temp=moduleMapper.selectByPrimaryKey(module.getId());
        AssertUtil.isTrue(null==temp,"待更新记录不存在！");

        // 层级 grade 非空, 0|1|2
        Integer grade = module.getGrade();
        AssertUtil.isTrue( null == grade || !(grade == 0 || grade == 1 || grade == 2),  "菜单层级不合法!");

        // 模块名称 moduleName 非空
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"模块名称不能为空!");
        // 模块名称 moduleName 同一层级下模块名称唯一
        Module test = moduleMapper.queryModuleByGradeAndModuleName(grade, module.getModuleName());
        if (test !=null){
            AssertUtil.isTrue( !(test.getId().equals(module.getId())),  "同一层级下模块名称重复!");
        }


        // 如果是二级菜单 (grade=1)
        if (grade == 1) {
            // 地址 url 二级菜单 (grade=1) 非空
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),  "URL不能为空!");

            // 地址 url 二级菜单 (grade=1) 且不可重复
            Module test2 = moduleMapper.queryModuleByGradeAndUrl(grade, module.getUrl());
            if(null != test2){
                AssertUtil.isTrue( !(test2.getId().equals(module.getId())),  "该层级下地址不可重复!");
            }
        }

            //权限吗 optValue 非空
            AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"权限码不能为空");
            //权限吗 optValue 不可重复
              Module test3 = moduleMapper.queryModuleByOptValue(module.getOptValue());
              if(null!= test3){
            AssertUtil.isTrue(!(test3.getId().equals(module.getId())),"权限码重复！");
        }


        /*设置参数的默认值*/
        module.setUpdateDate(new Date());

        /*3.执行添加操作，判断受影响的行数*/
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module)<1,"修改资源失败");
    }

    /**
     * 菜单管理界面删除功能
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteModule(Integer id) {
        // 判断id是否为空
        AssertUtil.isTrue( null == id,  "待删除记录不存在！");

        // 通过id查询资源对象
        Module temp = moduleMapper.selectByPrimaryKey(id);
        // 判断资源对象是否为空
        AssertUtil.isTrue( null == temp,  "待删除记录不存在！");

        // 如果当前资源存在子记录（将id当做父id查询资源记录）
        Integer count = moduleMapper.queryModuleByParentId(id);
        // 如果存在子记录，则不可删除
        AssertUtil.isTrue( count > 0,  "该资源存在子记录，不可删除！");

        // 通过资源id查询权限表中是否存在数据
        count = permissionMapper.countPermissionByModuleId(id);
        // 判断是否存在，存在则删除
        if (count > 0) {
            // 删除指定资源id的权限记录
            permissionMapper.deletePermissionByModuleId(id);
        }

        // 设置记录无效
        temp.setIsValid((byte) 0);
        temp.setUpdateDate(new Date());

        // 执行更新
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(temp) < 1, "删除资源失败");
    }
}
