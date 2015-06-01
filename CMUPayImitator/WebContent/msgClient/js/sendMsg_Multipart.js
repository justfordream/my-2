/**
 * @date 20130305
 * @author panliguan
 * Ajax发送报文，Multipart方式发送
 */

window.onload = function(){
	initBusinessSelect();
	initAddressSelect();
	window.document.getElementById("result").innerText = "";
	window.document.getElementById("receiveMsg").innerText = "";
}

/**
 * 
 */
function sendMsg(){
	var t = document.getElementById("address"); 
	var address = t.options[t.selectedIndex].value;
	var xmlhead = window.document.getElementById("xmlhead").value;
	var xmlbody = window.document.getElementById("xmlbody").value;
	
	if(address == null || address == "" || address == "undefined"){
		window.document.getElementById("result").innerHTML = "<font color='red'>地址不能为空！</font>";
		return;
	}
	if(xmlhead == null || xmlhead == "" || xmlhead == "undefined"){
		window.document.getElementById("result").innerHTML = "<font color='red'>报文头不能都为空！</font>";
		return;
	}
	if(xmlbody == null || xmlbody == "" || xmlbody == "undefined"){
		window.document.getElementById("result").innerHTML = "<font color='red'>报文体不能都为空！</font>";
		return;
	}
	
	sengMsg(address, xmlhead, xmlbody);
}

function sengMsg(address, xmlhead, xmlbody){

	var xhr = createXMLHttpRequest();
	xhr.onreadystatechange = function(){
		if((xhr.readyState == 4) && (xhr.status == 200 || xhr.status == 304)){
			window.document.getElementById("receiveMsg").value = xhr.responseText;
		}
	};
	
	xhr.open("post", "../httpPostMsgMultipartServlet", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send("address=" + address + "&xmlhead=" + xmlhead + "&xmlbody=" + xmlbody);
}
/////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 初始化选择报文下拉框
 */
function initMesSelect(){
	var t = document.getElementById("businessSelect"); 
	var directoryName = t.options[t.selectedIndex].value;
	
	var xhr = createXMLHttpRequest();
	xhr.onreadystatechange = function(){
		if((xhr.readyState == 4) && (xhr.status == 200 || xhr.status == 304)){
			InitSelect(xhr.responseText, 'mesSelect');
		}
	};
	
	xhr.open("post", "../InitSelectServlet", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send("serverId=6" + "&directoryName=" + directoryName);
}

/**
* 页面点击业务类型下拉框触发初始化
*/
function initBusinessSelect(){
	var xhr = createXMLHttpRequest();
	xhr.onreadystatechange = function(){
		if((xhr.readyState == 4) && (xhr.status == 200 || xhr.status == 304)){
			InitSelect(xhr.responseText, 'businessSelect');
		}
	};
	
	xhr.open("post", "../InitSelectServlet", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send("serverId=5");
}
/**
 * 初始化地址下拉框
 */
function initAddressSelect(){
	var xhr = createXMLHttpRequest();
	xhr.onreadystatechange = function(){
		if((xhr.readyState == 4) && (xhr.status == 200 || xhr.status == 304)){
			InitSelect(xhr.responseText, 'address');
		}
	};
	
	xhr.open("post", "../InitSelectServlet", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send("serverId=9");
}

/**
 * 取得发送CRM侧的请求报文模板
 */
function getSendMesSelect(){
	var t = document.getElementById("businessSelect"); 
	var parentDir = t.options[t.selectedIndex].value;
	
	t = document.getElementById("mesSelect"); 
	var fileName = t.options[t.selectedIndex].value;
	
	var xhr = createXMLHttpRequest();
	xhr.onreadystatechange = function(){
		if((xhr.readyState == 4) && (xhr.status == 200 || xhr.status == 304)){
			window.document.getElementById("xmlhead").value =  xhr.responseText.substring(0, xhr.responseText.indexOf("_|body|_"));
			window.document.getElementById("xmlbody").value =  xhr.responseText.substring(xhr.responseText.indexOf("_|body|_")+8, xhr.responseText.length);
		}
	};
	
	xhr.open("post", "../InitSelectServlet", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send("serverId=7" + "&parentDir=" + parentDir + "&fileName=" + fileName);
}

/**
 * 取消功能
 */
function reset(){
	window.location.href=window.location.href;
}

/**
 * Ajax验证浏览器
 * @returns xmlhttp
 */
function createXMLHttpRequest() {
	var xmlhttp = null;
	
	try{
		xmlhttp = new XMLHttpRequest();
	}catch(e1){// IE7以下的浏览器以ActiveX组件的方式来获得XMLHttpRequest对象
		alert("error e1 = " + e1);
		try{
			xmlhttp = new ActiveXObject((navigator.userAgent.toLowerCase().indexOf('msie 5') != -1) ? 'Microsoft.XMLHTTP' : 'Msxml2.XMLHTTP');
		}catch(e2){
			alert("error e2 = " + e2);
		}
	}

	return xmlhttp;
}

/**
 * 取消功能
 */
function reset(){
	window.location.href=window.location.href;
}

//去左空格
function ltrim(s){   
	return s.replace( /^\s*/, "");
}

//去右空格
function rtrim(s){   
	return s.replace( /\s*$/, "");
}

function trim(s){
	return rtrim(ltrim(s));
}



