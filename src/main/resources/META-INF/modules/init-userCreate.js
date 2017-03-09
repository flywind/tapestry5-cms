(function(){
	requirejs.config({
		shim: {
			"plugin/ztree": ["jquery"],
			"plugin/treetable": ["jquery","plugin/ztree"]
		},
		paths: {
			"plugin/ztree": "plugins/ztree/jquery.ztree.all.min",
			"plugin/treetable": "plugins/treeTable/javascripts/src/jquery.treetable"
			
		},
		waitSeconds: 0
	});
	
	define(["jquery","app","plugin/treetable"],function($,app,za){
		var init,initRole;
		var reset;
		init = function(spec){
			var setting = {
	                view: {
	                    dblClickExpand: false
	                },
	                data: {
	                    simpleData: {
	                        enable: true
	                    }
	                },
	                callback: {
	                    onClick: onClick
	                }
	            };

				spec = eval(spec);
				var arr = new Array();
				$.each(spec,function(i,o){
					var item = { id:o.id, pId:o.parentId, name:o.name, open:o.rootNode};
					arr[i] = item;
				})

	            function onClick(e, treeId, treeNode) {
	                var zTree = $.fn.zTree.getZTreeObj("tree"),
	                        nodes = zTree.getSelectedNodes(),
	                        id = "",
	                        name = "";
	                nodes.sort(function compare(a,b){return a.id-b.id;});
	                for (var i=0, l=nodes.length; i<l; i++) {
	                    id += nodes[i].id + ",";
	                    name += nodes[i].name + ",";
	                }
	                if (id.length > 0 ) id = id.substring(0, id.length-1);
	                if (name.length > 0 ) name = name.substring(0, name.length-1);
	                $('input[name="companyId"]').val(id);
	                $("#companyName").val(name);
	                hideMenu();
	            }

	            function showMenu() {
	                var cityObj = $("#companyName");
	                var cityOffset = $("#companyName").offset();
	                $("#menuContent").css({left:cityOffset.left + "px", top:cityOffset.top + cityObj.outerHeight() + "px"}).slideDown("fast");

	                $("body").bind("mousedown", onBodyDown);
	            }
	            function hideMenu() {
	                $("#menuContent").fadeOut("fast");
	                $("body").unbind("mousedown", onBodyDown);
	            }
	            function onBodyDown(event) {
	                if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
	                    hideMenu();
	                }
	            }

	            $.fn.zTree.init($("#tree"), setting, arr);
	            $('input[name="companyName"]').focus(showMenu);

		}
		
		initRole = function(spec){
			var setting = {
	                check: {
	                    enable: true ,
	                    chkboxType: { "Y": "ps", "N": "ps" }
	                },
	                view: {
	                    dblClickExpand: false
	                },
	                data: {
	                    simpleData: {
	                        enable: true
	                    }
	                },
	                callback: {
	                    onCheck: onCheck
	                }
	            };
			
				spec = eval(spec);
				var arr = new Array();
				
				$.each(spec,function(i,o){
					var item = { id:o.id, name:o.name, checked:false};
					arr[i] = item;
				})

	            function onCheck(e, treeId, treeNode) {
	                var zTree = $.fn.zTree.getZTreeObj("roleTree"),
	                        nodes = zTree.getCheckedNodes(true),
	                        id = "",
	                        name = "";
	                nodes.sort(function compare(a,b){return a.id-b.id;});
	                for (var i=0, l=nodes.length; i<l; i++) {
	                    id += nodes[i].id + ",";
	                    name += nodes[i].name + ",";
	                }
	                if (id.length > 0 ) id = id.substring(0, id.length-1);
	                if (name.length > 0 ) name = name.substring(0, name.length-1);
	                $("#roleIds").val(id);
	                $('input[name="roles"]').val(name);
//	                hideMenu();
	            }

	            function showMenu() {
	                var cityObj = $('input[name="roles"]');
	                var cityOffset = $('input[name="roles"]').offset();
	                $("#roleMenuContent").css({left:cityOffset.left + "px", top:cityOffset.top + cityObj.outerHeight() + "px"}).slideDown("fast");

	                $("body").bind("mousedown", onBodyDown);
	            }
	            function hideMenu() {
	                $("#roleMenuContent").fadeOut("fast");
	                $("body").unbind("mousedown", onBodyDown);
	            }
	            function onBodyDown(event) {
	                if (!(event.target.id == "menuBtn" || event.target.id == "roleMenuContent" || $(event.target).parents("#roleMenuContent").length>0)) {
	                    hideMenu();
	                }
	            }

	            $.fn.zTree.init($("#roleTree"), setting, arr);
	            $('input[name="roles"]').click(showMenu);

		}
		
		
		return{
			init:init,
			initRole:initRole
		}
	});
	
}).call(this);

