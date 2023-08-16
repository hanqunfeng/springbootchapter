// 自定义扩展，弹窗iframe
;(function ($) {

    //缺省值
    var defaults = {
        src: "src",
        width: $(window).width() * 0.9,
        height: $(window).height(),
        top: 0
    }

    // 1.jQuery.extend(Object);　　　// jQuery 本身的扩展方法
    // 2.jQuery.fn.extent(Object);　 // jQuery 所选对象扩展方法

    // 如果只有一个方法开放访问，则可以直接定义为$.fn.方法名称= function(params){}
    $.fn.extend({
        boxIframe: function () {

            // Establish our default settings

            if ($(this)) {
                $.each($(this), function () {
                    let url = $(this).attr("src");
                    let options = {src: url};
                    $(this).showBox(options);
                });
            } else {
                return false;
            }
        },
        showBox: function (options) {
            $(this).click(function () {

                //合并属性
                var settings = $.extend(defaults, options);

                //显示底部阴影蒙层
                coverLayer(1);
                var tempContainer = $('<div class=tempContainer></div>');//图片容器
                with (tempContainer) {//width方法等同于$(this)
                    //将tempContainer元素加入到body标签下
                    appendTo("body");

                    //一定要加上这行代码，否则不会显示，这里是设置当前的tempContainer元素的显示位置
                    css('top', settings.top);
                    var iframeHtml = "<iframe name='boxIframe' id='boxIframe' src='" + settings.src + "' width=" + settings.width + " height=" + settings.height + " >" +
                        "<p>你的浏览器不支持iframe标签</p>" +
                        " </iframe>";

                    //将iframe元素增加到tempContainer元素
                    html(iframeHtml);

                }
                tempContainer.click(function () {
                    //删除tempContainer元素
                    $(this).remove();
                    //关闭底部阴影蒙层
                    coverLayer(0);
                    //删除底部阴影蒙层元素
                    $(".over").remove();

                });
            });
        }
    });

//使用禁用蒙层效果
    function coverLayer(tag) {
        var overContainer = $('<div class=over></div>');//蒙层
        with (overContainer) {
            if (tag == 1) {
                appendTo("body");
                css('height', $(document).height());
                css('display', 'block');
                css('opacity', 1);
                css("background-color", "#FFFFFF");
                css("background-color", "rgba(0,0,0,0.7)");  //蒙层透明度
            } else {
                css('display', 'none');

            }
        }
    }
})(jQuery);
