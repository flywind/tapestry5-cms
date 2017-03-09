define(["jquery","plugin/adialog","t5/core/dom","t5/core/events","t5/core/forms","t5/core/zone"],function($,adialog,dom,events,forms,zone){
	var update,close,fillInput,clearError,updateSuccess;
	
	update = function(spec){
		var editBtn = $('#editBtn'),editDialogCloseBtn = $('#editDialogCloseBtn');
		var editDialog;
		editBtn.on('click',function(e){
			e.preventDefault();
			var rows = $.map($('#versionLogListId').bootstrapTable('getSelections'), function (row) {
				
                return row.id;
            });
			
			var row = $('#versionLogListId').bootstrapTable('getSelections');
			
			if(rows.length < 1){
				art.dialog({
					title:spec.editAlertTitleMsg,
					padding:'15px 60px',
					lock:true,
					time:2,
					content:spec.editSelectOneMsg
				})
				return;
			}
			
			if(rows.length > 1){
				art.dialog({
					title:spec.editAlertTitleMsg,
					padding:'15px 60px',
					lock:true,
					time:2,
					content:spec.editAlertMsg
				})
				return;
			}
			
			clearError("editForm",["title","titleEn"]);
			fillInput("editForm",row[0]);
			
			editDialog = art.dialog({
				title:spec.editMsg,
				id:'editDialogId',
				lock:true,
				content:document.getElementById('editDialog')
			});
		});
		
		editDialogCloseBtn.on('click',function(e){
			e.preventDefault();
			art.dialog.list['editDialogId'].close();
		});
	};
	updateSuccess = function(){
		art.dialog.list['editDialogId'].close();
	}
	//清空错误
	clearError = function(formId,arr){
		$.each(arr,function(i,v){
			var o = $('#'+formId+' [id="u'+v+'"]');
			if(o.parent().hasClass('has-error')){
				o.parent('.has-error').removeClass('has-error');
				o.parent().parent('.has-error').removeClass('has-error');
				o.next('p.help-block').remove();
			}
		});
	};
	//填充数据
	fillInput = function(formId,obj){
		for(var p in obj){
		   if(typeof(obj[p]) == "function" ){ 
			  obj[p](); 
		   } else {
			   if(obj[p]){
				   $('#'+formId+' [id="u'+p+'"]').val(obj[p]);
			   }
		   } 
		}
	}
	
	close = function(){
		$(':input','#addForm').not(':button, :submit, :reset, :hidden').val('').removeAttr('checked');
		art.dialog.list['addDialog'].close();
	};
	
	return {
		update:update,
		close:close,
		updateSuccess:updateSuccess
	}
})