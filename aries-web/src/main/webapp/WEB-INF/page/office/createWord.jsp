<%@ page language="java"
	import="java.util.*,com.zhuozhengsoft.pageoffice.*,com.zhuozhengsoft.pageoffice.wordwriter.*"
	pageEncoding="gb2312"%>
<%@ taglib uri="http://java.pageoffice.cn" prefix="po"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>�½��ĵ�</title>
		<script type="text/javascript">
		        function Save() {
		            document.getElementById("PageOfficeCtrl1").WebSave();
		            if(document.getElementById("PageOfficeCtrl1").CustomSaveResult=="ok"){
			             alert('����ɹ���');
			               //�����б�ҳ��
			             if(window.external.CallParentFunc("freshIndex()")=='poerror:parentlost'){
		                      alert('��ҳ��رջ���תˢ���ˣ����¸�ҳ�溯��û�е��óɹ���');
		                    }
			             window.external.close();//�رյ�ǰPOBrower�����Ĵ���                      
		            }else{
		            	alert('����ʧ�ܣ�');
		            }
		        }
		
		        function Cancel() {
		             window.external.close();
		        }
		
		        function getFocus() {
		            var str = document.getElementById("fileSubject").value;
		            if (str == "�������ĵ�����") {
		                document.getElementById("fileSubject").value = "";
		            }
		        }
		        function lostFocus() {
		            var str = document.getElementById("fileSubject").value;
		            if (str.length <= 0) {
		                document.getElementById("fileSubject").value = "�������ĵ�����";
		            }
		        }
				function BeforeDocumentSaved() {
					var str = document.getElementById("fileSubject").value;
					if (str.length <= 0) {
						document.getElementById("PageOfficeCtrl1").Alert("�������ĵ�����");
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
						�ĵ����⣺
						<input name="fileSubject" id="fileSubject" type="text"
							onfocus="getFocus()" onblur="lostFocus()" class="boder"
							style="width: 180px;" value="�������ĵ�����" />
						<input type="button" onclick="Save()" value="���沢�ر�" />
						<input type="button" onclick="Cancel()" value="ȡ��" />
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
