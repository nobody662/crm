layui.use(['table','layer'],function(){
       var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    //角色列表展示
    var  tableIns = table.render({
        elem: '#roleList',
        url : ctx+'/role/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "roleListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'roleName', title: '角色名', minWidth:50, align:"center"},
            {field: 'roleRemark', title: '角色备注', minWidth:100, align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '修改时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#roleListBar',fixed:"right",align:"center"}
        ]]
    });

    // 多条件搜索
    $(".search_btn").on("click",function () {
        table.reload("roleListTable",{
            page:{
                curr:1
            },
            where:{
                // 角色名
                roleName:$("input[name='roleName']").val()
            }
        })
    });

    // 头工具栏事件
    table.on('toolbar(roles)',function (obj) {
        switch (obj.event) {
            case "add":
                openAddOrUpdateRoleDialog();
                break;
            case "grant":
                //获得数据表格选中的行数据 单一表格table.checkStatus("saleChanceTable")和这个效果一致
                var checkStatus = table.checkStatus(obj.config.id);
                //获得授权对话框
                openAddGrantDialog(checkStatus.data);
                break;
        }
    });

    //行工具栏监听
    table.on('tool(roles)',function (obj) {
        var layEvent =obj.event;
        if(layEvent === "edit"){
            openAddOrUpdateRoleDialog(obj.data.id);
        }else if(layEvent === "del"){
            layer.confirm("确认删除当前记录?",{icon: 3, title: "角色管理"},function (index) {
                //关闭弹出框
                layer.close(index);

                $.post(ctx+"/role/delete",{id:obj.data.id},function (data) {
                    if(data.code==200){
                        layer.msg("删除成功",{icon: 6});
                        //刷新表格
                        tableIns.reload();
                    }else{
                        layer.msg(data.msg,{icon:5});
                    }
                })
            })
        }
    });


    /**
     * 打开添加和更新操作框
     * @param id
     */
    function openAddOrUpdateRoleDialog(id) {
        var title="角色管理-角色添加";
        var url=ctx+"/role/addOrUpdateRolePage";
        if(id){
            title="角色管理-角色更新";
            url=url+"?id="+id;
        }
        layui.layer.open({
            title:title,
            type:2,
            area:["500px","400px"],
            maxmin:true,
            content:url
        })
    }

    /**
     * 打开角色授权界面
     * @param data
     */
    function openAddGrantDialog(data){
        //判断是否选择了角色记录
        if(data.length==0){
            layer.msg("请选需要授权的角色!",{icon:5});
            return;
        }
        //判断授权角色数量
        if(data.length>1){
            layer.msg("不支持批量角色授权!",{icon:5});
            return;
        }

        var title="角色管理-角色授权";
        var url=ctx+"/module/toAddGrantPage?roleId="+data[0].id;

        layui.layer.open({
            title:title,
            type:2,
            area:["600px","200px"],
            //可最大化最小化设置
            maxmin:true,
            content:url
        })
    }

});
