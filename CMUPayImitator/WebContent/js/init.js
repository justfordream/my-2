/**
 * 页面初始化使用
 * 20130423
 * panliguan
 */




//初始化页面的select控件
function CreateOption(value, text){
	var option = document.createElement("option");
	option.value = value;
	option.innerHTML = text;
	return option;
}
function InitSelect(selectCode, name){
	var select = window.document.getElementById(name);
	select.options.length = 1;		//每次加载下拉框前，首先清除掉之前加载的下拉框选项，否则下拉框会累加
	
	var selectList = eval(selectCode);
	for(var j = 0; j < selectList.length; j++){
		var option = CreateOption(selectList[j][0], selectList[j][1]);
		select.appendChild(option);
	}
	
}