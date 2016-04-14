window.lazytemplateserver="http://us.newasst.com:8085";

var $table = $('#table');
var sid=0
var  columns ="title,subtitle,lazytopicposition_name,topictemplate_name,creation,launchtime,expired,topicstatus,action";
var limit=10;
var isFirst=true;
var st=0;
var et=0;
var template="";
var position="";
var noHistory=false;
var total=0;

$(function () {
    $('#datetimepicker').datetimepicker();
    $('#datetimepicker02').datetimepicker();
    $('#datetimepicker03').datetimepicker();
    $('#datetimepicker04').datetimepicker();
    $.ajax({
        url:"/topic/locations",
        type:"GET",
        dataType:"json",
        success:function(rsp){
            var options='<option value="None">请选择推广位</option>';
            if(rsp.code==0) {
                var data=rsp.jsonObj
                $.each(data, function (index, value) {
                    options += '<option value="' + value.name + '">' + value.name + "</option>"
                });
                $("select.position").each(function(){
                    $(this).html(options);
                });
            }
        }
    });
    $.ajax({
        url:"/topic/templates",
        type:"GET",
        dataType:"json",
        success:function(rsp){
            var options='<option value="None" url-data="请选择专题模板...">请选择专题模板</option>';
            if(rsp.code==0) {
                var data=rsp.jsonObj;
                $.each(data, function (index, value) {
                    options += '<option value="' + value.name+ '" topic-url="'+lazytemplateserver+'" url-data="'+value.template+'">' + value.name + "</option>"
                });
                $("select.template").each(function(){
                    $(this).html(options);
                })
            }
        }
    })
    $("#search_btn").click(function(){
        //isFirst=true;
        sid=0;
        $("#table").bootstrapTable("refresh");
    });
    $('#detailModal').on('show.bs.modal', function (e) {
        var $modal = $(this),
            esseyId = e.relatedTarget.id;

        // ajax
    });
    $("select.templateContentSelect").change(function(){
        var v=$(this).children('option:selected').attr("url-data");
        $("#subjectDesc").val(v);
        var url=$(this).children('option:selected').attr("topic-url");
        console.info(url);
        $('#refto_id').val(url);
        $('#frame').attr('src',url+'&nonce='+new Date().valueOf());
        refreshTemplate();
    });
    $("#new_topic_form").ajaxForm({
        success:function(data,status,t){
            if(data.code){
                alert("error->"+data.code+"," + data.jsonObj);
                return;
            }
            $('#table').bootstrapTable('refresh');
            $('#addModal').modal('hide');
        },
        dataType:"json",
        error:function(error,status,x){
            alert("error->"+error+","+status+","+x)
        }
    });
});

function changeTarget(url,formid){
    $(formid).attr("action",url)
}
function operateFormatter(value, row, index) {
    var data = row;
    if(data.topicstatus == '发布'){
        return [
            '<a class="see" href="javascript:void(0)" id="edit' + index + '" title="查看" data-toggle="modal" data-target="#editModal" data-backdrop="static">查看',
            '</a>'
        ].join('');
    }
    return [
        '<a class="edit" href="javascript:void(0)" id="edit' + index + '" title="编辑" data-toggle="modal" data-target="#editModal" data-backdrop="static">编辑',
        '</a>'
    ].join('');
}


function paramsEncoder(request){
    st=(new Date($("#datetimepicker").val()).valueOf());
    et=new Date($("#datetimepicker02").val()).valueOf();
    template=$("#topic_template_select").val();
    position=$("#topic_position_select").val();
    noHistory=$("#noHistory").is(":checked");

    //limit=request.limit;//怎么获取limit的值
    //if(sid>0) {
    //    request.sid=sid;
    //    alert(request.sid);
    //}
    request.columns=columns;
    request.st=st;
    request.et=et;
    if(template!="None") request.template=template;
    if(position!="None") request.position=position;
    //request.isFirst=isFirst;
    request.noHistory=noHistory;
    return request;
}
function responseHandler(response){
    //if(isFirst) {
    //    isFirst=false;
    //    total=response.total;
    //}else response.total=total;
    //sid=response.sid;

    if(response.rows && response.rows.code>=0) {
        response.rows = response.rows.jsonObj;
    }else{
        response.rows=[];
        response.total=0;
    }
    return response;
}
function getFormatDate(date, pattern) {
    if (date == undefined) {
        date = new Date();
    }
    if (pattern == undefined) {
        pattern = "yyyy-MM-dd HH:mm";
    }
    return date.format(pattern);
}
function dateSimpleStringFormatter(value,row,index){
    return timeFormatter(value);
}

//window.operateEvents = {
//    'click .check': function (e, value, row, index) {
//    },
//    'click .edit': function (e, value, row, index) {
//    }
//};

var promotionPos = $('#promotion-pos');
//var deleteAlert = $('#delete-alert-modal');
var tabs = $('#myTabs');
promotionPos.on('change', function (e) {
    var index = $(this)[0].selectedIndex;
    tabs.find('a:eq(' + index + ')').tab('show');
});


var timeFormatter = function (value) {
    var date = new Date(value),
        y = date.getFullYear(),
        M = date.getMonth() + 1,
        d = date.getDate(),
        h = date.getHours(),
        m = date.getMinutes();
    return y + '-' + M + '-' + d + ' ' + h + ':' + (m < 10 ? ('0' + m) : m);
}
window.operateEvents = {
    'click .edit': function (e, value, row, index) {
        $('#addModal').find('.d_input').prop('disabled',false);
        $('#addModal').find('.d_button').show();
        var data=row;
        $("#subjectTitle").val(data.title);
        $("#subjectSubtitle").val(data.subtitle);
        $("#datetimepicker03").val(timeFormatter(data.launchtime));
        $("#datetimepicker04").val(timeFormatter(data.expired));
        $("#subjectPosition").val(data.lazytopicposition_name);
        $("#subjectImage").val(data.topictemplate_name);
        $('#addModal').modal('show');
        $('#subjectDesc').val(data.action);
        e.stopPropagation();
    },
    'click .see': function (e, value, row, index) {
        var data=row;
        console.log(data);
        $('#addModal').find('.d_input').prop('disabled',true);
        $('#addModal').find('.d_button').hide();
        $("#subjectTitle").val(data.title);
        $("#subjectSubtitle").val(data.subtitle);
        $("#datetimepicker03").val(timeFormatter(data.launchtime));
        $("#datetimepicker04").val(timeFormatter(data.expired));
        $("#subjectPosition").val(data.lazytopicposition_name);
        $("#subjectImage").val(data.topictemplate_name);
        $('#addModal').modal('show');
        $('#subjectDesc').val(data.action);
        refreshTemplate();
        e.stopPropagation();
    }
};

function addPromotion(){
    var modal = $('#addModal');
    modal.find('.d_input').prop('disabled',false);
    modal.find('.d_input[type="text"]').val('');
    modal.find('select.d_input').val('');
    $('#subjectDesc').val('');
    modal.find('.d_button').show();
    modal.modal();
}
var appSearch;
$(function() {
        GetCursor();
        appSearch = new yAppChooser({
            container: $('#add-app-container'),
            singleMode: false,
            url: '/admin/apps/search_c_title'
        });
    }
);

function searchApp(){
    appSearch.clear();
    $('#add-app-modal').modal();
}

function getChoosedAppIDs(){
    var data = appSearch.getData();
    console.log(data);
    var returnValue = '';
    $.each(data, function (index, each) {
        returnValue += each.id+',';
    });
    returnValue = returnValue.substr(0,returnValue.length - 1);
    $('#add-app-modal').modal('hide');
    insertText(returnValue);
}

function refreshTemplate(){
    var src = $('#subjectDesc').val();
    var base_url=($("#subjectImage").children('option:selected').attr('topic-url'));
    var fullUrl=base_url+src.replace(' ','').replace('，',',');
    $('#frame').get(0).src = fullUrl;
}

/*光标焦点*/
var rie; //全局变量
function GetCursor() {
    if (document.all) {//IE要保存Range
        var obj = document.getElementById('subjectDesc');
        obj.focus();
        rie = document.selection.createRange();
    }
}
//光标定位与指定位置插入文本
//str ---   要插入的字符
function insertText(str) {
    var obj = document.getElementById('subjectDesc');
    if (document.selection) { //ie
        obj.focus();//先激活当前对象
        rie.text = str;
    } else if (typeof obj.selectionStart === 'number' && typeof obj.selectionEnd === 'number') { //非ie
        var startPos = obj.selectionStart,
            endPos = obj.selectionEnd,
            cursorPos = startPos,
            tmpStr = obj.value;
        obj.value = tmpStr.substring(0, startPos) + str + tmpStr.substring(endPos, tmpStr.length);
        cursorPos += str.length;
        obj.selectionStart = obj.selectionEnd = cursorPos;
    } else {
        obj.value += str;
    }
}

$(function(){
//uploadify
var uploadfile = $("#uploadfile");
uploadfile.uploadify({
    swf: '../../lib/fileuploadplugin/uploadify.swf',
    //uploader: '/fileupload',
    uploader: 'http://us.newasst.com:8085/upload.php',
    auto: true,
    multi: false,
    fileTypeExts: '*.gif; *.jpg; *.png',
    buttonText: '选择文件',
    onUploadSuccess: function (file, data, response) {
        var temp_mediakey = $.parseJSON(data).mediakey;
        $('#uploadImageResult').text(temp_mediakey);
        var temp_icon = $('#subjectDesc').val();
        temp_icon = temp_icon.replace('icon=','icon='+temp_mediakey+'&');
        $('#subjectDesc').val(temp_icon);
        refreshTemplate();
        console.info($.parseJSON(data));
    },
    onSelect: function () {
        uploadfile.uploadify('settings', 'formData', { });
    },
    onSelectError: function (file, errorCode, errorMsg) {
        switch (errorCode) {
            case -100:
                alert("上传的文件数量已经超出系统限制的" + uploadfile.uploadify('settings', 'queueSizeLimit') + "个文件！");
                break;
            case -110:
                alert("文件 [" + file.name + "] 大小超出系统限制的" + uploadfile.uploadify('settings', 'fileSizeLimit') + "大小！");
                break;
            case -120:
                alert("文件 [" + file.name + "] 大小异常！");
                break;
            case -130:
                alert("文件 [" + file.name + "] 类型不正确！");
                break;
        }
    }
});
});

function uploadImage(){
    $('#upload-image-modal').modal();
    $('#uploadImageResult').empty();
}

