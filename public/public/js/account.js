$(function () {
    $('#datetimepicker').datetimepicker();
});

$(function(){
    $("#add_admin_btn").click(function(){
        var formURL=$("#newAdminModel").attr("action");
        $.ajax({
            url:formURL,
            type:"POST",
            data:{
                "name":$("#name").val(),
                "passwd":$("#passwd").val(),
                "role":$("#role").val(),
                "username":$("#username").val(),
                "idcard":$("#idcard").val(),
                "phone":$("#phone").val(),
                "email":$("#email").val(),
                "address":$("#address").val()
            },
            dataType:"json",
            success:function(data){
                if(data.code==0&&data.jsonObj>0) alert("创建账户成功")
                else alert("创建用户失败！")
            }

        })
    })
})
//var $table = $('#table');
//
//$(function () {
//    $.ajax({
//        url:"/admins",
//        dataType:"json",
//        type:"get",
//        success:function(data){
//            if(data.code==0) $table.bootstrapTable({data:data.jsonObj})
//        },
//        error:function(error){
//            alert("error->"+error)
//        }
//    })
//    //$table.bootstrapTable({
//    //    data: testData
//    //});
//
//    $(window).resize(function () {
//        $table.bootstrapTable('resetView');
//    });
//
//    for (var i = 0; i < 5; i++) {
//        $table.bootstrapTable('append', testData);
//    }
//});

function operateFormatter(value, row, index) {
    if(row.state=="apply"){
    return [
    '<a class="edit" href="javascript:void(0)" data-toggle="modal" data-target="#changeState">审核',
    '</a>'
    ].join('');}
    else //if(row.state=="no_pass")
    {
        return [
    '<a class="edit" href="javascript:void(0)" data-toggle="modal" data-target="#seeReason">-',
    '</a>'
    ].join('');
    }
    //if(row.role=="运营商")
    //{
    //    return [
    //'<a class="edit" href="javascript:void(0)" data-toggle="modal" data-target="#editModal">编辑',
    //'</a>'
    //].join('');
    //}
    //if(row.role=="管理员")
    //{
    //    return [
    //'<p>无',
    //'</p>'
    //].join('');
    //}
}
function statusHandler(value,row,index){
    if(value=="apply"){
        return "申请";
    }
    if(value=="release"){
        return "通过";
    }
    if(value=="no_pass"){
        return "未通过";
    }
    return "unknown";
}
function formatterRole(value, row, index) {
    if(value=="admin"){
    return [
    '<p style="color:#CC33CC;">管理员',
    '</p>'
    ].join('');}
    if(value=="operator"){
        return [
    '<p style="color:black;">运营商',
    '</p>'
    ].join('');
    }
    if(value=="developer"){
        return [
        '<p style="color:black;">开发者',
        '</p>'
        ].join('');
    }else{
        return [
        '<p style="color:black;">黑户',
        '</p>'
        ].join('');
    }
}
var page_start=0;
function responseHandler(rsp){
    page_start=rsp.start;
    rsp.rows=rsp.rows.jsonObj
    return rsp;
}
function paramsHandler(params){
    params.start=page_start;
    return params;
}
/*window.operateEvents = {
    'click .check': function (e, value, row, index) {
        $('#detailModal').modal();
    },
    'click .edit': function (e, value, row, index) {
        $('#detailModal').modal();
    }
};*/
