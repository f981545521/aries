<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>系统错误</title>
</head>
<body>
    <div style="margin: 100px;">
        <h3 style="color: red">系统错误</h3>
        <h3>状态码：${state}</h3>
        <h3>错误信息：${message}</h3>
        <h3>请求路径：${requestUrl}</h3>
    </div>
</body>
</html>
