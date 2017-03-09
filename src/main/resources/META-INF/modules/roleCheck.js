define(["app","jquery"],function(app,$){
	var error,success;
	
	//角色名重复执行此函数
	error = function(spec){
		var $element = $('#uError'),$checkZone = $('#roleCheckZone'),$obj=$('#name'),$help = $('.help-block');
		if($obj.val() != null){
			$help.hide();
		}
		$checkZone.show();
		$element.text(spec.info);
		$obj.val('');
	};
	
	//角色名不重复执行此函数
	success = function(spec){
		var $element = $('#uError'),$help = $('.help-block');
		if($obj.val() != null){
			$help.hide();
		}
		$element.text("").hide();
	}
	
	return {
		error:error,
		success:success
	}
	
})