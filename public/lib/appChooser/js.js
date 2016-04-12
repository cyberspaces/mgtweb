/**
 * app通用搜索框
 * @param options{
 * url:请求地址
 * }
 */
function yAppChooser(options) {
    var that = this;
    that.options = options;
    //选择的数据
    var choosedData = [];
    this.dom = $('<div class="yChooser-body">'
        + '<div class="yChooser-search">'
        + '<input class="text" type="text" placeholder="请输入关键字.."/>'
        + '<a href="javascript:;" class="search">搜索</a>'
        + '</div>'
        + '<div class="yChooser-result">'
        + '</div>'
        + '<div class="yChooser-btn">'
        + '<a href="javascript:;" class="operate-btn sure">确认选择</a><a class="operate-btn cancel" href="javascript:;">取消</a>'
        + '</div>'
        + '</div>');
    var box = this.options.container;
    if (box.css('position') == 'static') {
        box.css('position', 'relative');
    }
    this.infoDom = $('<div class="info"></div>');
    this.dom.appendTo(box);
    this.textInput = this.dom.find('.text');
    this.searchBtn = this.dom.find('.search');
    this.resultBox = this.dom.find('.yChooser-result');
    if (!options.showBtn) {
        this.resultBox.css('bottom', '0');
        this.dom.find('.yChooser-btn').remove();
    }
    ;
    //请求
    var ajaxRequest,ajaxData;
    this.searchBtn.on('click', function () {
        //如果搜索框值为空
        if (!that.textInput.val()) {
            that.infoDom.html('关键字不能为空');
            that.infoDom.appendTo(that.resultBox);
            return;
        }
        that.infoDom.remove();
        //取消上一次的请求
        ajaxRequest && ajaxRequest.abort();
        //清空内容
        that.resultBox.empty();
        //添加背景
        that.resultBox.addClass('background-loading');
        //ajax请求
        ajaxRequest = $.ajax({
            type: 'get',
            dataType:'json',
            url: that.options.url + '?params=' + that.textInput.val()+"&columns=id,title",
            success: function (res) {
                if (res.code) {
                    that.infoDom.text('查询失败，error code:' + res.code);
                    that.infoDom.appendTo(that.resultBox);
                }
                else {
                    ajaxData = res.jsonObj;
                    if(ajaxData.length == 0){
                        that.infoDom.text('没有找到任何结果');
                        that.infoDom.appendTo(that.resultBox);
                    }
                    for (var i = 0, l = ajaxData.length; i < l; i++) {
                        if (options.singleMode) {
                            var item = $('<div class="item" style="text-align:center;" dataIndex="' + i + '">' + ajaxData[i].title + '</div>');
                        }
                        else {
                            var item = $('<div class="item" dataIndex="' + i + '"><input type="checkbox" />' + ajaxData[i].title + '</div>');
                        }
                        item.appendTo(that.resultBox);
                    }
                }
            },
            error: function () {
                that.infoDom.text('网络错误，请检查');
                that.infoDom.appendTo(that.resultBox);
            },
            complete: function () {
                that.resultBox.removeClass('background-loading');
            }
        });
//        setTimeout(function () {
//            that.resultBox.removeClass('background-loading');
////            that.infoDom.text('没有查询到结果');
////            that.infoDom.appendTo(that.resultBox);
//            ajaxData = [
//                {
//                    name: '小生活',
//                    id: 1
//                },
//                {
//                    name: '小生活',
//                    id: 2
//                },
//                {
//                    name: '小生活',
//                    id: 3
//                },
//                {
//                    name: '小生活',
//                    id: 4
//                },
//                {
//                    name: '小生活',
//                    id: 5
//                }
//            ];
//            for (var i = 0, l = ajaxData.length; i < l; i++) {
//                if (options.singleMode) {
//                    var item = $('<div class="item" style="text-align:center;" dataID=' + ajaxData[i].id + '>' + ajaxData[i].name + '</div>');
//                }
//                else {
//                    var item = $('<div class="item" dataID=' + ajaxData[i].id + '><input type="checkbox" />' + ajaxData[i].name + '</div>');
//                }
//                item.appendTo(that.resultBox);
//            }
//        }, 2000)
    });

    var dataChange = function () {
        var IDS = [];
        that.dom.find('.item').each(function (index, each) {
            each = $(each);
            each.hasClass('active') && IDS.push(each.attr('dataIndex'));
        });
        var datas = [];
        for (var i = 0, l = IDS.length; i < l; i++) {
            datas.push(ajaxData[IDS[i]]);
        }
        return datas;
    }

    this.resultBox.on('click', '.item', function (e) {
        //单选模式
        if (options.singleMode) {
            var fireDom = $(e.currentTarget);
            if (fireDom.hasClass('active')) {
                fireDom.removeClass('active');
            }
            else {
                that.resultBox.find('.item').removeClass('active');
                fireDom.addClass('active');
            }
        }
        else {
            var fireDom = $(e.currentTarget);
            var checkBox = fireDom.children('input[type="checkbox"]');
            if (fireDom.hasClass('active')) {
                fireDom.removeClass('active');
                checkBox.prop('checked', false);
            }
            else {
                fireDom.addClass('active');
                checkBox.prop('checked', true);
            }
        }
        choosedData = dataChange();
    });

    this.dom.find('.sure').click(function () {
        that.options.onSure && that.options.onSure(that.getData);
        that.dom.remove();
    });


    this.dom.find('.cancel').click(function () {
        that.options.cancel && that.options.cancel();
    });

    this.clear = function () {
        that.textInput.val('');
        that.resultBox.empty();
        choosedData = [];
    }

    this.getData = function () {
        var data = [];
        for (var i = 0, l = choosedData.length; i < l; i++) {
            data.push(choosedData[i]);
        }
        return data;
    }
};