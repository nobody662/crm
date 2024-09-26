package com.msb.crm.dao;

import com.msb.crm.base.BaseMapper;
import com.msb.crm.model.TreeModel;
import com.msb.crm.vo.Module;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.ui.Model;

import java.util.List;

@Mapper
public interface ModuleMapper extends BaseMapper<Module,Integer> {

    //查询所有的授权树资源列表
    public List<TreeModel> queryAllModules();

    //查询菜单管理界面的资源
    public List<Module> queryModuleList();

    //通过模块名moduleName查询资源对象 同一层级下模块名称唯一
    Module queryModuleByGradeAndModuleName(@Param("grade")Integer grade, @Param("moduleName")String moduleName);
    //通过url查询资源对象
    Module queryModuleByGradeAndUrl(@Param("grade") Integer grade, @Param("url")String url);
    //通过权限码optValue查询资源对象
    Module queryModuleByOptValue(String optValue);

    //用过id查询查询资源记录
    // 如果当前资源存在子记录（将id当做父id查询资源记录）
    Integer queryModuleByParentId(Integer id);

}