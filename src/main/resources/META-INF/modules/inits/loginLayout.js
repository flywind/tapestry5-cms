(function(){
	requirejs.config({
		shim : {
			"jmd5": ["jquery"]
		},
		paths : {
			"jmd5" : "plugins/md5/jquery.md5"
		},
		waitSeconds: 0
	});
	
	define(['jquery','jmd5'],function($,m){
		var init;
		
		init = function(){
			
			var $imgHolder 	= $('#signup-bg-list');
			var $bgBtn 		= $imgHolder.find('.signup-chg-bg');
			var $target 	= $('#bg-overlay');

			$bgBtn.on('click', function(e){
				e.preventDefault();
				e.stopPropagation();

				var $el = $(this);
				if ($el.hasClass('active') || $imgHolder.hasClass('disabled'))return;

				$imgHolder.addClass('disabled');
				var url = $el.attr('src').replace('/thumbs','');

				$('<img/>').attr('src' , url).load(function(){
					$target.css('background-image', 'url("' + url + '")').addClass('bg-img');
					$imgHolder.removeClass('disabled');
					$bgBtn.removeClass('active');
					$el.addClass('active');

					$(this).remove();
				});

			});

			$('#loginSubmitBtn').on('click', function(e){
				var tynamoPassword = $.md5($('#tynamoPassword').val());
				var dynamicPwd = $('#dynamicPwd').val();
				//清除动态密码
				$('#dynamicPwd').val('');
				
				//组合密码
				var makeUpPwd = tynamoPassword + dynamicPwd;
				
				$('#tynamoPassword').val($.md5(makeUpPwd));
				$('#tynamoLoginForm').submit();
			});
		};
		
		return {
			init: init
		}
	})
}).call(this);