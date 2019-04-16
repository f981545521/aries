<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="UTF-8">
    <title>HELLO WORLD</title>
    <!--PageOffice.js和jquery.min.js文件一定要引用-->
    <script type="text/javascript" src='${pageContext["request"].contextPath}/jquery.min.js'></script>
    <script type="text/javascript" src='${pageContext["request"].contextPath}/pageoffice.js' id="po_js_main"></script>
</head>
<body>
    <h1>HELLO WORLD</h1>
    <div>
        <h3>新建文件</h3>
        <a href="javascript:POBrowser.openWindowModeless('/office/create' , 'width=1200px;height=800px;')" style=" color:Blue; text-decoration:underline;">新建文件</a>
    </div>
    <div>
        <h3>读取文件</h3>
        <a href="javascript:POBrowser.openWindowModeless('/office/read'   , 'width=1200px;height=800px;');">读取文件</a>
    </div>
    <div>
        <h3>编辑模板</h3>
        <a href="javascript:POBrowser.openWindowModeless('/office/template'   , 'width=1200px;height=800px;');">编辑模板</a>
    </div>
</body>
<script type="text/javascript">
    function  freshIndex(){
        window.location.reload();
    }
</script>
</html>