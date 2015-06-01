var req = {
		req_k1:"k1"
};

var res = {
		res_k2:"k2"
};

function getReq(key){
	return req["req_" + key];
}

function getRes(key){
	return res["res_" + key];
}