define(["jquery"],function($){
	var sendCode,sendClick,isGo;
	sendCode = function() {
		var sendCodeObj = $('a[data-update-zone="codeSendZone"]'),
			showtime = $('span[showtime="true"]');
			
			//阻止按钮在60秒内再次提交
			sendClick(); 
			
			sendCodeObj.data('dcount','true');
			sendCodeObj.attr('disabled',"true");
			var step = 60;
			var txt = showtime.text();
			var t = setInterval(function() {
				step -= 1;
				showtime.text(txt + " " + step + "S");
				if (step <= 0) {
					sendCodeObj.removeAttr('disabled');
					showtime.text(txt);
					sendCodeObj.data('dcount','false');
					clearInterval(t);
				}
			}, 1000);
		
	}
	
	sendClick = function(){
		var sendCodeObj = $('a[data-update-zone="codeSendZone"]');
		sendCodeObj.click(function(){
			if (sendCodeObj.data('dcount') == 'true') {
				return false;
			}
		})
	}

	return {
		sendCode:sendCode,
		sendClick:sendClick
	}
})