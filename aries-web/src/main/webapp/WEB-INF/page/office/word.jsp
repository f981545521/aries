<%@ page language="java"
		 import="java.util.*,com.zhuozhengsoft.pageoffice.*,com.zhuozhengsoft.pageoffice.wordwriter.*"
		 pageEncoding="gb2312"%>
<%@ taglib uri="http://java.pageoffice.cn" prefix="po"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>在线打开文件</title>
</head>
<body>
<input type="hidden" name="objectId" id="objectId" value="${objectId}">
<input type="hidden" name="fileSubject" id="fileSubject" value="${fileSubject}">
<div style="width:1000px;height:700px;">
	${pageoffice}
</div>

<script type="text/javascript">
    function Save() {
        document.getElementById("PageOfficeCtrl1").WebSave();
    }
</script>
</body>
</html>