<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<script type="text/javascript" src="../js/jquery164.js"></script>
</head>
<body>
	<h2>RPC服务监控查询</h2>
	<form method="get" action="show">
		date: <input id="date" name="date" type="text" onchange="getService()"></input>
		service: 
		<select id="service" name="service" onchange="getMethod()">
			<option value="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
		</select>
		method: 
		<select id="method" name="method">
			<option value="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
		</select>
		<input type="submit" value="查询"/>
	</form>
</body>
<script type="text/javascript">
	//service查询
	function getService() {
		$.get("getServices?date="+ $("#date").val(), function(data){
			var val = '<option value="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>';
			var services = data.split(',');
			for (var i=0; i<services.length; i++) {
				val += '<option value="'+services[i]+'" >'+services[i]+'</option>';
			}
			$("#service").html(val);
		});
	}
	//method查询
	function getMethod() {
		$.get("getMethods?date="+$("#date").val()+'&service='+$("#service").val(), function(data){
			var val = '<option value="">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>';
			var methods = data.split(',');
			for (var i=0; i<methods.length; i++) {
				val += '<option value="'+methods[i]+'" >'+methods[i]+'</option>';
			}
			$("#method").html(val);
		});
	}
</script>
</html>