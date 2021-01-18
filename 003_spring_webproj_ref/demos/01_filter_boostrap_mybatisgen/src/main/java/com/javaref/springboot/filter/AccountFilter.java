package com.javaref.springboot.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;

/***************************************************
 * 用户权限处理
 */

@Component  // 使这个类对象可以被Spring注入
@WebFilter(urlPatterns = "/*" /*所有url都要被Filter匹配*/)
public class AccountFilter implements Filter {
    // doFilter方法执行过滤时，遇到下列uri跳过不做处理
    private final String[] IGNORE_URIS = {
            "/index",  "/css/", "/js/", "/images/",  "/account/login", "/account/logOut", "/account/validateAccount"};

    private boolean isInIgnoreList(String uri) {
        boolean isMatchIgnore = Stream.of(IGNORE_URIS).anyMatch((ignPrefix) -> uri.startsWith(ignPrefix));
        return isMatchIgnore;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        // 执行过滤操作的URI
        HttpServletRequest  httpReq  = (HttpServletRequest)req;
        HttpServletResponse httpResp = (HttpServletResponse)resp;
        String uri = httpReq.getRequestURI();

        // 如果再忽略列表中，则跳过
        if (isInIgnoreList(uri)) {
            chain.doFilter(req,resp);
            // 不要忘记return，否则doFilter之后，还会继续后面的步骤
            return;
        }

        // 从Session中找Account对象，看是否已经登录
        // "account"是用户在访问"/validateAccount"时，被AccountController设置到session中的
        Object account = httpReq.getSession().getAttribute("account");
        System.out.println("get SessionAccount: " + account);
        if (null == account) {
            // 如果没有登录，则跳转登录页面
            httpResp.sendRedirect("/account/login");
            return;
        }

        // 检查结束，放行，由下一个Filter（如果有）继续处理
        chain.doFilter(req, resp);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //  这里可以添加代码：加载Filter启动需要的资源，打印启动日志等
        Filter.super.init(filterConfig);
    }
}
