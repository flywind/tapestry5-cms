define(["app","jquery"],function(app,$){
	var error,ok;
	error = function(spec){
		var element = $('#uError'),obj = $('#'+spec.id),help = $('.help-block'),userCheckZone = $('#userCheckZone');
		if(obj.val() != null){
			help.hide();
		}
		userCheckZone.show();
		element.text(spec.error);
		obj.val('');
	};
	
	ok = function(spec){
		var element = $('#uError'),obj = $("#"+spec.id),help = $('.help-block');
		if(obj.val() != null){
			help.hide();
		}
		element.text("").hide();
	}
	
	return {
		error:error,
		success:ok
	}
	
})