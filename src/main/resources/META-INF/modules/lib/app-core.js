(function($){
	jQuery.core = {
			// web的url根路径，例如/cms
	        getContextPath : function() {
	            return $("#web_contextPath").val();
	        },
	        // 当前页面的名称
	        getPageName : function() {
	            return $("#web_pageName").val();
	        },
	        //Start Loading
	        loadingStart : function(o){
	        	var o = o || 'pageloading-mask';
	        	var loading = $('.'+ o);
				if(loading.size() > 0){
					loading.remove();
				}else{
					var loadHtml = '<div class="pageloading-mask"><div></div></div>';
					$('body').prepend(loadHtml);
				}
	        },
	        //Close Loading
	        loadingClose : function(o){
	        	var o = o || 'pageloading-mask';
	        	var loading = $('.'+ o);
				if(loading.size() > 0){
					loading.remove();
				};
	        },
	        /*
	         * Go to edit page,like pagelink
	         * page : go to page's name
	         * gridId : datagrid's id
	         * info : request message. It's object
	         * 		  info.dialogTitle : dialog title
	         *        info.editAlertMsg : Edit warning 
	         *        info.noSelectMsg : no select tips   
	         * language : system language
	         * */
	        editGridData : function(page,gridId,info,language){
	        		
        		var gridObj = $('#' + gridId);
    			var row = gridObj.datagrid('getSelected');
    			var rows = gridObj.datagrid('getSelections');
    			
    			var info = info || {dialogTitle:'Tips',editAlertMsg:'Only supports a single record editor'};
    			
    			if(!row){
    				art.dialog({
    					id : 'noItemSelect',
    					lock : true,
    					padding : '25px 40px',
    					content : info.noSelectMsg,
    					title  : info.dialogTitle
    				}).time(3);
    				return;
    			}
    			
    			if(rows.length > 1){
    				art.dialog({
    					id : 'multItemSelect',
    					lock : true,
    					padding : '25px 40px',
    					content : info.editAlertMsg,
    					title  : info.dialogTitle
    				}).time(3);
    				return;
    			}
    			
    			var url = '';
    			var language = language || "";
	        	var newl = "";
	    		if(language != null){
	    			if(language.indexOf("-")>0){
	    				newl = language.replace("-","_");
	    				url = $("#web_contextPath").val() + "/" + newl + "/" + page + '/' + row.id;
	    			}else{
	    				url = $("#web_contextPath").val() + "/"  + language + "/" + page + '/' + row.id;
	    			}
	    		}else{
	    			url = $("#web_contextPath").val() + page + '/' + row.id;
	    		}

    			window.location.href = url;

	        },
	        /*
	         * Ajax delete grid data
	         * dom : t5/core.dom
	         * url : ajax request url
	         * gridId : datagrid's id
	         * info : request message.It's object
	         * 		  info.dialogTitle : dialog title
	         *        info.noSelectMsg : no select tips     
	         * */
	        deleteMultGridData : function(dom,url,gridId,info){

    			var gridObj = $('#' + gridId);
    			
    			var row = gridObj.datagrid('getSelected');
    			var rows = gridObj.datagrid('getSelections');
    			
    			var info = info || {dialogTitle:'Tips',noSelectMsg:'Select at least one record'};
    			
    			if(!row){
    				art.dialog({
    					id : 'noItemSelect',
    					lock : true,
    					padding : '25px 40px',
    					content : info.noSelectMsg,
    					title  : info.dialogTitle
    				}).time(3);
    				return;
    			}
    			
    			var arr = [],ids='';
    			
    			$.each(rows,function(i,v){
    				arr.push(v.id);
    			});
    			
    			var data = {
    				ids : arr.join(',')
    			};
    			
    			if(typeof(url) == 'undefined'){
    				console.log('Ajax request url is null..');
    				return;
    			}
    			
    			$.core.loadingStart();
    			
    			var ajaxOpts = {
    				data : 	data,
    				success : function(data){
    					if(data.text){
    						var d = $.parseJSON(data.text);
    						if(d.success){
    							$.core.loadingClose();
    							gridObj.datagrid('reload');
    							art.dialog({
    								id : 'successResult',
    								lock : true,
    								padding : '25px 40px',
    								content : d.success,
    								title  : info.dialogTitle
    							}).time(2);
    						}else if(d.error){
    							$.core.loadingClose();
    							art.dialog({
    								id : 'errorResult',
    								lock : true,
    								padding : '25px 40px',
    								content : d.error,
    								title  : info.dialogTitle
    							}).time(2);
    							if(d.errorMsg){
    								consle.log(d.errorMsg);
    							}
    						}
    					}
    				},
    				error : function(){
    					$.core.loadingClose();
    				}
    			};
    			
    			dom.ajaxRequest(url,ajaxOpts);
	    			
	        },
	        //系列化表单数据
	        getFormData : function(formId){
	        	var o = {};
				var a = $('#'+formId).serializeArray();
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
	        },
	        // 当前页面的url
	        getCurrentPageUrl : function() {
	            return $("#web_contextPath").val() + "/" + $("#web_pageName").val();
	        },
	        // 当前语言页面的url
	        getCurrentPageLanguageUrl : function(l) {
	        	var language = l || "";
	        	var newl = "";
	    		if(language != null){
	    			if(language.indexOf("-")>0){
	    				newl = language.replace("-","_");
	    				return $("#web_contextPath").val() + "/"+ newl + "/" + $("#web_pageName").val();
	    			}
	    			return $("#web_contextPath").val() + "/"+ language + "/" + $("#web_pageName").val();
	    		}
	            return $("#web_contextPath").val() + "/" + $("#web_pageName").val();
	        },
	        // 当前父级页面的url
	        getParentPageLanguageUrl : function(l,parentUrl) {
	        	var language = l || "";
	        	var newl = "";
	    		if(language != null){
	    			if(language.indexOf("-")>0){
	    				newl = language.replace("-","_");
	    				return $("#web_contextPath").val() + "/"+ newl + "/" + parentUrl;
	    			}
	    			return $("#web_contextPath").val() + "/"+ language + "/" + parentUrl;
	    		}
	            return $("#web_contextPath").val() + "/" + parentUrl;
	        },
	        // 打开新窗口，例如$.core.openNewPage('leadershiptable/NewContractStatistics')
	        openNewPage : function(pageName) {
	            window.open(this.getContextPath() + '/' + pageName);
	        },
	        // 页面重定向，例如$.core.redirectToPage('leadershiptable/NewContractStatistics')
	        redirectToPage : function(pageName) {
	            window.location = this.getContextPath() + '/' + pageName;
	            return;
	        },
	        /**
	    	 * 字符串转JSON对象
	    	 */
	        stringToJSON : function(data) {
	    	    try {
	    	        data = JSON.parse(data);
	    	    } catch (e) {
	    	        data = data;
	    	    }
	    	    return data;
	    	},
	    	/**
	    	 * JSON对象转字符串
	    	 */
	    	JSONToString : function(data) {
	    	    try {
	    	        data = JSON.stringify(data);
	    	    } catch (e) {
	    	        data = data;
	    	    }
	    	
	    	    return data;
	    	},
	    	/* 跳转 */
	    	jump : function(url, target) {
	    	    if (!url) {
	    	        return;
	    	    }

	    	    if (target) {
	    	        window.open(url, target);
	    	    } else {
	    	        location.href = url;
	    	    }
	    	},
	    	/* 数组消重 */
	    	unique : function(arry) {
	    	    var result = [],
	    	        temp = {};
	    	    for (var i = 0; i < arry.length; i++) {
	    	        if (!temp[arry[i]]) {
	    	            result.push(arry[i]);
	    	            temp[arry[i]] = 1;
	    	        }
	    	    }
	    	    return result;
	    	},
	    	/* 生成随机数 */
	    	randomCode : function(type, num) {
	    	    type = type || 1;
	    	    num = num || 1;
	    	    var letterAndNumberArray = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
	    	    var randomCode = '';

	    	    for (var i = 0; i < num; i++) {
	    	        /* 数字随机 */
	    	        if (type == 1) {
	    	            randomCode += letterAndNumberArray[$.core.selectFrom(0, 9)];
	    	            continue;
	    	        }
	    	        /* 字母随机 */
	    	        if (type == 2) {
	    	            randomCode += letterAndNumberArray[$.core.selectFrom(10, 35)];
	    	            continue;
	    	        }
	    	        /* 3表示数据与字母随机 */
	    	        if (type == 3) {
	    	            randomCode += letterAndNumberArray[$.core.selectFrom(0, 35)];
	    	        }
	    	    }

	    	    return randomCode;
	    	},
	    	/* 指定范围的随机数 */
	    	selectFrom : function(lowerValue, upperValue) {
	    	    var choices = upperValue - lowerValue + 1;
	    	    return Math.floor(Math.random() * choices + lowerValue);
	    	},
	    	/* 获得?号后面的参数字符串  */
	    	getSearchStr : function(str) {
	    	    if (str) {
	    	        return str.substr(str.indexOf('?') + 1);
	    	    }

	    	    return location.search.substr(1);
	    	},

	    	/* 对象转成&key=value字符串 */
	    	paramToString : function(param) {
	    	    if (!param) {
	    	        return '';
	    	    }

	    	    var str = '';
	    	    $.each(
	    	        param,
	    	        function(k, v) {
	    	            str += '&' + k + '=' + v;
	    	        }
	    	    );

	    	    return str;
	    	},

	    	/* 判断两个对象里的值是否相同 */
	    	isSameObject : function(o1, o2){
	    		for(var key in o1){
	    			if (o1[key] != o2[key]){
	    				return false;
	    			}
	    		}
	    		return true;
	    	},

	    	/* 追加参数 */
	    	appedUrlParams : function(params, url) {
	    	    if (!(params && url)) {
	    	        return;
	    	    }

	    	    var urlParams = $.core.getSearch(url);
	    	    var paramsStr = '';
	    	    params = $.extend(urlParams, params);
	    	    paramsStr = $.core.paramToString(params);
	    	    paramsStr = paramsStr.substr(1);
	    	    url = url.replace(url.substr(url.indexOf('?')), '');
	    	    return url + '?' + paramsStr;
	    	},

	    	/* 换成特殊值 */
	    	escapeValue : function(str) {
	    	    if (str == 'null') {
	    	        return null;
	    	    }
	    	    if (str == 'undefined') {
	    	        return undefined;
	    	    }
	    	    return str;
	    	},

	    	/* urlencode */
	    	urlencode : function(str) {
	    	    str = (str + '').toString();

	    	    return encodeURIComponent(str).replace(/!/g, '%21').replace(/'/g, '%27').replace(/\(/g, '%28').replace(/\)/g, '%29').replace(/\*/g, '%2A').replace(/%20/g, '+');
	    	},

	    	/* 将null, 0, undefined的值替换成''-空字符串 */
	    	getNormalValue : function(value) {
	    	    if (value) {
	    	        return value;
	    	    }

	    	    return '';
	    	},

	    	/* 通过GET获取参数值转换成数字类型 */
	    	stringToNumber : function(string) {
	    	    var num = string * 1;

	    	    if (isNaN(num)) {
	    	        return 0;
	    	    }

	    	    return num;
	    	},
	    	/* 数字转16进制 */
	    	dec2hex : function(num) {
	    	    return Number(num).toString(16);
	    	},

	    	/* 数字转字符串ascii */
	    	dec2string : function(num) {
	    	    return $.core.hex2String($.core.dec2hex(num));
	    	},

	    	/* 16进制转数字 */
	    	hex2dec : function(string) {
	    	    if(string == ''){
	    	        return '';
	    	    }
	    	    return parseInt(string, 16);
	    	},

	    	/* 16进制转字符串ascii */
	    	hex2string : function(str) {
	    	    var val = "";
	    	    var arr = str.split(",");
	    	    if (arr.length == 0) {
	    	        return str;
	    	    }
	    	    for (var i = 0; i < arr.length; i++) {
	    	        val += arr[i].fromCharCode(i);
	    	    }
	    	    return val;
	    	},

	    	/* 字符串ascii转16进制 */
	    	string2hex : function(str) {
	    	    var byteCount = 0;
	    	    for (i = 0; i < str.length; i++) {
	    	        byteCount = str.charCodeAt(i);
	    	        if (byteCount.length == 1) {
	    	            byteCount = "0" + byteCount;
	    	        }
	    	        byteCount += byteCount.toString(16).toUpperCase();
	    	    }
	    	    return byteCount;
	    	},

	    	/* 字符串ascii转10进制 */
	    	string2dec : function(str) {
	    	    return $.core.hex2dec($.core.string2hex(str));
	    	},

	    	/* 字符补零 */
	    	fillZero : function(str, len) {
	    	    if (!str) {
	    	        return;
	    	    }
	    	    if (!len) {
	    	        return str;
	    	    }
	    	    var strLen = str.length;
	    	    if (strLen < len * 2) {
	    	        for (var i = 0; i < len * 2 - strLen; i++) {
	    	            str = '0' + str;
	    	        }
	    	    }
	    	    return str;
	    	},
	    	/* 获取某个元素下所有隐藏域的name并以数组返回 */
	    	getHiddenFields : function(parentElement) {
	    	    parentElement = parentElement || $(document.body);
	    	    var hiddenFields = parentElement.find('input[type="hidden"]'),
	    	        length = hiddenFields.length,
	    	        i = 0,
	    	        result = [];

	    	    if (!length) {
	    	        return result;
	    	    }

	    	    for (; i < length; i++) {
	    	        result.push($(hiddenFields[i]).attr('name'));
	    	    }

	    	    return result;
	    	},

	    	/* 根据参数删除隐藏域字段 */
	    	removeHiddenField : function(fields, parentElement) {
	    	    if (!fields) {
	    	        return;
	    	    }

	    	    parentElement = parentElement || $('#J_PostForm');
	    	    var i = 0,
	    	        length = fields.length,
	    	        hiddenFieldsName = $.core.getHiddenFields(parentElement);

	    	    for (var key in fields) {
	    	        if ($.inArray(key, hiddenFieldsName) != -1) {
	    	            $('#' + key).remove();
	    	        }
	    	    }
	    	},
	    	/* 解决在ie8下 new Date(time) 得不到日期的问题 */
	    	getNewDateOfFormatter : function(dateTime) {
	    		var strSeparator = "-";  
	    		var oDate = dateTime.split(strSeparator); 
	    	    var nDate = new Date(oDate[0], oDate[1]-1, oDate[2]); 
	    	    
	    	    return nDate;
	    	},
	    	/* 将数组里的值全部转成数字类型 */
	    	stringToNumberInArray : function(array) {
	    	    var result = [],
	    	        i = 0,
	    	        len = array.length;

	    	    if (len == 0) return result;

	    	    for (; i < len; i++) {
	    	        result.push(array[i] * 1);
	    	    }

	    	    return result;
	    	},
	    	/* 重复执行函数setIntervalTimeout */
	    	addInterval : function(fun, time, params) {
	    	    return setTimeout(function() {
	    	        fun(params);
	    	        setTimeout(arguments.callee, time);
	    	    }, time);
	    	},
	    	delInterval : function(id) {
	    	    clearTimeout(id);
	    	},

	    	/* 重复执行函数setInterval */
	    	setInterval : function(fun, time, params) {
	    	    return setInterval(function() {
	    	        fun(params);
	    	    }, time);
	    	},
	    	clearInterval : function(id) {
	    	    clearInterval(id);
	    	},

	    	/* 间隔执行函数setTimeout */
	    	addTimeout : function(fun, time, params) {
	    	    return setTimeout(function() {
	    	        fun(params);
	    	    }, time);
	    	},
	    	delTimeout : function(id) {
	    	    clearTimeout(id);
	    	},

	    	/* 获取去掉..的URL，用于需要加绝对路径的URL,例如：http://xxx.com/../upkeep/xxx */
	    	getUrlOfTrimPoint : function(url) {
	    	    return url.replace('\.\.', '');
	    	},
	    	/* 拼接URL地址 */
	    	pieceUrl : function(url, domain) {
	    	    if (!url) {
	    	        return;
	    	    }

	    	    if (domain) {
	    	        url = domain + url;
	    	    }

	    	    var hasParam = url.indexOf('?') != -1;

	    	    url += (hasParam ? '&' : '?') + 'sid=123456';

	    	    return url;
	    	},

	    	/* 将字符串转成小写，不同单词间用-替换 */
	    	getTransformClassName : function(str) {
	    	    if (!str) {
	    	        return '';
	    	    }

	    	    return str.replace(/([A-Z])/g, '-$1').toLowerCase().substr(1);
	    	},

	    	/* 联系人/手机拼凑,主要用于datagrid中formatter格式化字段 */
	    	pieceContacts : function(value) {
	    	    if (!value) {
	    	        return false;
	    	    }

	    	    var html,
	    	        splitCode = '<em class="split">/</em>',
	    	        firstValue = '',
	    	        secondValue = '';

	    	    html = '<ul class="list-unstyled">';

	    	    for (var i = 0; i < arguments.length; i++) {
	    	        firstValue = arguments[i][0] ? arguments[i][0] : '';
	    	        secondValue = arguments[i][1] ? arguments[i][1] : '';
	    	        if (!(firstValue || secondValue)) {
	    	            continue;
	    	        }

	    	        firstValue = firstValue ? firstValue : '-';
	    	        secondValue = secondValue ? secondValue : '-';

	    	        html += '<li class="contacts-list-item">';
	    	        html += '<span class="contacts-name">' + firstValue + splitCode + '</span>';
	    	        html += '<span class="contacts-tel">' + secondValue + '</span>';
	    	        html += '</li>';
	    	    }

	    	    html += '</ul>';

	    	    return html;
	    	},

	    	/* 判断对象里的值是否全部为空 */
	    	isEmptyByObjectValue : function(obj) {
	    	    if (!obj && typeof obj != 'object') {
	    	        return false;
	    	    }

	    	    var result = true;
	    	    for (var i in obj) {
	    	        if (obj[i] != '') {
	    	            result = false;
	    	            break;
	    	        }
	    	    }
	    	    return result;
	    	},
	    	/* 判断一个URL是否和当前域相同 */
	    	isSameDomain : function(url) {
	    	    var protocol = 'http://';
	    	    hasProtocol = url.indexOf(protocol) != -1;

	    	    if (!hasProtocol) {
	    	        return true;
	    	    }

	    	    var isExist = url.indexOf(location.host) != -1;
	    	    if (isExist) {
	    	        return true;
	    	    }

	    	    return false;
	    	},
	    	/******
	    	取一组对象里的不重复的值并以数组类型返回
	    	data:JSON数据源
	    	field:需要操作的字段名称
	    	******/
	    	getItemsToArray : function(data, field) {
	    	    if (!data) {
	    	        return false;
	    	    }

	    	    var temp = {},
	    	        result = [];

	    	    $.each(
	    	        data,
	    	        function(index, value) {
	    	            if (!temp[value[field]]) {
	    	                result.push(value[field]);
	    	                temp[value[field]] = 1;
	    	            }
	    	        }
	    	    );

	    	    return result;
	    	},
	    	/* 清除值 */
	    	clearValueToElement : function(data, parentElement){
	    		if (!data) {
	    	        return;
	    	    }

	    	    parentElement = parentElement || $(document.body);
	    	    var $element = null;
	    	    var $tagName = '';

	    	    $.each(
	    	        data,
	    	        function(name, value) {
	    	            $element = parentElement.find('[name="'+name+'"]');

	    	            if ($element.length == 0) {
	    	                return true;
	    	            }

	    	            $tagName = $element[0].tagName;
	    	            $tagType = $element[0].type;

	    	            if ($tagName == 'INPUT' || $tagName == 'TEXTAREA' || $tagName == 'SELECT') {
	    	                if ($tagType != 'file') {
	    	                    $element.val('');
	    	                }

	    	                return true;
	    	            }

	    	            if ($tagName == 'SPAN' || $tagName == 'DIV') {
	    	                $element.html('');
	    	                $element.attr('title', '');
	    	                return true;
	    	            }

	    	        }
	    	    );
	    	},
	    	/* 根据字段获取消重值 */
	    	getValuesArrayByFeild : function(field, data) {
	    	    var result = [];

	    	    $.each(
	    	        data,
	    	        function(index, value) {
	    	            if ($.inArray(value[field], result) != -1) {
	    	                return true;
	    	            }
	    	            result.push(value[field]);
	    	        }
	    	    );

	    	    return result;
	    	},
	    	//截取日期
	    	getCuttingDate : function (dataTime) {
	    	    if (!dataTime) {
	    	        return '';
	    	    }
	    	    return dataTime.split(' ')[0];
	    	}
	}
})(jQuery)