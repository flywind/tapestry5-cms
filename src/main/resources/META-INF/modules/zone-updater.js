define(["jquery", "t5/core/zone"], function($, zoneManager) {

    return function(elementId, clientEvent, listenerURI, zoneElementId) {
        var $element = $("#" + elementId);

        if (clientEvent) {
            $element.on(clientEvent, updateZone);
        }
    
        function updateZone() {
            var listenerURIWithValue = listenerURI;
    
            if ($element.val()) {
                listenerURIWithValue = appendQueryStringParameter(listenerURIWithValue, 'param', $element.val());
            }
    
            zoneManager.deferredZoneUpdate(zoneElementId, listenerURIWithValue);
        }
    }

    function appendQueryStringParameter(url, name, value) {
        if (url.indexOf('?') < 0) {
            url += '?'
        }
        else {
            url += '&';
        }
        //value = escape(value);
        //value = encodeURI(encodeURI(value));
        url += name + '=' + value;
        return url;
    }

});