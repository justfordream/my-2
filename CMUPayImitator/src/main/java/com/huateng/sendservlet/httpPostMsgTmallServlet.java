package main.java.com.huateng.sendservlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;



/**
 * chenjun
 * 主动请求其它接口的参数流写入(POST方式)
 * 输入：http地址端口等
 */
public class httpPostMsgTmallServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Logger log = Logger.getLogger(httpPostMsgTmallServlet.class);
	
    public httpPostMsgTmallServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
    		res.getWriter().print(request.getParameter("xmldata"));
    		res.getWriter().close();
    		log.debug("recieve msg:"+request.getParameter("xmldata"));
        }

	}


