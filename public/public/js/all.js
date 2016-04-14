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
        'name': '小生活public/js/all.js testData',
        'update_time': '2013-11-11 12:12:12',
        'type': '社交通讯XXXXXXXXXXXXX',
        'size': '10.8MB',
        'queue': '1',
        'id': 100
    }
];

$(function () {
//    $table.bootstrapTable({
//        data: testData
//    });

    $.ajax({
        url:"/admin/apps/getApps?ts="+new Date().valueOf(),
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

    for (var i = 0; i < 37; i++) {
        $table.bootstrapTable('append', testData);
    }
});

function operateFormatter(value, row, index) {
    return [
        '<a href="javascript:void(0)" class="detail" title="详情">详情',
        '</a>  ',
        '<a href="javascript:void(0)" class="delete" title="删除">删除',
        '</a>  ',
        '<a href="javascript:void(0)" class="history" title="历史版本">历史版本',
        '</a>'
    ].join('');
}

window.operateEvents = {
    'click .detail': function (e, value, row, index) {
        showDetailModal(row);
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
