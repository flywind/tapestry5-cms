define(["jquery","plugin/ztree","app"],function($,app){
	var move;
	move = function(spec){
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
                $('[name="targetId"]').val(id);
                $("#targetName").val(name);
                hideMenu();
            }

            function showMenu() {
                var cityObj = $("#targetName");
                var cityOffset = $("#targetName").offset();
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
            $("#menuBtn").click(showMenu);
	}
	
	return{
		init:move
	}
	
})