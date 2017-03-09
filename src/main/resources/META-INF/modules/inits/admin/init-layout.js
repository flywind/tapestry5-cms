(function(){
	requirejs.config({
		shim : {
			"nanoScroller" : ["jquery"],
			"menu" : ["jquery"],
			"mresize" : ["jquery"],
			"t5admin" : ["jquery"]
		},
		paths : {
			"nanoScroller" : "plugins/nanoScrollerJS/nanoScrollerJS.min",
			"menu" : "plugins/metismenu/metismenu.min",
			"mresize" : "plugins/resizeEnd/resizeEnd.min",
			"t5admin" : "lib/t5admin"
			
		},
		waitSeconds: 0
	});

	define(["jquery","menu","nanoScroller","mresize","bootstrap/popover","bootstrap/dropdown","t5admin"],function($,m,n,ms,p,bd,t5admin){
		
		var init;
		
		init = function(){
			
			//Init layout
			t5admin.navgation();
	        t5admin.aside();
	        t5admin.scrollToTop();
	        t5admin.megaDropdown();
	        t5admin.langSelector();

	        //Activate the Bootstrap tooltips
	        var tooltip = $('.add-tooltip');
	        if (tooltip.length)tooltip.tooltip();
	
	        var popover = $('.add-popover');
	        if (popover.length)popover.popover();
	
	        // Update nancoscroller
	        $('#navbar-container .navbar-top-links').on('shown.bs.dropdown', '.dropdown', function () {
	            $(this).find('.nano').nanoScroller({preventPageScrolling: true});
	        });
	
	        $.flyNav('bind');
	        $.flyAside('bind');  
	
		}
		
		return {
			init : init
		}
		
	})

}).call(this);