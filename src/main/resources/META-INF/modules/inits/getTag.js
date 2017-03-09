(function(){
	define(["jquery"],function($){
		var toTag;
		toTag = function(){	
			$('#tagId').on('click',function(){
				alert(1)
				var d = $(this).attr('data-tag');
				$('#tid').val(d);
				$('#tagForm').submit();
			})
		}

		return {
			toTag : toTag
		}
	})
}).call(this);