define(["jquery","t5/core/dom","t5/core/forms","t5/core/zone","t5/core/events"],function($,dom,forms,events){
	function init(spec){
		var $element = $('#'+spec.elementId),
		    listenerURI = spec.listenerURI;
		
		$element.bind("blur",function(){
			var value = this.value;
			asyncValidateInServer(value,listenerURI);
		});
	};
	
	function asyncValidateInServer(value,listenerURI) {
		var value = value;
		var listenerURIWithValue = listenerURI;
    			
		if (value) {
			listenerURIWithValue = addQueryStringParameter(listenerURIWithValue, 'param', value);
			
			dom.ajaxRequest(listenerURIWithValue,{
				method: 'get',
				onFailure: function(t) {
				    alert('Error communication with the server: ' + t.responseText.stripTags());
				},
				onException: function(t, exception) {
				    alert('Error communication with the server: ' + exception.message);
				},
				onSuccess: function(t) {
					if (t.responseJSON.error) {
						alert(t.responseJSON.error);
						//this.field.showValidationMessage(t.responseJSON.error);
					}
				}
			});
		}
	};
	
	function addQueryStringParameter(url, name, value) {
		if (url.indexOf('?') < 0) {
			url += '?'
		} else {
			url += '&';
		}
		value = escape(value);
		url += name + '=' + value;
		return url;
	};
	
	return{
		init:init
	}
});