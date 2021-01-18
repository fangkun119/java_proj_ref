package com.javaref.springboot.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;

@Component
@WebFilter(urlPatterns = "/*")
public class AccountFilter implements Filter {
    // 不需要登录的 URI
    private final String[] IGNORE_URI = {"/index", "/account/validataAccount", "/css/", "/js/", "/account/login", "/images"};

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String uri = request.getRequestURI();

        // 当前访问的URI是不是在Ignore列表里
        System.out.println("uri:" + uri);
        boolean pass = canPassIgnore(uri);
        if (pass) {
            chain.doFilter(request, response);
            return;
        }

        // 是否已登录，从session里找 Account
        Object account = request.getSession().getAttribute("account");
        System.out.println("getSession account:" + account);
        if (null == account) {
            // 没登录 跳转登录页面
            response.sendRedirect("/account/login");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean canPassIgnore(String uri) {
        if (Stream.of(IGNORE_URI).anyMatch(prefix -> uri.startsWith(prefix))) {
            return true;
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }
}
