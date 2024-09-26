layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;


    //营销机会列表展示
    var  tableIns = table.render({
        // HTML 中表格的 ID
        elem: '#saleChanceList',
        //controller数据接口
        url : ctx+'/sale_chance/list',
        //单元格最小宽度
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        //开启头部工具栏
        toolbar: "#toolbarDemo",
        id: "saleChanceTable",   // layui 表格实例 ID 用于批量删除和查询数据
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
            {field: 'uname', title: '分配人', align:'center'},
            {field: 'assignTime', title: '分配时间', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'updateDate', title: '修改时间', align:'center'},
            //调用函数返回格式化结果
            {field: 'state', title: '分配状态', align:'center',templet:function(d){
                    return formatterState(d.state);
                }},
            {field: 'devResult', title: '开发状态', align:'center',templet:function (d) {
                    return formatterDevResult(d.devResult);
                }},
            {title: '操作', templet:'#saleChanceListBar',fixed:"right",align:"center", minWidth:150}
        ]]
    });

    //格式化状态值
    function formatterState(state){
        if(state==0){
            return "<div style='color:yellow '>未分配</div>";
        }else if(state==1){
            return "<div style='color: green'>已分配</div>";
        }else{
            return "<div style='color: red'>未知</div>";
        }
    }
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
        table.reload("saleChanceTable", { // 修改此处的ID为正确的 "saleChanceTable"
            page: {
                curr: 1
            },
            where: {
                customerName: $("input[name='customerName']").val(), // 客户名
                createMan: $("input[name='createMan']").val(), // 创建人
                state: $("#state").val() // 分配状态
            }
        });
    });



    // 头工具栏事件
    /*对应html页面中lay-filter 属性值*/
    table.on('toolbar(saleChances)',function (data) {
    //data.event:对应元素上设置的lay-event值
        switch (data.event) {
            //添加
            case "add":
                openSaleChanceDialog();
                break;
                //删除
            case "del":
                //console.log(table.checkStatus(obj.config.id).data);
                deleteSaleChance(data);
                break;
        }
    });


    /**
     *
     * 批量删除
     *   data:选择的待删除记录数组
     */
    function deleteSaleChance(data){
        //获得数据表格选中的行数据
        var checkStatus = table.checkStatus("saleChanceTable");
       //获得被选中记录对应的数据
        var saleChanceDate = checkStatus.data;

        //判断用户是否选择批量删除
        if(saleChanceDate.length<0){
            layer.msg("请选择待删除记录!",{icon:5});
            return;
        }
        //询问是否删除
        layer.confirm("确定删除选中的记录",{icon:3,title:'营销机会管理'},function (index) {
            //关闭确认框
            layer.close(index);
            var ids="ids=";
            //循环选中的行记录数据
            for(var i=0;i<saleChanceDate.length;i++){
                //如果在最后一个了
                if(i<saleChanceDate.length-1){
                    ids=ids+saleChanceDate[i].id+"&ids=";
                }else{
                    ids=ids+saleChanceDate[i].id;
                }
            }

            $.ajax({
                type:"post",
                url:ctx+"/sale_chance/delete",
                data:ids,
                success:function (data) {
                    if(data.code==200){
                        layer.msg("删除成功",{icon: 6});
                        //刷新表格
                        tableIns.reload();
                    }else{
                        layer.msg(data.msg,{icon:5});
                    }
                }
            })
        })
    }


//行工具栏监听
    table.on('tool(saleChances)',function (obj) {
        //判断类型 obj.event
          var layEvent =obj.event;
          if(layEvent === "edit"){
              openSaleChanceDialog(obj.data.id);
          }else if(layEvent === "del"){
              //弹出删除修改框确认
              layer.confirm("确认删除当前记录?",{icon: 3, title: "营销机会数据管理"},function (index) {
                  //关闭确认框
                  layer.close(index);

                  //发送请求
                  $.post(ctx+"/sale_chance/delete",{ids:obj.data.id},function (data) {
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
     * 打开添加或更新对话框 有id就是修改操作 没有就是添加操作
     */
    function openSaleChanceDialog(saleChanceId) {
        var title="营销机会管理-机会添加";
        var url=ctx+"/sale_chance/toSaleChancePage";
        //判断id
        if(saleChanceId !=null && saleChanceId !=''){
            title="营销机会管理-机会更新";
            url=url+"?saleChanceId="+saleChanceId;
        }
        layui.layer.open({
            title:title,
            type:2,
            area:["700px","650px"],
            //可最大化最小化设置
            maxmin:true,
            content:url
        })
    }








});
