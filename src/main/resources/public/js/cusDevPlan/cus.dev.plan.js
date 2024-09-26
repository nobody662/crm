layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    //营销机会列表展示
    var  tableIns = table.render({
        //表格id属性值
        elem: '#saleChanceList',
        //controller数据接口
        url : ctx+'/sale_chance/list?flag=1',
        //单元格最小宽度
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        //开启头部工具栏
        toolbar: "#toolbarDemo",
        //批量删除选择的id
        id : "saleChanceTable",
        cols : [[
            //复选框
            {type: "checkbox", fixed:"center"},

            {field: "id", title:'编号',fixed:"true"},
            {field: 'chanceSource', title: '机会来源',align:"center"},
            {field: 'customerName', title: '客户名称',  align:'center'},
            {field: 'cgjl', title: '成功几率', align:'center'},
            {field: 'overview', title: '概要', align:'center'},
            {field: 'linkMan', title: '联系人',  align:'center'},
            {field: 'linkPhone', title: '联系电话', align:'center'},
            {field: 'description', title: '描述', align:'center'},
            {field: 'createMan', title: '创建人', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'updateDate', title: '修改时间', align:'center'},
            {field: 'devResult', title: '开发状态', align:'center',templet:function (d) {
                    //调用函数返回格式化结果
                    return formatterDevResult(d.devResult);
                }},
            {title: '操作', templet:'#op',fixed:"right",align:"center", minWidth:150}
        ]]
    });

    //格式化开发状态
    function formatterDevResult(value){
        /**
         * 0-未开发
         * 1-开发中
         * 2-开发成功
         * 3-开发失败
         */
        if(value==0){
            return "<div style='color: yellow'>未开发</div>";
        }else if(value==1){
            return "<div style='color: orange;'>开发中</div>";
        }else if(value==2){
            return "<div style='color: green'>开发成功</div>";
        }else if(value==3){
            return "<div style='color: red'>开发失败</div>";
        }else {
            return "<div style='color: blue'>未知</div>"
        }
    }


    // 多条件搜索
    $(".search_btn").on("click",function () {
        table.reload("saleChanceTable",{
            page:{
                curr:1
            },
            where:{
                customerName:$("input[name='customerName']").val(),// 客户名
                createMan:$("input[name='createMan']").val(),// 创建人
                devResult:$("#devResult").val()    //开发状态
            }
        })
    });

//行工具栏监听
    table.on("tool(saleChances)",function (obj) {
        var layEvent = obj.event;
        if(layEvent==="dev"){
            openCusDevPlanDialog("计划项数据开发",obj.data.id);
        }else if(layEvent ==="info"){
            openCusDevPlanDialog("计划项数据详情",obj.data.id);
        }
    });

    /**
     * 打开计划项数据开发界面
     */
    function openCusDevPlanDialog(title,sid) {
        layui.layer.open({
            title:title,
            type:2,
            area:["700px","550px"],
            maxmin:true,
            //后端接口地址
            content:ctx+"/cus_dev_plan/toCusDevPlanDataPage?sid="+sid
        })
    }





});
