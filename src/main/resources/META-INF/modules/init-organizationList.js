define(["jquery"],function(){
	var oInit;
	oInit = function(){
		var h = $('.main-sidebar').innerHeight()-20;
		$('#treeH').innerHeight(h);
		$('#contentH').innerHeight(h);
		$(window).resize(function(){
			h = $('.sidebar').innerHeight()-20;
			$('#treeH').innerHeight(h);
			$('#contentH').innerHeight(h);
		})
	}
	
	return{
		init:oInit
	}
})