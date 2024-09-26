layui.use(['form', 'layer','formSelects'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;
    var  formSelects = layui.formSelects;


    /**
     * 取消按钮弹出框
     */
    $("#closeBtn").click(function (){
        //在iframe关闭自己时
        var index=parent.layer.getFrameIndex(window.name);//得到索引
        parent.layer.close(index);
    })

    /**
     * 获得用户角色
     * @type {jQuery|*}
     */
    var userId=$("input[name='id']").val();
    formSelects.config('selectId',{
        type:"post",
        searchUrl:ctx+"/role/queryAllRoles?userId="+userId,
        //自定义返回数据中name的key, 默认 name
        keyName: 'roleName',
        //自定义返回数据中value的key, 默认 value
        keyVal: 'id'
    },true);

    /**
     * 添加或者更新按钮提交
     */
    form.on('submit(addOrUpdateUser)',function (data) {

        var index= top.layer.msg("数据提交中,请稍后...",{icon:16,time:false,shade:0.8});
        var url = ctx+"/user/add";
        //通过id判断是更新操作
        if($("input[name='id']").val()){
            url=ctx+"/user/update";
        }

        $.post(url,data.field,function (res) {
            if(res.code==200){
                top.layer.msg(res.msg,{icon: 6});
                top.layer.close(index);
                layer.closeAll("iframe");
                // 刷新父页面
                parent.location.reload();
            }else{
                layer.msg(res.msg,{icon: 5});
            }
        });
        return false;
    });

    /**
     * 加载角色下拉框
     * 配置远程搜索、请求参数、请求类型等
     *
     * formSelects.config(ID, Options, isJson)
     *
     * @param ID xm-select的值
     * @param Options 配置项
     * @param isJson 是否传输json数据，true将添加请求头 Content-Type: application/json; charset=UTF-8
     */
    var userId = $("[name='id']").val();
    formSelects.config("selectId", {
        type: "post", // 请求方式
        searchUrl: ctx + "/role/queryAllRoles?userId="+userId,  // 请求地址
        keyName: 'roleName',  // 下拉框中显示的文本内容，要与返回的数据中的key一致
        keyVal: 'id'  // 下拉框中的值 用于传值
    }, true);

});