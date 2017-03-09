define(["jquery","t5/core/dom","app","plugin/adialog"],function($,dom,app,adialog){
	var init;
	
	init = function(spec){
		var deleteUrl = spec.deleteUrl;
		var $table = $('#sysLogListId');
		
		$table.on('check.bs.table uncheck.bs.table check-all.bs.table uncheck-all.bs.table', function () {
			$('[data-button-type=bootstrap]').each(function(){
				$(this).prop('disabled', !$table.bootstrapTable('getSelections').length);
			})
	        
	    });
		
		//Triggler delete data event
		$('#delete').on('click',function(){

			var rows = $table.bootstrapTable('getSelections');
			
			if(rows.length < 1){
				art.dialog({
					id : 'noLogSelected',
					content : spec.noSelectMsg,
					title : spec.dialogTitle,
					lock : true,
					padding : '20px 40px'
				}).time(3);
				
				return false;
			};
			
			var selectData = {};
			selectData.opTime = rows[0].opTime;
			selectData.customerCode = rows[0].customerCode;
			
			//Open Loading...
			$.core.loadingStart();
			
			var ajaxOpts = {
				data : selectData,
				success : function(data){
					
					//Reload datagrid data
					$table.bootstrapTable('refresh');
					
					//Colse Loading...
					$.core.loadingClose();
					
					//Output Result
					if(data.statusText == "OK"){
						var d = $.parseJSON(data.text);
						if(d.success){
							art.dialog({
								id : 'successDialog',
								content : d.success,
								title : spec.dialogTitle,
								lock : true,
								padding : '20px 100px'
							}).time(3);
						}else if(d.error){
							art.dialog({
								id : 'errorDialog',
								content : d.error,
								title : spec.dialogTitle,
								lock : true,
								padding : '20px 100px'
							}).time(3);
						}
						
					}
				},
				error : function(jqXHR, textStatus, errorThrown){
					//Colse Loading...
					$.core.loadingClose();
				}
			};

			dom.ajaxRequest(deleteUrl,ajaxOpts);
		});
	};
	
	return {
		init : init
	}
})