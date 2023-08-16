// 自定义扩展，弹窗iframe
;(function ($) {

    var timer, sleep = 1000;

    //缺省值
    var defaults = {
        url: "url", //json数据请求地址
        inputLength: 2 //输入两个字符开始查询
    }

    // 1.jQuery.extend(Object);　　　// jQuery 本身的扩展方法
    // 2.jQuery.fn.extent(Object);　 // jQuery 所选对象扩展方法

    // 如果只有一个方法开放访问，则可以直接定义为$.fn.方法名称= function(params){}
    $.fn.extend({
        suggestList: function (options) {

            // Establish our default settings
            //合并属性
            var settings = $.extend(defaults, options);


            $(this).after('<div class="suggest-list" style="display: none"></div>');


            //获得焦点
            $(this).bind('focus', function () {
                if ($(this).val().length >= settings.inputLength) { //输入两个字符开始查询
                    $(".suggest-list").show();
                }
            });

            //失去焦点
            $(this).bind('blur', function () {
                $(".suggest-list").hide();
            });

            // input事件是html5的标准事件，对于检测 textarea, input:text, input:password 和 input:search 这几个元素通过用户界面发生的内容变化非常有用，在内容修改后立即被触发。
            // propertychange事件:此事件会在元素内容发生改变时立即触发，即便是通过js改变的内容也会触发此事件。元素的任何属性改变都会触发该事件，不止是value。只有IE11以下浏览器支持此事件。
            $(this).bind("input propertychange", function (event) {

                if ($(this).val().length >= settings.inputLength) { //输入两个字符开始查询

                    if (timer) {
                        clearTimeout(timer);
                    }

                    let obj = $(this);
                    //避免快速连续输入时导致查询频率过快，实现短时间内输入多个字符只查询一次的效果
                    timer = setTimeout(function () {
                        getSuggestList(settings, obj)
                    }, sleep);
                } else {
                    if (timer) {
                        clearTimeout(timer);
                    }
                    $(".suggest-list").html("");
                    $(".suggest-list").hide();
                }

            });
        }
    });

    function getSuggestList(settings, obj) {
        // alert(obj);
        $.ajax({
            type: 'GET',
            url: settings.url + obj.val(),
            dataType: "json",
            success: function (data) {

                $(".suggest-list").html("");
                $(".suggest-list").hide();

                if (data != null) {
                    let size = 0;
                    data.forEach(item => {
                        $(".suggest-list").append('<div class="suggest-item">' + item + '</div>');
                        size++;
                    });


                    if (size > 0) {
                        $(".suggest-list").show();

                        $(".suggest-item").off("click");
                        $(".suggest-item").on("click", function () {
                            $(".queryTitle").val(obj.text());
                            $(".suggest-list").hide();
                        });
                    }

                }
            }

        });
    }

})(jQuery);
