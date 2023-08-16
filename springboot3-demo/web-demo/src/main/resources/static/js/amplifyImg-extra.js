// 自定义扩展，放大图片
;(function ($) {

    //属性扩展，第一个参数为true表示深度合并，否则为浅合并，后面的spoptions会覆盖前面的属性
    // var spoptions = {
        //     title:"111",
        //     datasource: {
        //         sync: true,
        //         url:null
        //     }
    // var settings = $.extend(true,{
    //     title:"my title",
    //     inputTarget:"",
    //     value_label:["id","name"],
    //     datasource: {
    //         sync: true,
    //         url:null
    //     }
    // }, spoptions);


    // 如果只有一个方法开放访问，则可以直接定义为$.fn.方法名称= function(params){}
    $.fn.extend({
        amplifyEach: function () {
            if ($(this)) {
                $.each($(this), function () {
                    $(this).amplify();
                });
            } else {
                return false;
            }
        },
        amplify: function () {
            $(this).click(function () {
                var currImg = $(this);
                coverLayer(1);
                var tempContainer = $('<div class=tempContainer></div>');//图片容器
                with (tempContainer) {//width方法等同于$(this)
                    appendTo("body");
                    var windowWidth = $(window).width();
                    var windowHeight = $(window).height();
                    //获取图片原始宽度、高度
                    var orignImg = new Image();
                    orignImg.src = currImg.attr("src");
                    var currImgWidth = orignImg.width;
                    var currImgHeight = orignImg.height;
                    if (currImgWidth < windowWidth) {//为了让图片不失真，当图片宽度较小的时候，保留原图
                        if (currImgHeight < windowHeight) {
                            var topHeight = (windowHeight - currImgHeight) / 2;
                            if (topHeight > 35) {/*此处为了使图片高度上居中显示在整个手机屏幕中：因为在android,ios的微信中会有一个title导航，35为title导航的高度*/
                                topHeight = topHeight - 35;
                                css('top', topHeight);
                            } else {
                                css('top', 0);
                            }
                            html('<img border=0 src=' + currImg.attr('src') + '>');
                        } else {
                            css('top', 0);
                            html('<img border=0 src=' + currImg.attr('src') + ' height=' + windowHeight + '>');
                        }
                    } else {
                        var currImgChangeHeight = (currImgHeight * windowWidth) / currImgWidth;
                        if (currImgChangeHeight < windowHeight) {
                            var topHeight = (windowHeight - currImgChangeHeight) / 2;
                            if (topHeight > 35) {
                                topHeight = topHeight - 35;
                                css('top', topHeight);
                            } else {
                                css('top', 0);
                            }
                            html('<img border=0 src=' + currImg.attr('src') + ' width=' + windowWidth + ';>');
                        } else {
                            css('top', 0);
                            html('<img border=0 src=' + currImg.attr('src') + ' width=' + windowWidth + '; height=' + windowHeight + '>');
                        }
                    }
                }
                tempContainer.click(function () {
                    $(this).remove();
                    coverLayer(0);
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
