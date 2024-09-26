layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    /**
     * 关闭弹出框
     */
    $("#closeBtn").click(function (){
        //在iframe关闭自己时
        var index=parent.layer.getFrameIndex(window.name);//得到索引
        parent.layer.close(index);
    })

    /**
     * 加载指派人的下拉框
     */
    $.ajax({
        type: "get",
        url: ctx + "/user/queryAllSales",
        data: {},
        success: function (data) {
            if (data != null) {
                // 获取隐藏域中指派人的ID
                var assignManId = $("#assignManId").val();
                // 遍历返回的数据
                for (var i = 0; i < data.length; i++) {
                    var opt = "";
                    // 如果看到的ID与隐藏域中的ID相等，则显示被选中
                    if (assignManId == data[i].id) {
                        opt = "<option value='" + data[i].id + "' selected>" + data[i].uname + "</option>";
                    } else {
                        // 设置下拉选项
                        opt = "<option value='" + data[i].id + "'>" + data[i].uname + "</option>";
                    }
                    // 将下拉选项设置到下拉框中
                    $("#assignMan").append(opt);
                }
                // 重新渲染下拉框的内容
                layui.form.render("select");
            }
        }
    });



    /**
     * 表单监听 数据提交按钮
     */
    form.on('submit(addOrUpdateSaleChance)',function (data) {
        //提交时加载数据层
        var index= layer.msg("数据提交中,请稍后...",{icon:16,time:false,shade:0.8});
        //请求后端接口地址
        var url = ctx+"/sale_chance/add";
        // 通过获取隐藏域中的id来判断是更新还是添加 如果不为空 就执行更新操作
        if($("input[name='id']").val()){
            url=ctx+"/sale_chance/update";
        }
        $.post(url,data.field,function (res) {
            if(res.code==200){
                layer.msg(res.msg,{icon: 6});
                //关闭加载层
                layer.close(index);
                //关闭弹出层
                layer.closeAll("iframe");
                // 刷新父页面 渲染数据
                parent.location.reload();
            }else{
                layer.msg(res.msg,{icon:5});
            }
        });
        return false;
    });

});