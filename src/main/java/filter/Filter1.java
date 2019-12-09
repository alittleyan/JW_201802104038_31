package filter;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebFilter(filterName = "Filter1",urlPatterns ={"/*"} )/*通配符（*）表示对所有的web资源有效*/
public class Filter1 implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String dateTime = df.format(date); // Formats a Date into a date/time string.

        String path = request.getRequestURI();

        if (!path.contains("/login")){
            if ( request.getMethod() == "POST"||request.getMethod() =="PUT"){
                request.setCharacterEncoding("UTF-8");
                response.setContentType("text/html;charset=UTF-8");
            }else {
                response.setContentType("text/html;charset=UTF-8");
            }
        }
        System.out.println("请求的资源为：" + path + " @ "+ dateTime);
        System.out.println("Filter1 begins");
        System.out.println("Filter1 end");
        filterChain.doFilter(servletRequest,servletResponse);//执行其他的过滤器，如过滤器已经执行完毕，则执行原请求
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
