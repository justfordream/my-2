/**
 * @date 20130224
 * @author panliguan
 * Ajax发送报文
 */

window.onload = function(){
	initBusinessSelect();
	initAddressSelect();
	window.document.getElementById("result").innerText = "";
	window.document.getElementById("receiveMsg").innerText = "";
};


/**
 * 
 */
function send(){
	var t = document.getElementById("address"); 
	var address = t.options[t.selectedIndex].value;
	var sendMsg = window.document.getElementById("sendMsg").value;

	if(address == null || address == "" || address == undefined){
		window.document.getElementById("result").innerHTML = "<font color='red'>地址不能为空！</font>";
		return;
	}
	if(sendMsg == null || sendMsg == "" || sendMsg == undefined){
		window.document.getElementById("result").innerHTML = "<font color='red'>消息报文不能都为空！</font>";
		return;
	}
	
	sengMsg(address, sendMsg);
}

function sengMsg(address, sendMsg){
	var xhr = createXMLHttpRequest();
	xhr.onreadystatechange = function(){
		if((xhr.readyState == 4) && (xhr.status == 200 || xhr.status == 304)){
			printMsg(xhr.responseText);
		}
	};
	
	xhr.open("post", "../httpPostMsgServlet", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send("address=" + address + "&sendMsg=" + sendMsg);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
	xhr.send("serverId=16");
}

/**
 *	初始化发送地址选项 
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
	xhr.send("serverId=15");
}

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
	xhr.send("serverId=14" + "&directoryName=" + directoryName);
}

/**
 * 取得发送银行侧的请求报文模板
 */
function getSendMesSelect(){
	
	var t = document.getElementById("businessSelect"); 
	var parentDir = t.options[t.selectedIndex].value;
	
	t = document.getElementById("mesSelect"); 
	var fileName = t.options[t.selectedIndex].value;
	
	var xhr = createXMLHttpRequest();
	xhr.onreadystatechange = function(){
		if((xhr.readyState == 4) && (xhr.status == 200 || xhr.status == 304)){
			window.document.getElementById("sendMsg").value =  xhr.responseText;
		}
	};
	
	xhr.open("post", "../InitSelectServlet", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send("serverId=17" + "&parentDir=" + parentDir + "&fileName=" + fileName);
}

/**
 * 显示接收报文结果
 */
function printMsg(result){
	//window.document.getElementById("receiveMsg").innerHTML = result;
	window.document.getElementById("receiveMsg").value = result;
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

