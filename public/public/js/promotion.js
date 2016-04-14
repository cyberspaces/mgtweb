var paging_isFirst=true;
var paging_total=0;

//初始化
var promotionPos = $('#promotion-pos');
var promotionPosModal = $('#subjectPosition');
var tabs = $('#myTabs');
var tabsModal = $('#myTabsModal');
$('#search-start-time').datetimepicker();
$('#search-end-time').datetimepicker();
$('#time-add-start').datetimepicker();
$('#time-add-end').datetimepicker();

promotionPos.on('change', function (e) {
    var index = $(this)[0].selectedIndex;
    tabs.find('a:eq(' + index + ')').tab('show');
});

promotionPosModal.on('change', function (e) {
    var index = $(this)[0].selectedIndex;
    tabsModal.find('a:eq(' + index + ')').tab('show');
});


//推广计划表格
var promotionPlanTable = (function () {
    var $table = $('#table');
    var deleteAlert = $('#time-delete-modal');
    //初始化显示个列表
    $table.bootstrapTable({
        columns: [
            {
                title: '描述'
            },
            {
                title: '开始时间'
            },
            {
                title: '结束时间'
            },
            {
                title: '状态'
            },
            {
                title: '操作'
            }
        ]
    });
    $(window).resize(function () {
        $table.bootstrapTable('resetView');
    });
    //参数赋值
    var queryParams = function (params) {
        $('#search-start-time').val() && (params.launchtime = new Date($('#search-start-time').val()).getTime());
        $('#search-end-time').val() && (params.expired = new Date($('#search-end-time').val()).getTime());
        params.mainposition = $('#promotion-pos').val();
        if(params.mainposition=="分类-推荐应用列表"){
            params.subposition = $('#sub-position').val();
        }
        params.isFirst=paging_isFirst
        return params;
    };

    var timeFormatter = function (value) {
        var date = new Date(value),
            y = date.getFullYear(),
            M = date.getMonth() + 1,
            d = date.getDate(),
            h = date.getHours(),
            m = date.getMinutes();
        return y + '-' + M + '-' + d + ' ' + h + ':' + (m < 10 ? ('0' + m) : m);
    }
    var ajaxResponseHandler=function(response){
        if(response.code==0){
            var res=new Object()
            if(paging_isFirst) {
                res.total = response.total;
                paging_total=res.total;
                paging_isFirst=false;
            }else{
                res.total=paging_total;
            }
            res.rows=response.jsonObj;
            return res;
        }else {
            alert(response);
            return response;
        }
    };
    var chooseDeleteTime;
    return {
        init: function () {
            paging_isFirst=true;
            paging_total=-1;
            //摧毁旧的
            $table.bootstrapTable('destroy');
            $table.bootstrapTable({
                method: 'get',
                url: '/admin/promoted/gets',
                cache: false,
                pagination: true,
                pageSize: 25,
                pageList: [10, 25, 50],
                showColumns: true,
                showRefresh: true,
                minimumCountColumns: 2,
                sidePagination: 'server',//设置为服务器端分页
                queryParams: queryParams,//参数
                responseHandler:ajaxResponseHandler,
                columns: [
                    {
                        field: 'title',
                        title: '推广名'
                    },{
                        field:"description",
                        title:"描述"
                    },{
                        field:"mainposition",
                        title:"推广位置"
                    },{
                        field:"subposition",
                        title:"分类项推广位置"
                    },
                    {
                        field: 'launchtime',
                        title: '开始时间',
                        sortable: true,
                        formatter: timeFormatter
                    },
                    {
                        field: 'expired',
                        sortable: true,
                        title: '结束时间',
                        formatter: timeFormatter
                    },
                    {
                        field: 'status',
                        title: '状态'
                    },
                    {
                        title: '操作',
                        formatter: function (value, row, index) {
                            if(row.status == '发布'){
                                return '<a href="javascript:void(0)" class="detail" title="详情">详情</a>';
                            }
                            return [
                                '<a href="javascript:void(0)" class="detail" title="编辑">编辑 &nbsp &nbsp',
                                '</a>',
                                '<a href="javascript:void(0)" class="delete" title="删除">删除',
                                '</a>'
                            ].join('');
                        },
                        events: {
                            'click .detail': function (e, value, row, index) {
                                appPlanTable.init(row);
                                e.stopPropagation();
                            },
                            'click .delete': function (e, value, row, index) {
                                deleteAlert.modal();
                                chooseDeleteTime = row;
                                deleteAlert.find('.app-name').text(row.description || '（无描述）');
                                e.stopPropagation();
                            }
                        }
                    }
                ]

            });
        },
        deleteChooseTime: function () {
            $table.bootstrapTable('remove', {
                field: 'id',
                values: [choosedDeleteApp.id]
            });
            deleteAlert.modal('hide');
        },
        table: $table,
        refresh: function () {
            $table.bootstrapTable('refresh');
        }
    }
})();

//添加时间计划弹窗
var addTimePlanModal = (function () {
    var modal = $('#time-add-modal'),
        des = $('#time-description'),
        startTime = $('#time-add-start'),
        endTime = $('#time-add-end');
    //清空数据
    var clear = function () {
        des.empty();
        startTime.empty();
        endTime.empty();
    };
    return {
        //显示
        show: function () {
            clear();
            modal.modal();
        },
        //确定提交
        confirm: function () {
            //todo：ajax请求
            //成功，刷新推广计划表格
            //上传数据
            $("#new_promoted_form").find('select[name="subposition"]').val('');
            $("#new_promoted_form").ajaxForm({
                success:function(data,status,t){
                    promotionPlanTable.refresh();
                },
                dataType:"json",
                error:function(error,status,x){
                    alert("error->"+error+","+status+","+x)
                }
            });
            $("#new_promoted_form").submit();
            modal.modal('hide');
        }
    }
})();

//推广计划应用列表表格
var appPlanTable = (function () {
    var $table = $('#subTable'),
        modal = $('#time-detail-modal'),
        deleteAlert = $('#delete-app-modal'),
        deleteIndex,
        addAlert = $('#add-app-modal'),
        chooseDeleteApp,
        initialData;
        appSearch = new yAppChooser({
            container: $('#add-app-container'),
            singleMode: false,
            url: '/admin/apps/search_c_title'
        });
    var currentStatus;

    var timeFormatter = function (value) {
        var date = new Date(value),
            y = date.getFullYear(),
            M = date.getMonth() + 1,
            d = date.getDate(),
            h = date.getHours(),
            m = date.getMinutes();
        return y + '-' + M + '-' + d + ' ' + h + ':' + (m < 10 ? ('0' + m) : m);
    }
    //上升下降事件
    var upDownEvent = {
        'click .down': function (e, value, row, index) {
            var initData = $table.bootstrapTable('getData');
            var toIndex = index + 1;
            if (!initData[toIndex].title) {
                alert('下一行没有数据，没法交换');
                return;
            }
            var temp = initData[toIndex];
            initData[toIndex] = row;
            initData[index] = temp;
            $table.bootstrapTable('load', initData);
        },
        'click .up': function (e, value, row, index) {
            var initData = $table.bootstrapTable('getData');
            var toIndex = index - 1;
            if (!initData[toIndex].title) {
                alert('上一行没有数据，没法交换');
                return;
            }
            var temp = initData[toIndex];
            initData[toIndex] = row;
            initData[index] = temp;
            $table.bootstrapTable('load', initData);
            console.log(initData);
        },
        'click .delete': function (e, value, row, index) {
            deleteAlert.modal();
            chooseDeleteApp = row;
            deleteIndex = index;
            deleteAlert.find('.app-name').text(row.title);
        },
        'click .add': function (e, value, row, index) {
            appSearch.clear();
            addAlert.modal();
        },
        'change .promotion_type':function(e, value, row, index){
            var target = $(e.currentTarget);
            row.promotionType = target.val();
            row.lazyapp_id = (row.lazyapp_id + '').split('_')[0] + '_'+ row.promotionType;
        }
    };

    //初始化表格
    $table.bootstrapTable({
        pagination: false,
        pageSize: 20,
        columns: [
            {
                title: '应用名',
                field: 'title'
            },
            {
                title: '推荐类型',
                formatter: function (value, row, index) {
                    if (!row.title)return;
                    //if (index == 0) {
                    //    return '<a href="javascript:void(0)" class="down" title="降">降</a>';
                    //}
                    //else if (index == 19) {
                    //    return '<a href="javascript:void(0)" class="up" title="升">升</a>';
                    //}
                    //return [
                    //    '<a href="javascript:void(0)" class="up" title="升">升 &nbsp &nbsp',
                    //    '</a>',
                    //    '<a href="javascript:void(0)" class="down" title="降">降',
                    //    '</a>'
                    //].join('');
                    var options = $('<a><select class="promotion_type">' +
                    '<option value="A">付费推广</option>'+
                    '<option value="B">首发</option>'+
                    '<option value="C">推优</option>'+
                    '<option value="D">免费</option>'+
                    '</select></a>');
                    var values = options.children();
                    if(row.promotionType){
                        values.val(row.promotionType);
                        values.find('option').removeAttr('selected');
                        values.find('option[value="' + row.promotionType + '"]').attr('selected','selected');
                    }
                    if(currentStatus == '发布'){
                        values.attr('disabled','disabled');
                    }
                    return options.html();
                },
                events: upDownEvent
            },
            {
                title: '应用类别',
                field: 'apptype'
            },
            {
                title: '操作',
                formatter: function (value, row, index) {
                    if(initialData.status == '发布')return;
                    var initData = $table.bootstrapTable('getData');
                    if(index == 0){
                        if(!initData[0].title){
                            return '<a href="javascript:void(0)" class="add" title="添加">添加';
                        }
                    }
                    var pre = index - 1;
                    if (row.title) {
                        return [
                            '<a href="javascript:void(0)" class="detail" title="详情">详情 &nbsp &nbsp',
                            '</a>',
                            '<a href="javascript:void(0)" class="delete" title="删除">删除',
                            '</a>'
                        ].join('');
                    }
                    else if(initData[pre] && initData[pre].title)
                    return '<a href="javascript:void(0)" class="add" title="添加">添加';
                },
                events: upDownEvent
            }
        ]
    });
    $(window).resize(function () {
        $table.bootstrapTable('resetView');
    });

    //添加请求
    var addAction = function(status){
        var initData = $table.bootstrapTable('getData');
        console.log(initData);
        var action = '';
        for(var i = 0;i<20;i++) {
            if (!initData[i].title) {
                break;
            }
            action += (initData[i].lazyapp_id + ',')
        }
        $.ajax({
            type:'post',
            url:'/admin/promoted/add/action',
            data:{
                id:initialData.id,
                action:action.substr(0,action.length - 1),
                status:status,
                launchtime:initialData.launchtime,
                expired:initialData.expired,
                mainposition:initialData.mainposition,
                subposition:initialData.subposition
            },
            dataType:'json',
            success: function (res) {
                if(!res.code){
                    modal.modal('hide');
                    promotionPlanTable.refresh();
                }
                else{
                    alert('error code:' + res.code + '/n' + 'reason:' + res.jsonObj);
                }
            },
            error: function () {
                alert("网络异常");
            }
        });
    }
    var initAjax;
    return  {
        //初始化表格内容
        init: function (_initialData) {
            if(_initialData.status=='发布'){
                $('#time-add-btns').hide();
            }
            else{
                $('#time-add-btns').show();
            }
            currentStatus = _initialData.status;
            initAjax && initAjax.abort();
            initialData = _initialData;
            $('#time-detail-start').text(timeFormatter(initialData.launchtime));
            $('#time-detail-end').text(timeFormatter(initialData.expired));
            $('#time-detail-des').text(initialData.description || '无');
            $('#time-detail-status').text(initialData.status);
            $table.bootstrapTable('load', []);
            $table.bootstrapTable('showLoading');
            var action = _initialData.action;
            var newAction = '', actionArray;
            if(action) {
                actionArray = action.split(',');
                $.each(actionArray, function (index, each) {
                    var eachArray = each.split('_');//id_A
                    newAction += (eachArray[0] + ',')
                });
            }
            initAjax = $.ajax({
                type: "GET",
                dataType: 'json',
                url: '/admin/apps/gets?ids=' + newAction.substr(0, newAction.length - 1),
                success: function (data) {
                    if (data.code) {
                        alert('错误代码：' + data.code);
                    }
                    else {
                        modal.modal();
                        var obj = data.jsonObj;
                        for(var i = 0,j = obj.length;i < j;i++){
                            var _array = actionArray[i].split('_');
                            if(_array.length > 1) {
                                obj[i].promotionType = actionArray[i].split('_')[1];
                                obj[i].lazyapp_id += ('_' + obj[i].promotionType);
                            }
                            else{
                                obj[i].promotionType = 'A';
                                obj[i].lazyapp_id += '_A';
                            }
                        }
                        for (var i = obj.length; i < 20; i++) {
                            obj.push({});
                        }
                        $table.bootstrapTable('load', obj);
                    }
                },
                complete: function () {
                    $table.bootstrapTable('hideLoading');
                },
                error: function () {
                    alert('network error!');
                    modal.modal('hide');
                }
            })
        },
        save: function () {
            addAction('发布');
        },
        saveDraft: function () {
            addAction('草稿');
        },
        deleteChooseApp: function () {
            $table.bootstrapTable('remove', {
                field: 'id',
                values: [chooseDeleteApp.id]
            });
            $table.bootstrapTable('append',{})           
             deleteAlert.modal('hide');
        },
        confirmAddApp: function () {
            var data = appSearch.getData();
            if (data.length == 0) {
                alert('你没有选择任何项');
                return;
            }
            var initData = $table.bootstrapTable('getData');
            for (var i = 0, l = initData.length,j=0; i < l; i++) {
                if (!initData[i].title) {
                    if(data[j]) {
                        data[j].lazyapp_id = data[j].id + '_A';
                        initData.splice(i, 1, data[j]);
                        j++;
                    }
                }
            }
            addAlert.modal('hide');
            $table.bootstrapTable('load', initData);
        }
    }
})();


