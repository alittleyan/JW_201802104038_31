package controller;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //读取参数
        String user = request.getParameter("user");
        String code = request.getParameter("code");
        //根据参数值，向客户端响应不同的信息
        PrintWriter out = response.getWriter();
        if ("xg".equals(user)&&"xg".equals(code)) {
            //创建JSON对象message，以便往前端响应信息
            JSONObject message = new JSONObject();
            try {
                message.put("message", "登陆成功");
                //响应message到前端
                response.getWriter().println(message);
            } catch (Exception e) {
                message.put("message", "网络异常");
                //响应message到前端
                response.getWriter().println(message);
            }
        }
        out.close();
    }
}
