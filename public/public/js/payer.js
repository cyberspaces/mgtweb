$(function () {
    $('#datetimepicker').datetimepicker();
});

var $table = $('#table');

var testData = [
    {
        'title': '小生活',
        'apply_time': '1999-09-09 12:12:12',
        'applicant': '腾讯公司',
        'valid_date': '2015-02-13至2015-04-15',
        'type': '全付',
        'position': '首页top1',
        'state': '未审核'
    },
    {
        'title': '小生活',
        'apply_time': '1999-09-09 12:12:12',
        'applicant': '腾讯公司',
        'valid_date': '2015-02-13至2015-04-15',
        'type': '全付',
        'position': '首页top1',
        'state': '审核中'
    },
    {
        'title': '小生活',
        'apply_time': '1999-09-09 12:12:12',
        'applicant': '腾讯公司',
        'valid_date': '2015-02-13至2015-04-15',
        'type': '全付',
        'position': '首页top1',
        'state': '审核通过'
    },
    {
        'title': '小生活',
        'apply_time': '1999-09-09 12:12:12',
        'applicant': '腾讯公司',
        'valid_date': '2015-02-13至2015-04-15',
        'type': '全付',
        'position': '首页top1',
        'state': '审核未通过'
    },
    {
        'title': '小生活',
        'apply_time': '1999-09-09 12:12:12',
        'applicant': '腾讯公司',
        'valid_date': '2015-02-13至2015-04-15',
        'type': '全付',
        'position': '首页top1',
        'state': '已过期'
    }
];

$(function () {
    $table.bootstrapTable({
        data: testData
    });

    $(window).resize(function () {
        $table.bootstrapTable('resetView');
    });

    for (var i = 0; i < 3; i++) {
        $table.bootstrapTable('append', testData);
    }


    //$('#detailModal').on('show.bs.modal', function (e) {
    //    var $modal = $(this),
    //        esseyId = e.relatedTarget.id;
    //
    //    // ajax
    //
    //
    //});

});

function operateFormatter(value, row, index) {
    var state = row.state;

    var operate = {
        text: '',
        modalId: ''
    };

    switch (state) {
        case '未审核':
        case '审核中':
            operate.text = '更新状态';
            operate.modalId = 'updateModal';
            break;
        case '审核通过':
            operate.text = '无';
            break;
        case '审核未通过':
            operate.text = '查看原因';
            operate.modalId = 'showReasonModal';
            break;
        case '已过期':
            operate.text = '删除记录';
            operate.modalId = 'deleteModal';
            break;
        default:
            operate.text = '无';
    }

    return [
        '<a href="javascript:void(0)" id="row_' + index + '"  data-toggle="modal" data-target="#' + operate.modalId + '">' + operate.text + '',
        '</a>  '
    ].join('');
}


//window.operateEvents = {
//    'click .check': function (e, value, row, index) {
//    },
//    'click .edit': function (e, value, row, index) {
//    }
//};
