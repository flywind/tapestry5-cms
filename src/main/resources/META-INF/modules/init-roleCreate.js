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
	
	define(["jquery","plugin/treetable","app"],function($,ta,app){
		var init;
		//第二个参数在编辑时使用，用于为选择的资源默认勾选
		init = function(spec, slectIds){
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
				selectIds = eval(slectIds);
				var arr = new Array();
				$.each(spec,function(i,o){
					var checkedValue = false;
					var isOpen = false;
					if (slectIds.indexOf(o.id) != -1) {
						checkedValue = true;
						isOpen = true;
					}
					var item = { id:o.id, pId:o.parentId, name:o.name, checked:checkedValue, open:isOpen};
					arr[i] = item;
				})

	            /*var zNodes =[
	                <c:forEach items="${resourceList}" var="r">
	                <c:if test="${not r.rootNode}">
	                { id:${r.id}, pId:${r.parentId}, name:"${r.name}", checked:${zhangfn:in(role.resourceIds, r.id)}},
	                </c:if>
	                </c:forEach>
	            ];*/

	            function onCheck(e, treeId, treeNode) {
	                var zTree = $.fn.zTree.getZTreeObj("tree"),
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
	                $("#resourceIds").val(id);
	                $('input[name="resources"]').val(name);
//	                hideMenu();
	            }

	            function showMenu() {
	                var cityObj = $('input[name="resources"]');
	                var cityOffset = $('input[name="resources"]').offset();
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
	            $('input[name="resources"]').click(showMenu);

		}
		
		return{
			init:init
		}
	});
	
}).call(this);
