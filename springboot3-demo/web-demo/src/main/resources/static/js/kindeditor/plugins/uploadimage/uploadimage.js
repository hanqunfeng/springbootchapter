KindEditor.plugin('uploadimage', function(K) {
    var editor = this, name = 'uploadimage';
    //点击图标时执行
    editor.clickToolbar(name, function() {
        //弹出框的基础样式延续自带swfupload的css,加了一部分自定义一些ke-自定义的css,参考uploadimage.css
        var html = '<div style="padding:20px;"><div class="swfupload"><div class="ke-swfupload"><div class="ke-swfupload-top"><div class="ke-inline-block ke-swfupload-button"><a href="javascript:;" class="ke-selectPic">选择图片</a><span class="ke-none"><input type="file" multiple="multiple" id="ke-filePic" class="ke-filePic" /></span></div><div class="ke-inline-block ke-swfupload-desc">允许用户同时上传20张图片，单张图片容量不超过5MB</div><span class="ke-button-common ke-button-outer ke-swfupload-startupload"><input type="button" class="ke-button-common ke-button" value="开始上传"></span></div><div class="ke-swfupload-body"></div></div></div></div>';
        //KindEditor的弹层
        var dialog = K.dialog({
            width : 652,
            height : 510,
            title : '批量图片上传',
            body : html,
            closeBtn : {
                name : '关闭',
                click : function(e) {
                    dialog.remove();
                }
            },
            previewBtn : {
                name : '全部插入',
                click : function(e) {
                    callAll();
                }
            },
            yesBtn : {
                name : '全部清空',
                click : function(e) {
                    callClear();
                }
            },
            noBtn : {
                name : '取消',
                click : function(e) {
                    dialog.remove();
                }
            }
        });

        //选择图片按钮
        $(".ke-selectPic").bind('click',function(){
            $(".ke-filePic").click();
        });

        //监听隐藏域fileinput的change事件，使用formData做多图数据存储器
        var xindex=0;
        var formData = new FormData();
        var inputBox = document.getElementById("ke-filePic");
        inputBox.addEventListener("change",function(e){
            $.each(inputBox.files,function(i,item){
                //添加预览,#可以给个默认图
                var pic=$('<div class="ke-piclist"><span class="ke-picdel pic_'+xindex+'" data-id="'+xindex+'" title="删除">x</span><div class="ke-pictu"><span>等待上传</span><img src="#" /></div><div class="ke-pictext">'+item.name+'</div></div>');
                $(".ke-swfupload-body").append(pic);

                //添加到存储
                formData.append("pic_"+xindex, item);

                //预览图片
                var reader = new FileReader();
                reader.readAsDataURL(item);
                reader.onload = function(event){
                    pic.find("img").attr('src',this.result);
                }

                //绑定删除按钮
                pic.on("click", ".ke-picdel", function(){
                    var _index=$(this).attr("data-id");
                    formData.delete("pic_"+_index);//删除参数
                    pic.remove();//删除图片
                })
                xindex++;
            });
        });

        //这里是循环单张上传图片,也可以自己一次性ajax批量上传多种图片,这里只是延续使用单图上传的服务接口
        $(".ke-swfupload-startupload").bind('click',function(){
            var i = formData.entries();
            for(var pair of formData.entries()) {
                //循环上传图片
                var formData2 = new FormData();
                formData2.append("name",pair[0]);//回调使用的图片名称
                formData2.append("imgFile",pair[1]);//上传图片的参数imgFile
                $.ajax({
                    url : editor.uploadJson,//服务器单图上传接口，可以参考multiimage的配置
                    type : 'post',
                    data : formData2,
                    dataType: 'json',
                    processData: false,
                    contentType: false,
                    //async: false,//是否同步
                    success : function(data){
                        //data.name就是回调对应图片名
                        var _parent=$("."+data.name).parents('.ke-piclist');
                        //添加新属性值
                        _parent.data('url',data.url);
                        //移除待上传字体
                        _parent.find(".ke-pictu span").remove();
                        //删除参数
                        formData.delete(data.name);
                        //console.log(data.name);
                    }
                })
            }
        });

        //插入全部图片
        function callAll(){
            var len=0;
            $(".ke-piclist").each(function(i,item){
                var _url=$(this).data("url");
                if(_url && _url!='' && typeof(_url)!="undefined"){
                    editor.insertHtml('<img src="'+_url+'" alt="" />');
                    len++;
                }
            });
            //关闭
            if(len>0){
                dialog.remove();
                formData = new FormData();
            }
        }
        //全部清空
        function callClear(){
            $(".ke-piclist").remove();
            formData = new FormData();
        }
    });
});
