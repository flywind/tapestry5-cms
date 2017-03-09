(function(){
	define(["jquery","t5/core/dom"],function($,dom){
		var page = {};
		page.count = function(url){
			var time = setTimeout(function(){
				var opts = {
					success: function(data){
						clearTimeout(time);
					},
					error: function(){
						clearTimeout(time);
					}
				}
				dom.ajaxRequest(url,opts);
			},3000);
			
			$('#pageClose').on('click',function(){
				window.open("about:blank","_self").close();
			});
		}
		
		return {
			count: page.count
		}
	});
}).call(this);