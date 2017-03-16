(function(){

	define(['jquery','bootstrap/dropdown','bootstrap/tab'],function($,d,t,a){
		var page = {};
		page.init = function(){
			page.scrollTop();
			
			var aObj = $('[data-animate]');
			aObj.delegate("a","mouseenter",function(){
				var _this = $(this);
				var cls = 'animated '+_this.parent().attr('data-animate');
				_this.parent().addClass(cls);
			});
			aObj.delegate("a","mouseleave",function(){
				var _this = $(this);
				var cls = 'animated '+_this.parent().attr('data-animate');
				_this.parent().removeClass(cls);
			});
			
		}
		page.scrollTop = function(){
			var flyScrollTop = $('.scroll-top'), 
				ht = $('.header-top'),
				hc = $('.header-content'),
				mybody = $('#mybody'),
				flyWindow = $(window), 
				isMobile = function(){
		            return ( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) )
		        }();
		        if (flyScrollTop.length && !isMobile) {
		            var isVisible = false;
		            var offsetTop = 250,navOffsetTop = 30;
		            function calcScroll(){
		            	if (flyWindow.scrollTop() > navOffsetTop) {
		            		hc.css({
		            			position: 'fixed',
		            	    	width: '100%',
		            	    	'z-index':100,
		            	    	top: 0
		            		});
		            		mybody.css({
		            			'margin-top': '85px'
		            		});
		                }else if (flyWindow.scrollTop() < navOffsetTop) {
		                	hc.css({
		            			position: 'initial',
		            	    	width: '100%',
		            	    	'z-index':100,
		            	    	top: 0
		            		});
		                	mybody.css({
		            			'margin-top': 0
		            		});
		                }
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