$(function(){

	$("#selectAll").click(function(){

		var _this=$(this);
		if(_this.prop('checked')){
			$("#resultsTable tbody input:checkbox[name='ids']").prop("checked","true");
		}else{
			$("#resultsTable tbody input:checkbox[name='ids']").prop("checked",false);
		};
	});
	$("#delete").click(function(){
		if($("#resultsTable tbody input:checkbox:checked[name='ids']").length<=0){
			bootbox.alert("<font color\='red'>最少选择一个需要删除的条目！</font>");
		}else{
			bootbox.confirm("<font color\='red'>确定删除所选择的所有条目吗？</font>",function(result){
				if(result){
					$("#resultForm").submit();
				}
			});
		}
	});



	if($("#resultsTable").find("tbody tr").length == 0){
		if($("#notadd")){

		}else{
		var tdlen = $("#resultsTable thead th").length;
		$("<tr><td colspan='" + tdlen + "'>未发现任何记录，您可以<a href='#'>添加</a>记录!</td></tr>").
			appendTo("#resultsTable tbody").find("a").click(function(){
				$("#resultsForm .create").click();
			});
		}
	}
	$("#resultsTable thead th.header").each(function(){
		var _this = $(this);
		if(_this.find("span").text() == $("#sortName").val()){
			if($("#sortType").val() == 'asc'){
				_this.find("i").removeAttr("class").addClass("icon-sort-up");
				_this.addClass("header_up");
			}else{
				_this.find("i").removeAttr("class").addClass("icon-sort-down");
				_this.addClass("header_down");
			}
		}
	});


	$("#resultsTable thead th.header").click(function(){
		$("#sortName").val($(this).find("span").text());
		if($(this).is(".header_up"))
			$("#sortType").val("desc");
		else
			$("#sortType").val("asc");
		$("#queryForm").find("#submitbtn").click();
	});


	$("#queryForm").find("#submitbtn").click(function(){
		var errored = false;
		$("#queryForm").find(":text").each(function(){
			if(!errored){
				// var regexp = /\'|&/g;
				var regexp = /&/g;
				if(regexp.test($(this).val())){
					alert("查询输入框："+ $.trim($(this).parent().prev().text()) +"中请勿输入非法字符'或者&");
					errored = true;
					$(this).focus();
				}
			}
		});
		if(!errored){
			$("#queryForm").find(".validate").each(function(){
				var regex = new RegExp($(this).next().attr("title"));
				if(!regex.test($(this).val())){
					$(this).next().show();
					errored = true;
					$(this).focus();
				}else{
					$(this).next().hide();
				}
			});
		}
		//alert(ss);
		if(!errored){
				document.getElementById("queryForm").submit();
		}
	});
});
