window.lazystorageserver="http://us.newasst.com:8085";
window.lazystorageserveruploader=lazystorageserver+"/upload.php";

$.fn.form = function(method, data){
    var $this = this;
    var _funcs = {};
    _funcs.load = function(d){
        for(var key in d){
            $this.find('input[name="' + key +'"]').val(data[key]);
            $this.find('textarea[name="' + key +'"]').val(data[key]);
            $this.find('select[name="' + key +'"]').val(data[key]);
            $this.find('radio[name="' + key +'"]').val(data[key]);
        }
    }

    _funcs[method](data);
}
//???
var upLoadApp = $('#uploadapp-pos');
var upLoadAppModal = $('#subjectPosition');
var tabs = $('#myTabs');
var tabsModal = $('#myTabsModal');

upLoadApp.on('change', function (e) {
    var index = $(this)[0].selectedIndex;
    tabs.find('a:eq(' + index + ')').tab('show');
});

upLoadAppModal.on('change', function (e) {
    var index = $(this)[0].selectedIndex;
    tabsModal.find('a:eq(' + index + ')').tab('show');
});


//APP上传
var uploadForm = $('#form-upload-app');
$('#app-file').uploadify({
    //uploader:'/fileupload', //服务器地址
    uploader: lazystorageserveruploader,
    auto : true,//自动上传
    'buttonText':'选择apk',
    queueSizeLimit: 1,
    multi:false,
    removeTimeout:0,
    formData:{},
    fileTypeExts:'*.apk',
    //flash
    'swf': "../../lib/uploadify/uploadify.swf",
    onUploadSuccess: function (fileObj, data, response){
        if(response) {
            console.info($.parseJSON(data));
            data = $.parseJSON(data);
            var _formdata= data; //$.parseJSON(data.jsonObj[0]);
            if(data.errcode == 0){
                _formdata.downloadurls = lazystorageserver + '/' + _formdata.mediakey;
                uploadForm.form('load', _formdata);
                $("#package_id").val(_formdata.mediakey)
            }
            else{
                alert($.parseJSON(data));
            }
        }
        else{
            alert('error');
        }
    }
});

//----------------------选择应用截图上传---------------------------
for(var i = 1;i<=5;i++) {
    +function(index) {
        $('#app-screenshot-file-' + i).uploadify({
            uploader: lazystorageserveruploader, //服务器地址
            auto: true,//自动上传
            'buttonText': '选择截图' + index,
            queueSizeLimit: 1,
            multi: false,
            formData: {},
            fileTypeExts: '*.png;*.jpg;*.gif',
            //flash
            'swf': "../../lib/uploadify/uploadify.swf",
            onUploadSuccess: function (fileObj, data, response) {
                if (response) {
                    data = $.parseJSON(data);
                    if (data.errcode == 0) {
                        $('.app-screenshot:eq(' + (index - 1) + ')').children('img').attr('src', lazystorageserver + '/' + data.mediakey);
                        $('#app-screenshot-file-' + i).val(lazystorageserver + '/' + data.mediakey);
                    } else {
                        alert(data);
                    }
                }
                else {
                    alert('error');
                }
            }
        });
    }(i)
}

//----------------------企业开发者身份信息上传---------------------------
//----------------------企业开发者身份信息上传---------------------------
//----------------------企业开发者身份信息上传---------------------------

//选择一张图片 App logo 选择应用图标上传
function appCommonFile(id){
    var $appEA = $('#' + id);
    $appEA.uploadify({
        uploader: lazystorageserveruploader, //服务器地址
        auto : true,//bu自动上传
        'buttonText':'选择一张图片',
        queueSizeLimit: 1,
        multi:false,
        removeTimeout:0,
        formData:{},
        fileTypeExts:'*.jpg;*.png;*.gif',
        'swf': "../../lib/uploadify/uploadify.swf",
        onUploadSuccess: function (fileObj, data, response){
            if(response) {
                data = $.parseJSON(data);
                if(data.errcode == 0){
                    var $view = $('#' + id).parent().siblings('.view-sixth');
                    $view.children('img').attr('src', lazystorageserver + '/' + data.mediakey);
                    $view.children('input[type="hidden"]').val(lazystorageserver + '/' + data.mediakey);
                } else{
                    alert(data);
                }
            }
            else{
                alert('error');
            }
        }
    });
}

appCommonFile('app-icon');

appCommonFile('app-Enterprise_A');

appCommonFile('app-Enterprise_B');

appCommonFile('app-Enterprise_C');

appCommonFile('app-Enterprise_D');

$('.mask>.btn').on('click', function(){
    var $this = $(this);
    $this.parent().siblings('img').attr('src', '../../public/img/screen.jpg');
    $this.parent().siblings('input[type="hidden"]').val('');
});

uploadForm.on('keypress','input', function(e){
    var $this = $(e.currentTarget);
    if(!$this.val()){
        $this.css('border-color', 'red');
    }
    else{
        $this.css('border-color', '');
    }
});


/**
 * 提交
 */
function submit(btn){
    var $btn = $(btn);
    var valiate = true;
    uploadForm.find('input').each(function (index, item) {
        var $item = $(item);
        if(!$item.val()){
            $item.css('border-color', 'red');
            valiate = false;
        }
    });

    valiate = true;

    if(!valiate){
        alert('存在未输入的项目');
        return;
    }
    var params = uploadForm.serializeArray();
    var screenshots = [];
    uploadForm.find('.app-screenshot')
        .each(function(i,item){
           var $item = $(item);
            var src = $item.children('img').attr('src');
            if(!src || src == '../../public/img/screen.jpg')return;
            screenshots.push(src);
        });
    var s_value={"normal": screenshots, "small": screenshots};
    params.push({"name":"screenshots","value": JSON.stringify(s_value)});
    $btn.hide();
    $.ajax({
        url:'/admin/apk/create_review_release',
        type:'post',
        dataType:'json',
        data:params,
        success:function(data){
            if(data.code >= 0)
                alert('上传成功');
            else
                alert(data.jsonObj);
        },
        error:function(){
            alert('网络或者服务器错误');
        },
        complete:function(){
            //$btn.prop('disabled', false);
            $btn.show();
        }
    })
}

