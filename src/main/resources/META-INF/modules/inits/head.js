(function(){
	define(["jquery"],function($){
		var page = {};
		page.init = function(){
			$('#sbtn').on('click',function(){
				var sv = $('#searchValue').val();
				if(sv){
					alert(1);
				}
				
			});
		}
		
		return {
			init: page.init
		}
	});
}).call(this);