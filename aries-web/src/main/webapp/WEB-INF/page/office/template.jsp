<%@ page language="java"
         import="java.util.*,com.zhuozhengsoft.pageoffice.*,com.zhuozhengsoft.pageoffice.wordwriter.*"
         pageEncoding="gb2312" %>
<%@ taglib uri="http://java.pageoffice.cn" prefix="po" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>在线打开文件</title>
</head>
<script type="text/javascript" src='${pageContext["request"].contextPath}/jquery.min.js'></script>
<body>
<input type="hidden" name="objectId" id="objectId" value="${objectId}">
<input type="hidden" name="fileSubject" id="fileSubject" value="${fileSubject}">
<div style="width:1000px;height:700px;">
    ${pageoffice}
</div>
<div>
    <input type="text" id="tagName" name="tagName">
    <button type="button" id="addTagName">添加标签名称</button>
</div>

<script type="text/javascript">
    function Save() {
        document.getElementById("PageOfficeCtrl1").WebSave();
    }
    $("#addTagName").on("click", function () {
        var v = $("#tagName").val();
        addTag(v);
    });

    //添加Tag
    function addTag(tagName) {
        try {
            var tmpRange = document.getElementById("PageOfficeCtrl1").Document.Application.Selection.Range;
            tmpRange.Text = tagName;
            tmpRange.Select();
            return "true";
        } catch (e) {
            return "false";
        }
    }
</script>
</body>
</html>