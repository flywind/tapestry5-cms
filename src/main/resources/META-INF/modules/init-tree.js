(function(){
	requirejs.config({
		shim:{
			"plugin/ztree":["jquery"]
		},
		paths:{
			"plugin/ztree": "plugins/ztree/jquery.ztree.all.min"
		},
		waitSeconds:0
	});
	
	define(["jquery","plugin/ztree","app"],function($,ztree,app){
		var initTree;
		var searchOrg;
		var boundClickButton;
		var opType = 2;
		
		initTree = function(spec){
			var setting = {
	            data: {
	                simpleData: {
	                    enable: true
	                }
	            },
	            callback : {
	                onClick : function(event, treeId, treeNode) {
	                	var orgTree = $.fn.zTree.getZTreeObj("tree");
	                	var nodes = orgTree.getSelectedNodes();
	                	if (nodes.length > 1) {
	                		disabledButton();
	                	} else {
	                		controlButton(event, treeId, treeNode);
	                		showEditForm(treeNode);
	                	}
	                }
	            }
	        };
			spec = eval(spec);
			var arr = new Array();
			$.each(spec,function(i,o){
				var v ={ id:o.id,pId:o.parentId,name:o.name,contactName:o.contactName,contactNumber:o.contactNumber,address:o.address,open:o.rootNode};
				arr[i] = v;
			});
			
	        $.fn.zTree.init($("#tree"), setting, arr);
	        
	        if (opType == 1) {
	        	var pid = $('#pId').val();
	        	var zTree = $.fn.zTree.getZTreeObj("tree");
				var nodes = zTree.transformToArray(zTree.getNodes());
				
				for (var i = 0; i < nodes.length; i++) {
					var node = nodes[i];
					if (node.id == pid) {
						zTree.selectNode(node);
						zTree.expandNode(node, true);
						break;
					}
				}
				
				resetOrgTreeDivSize();
				initCreateForm();
	        } else if (opType == 2) {
	        	var id = $('#eId').val();
	        	var zTree = $.fn.zTree.getZTreeObj("tree");
				var nodes = zTree.transformToArray(zTree.getNodes());
				
				for (var i = 0; i < nodes.length; i++) {
					var node = nodes[i];
					if (node.id == id) {
						zTree.selectNode(node);
						zTree.expandNode(node, true);
						break;
					}
				}
	        	resetOrgTreeDivSize();
	        } else if (opType == 3) {
	        	var pId = $('#pdId').val();
				var zTree = $.fn.zTree.getZTreeObj("tree");
				var nodes = zTree.transformToArray(zTree.getNodes());
				
				for (var i = 0; i < nodes.length; i++) {
					var node = nodes[i];
					if (node.id == pId) {
						zTree.selectNode(node);
						zTree.expandNode(node, true);
						enableButton();
						$('#dId').val(node.id);
						$('#pdId').val(node.pId);
						showEditForm(node);
						break;
					}
				}
				resetOrgTreeDivSize();
				opType = 2;
	        }
	        
	        //展示组织机构
	        $('#showBtn').click(function() {
	        	var treeObj = $.fn.zTree.getZTreeObj("tree");
	        	treeObj.expandAll(true);
	        });
	        //收起组织机构
	        $('#retractBtn').click(function() {
	        	var treeObj = $.fn.zTree.getZTreeObj("tree");
	        	treeObj.expandAll(false);
	        });
	        
		};
		
		/**
		 * 给3个按钮绑定点击事件(添加下级单位，编辑，删除)
		 */
		boundClickButton = function() {
			$('#addChildBtn').click(function() {
				$('#createChildForm').removeClass('dis-none');
				$('#editForm').addClass('dis-none');
				initCreateForm();
				resetOrgTreeDivSize();
			});
			$('#editBtn').click(function() {
				$('#editForm').removeClass('dis-none');
				$('#createChildForm').addClass('dis-none');
				resetOrgTreeDivSize();
			});
			$('#delBtn').click(function() {
				$('#createChildForm').get(0).reset();
				$('#editForm').get(0).reset();
				$('#createChildForm').addClass('dis-none');
				$('#editForm').addClass('dis-none');
				$('#deleteForm').submit();
				disabledButton();
				opType = 3;
			});
			
			//给两个提交按钮绑定事件
			$('#createBtn').click(function() {
				opType = 1;
			});
			$('#editBtn').click(function() {
				opType = 2;
			});
			
			$(window).resize(function () {
				resetOrgTreeDivSize();
			});
			resetOrgTreeDivSize();
		}
		

		searchOrg = function() {
			$("#orgSearchButton").click(function() {
				var orgName = $("#organizationName").val();
				var zTree = $.fn.zTree.getZTreeObj("tree");
				var nodes = zTree.transformToArray(zTree.getNodes());

				zTree.refresh();
				$.each(nodes, function(i, node) {
					if (orgName && (node.name).indexOf(orgName) >= 0) {
						node.checked = true;
						node.checked = false;
						zTree.selectNode(node, true);
					}
				});
				
				$('#createChildForm').addClass('dis-none');
				$('#editForm').addClass('dis-none');
				
				var selectNodes = zTree.getSelectedNodes();
				if (selectNodes.length > 1) {
					disabledButton();
				} else {
					controlButton(null, null, selectNodes[0]);
					showEditForm(selectNodes[0]);
				}
				
				resetOrgTreeDivSize();
			});
		}
		
		/**
		 * 按钮组织机构的3个按钮(添加下级单位，编辑，删除)
		 * 
		 * @param event
		 * @param treeId
		 * @param treeNode
		 */
		controlButton = function(event, treeId, treeNode) {
			$('#addChildBtn').removeAttr('disabled');
			$('#editBtn').removeAttr('disabled');
			if (treeNode.pId) {
				$('#delBtn').removeAttr('disabled');
			} else {
				$('#delBtn').attr('disabled', 'disabled');
			}
			
			//添加页面将当前node信息填充到上级单位名称文本框
			$('#pname').val(treeNode.name);
			$('#pId').val(treeNode.id);
			
			//编辑页面填充数据
			$('#eId').val(treeNode.id);
			$('#eName').val(treeNode.name);
			$('#eContactName').val(treeNode.contactName);
			$('#eContactNumber').val(treeNode.contactNumber);
			$('#eAddress').val(treeNode.address);
			
			//为删除FROM填充值
			$('#dId').val(treeNode.id);
			$('#pdId').val(treeNode.pId);
		}
		
		/**
		 * 禁用3个按钮(添加下级单位，编辑，删除)
		 */
		disabledButton = function() {
			$('#addChildBtn').attr('disabled', 'disabled');
			$('#editBtn').attr('disabled', 'disabled');
			$('#delBtn').attr('disabled', 'disabled');
		}
		
		/**
		 * 启用3个按钮(添加下级单位，编辑，删除)
		 */
		enableButton = function() {
			$('#addChildBtn').removeAttr('disabled');
			$('#editBtn').removeAttr('disabled');
			$('#delBtn').removeAttr('disabled');
		}
		
		/**
		 * 显示编辑组织构造的FORM
		 */
		showEditForm = function(treeNode) {
			$('#createChildForm').addClass('dis-none');
			$('#editForm').removeClass('dis-none');
			$('#eId').val(treeNode.id);
			$('#eName').val(treeNode.name);
			$('#eContactName').val(treeNode.contactName);
			$('#eContactNumber').val(treeNode.contactNumber);
			$('#eAddress').val(treeNode.address);
		}
		
		/**
		 * 重新设置组织机构区域大小
		 */
		resetOrgTreeDivSize = function() {
			var height = $(window).height() - 225;
			$('#orgTreeDiv').height(height);
		}
		
		/**
		 * 初始化添加表单
		 */
		initCreateForm = function() {
			$('#name').val('');
			$('#contactName').val('');
			$('#contactNumber').val('');
			$('#address').val('');
		}
		
		return {
			initTree : initTree,
			searchOrg:searchOrg,
			boundClickButton:boundClickButton
		}
		
		
	})
}).call(this);

