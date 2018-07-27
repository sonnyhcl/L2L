package supplierA.conf;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SuppressWarnings("all")
public class MyCorsFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(MyCorsFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        // TODO Auto-generated method stub
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        String hostOrigin = request.getHeader("Origin");
        logger.debug(request.getHeader("Authorization")+"--"+request.getHeader("Host")+"--"+request.getHeader("Origin"));
        //TODO : evaulate whether the hostOrigin is valid
        if(hostOrigin == null){
            hostOrigin = "http://"+request.getHeader("Host");
            logger.debug("reset Host origin : "+hostOrigin);

        }
        response.setHeader("Access-Control-Allow-Origin", hostOrigin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods",
                "ACL, CANCELUPLOAD, CHECKIN, CHECKOUT, COPY, DELETE, GET, HEAD, LOCK, MKCALENDAR, MKCOL, MOVE, OPTIONS, POST, " +
                        "PROPFIND, PROPPATCH, PUT, REPORT, SEARCH, UNCHECKOUT, UNLOCK, UPDATE, VERSION-CONTROL");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Key, Authorization");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            //	System.out.println("\033[31;1m遇到跨域请求，MyCorsFilter正在审核......\033[0m");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }


}