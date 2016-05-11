<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<script type="text/javascript" src="../js/jquery164.js"></script>
<script type="text/javascript" src="../js/My97DatePicker/WdatePicker.js"></script>
</head>
<body>
	<h2>RPC服务监控查询</h2>
	<form method="get" action="show">
		时间: 
		<input type="text" name="startTime" id="d1" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate" style="width:160px"/>
		到
		<input type="text" name="endTime" id="d2" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" class="Wdate" style="width:160px" onchange="getService()"/>
		服务名: 
		<select id="service" name="service" onchange="getMethod()">
			<option value="">&nbsp;&nbsp;&nbsp;</option>
		</select>
		方法名: 
		<select id="method" name="method">
			<option value="">&nbsp;&nbsp;&nbsp;</option>
		</select>
		<input type="submit" value="查询"/>
	</form>
</body>
<script type="text/javascript">
	//service查询
	function getService() {
		$.get("getServices?startTime="+ $("#d1").val()+"&endTime="+ $("#d2").val(), function(data){
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
		$.get("getMethods?startTime="+ $("#d1").val()+"&endTime="+ $("#d2").val()+'&service='+$("#service").val(), function(data){
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