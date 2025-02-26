layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    //计划项数据展示
    var  tableIns = table.render({
        elem: '#cusDevPlanList',
        url : ctx+'/cus_dev_plan/list?saleChanceId='+$("input[name='id']").val(),
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "cusDevPlanTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'planItem', title: '计划项',align:"center"},
            {field: 'exeAffect', title: '执行效果',align:"center"},
            {field: 'planDate', title: '执行时间',align:"center"},
            {field: 'createDate', title: '创建时间',align:"center"},
            {field: 'updateDate', title: '更新时间',align:"center"},
            {title: '操作',fixed:"right",align:"center", minWidth:150,templet:"#cusDevPlanListBar"}
        ]]
    });

    /*头部工具栏监听*/
    table.on("toolbar(cusDevPlans)",function (obj) {
        switch (obj.event) {
            case "add" :
                openAddOrUpdateCusDevPlanDialog();
                break;
            case "success":
                updateSaleChanceDevResult($("input[name='id']").val(),2);
                break;
            case "failed":
                updateSaleChanceDevResult($("input[name='id']").val(),3);
                break;
        }
    });


/*行工具栏监听*/
    table.on("tool(cusDevPlans)",function (obj) {
        var layEvent = obj.event;
        if(layEvent === "edit"){
            openAddOrUpdateCusDevPlanDialog(obj.data.id);
        }else if(layEvent === "del"){
            deleteCusDevPlan(obj.data.id);
        }
    });


    /**
     * 打开添加或者修改计划页面
     * @param id 用于判断是添加还是更新操作 由于id是数据库自己生成的 所以点击添加按钮还没有id 所有为空是则代表数据库的添加操作
     * 并且通过拼接字符串的方式将id和sid传到后端
     */
    function openAddOrUpdateCusDevPlanDialog(id) {
        var title="计划项管理管理-添加计划项";
        //这里的sid是存在html页面中隐藏域中的id 也就是数据库中的id
        var url=ctx+"/cus_dev_plan/addOrUpdateCusDevPlanPage?sid="+$("input[name='id']").val();
        //id有值 则为更新操作
        if(id){
            title="计划项管理管理-更新计划项";
            url=url+"&id="+id;
        }
        layui.layer.open({
            title:title,
            type:2,
            area:["700px","500px"],
            maxmin:true,
            content:url
        })
    }

    /**
     * 删除计划项
     * @param id
     */
    function  deleteCusDevPlan(id){
        layer.confirm("确认删除当前记录?",{icon: 3, title: "开发项数据管理"},function (index) {
            //发送请求
            $.post(ctx+"/cus_dev_plan/delete",{id:id},function (data) {
                //判断删除结果
                if(data.code==200){
                    layer.msg("删除成功",{icon:6});
                    //刷新表格数据
                    tableIns.reload();
                }else{
                    layer.msg(data.msg,{icon:5});
                }
            })
        })
    }

    /**
     * 更新营销机会的开发状态
     * @param sid
     * @param devResult
     */
    function updateSaleChanceDevResult(sid,devResult) {
        layer.confirm("确认更新机会数据状态?",{icon: 3, title: "客户开发计划管理"},function (index) {
            $.post(ctx+"/sale_chance/updateSaleChanceDevResult",{
                id:sid,
                devResult:devResult
            },function (data) {
                //成功
                if(data.code==200){
                    layer.msg(data.msg,{icon:6});
                    //关闭所有窗口
                    layer.closeAll("iframe");
                    // 刷新父页面
                    parent.location.reload();
                }else{
                    layer.msg(data.msg,{icon:5});
                }
            })
        })
    }





});
