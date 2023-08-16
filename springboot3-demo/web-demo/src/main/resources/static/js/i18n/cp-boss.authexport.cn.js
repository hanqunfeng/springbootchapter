$(function(){
	$(".exporter").click(function(){
		var checkedlen = $("#resultsTable").find(":checkbox:checked").length;
		if(checkedlen == 0){
			alert("您并未选择任何条目，请至少选择一项待导出条目！");
			return false;
		}else{
			$("#resultsForm").attr("action",$(this).attr("title"));
			$("#resultsForm").submit();
		}
	});
});