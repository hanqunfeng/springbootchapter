;( function($) {
	$.fn.resetDetails = function(funcName) {
		var $detailform = $(this);
		$detailform.find("input:text:enabled:visible").val("");
		$detailform.find("textarea:enabled:visible").text("");
		$detailform.find("select:enabled:visible option").attr("selected",false);
		$detailform.find(":checkbox:enabled:visible").attr("checked",false);
		if(funcName != undefined)
			funcName.apply();
	};

})(jQuery);

function check(funcname){
	var errored = false;
	$("#detailForm").find(":text").each(function(){
		if(!errored){
			// var regexp = /\'|&/g;
			var regexp = /&/g;
			if(regexp.test($(this).val())){
				alert("输入框："+ $.trim($(this).parent().prev().text()) +"中请勿输入非法字符'或者&");
				errored = true;
				$(this).focus();
			}
		}
	});
	if(!errored){
		$("#detailForm").find(".validate").each(function(){
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
	if(!errored){
		if(funcname != undefined){
			if(funcname.apply())
				document.detailForm.submit();
		}else{
			document.detailForm.submit();
		}
	}
};

function checkNumber(numberStr){
	 var regObj = /^\d*$/g;
	 if(regObj.test(numberStr)){
	   return true;
	 }
	 else{
	   return false;
	 }
	}

function validateForm(){
	var errored = false;
	$("#detailForm").find(":text").each(function(){
		if(!errored){
			var regexp = /\'|&/g;
			if(regexp.test($(this).val())){
				alert("输入框："+ $.trim($(this).parent().prev().text()) +"中请勿输入非法字符'或者&");
				errored = true;
				$(this).focus();
			}
		}
	});
	if(!errored){
		$("#detailForm").find(".validate").each(function(){
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
	return errored;
};

function openWindow(url){
	var winWidth = (window.screen.width-1000) / 2;
	var winHeight = (window.screen.height - 450) / 2;
	window.open(url,"","height=450,width=1000,top=" + winHeight + ",left=" + winWidth + ",toolbar=no,menubar=no,scrollbars=yes,resizable=yes,location=no,status=no");
}
function closeWindow(){
	window.opener=null;
	window.open('','_self');
	window.close();
}
function SetWinHeight(obj) {
	var win = obj;
	if (document.getElementById) {
		if (win && !window.opera) {
			if (win.contentDocument && win.contentDocument.body.offsetHeight)
				win.height = win.contentDocument.body.offsetHeight;
			else if (win.Document && win.Document.body.scrollHeight)
				win.height = win.Document.body.scrollHeight;
		}
	}
}
