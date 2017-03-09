(function(){

	define(['jquery','bootstrap/dropdown','bootstrap/tab'],function($,d,t,a){
		var page = {};
		page.init = function(){
			page.scrollTop();
		}
		page.scrollTop = function(){
			var flyScrollTop = $('.scroll-top'), 
				flyWindow = $(window), 
				isMobile = function(){
		            return ( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) )
		        }();
		        if (flyScrollTop.length && !isMobile) {
		            var isVisible = false;
		            var offsetTop = 250;
		            function calcScroll(){
		                if (flyWindow.scrollTop() > offsetTop && !isVisible) {
		                    flyScrollTop.addClass('in').stop(true, true).css({'animation':'none'}).show(0).css({
		                        "animation" : "jellyIn .8s"
		                    });
		                    isVisible = true;
		                }else if (flyWindow.scrollTop() < offsetTop && isVisible) {
		                    flyScrollTop.removeClass('in');
		                    isVisible = false;
		                }
		            };
		
		            calcScroll();
		            flyWindow.scroll(calcScroll);
		            flyScrollTop.on('click', function(e){
		                e.preventDefault();
		                $('body, html').animate({scrollTop : 0}, 500);
		            });
		        }else{
		            flyScrollTop = null;
		            flyWindow = null;
		        }
		        isMobile = null;
		}
		return {
			init: page.init
		}
	});
}).call(this);