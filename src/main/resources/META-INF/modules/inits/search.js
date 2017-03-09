(function(){
	define(["jquery"],function($){
		var page = {};
		page.zoneCb = function(v){
			$('#searchValue').val(v).focus();
		}
		
		return {
			cb: page.zoneCb
		}
	});
}).call(this);