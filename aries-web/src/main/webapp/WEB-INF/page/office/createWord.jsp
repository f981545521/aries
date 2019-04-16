<%@ page language="java"
	import="java.util.*,com.zhuozhengsoft.pageoffice.*,com.zhuozhengsoft.pageoffice.wordwriter.*"
	pageEncoding="gb2312"%>
<%@ taglib uri="http://java.pageoffice.cn" prefix="po"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>新建文档</title>
		<script type="text/javascript">
		        function Save() {
		            document.getElementById("PageOfficeCtrl1").WebSave();
		            if(document.getElementById("PageOfficeCtrl1").CustomSaveResult=="ok"){
			             alert('保存成功！');
			               //返回列表页面
			             if(window.external.CallParentFunc("freshIndex()")=='poerror:parentlost'){
		                      alert('父页面关闭或跳转刷新了，导致父页面函数没有调用成功！');
		                    }
			             window.external.close();//关闭当前POBrower弹出的窗口                      
		            }else{
		            	alert('保存失败！');
		            }
		        }
		
		        function Cancel() {
		             window.external.close();
		        }
		
		        function getFocus() {
		            var str = document.getElementById("fileSubject").value;
		            if (str == "请输入文档主题") {
		                document.getElementById("fileSubject").value = "";
		            }
		        }
		        function lostFocus() {
		            var str = document.getElementById("fileSubject").value;
		            if (str.length <= 0) {
		                document.getElementById("fileSubject").value = "请输入文档主题";
		            }
		        }
				function BeforeDocumentSaved() {
					var str = document.getElementById("fileSubject").value;
					if (str.length <= 0) {
						document.getElementById("PageOfficeCtrl1").Alert("请输入文档主题");
						return false
					}
					else
						return true;
				}
		    </script>
	</head>
	<body>
		<form id="form2">
			<div id="content">
				<div id="textcontent" style="width: 1000px; height: 800px;">
					<div>
						文档主题：
						<input name="fileSubject" id="fileSubject" type="text"
							onfocus="getFocus()" onblur="lostFocus()" class="boder"
							style="width: 180px;" value="请输入文档主题" />
						<input type="button" onclick="Save()" value="保存并关闭" />
						<input type="button" onclick="Cancel()" value="取消" />
					</div>
					<div>
						&nbsp;
					</div>
					<po:PageOfficeCtrl id="PageOfficeCtrl1" />
				</div>
			</div>
		</form>
	</body>
</html>
