package com.huateng.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.huateng.vo.CmuData;

/**
 * 网关返回省中心工具类
 * 
 * @author 马博阳
 * 
 */
public class WebBackToMer {

    /**
     * 根据参数和请求地址、签名信息得到请求对象
     * 
     * @param params
     *            参数
     * @param url
     *            请求地址
     * @param sign
     *            签名信息
     * @return
     */
	public static String getBackResult(Map<String, String> params, String url,boolean isPost) {

        StringBuffer parameter = new StringBuffer("<html>\n");

        parameter.append("<head>\n");
        
        parameter.append("<title>Title for Page</title>\n");
        
        parameter.append("</head>\n");
        
        parameter.append("<body>\n");

        List<String> keys = new ArrayList<String>(params.keySet());

        parameter.append("<form id=\"requestPay\" name=\"requestPay\" action=\"" + url + "\" method=\""+(isPost?"post":"get")+"\">\n");

        for (int i = 0; i < keys.size(); i++) {

            String name = keys.get(i);

            String value = (String) params.get(name);

            parameter.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>\n");

        }

        parameter.append("<script type=\"text/javascript\" >\n");
        
        parameter.append("<!--\n");
        
        parameter.append("document.requestPay.submit();\n");
            
        parameter.append("//-->\n");
        
        parameter.append("</script>\n");
        
        parameter.append("</body>\n");
        
        parameter.append("</html>\n");

        return parameter.toString();
    }
    

    public static String getSignErrorResult(CmuData cmuData , String url){
        
        StringBuffer parameter = new StringBuffer("<html>\n");

        parameter.append("<head>\n");
        
        parameter.append("<title>Title for Page</title>\n");
        
        parameter.append("</head>\n");
        
        parameter.append("<body>\n");
        
        parameter.append("<form id=\"requestPay\" name=\"requestPay\" action=\"" + url + "\" method=\"post\">\n");
        
        parameter.append("<input type=\"hidden\" name=\"SessionID\" value=\"" + cmuData.getOrderId() + "\"/>\n");
        
        parameter.append("<input type=\"hidden\" name=\"Result\" value=\"" + cmuData.getStatus() + "\"/>\n");
        
        parameter.append("<input type=\"hidden\" name=\"Desc\" value=\"" + cmuData.getError() + "\"/>\n");
        
        parameter.append("<input type=\"hidden\" name=\"BankID\" value=\"" + cmuData.getBankID() + "\"/>\n");
        
        parameter.append("</form>");
        
        parameter.append("<SCRIPT type=\"text/javascript\" >\n");
        
        parameter.append("<!--\n");
        
        parameter.append("document.requestPay.submit();\n");
            
        parameter.append("//-->\n");
        
        parameter.append("</SCRIPT>\n");
        
        parameter.append("</body>\n");
        
        parameter.append("</html>\n");
        
        return parameter.toString();
        
    }
//    /**
//     * 后台通知请求
//     * 
//     * @param result
//     *            请求信息
//     */
//    public static void NoticeBackCmu(final String formUrl,final Map<String,String> params) {
//        
//        Runnable runnable = new Runnable() {
//            
//            public void run() {
//            	HttpClientUtil.formSubmit(formUrl, params, "UTF-8");
//            }
//        };
//        
//        AsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
//        
//        executor.submit(runnable);
//        
//    }
}
