define(["app","jquery"],function(app,$){
	var maintain;
	maintain = function(treeNodeId){
		$("#appendChildBtn").click(function() {
            location.href=$.core.getContextPath()+'/admin/sys/OrganizationAddChild/'+treeNodeId;
            return false;
        });
		$("#moveBtn").click(function() {
            location.href=$.core.getContextPath()+'/admin/sys/OrganizationMove/'+treeNodeId;
            return false;
        });
	}
	
	return{
		init:maintain
	}
})