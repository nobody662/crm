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
     * 确认按钮提交
     * 表单submit监听 addOrUpdateCusDevPlan是html页面中lay-filter属性值
     */
    form.on('submit(addOrUpdateCusDevPlan)',function (data) {
        //提交时数据加载层
        var index= top.layer.msg("数据提交中,请稍后...",{icon:16,time:false,shade:0.8});

        //得到所有表单元素值
        var formData = data.field;

        //请求地址
        var url = ctx+"/user/add";
        
        if($("input[name='id']").val()){
            url=ctx+"/cus_dev_plan/update";
        }
        //发送请求
        $.post(url,formData,function (res) {
            if(res.code==200){
                //成功
                top.layer.msg("操作成功",{icon: 6});
                //关闭加载层页面
                top.layer.close(index);
                //关闭弹出层
                layer.closeAll("iframe");
                // 刷新父页面,重新加载数据
                parent.location.reload();
            }else{
                layer.msg(res.msg,{icon:5});
            }
        });
        return false;
    });

});