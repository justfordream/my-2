/**
 * panlg
 * 2013-03-03
 */

window.onload = function(){
	GetAllReceiveMsg();
}

/**
 * 获取当天所有接收到CRM的报文记录
 */
function GetAllReceiveMsg(){
	var Obj = document.getElementById("serverID");
	var serverID = Obj.options[Obj.selectedIndex].value;

	var xhr = createXMLHttpRequest();
	xhr.onreadystatechange = function(){
		if((xhr.readyState == 4) && (xhr.status == 200 || xhr.status == 304)){
			printMsg(xhr.responseText);
		}
	}
	
	xhr.open("post", "../consultReceiveMsg", true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.send("serverID=" + serverID);
}

/**
 * 显示结果
 */
function printMsg(result){
	//window.document.getElementById("receiveMsg").innerHTML = result;
	window.document.getElementById("getMsg").value = result;
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

