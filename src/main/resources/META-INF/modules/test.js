define(["jquery","t5/core/dom"],function($,dom){
	var hello;
	//系列化表单
   $.fn.serializeObject = function() {
		var o = {};
		var a = this.serializeArray();
		$.each(a, function() {
			if (o[this.name] !== undefined) {
				if (!o[this.name].push) {
					o[this.name] = [ o[this.name] ];
				}
				o[this.name].push(this.value || '');
			} else {
				o[this.name] = this.value || '';
			}
		});
		return o;
   };
	
	hello = function(){
		$('#btn').click(function(){
			var data = $('#myform').serializeObject();
			var ajaxopts = {
					data: data
			}
			
			dom.ajaxRequest("http://localhost:588/tapestry-cms/rest/test",ajaxopts);
		})
	}
	return {
		hello:hello
	}
})