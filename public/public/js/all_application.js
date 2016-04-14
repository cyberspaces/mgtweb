
window.lazybackendserver="http://us.newasst.com:8080";

var promotionPos = $('#promotion-pos');
var deleteAlert = $('#delete-alert-modal');
var tabs = $('#myTabs');

promotionPos.on('change', function (e) {
    var index = $(this)[0].selectedIndex;
    tabs.find('a:eq(' + index + ')').tab('show');
});


var $table = $('#table');

var testData = [
    {
        'name': '小生活',
        'update_time': '2015-02-13',
        'type': '音乐视频',
        'size': '12.0M',
        'queue': '首页列表1、搜索关键字、分类推荐列表1',
        'desc': '第一款家庭关爱软件',
        'id': 0000
    },
    {
        'name': '微信',
        'update_time': '2015-02-13',
        'type': '音乐视频',
        'size': '15.6M',
        'queue': '首页列表2',
        'desc': '我就是喜欢微信',
        'id': 0001
    },
    {
        'name': 'QQ',
        'update_time': '2015-02-13',
        'type': '音乐视频',
        'size': '12.0M',
        'queue': '首页列表3',
        'desc': 'IO',
        'id': 0002
    },
    {
        'name': '知乎',
        'update_time': '2015-02-13',
        'type': '音乐视频',
        'size': '12.0M',
        'queue': '首页列表4',
        'desc': 'Lina',
        'id': 0003
    },
    {
        'name': '呵呵',
        'update_time': '2015-02-13',
        'type': '音乐视频',
        'size': '12.0M',
        'queue': '首页列表5',
        'desc': 'Luna',
        'id': 0004
    },
    {
        'name': 'Dota2',
        'update_time': '2015-02-13',
        'type': '音乐视频',
        'size': '12.0M',
        'queue': '首页列表6',
        'desc': 'Drow Ranger',
        'id': 0005
    },
    {
        'name': 'Ti5',
        'update_time': '2015-02-13',
        'type': '音乐视频',
        'size': '12.0M',
        'queue': '首页列表7',
        'desc': 'Wind Ranger',
        'id': 0006
    },
    {
        'name': '小金本',
        'update_time': '2015-02-13',
        'type': '音乐视频',
        'size': '12.0M',
        'queue': '首页列表1、搜索关键字、分类推荐列表1',
        'desc': 'Phantom Assassin',
        'id': 0007
    }

];

$(function () {
//    $table.bootstrapTable({
//        data: testData
//    });

        $.ajax({
            url:"/admin/apps/getApppkgs?ts="+new Date().valueOf(),
            type:"GET",
            dataType:"json",
            success:function(rsp){
                if(rsp.code==0) {
                    var jsonObj=rsp.jsonObj;
                    $table.bootstrapTable({
                            data: jsonObj
                     });
                }
            }
        });

    $table.on('all.bs.table', function (e, name, args) {
    });

    $(window).resize(function () {
        $table.bootstrapTable('resetView', {
//            height: getHeight()
        });
    });

    //for (var i = 0; i < 37; i++) {
    //    $table.bootstrapTable('append', testData);
    //}
});

function operateFormatter(value, row, index) {
    return [
        '<a href="javascript:void(0)" class="detail" title="详情">详情 &nbsp &nbsp',
        '</a>',
        '<a href="javascript:void(0)" class="delete" title="删除">删除',
        '</a>',
    ].join('');
}


$(function () {
    $('#datetimepicker').datetimepicker();
});


window.operateEvents = {
    'click .detail': function (e, value, row, index) {
        showDetailModal();
        e.stopPropagation();
    },
    'click .delete': function (e, value, row, index) {
        deleteAlert.modal();
        choosedDeleteApp = row;
        deleteAlert.find('.app-name').text(' ‘' + row.name + '’ ');
        e.stopPropagation();
    }
};

var choosedDeleteApp;

function deleteApp() {
    $table.bootstrapTable('remove', {
        field: 'id',
        values: [choosedDeleteApp.id]
    });
    deleteAlert.modal('hide');
}

function showDetailModal() {
    $('#detail-modal').modal();
}

function showAddModal(){
    $('#add-modal').modal();
}

function addPromotionSuccess(data){
    var options='<option value="None">请选择分类</option>';
     data = $.parseJSON(data);
     $.each(data, function(index, value) {
        options += '<option value="' + value.name + '">' + value.name + "</option>"
     });
     $('#promotion-pos').html(options);
}

$.ajax({
    type:'get',
    url: '/admin/apps/category/gets?params=0&columns=',
    success: addPromotionSuccess
});

//function addPromotion(){
//    var data = [{
//        mainposition:'leibiao1',
//        name:"test1",
//        subposition:''
//    },{
//        mainposition:'leibiao1',
//        name:"test2",
//        subposition:''
//    }];
//        addPromotionSuccess(data);
//}