$(function(){
	$("#login").click(function()){
		var username=$(#username).val();
		var password=$(#password).val();
		var user={username:username,password:password};
		$ajax({
			type:"post",
			url:"/login",
			data:user,
			datatype:"json",
			success:function(data){
				alert("成功");
			},
			error：function(e){
				alert("出错:"+e);
			}
		});
	});
});
$(function(){
    $("#cancel").click(function() {
        $("#username").val("").focus(); 
        $("#password").val("");
    });
})