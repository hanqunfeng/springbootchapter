(function ($) {
    // 启用严格模式：https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Strict_mode
    "use strict";

    function getJSLocale(key, params) {
        var result = ""; 	// 对应的资源的内容
        var paramsObj = {};	// 参数对象
        if (params) paramsObj = params;

        if (typeof (key) != 'undefined') {
            // 根据key取得对应的资源内容，如果没有找到则返回key值
            if ($.selectPanelLanguage[key] != undefined) {
                result = $.selectPanelLanguage[key];
            } else {
                result = key;
            }

            // 替换对应参数为value的值
            var regExp = new RegExp(); //替换资源中参数的正则
            for (var k in paramsObj) {
                regExp = eval("/{{:" + k + "}}/g");
                result = result.replace(regExp, paramsObj[k]);
            }

            // 如果没有找到对应的资源则返回 "No Value"
            if (/{{:[a-zA-Z]+}}/.test(result)) {
                result = result.replace(/{{:[a-zA-Z]+}}/g, "No Value");
            }
        }
        return result;
    }


    $.selectPanelLanguage = {
        selectedItems: "has selected {{:0}} Item(s)!",
        recurrentSelection: "the item you selected has exsited,please do not choose the same itme!",
        leafOnly: "only leaf-item can be selected!",
        notmultiple: "only choose one!"

    };
    $.fn.selectPanel = function (spoptions) {


        // Establish our default settings
        var settings = $.extend(true, {
            eventType: "click", //默认单击事件触发，可以设置为双击事件：dblclick
            multiple: true,  //默认多选
            width: "800px", //默认宽度
            title: "my title", //标题
            inputTarget: "",   //选择的值显示在哪个控件中，默认当前input
            inputType: "input", //控件类型,默认input，可以设置为select
            value_label: ["id", "name"], //返回值中要含有的属性，用于拼装显示值
            show_option_value: 2, //封装option的text格式，即显示值，默认为2，即value_label[0]--value_label[1]。0:value_label[0],1:value_label[1]
            real_option_value: 0, //select控件时的option的value，默认为0，即value_label[0]，这里就是id
            datasource: {
                sync: true,  //异步获取
                methodType: "post", //异步加载类型:post和get
                url: null,    //数据json地址，可以延迟加载数据，第一次只加载一层节点，然后点击展开时，会自动调用该url再次加载数据
                autoParam: [], //["id", "name"] url后跟的参数，这里就是value_label中定义的属性
                otherParam: {}, //其它url参数,{ key1:value1, key2:value2 }
                dataFilter: null, //返回值过滤器
                name: "name",  //树中的显示值
                title: "",
                showIcon: true,
                showLine: true,
                showTitle: true,
                chkStyle: "checkbox" //checkbox 或 radio
            },
            nodes: null    //数据nodes，与datasource互斥，优先级高于datasource
        }, spoptions);


        if (settings.inputType == "select") {
            if ($(this).find("option").length <= 0) {
                $(this).append('<option value="">' + $(this).attr('data-placeholder') + '</option>')
            }
        }

        var html = $('<div class="modal fade" id="selectPanel-Modal" tabindex="-1" role="dialog" ' +
            'aria-labelledby="myModalLabel" aria-hidden="true">' +
            '<div class="modal-dialog" style="width: ' + settings.width + '">' +
            '<div class="modal-content">' +
            '<div class="modal-header">' +
            '<button type="button" class="close" data-dismiss="modal" ' +
            'aria-hidden="true">&times;</button>' +
            '<h4 class="modal-title" >' + settings.title + '</h4>' +
            '</div>' +
            '<div class="modal-body" style="padding: 0px;">' +
            '<table style="width: 100%;border: 0px">' +
            '<tr>' +
            '<td class="well" style="width: 45%"><div ' +
            'style="height: 300px; width: 100%; background-color: white;overflow:auto">' +
            '<div id="selectPanel-treeDemo" class="ztree"></div>' +
            '</div></td>' +
            '<td style="text-align:center;vertical-align:center; width: 10%;">' +
            '<button class="btn btn-primary btn-sm btn-block" id="selectPanel-addItem">' +
            '<i class="icon-double-angle-right icon-2x"></i><br>add' +
            '</button>' +
            '<br>' +
            '<br>' +
            '<button class="btn btn-primary btn-sm  btn-block" id="selectPanel-removeItem">' +
            '<i class="icon-double-angle-left icon-2x"></i><br>remove' +
            '</button>' +
            '<br>' +
            '<br>' +
            '<button class="btn btn-primary btn-sm  btn-block" id="selectPanel-clearItems">' +
            '<i class="icon-trash icon-2x"></i><br>clear' +
            '</button>' +

            '</td>' +
            '<td class="well" style="text-align:center;vertical-align:center; width: 45%;">' +
            '<span>' + getJSLocale("selectedItems", {0: 0}) + '</span><select MULTIPLE style="height: 280px; width: 100%; background-color: white;" id="selectPanel-alternativeItems"></select></td>' +
            '</tr>' +
            '</table>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>' +
            '<button type="button" class="btn btn-primary" id="selectPanel-save">Save changes</button>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>');


        // https://treejs.cn/v3/api.php
        // https://my.oschina.net/dyhunter?tab=newest&catalogId=12364096&sortType=time

        // 参考：https://www.cnblogs.com/fonour/p/zTree.html
        return this.each(function () {
            $(this).on(settings.eventType, function () {
                var _this = $(this);
                var ztree_setting = {
                    async: {
                        enable: settings.datasource.sync,
                        type: settings.datasource.methodType,  //异步加载类型:post和get
                        url: settings.datasource.url,
                        autoParam: settings.datasource.autoParam,
                        otherParam: settings.datasource.otherParam,
                        dataFilter: settings.datasource.dataFilter
                    },
                    data: {
                        key: {
                            name: settings.datasource.name,  //zTree 节点数据保存节点名称的属性名称  默认值："name"
                            title: settings.datasource.title, //zTree 节点数据的提示信息 默认值："title"
                            isParent: "isParent" //zTree 节点数据保存节点是否为父节点的属性名称。
                        },
                        // 标准数据格式
                        // var nodes = [
                        // 	{name: "父节点1", children: [
                        // 			{name: "子节点1"},
                        // 			{name: "子节点2"}
                        // 		]}
                        // ];


                        // 简单数据格式
                        // var nodes = [
                        // 	{id:1, pId:0, name: "父节点1"},
                        // 	{id:11, pId:1, name: "子节点1"},
                        // 	{id:12, pId:1, name: "子节点2"}
                        // ];
                        simpleData: {
                            enable: true,   //设置是否启用简单数据格式（zTree支持标准数据格式跟简单数据格式，上面例子中是标准数据格式）
                            idKey: "id",     //设置启用简单数据格式时id对应的属性名称
                            pidKey: "pId"    //设置启用简单数据格式时parentId对应的属性名称,ztree根据id及pid层级关系构建树结构
                        }
                    },
                    view: {
                        showIcon: settings.datasource.showIcon,  //设置 zTree 是否显示节点的图标。默认值：true
                        selectedMulti: settings.multiple,   //设置是否允许同时选中多个节点  默认值：true
                        showLine: settings.datasource.showLine,         //设置 zTree 是否显示节点之间的连线 默认值：true
                        showTitle: settings.datasource.showTitle,     //设置 zTree 是否显示节点的 title 提示信息(即节点 DOM 的 title 属性)。  默认值：true
                        // addDiyDom: addDiyDom,               //方法，用于在节点上固定显示用户自定义控件
                        // addHoverDom: addHoverDom,           //方法，用于当鼠标移动到节点上时，显示用户自定义控件
                        // removeHoverDom: removeHoverDom,     //方法，用于当鼠标移出节点时，隐藏用户自定义控件
                    },
                    callback: {
                        // onClick: onClick,             //定义节点单击事件回调函数
                        // onRightClick: OnRightClick,   //定义节点右键单击事件回调函数
                        onDblClick: addItem,       //定义节点双击事件回调函数
                        onCheck: zTreeOnCheck,  //用于捕获 checkbox / radio 被勾选 或 取消勾选的事件回调函数
                        // beforeRemove: zTreeBeforeRemove,     //用于捕获节点被删除之前的事件回调函数，并且根据返回值确定是否允许删除操作
                        // onRemove: zTreeOnRemove,            //用于捕获删除节点之后的事件回调函数
                        // beforeRename: zTreeBeforeRename,    //用于捕获节点编辑名称结束（Input 失去焦点 或 按下 Enter 键）之后，更新节点名称数据之前的事件回调函数，并且根据返回值确定是否允许更改名称的操作
                        // onRename: zTreeOnRename,            //用于捕获节点编辑名称结束之后的事件回调函数
                        // onCheck: onCheck,              //定义节点复选框选中或取消选中事件的回调函数
                        onExpand: zTreeOnExpand,  //父节点被展开时触发的回调函数
                        onAsyncSuccess: zTreeOnAsyncSuccess //用于捕获异步加载正常结束的事件回调函数

                    },
                    //需要引入js/jquery.ztree.excheck.js
                    check:{
                        enable:true,  //true 、 false 分别表示 显示 、不显示 复选框或单选框
                        chkStyle: settings.datasource.chkStyle, //勾选框类型(checkbox 或 radio）[setting.check.enable = true 时生效]
                        nocheckInherit:true  //当父节点设置 nocheck = true 时，设置子节点是否自动继承 nocheck = true
                    },

                };
                html.appendTo('body');
                var modal = $('#selectPanel-Modal');
                modal.modal().on('hidden.bs.modal', function (e) {
                    $("#selectPanel-Modal").remove();
                });

                function zTreeOnCheck(event, treeId, treeNode) {
                    // alert(treeNode.tId + ", " + treeNode.name + "," + treeNode.checked);
                    treeObj.selectNode(treeNode);
                    addItem();
                };

                function zTreeOnExpand(event, treeId, treeNode) {
                    // bootbox.alert(treeId + "," +treeNode.tId + ", " + treeNode.name + "," + treeNode.data);
                };

                function zTreeOnAsyncSuccess(event, treeId, treeNode, msg) {
                    //更多数据的处理方式
                    if ((treeNode.name == "...更多" || treeNode.name == "...More") && treeNode.data != null) {
                        // bootbox.alert(treeId + "," +treeNode.tId + ", " + treeNode.name + "," + treeNode.data);

                        var parentNode = treeNode.getParentNode(); //获取[更多]节点的父节点
                        var children = treeNode.children;  //获取[更多]节点的子节点
                        for (var i = 0; i < children.length; i++) {
                            treeObj.addNodes(parentNode,children[i]); //将[更多]节点的子节点增加到[更多]节点的父节点上
                        }
                        treeObj.removeChildNodes(treeNode); //删除[更多]节点的子节点
                        treeObj.removeNode(treeNode); //删除[更多]节点
                    }
                };


                var treeObj = $.fn.zTree.init($("#selectPanel-treeDemo"), ztree_setting, settings.nodes);

                var alternativeItems = $("#selectPanel-alternativeItems");


                function addItem() {

                    if(settings.multiple == false){
                        clear();
                    }

                    var nodes = treeObj.getSelectedNodes();
                    for (var i = 0; i < nodes.length; i++) {
                        //显示值
                        var vl = nodes[i][settings.value_label[0]] + "--" + nodes[i][settings.value_label[1]];
                        if (settings.show_option_value == 0) {
                            vl = nodes[i][settings.value_label[0]];
                        }
                        if (settings.show_option_value == 1) {
                            vl = nodes[i][settings.value_label[1]];
                        }

                        if (nodes[i].isParent) {
                            bootbox.alert(getJSLocale('leafOnly'));
                        } else if (alternativeItems.find("option:contains('" + vl + "')").length >= 1) {
                            bootbox.alert(getJSLocale('recurrentSelection'));
                        } else {
                            treeObj.checkNode(nodes[i], true, false);
                            if (settings.real_option_value == 0) {
                                alternativeItems.append("<option value='" + nodes[i][settings.value_label[0]] + "'>" + vl + "</option>");
                            }
                            if (settings.real_option_value == 1) {
                                alternativeItems.append("<option value='" + nodes[i][settings.value_label[1]] + "'>" + vl + "</option>");
                            }
                        }
                    }
                    alternativeItems.siblings("span").text(getJSLocale("selectedItems", {0: alternativeItems.find("option").length}));
                }

                function removeItem() {
                    alternativeItems.find("option:selected").remove();
                    alternativeItems.siblings("span").text(getJSLocale("selectedItems", {0: alternativeItems.find("option").length}));
                }


                $("#selectPanel-addItem").on('click', addItem);

                $("#selectPanel-alternativeItems").dblclick(removeItem);

                $("#selectPanel-removeItem").on('click', removeItem);


                function clear(){
                    alternativeItems.find("option").remove();
                    alternativeItems.siblings("span").text(getJSLocale("selectedItems", {0: 0}));
                }

                $("#selectPanel-clearItems").on('click', clear());

                var inputTarget;
                if (settings.inputTarget == "" || settings.inputTarget == null) {
                    inputTarget = _this;
                } else {
                    inputTarget = $("#" + settings.inputTarget);
                }
                $("#selectPanel-save").on('click', function () {

                    var options = alternativeItems.find('option');

                    if (settings.multiple == false) {
                        if (options.length > 1) {
                            bootbox.alert(getJSLocale('notmultiple'));
                            return false;
                        }
                    }


                    if (settings.inputType == "select") {
                        inputTarget.html("");
                        inputTarget.append('<option value="">' + inputTarget.attr('data-placeholder') + '</option>')
                        for (var i = 0; i < options.length; i++) {
                            inputTarget.append("<option selected value='" + $(options[i]).val() + "'>" + $(options[i]).text() + "</option>");
                        }
                    } else {
                        var str = [];
                        for (var i = 0; i < options.length; i++) {
                            str.push($(options[i]).text());
                        }
                        inputTarget.val(str);
                    }


                    modal.modal('hide');
                });
            });
        });
    };
})(jQuery);




