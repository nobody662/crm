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
     * 提交按钮
     */
    form.on('submit(addOrUpdateRole)',function (data) {
        //提交时加载数据层
        var index= top.layer.msg("数据提交中,请稍后...",{icon:16,time:false,shade:0.8});
        //请求后端接口地址
        var url = ctx+"/role/add";
        // 通过获取隐藏域中的id来判断是更新还是添加 如果不为空 就执行更新操作
        if($("input[name='id']").val()){
            url=ctx+"/role/update";
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