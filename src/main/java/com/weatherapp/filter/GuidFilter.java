package com.example.weatherapp.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.UUID;

public class GuidFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        if (req instanceof HttpServletRequest) {
            HttpSession session = ((HttpServletRequest) req).getSession(true);
            Object guid = session.getAttribute("GUID");
            if (guid == null) {
                session.setAttribute("GUID", UUID.randomUUID().toString());
            }
        }
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {}
}
