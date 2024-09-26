$(function () {
    loadModuleInfo(); 
});

//定义树形结构对象
var zTreeObj;

/**
 * 加载资源树形数据
 */
function loadModuleInfo() {
    //发送请求拿取数据
    $.ajax({
        type:"get",
        url:ctx+"/module/queryAllModules?roleId="+$("input[name='roleId']").val(),
        dataType:"json",
        success:function (data) {

            // zTree 的参数配置，深入使用请参考 API 文档（setting 配置详解）
            var setting = {
                //复选框
                check: {
                    enable: true
                },
                //简单json数据
                data: {
                    simpleData: {
                        enable: true
                    }
                },
                //绑定函数
                callback: {
                    //当用户选择复选框或者单选框（checkbox/radio） 的时候触发的函数
                    onCheck: zTreeOnCheck
                }
            };
            zTreeObj = $.fn.zTree.init($("#test1"), setting, data);
        }
    })
}

/**
 * 当用户选择复选框或者单选框（checkbox/radio） 的时候触发的函数
 * @param event
 * @param treeId
 * @param treeNode
 */
function zTreeOnCheck(event, treeId, treeNode) {
    //alert(treeNode.tId + ", " + treeNode.name + "," + treeNode.checked);
    //获得所有被勾选的节点集合 如果checked=true 则表示勾选 这里可以获得所有的记录存在nodes中
    var nodes= zTreeObj.getCheckedNodes(true);

    //判断并遍历选重点节点集合 获取其中的id id值的形式要是mids=1&mids=2这样
    var mids="mids=";
    for(var i=0;i<nodes.length;i++){
        if(i<nodes.length-1){
            mids=mids+nodes[i].id+"&mids=";
        }else{
            mids=mids+nodes[i].id;
        }
    }

    $.ajax({
        type:"post",
        url:ctx+"/role/addGrant",
        //roleId获取隐藏域中的值 拼接上角色id roleId
        data:mids+"&roleId="+$("input[name='roleId']").val(),
        dataType:"json",
        success:function (data) {
            console.log(data);
        }
    })

}