(function(){
	requirejs.config({
		shim: {
			'validate': ['jquery','bootstrap/tooltip','bootstrap/popover'],
			"jmd5": ["jquery"]
		},
		paths: {
			'validate': 'plugins/bootstrapValidator/bootstrapValidator.min',
			"jmd5" : "plugins/md5/jquery.md5"
		},
		waiteSeconds: 0	
	});
	
	define(['jquery','validate','app','jmd5'],function($,v,a,m){
		var init;
		
		init = function(spec){
			var home = spec.index;
			var faIcon = {
				valid: 'fa fa-check-circle fa-lg text-success',
				invalid: 'fa fa-times-circle fa-lg',
				validating: 'fa fa-refresh'
			}
			var $signForm = $('#signForm');
			$signForm.bootstrapValidator({
				message: spec.msg,
				excluded: [':disabled'],
				feedbackIcons: faIcon,
				fields: {
					name: {
						container: 'popover',
						validators: {
							notEmpty: {
								message: spec.userRequiredMsg
							},
							stringLength: {
	                            min: 4,
	                            max: 15,
	                            message: spec.userStringLengthMsg
	                        },
							regexp: {
								regexp: /^[a-zA-Z0-9_\.]+$/,
								message: spec.userValidateMsg
							},
							remote: {
								type: "get",
								url: spec.checkUser,
								message: spec.userHasMsg,
								data: function(){
									return {
										name: $('#name').val()
									}
								},
								delay: 2000
							}
						}
					},
					email: {
						container: 'popover',
						validators: {
							notEmpty: {
								message: spec.emailRequiredMsg
							},
							emailAddress: {
								message: spec.emailValidateMsg
							}
						}
					},
					password: {
						container: 'popover',
						validators: {
							notEmpty: {
								message: spec.passwordRequiredMsg
							}
						}
					},
					confirmpwd: {
						container: 'popover',
						validators: {
							notEmpty: {
								message: spec.confirmpwdRequiredMsg
							},
							identical: {
								field: 'password',
								message: spec.noSameMsg
							}
						}
					}
				}
			}).on('success.form.bv', function(e) {
				var $form = $(e.target), bv = $form.data('bootstrapValidator');
				
			});
			
			$('#signBtn').on('click',function(){
				var isValid = $signForm.data('bootstrapValidator').isValid(),_this=$(this);
				if(!isValid){
					_this.addClass('animated shake');
					var at = setTimeout(function(){
						_this.removeClass('animated shake');
						clearTimeout(at);
					},600);
					
					return;
				}
				var p = $.md5($('#password').val()),d = $('#hp').val();
				$('#hp').val('');
				
				$('#confirmpwd').val($.md5($('#confirmpwd').val()));
				$('#password').val(p);
				var data = $.core.getFormData('signForm');
				data.ep = $.md5(p+d);
				$.ajax(spec.create,{
					data: data,
					type: 'POST',
					dataType: 'json',
					success: function(d){
						if(d.success){
							if(d.loginMessage){
								var alertHtml = '<div class="alert alert-pink alert-sign">'+d.error+ ': ' +d.loginMessage+'</div>';
								$('#alertMsg').append(alertHtml);
								
							}else{
								//console.log(d.success);
								window.location.href = home;
							}
						}	
					},
					error: function(){
						//conolse.log(d.error);
						if(d.error){
							var alertHtml = '<div class="alert alert-pink alert-sign">'+d.error+'</div>';
							$('#alertMsg').append(alertHtml);
						}
						//Clear form validate
						//$signForm.data('bootstrapValidator').destroy();
						//$signForm.data('bootstrapValidator', null);
					}
					
				});
			});
		}
		
		return {
			init: init
		}
	});
	
}).call(this);