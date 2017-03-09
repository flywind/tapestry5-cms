(function(){
	requirejs.config({
		shim: {
			"plugin/ztree": ["jquery"],
			"plugin/treetable": ["jquery","plugin/ztree"]
		},
		paths: {
			"plugin/ztree": "plugins/ztree/jquery.ztree.all.min",
			"plugin/treetable": "plugins/treeTable/javascripts/src/jquery.treetable"
			
		},
		waitSeconds: 0
	});
	
	define(["jquery","app","plugin/treetable"],function($,app,treetable){
		var init;
		init = function(spec){
			var s = setInterval(function(){
				$("#tableTree").css({'display':'block'});
				$("#tableTree").treetable({ expandable: true }).treetable("expandNode", 1);
			},0)
			
		}
		
		return{
			init:init
		}
	});
	
}).call(this);
