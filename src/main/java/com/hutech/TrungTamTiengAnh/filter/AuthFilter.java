package com.hutech.TrungTamTiengAnh.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        HttpSession session = req.getSession(false);

        // 1. Allow public access
        if (uri.equals("/") ||
                uri.equals("/login") ||
                uri.equals("/register") ||
                uri.equals("/error") ||
                uri.equals("/favicon.ico") ||
                uri.startsWith("/css/") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/images/") ||
                uri.startsWith("/fonts/")) {

            chain.doFilter(request, response);
            return;
        }

        // 2. Require login
        if (session == null || session.getAttribute("user") == null) {
            res.sendRedirect("/login");
            return;
        }

        // 3. Role-based access
        Object userObj = session.getAttribute("user");
        if (userObj instanceof com.hutech.TrungTamTiengAnh.entity.User user) {
            String role = user.getRole() == null ? "" : user.getRole().trim().toUpperCase();
            if (uri.startsWith("/admin") && !"ADMIN".equals(role)) {
                res.sendRedirect("/login");
                return;
            }
            if (uri.startsWith("/student") && !"STUDENT".equals(role)) {
                res.sendRedirect("/login");
                return;
            }
            if (uri.startsWith("/teacher") && !"TEACHER".equals(role)) {
                res.sendRedirect("/login");
                return;
            }
        }

        // 4. Continue
        chain.doFilter(request, response);
    }
}



