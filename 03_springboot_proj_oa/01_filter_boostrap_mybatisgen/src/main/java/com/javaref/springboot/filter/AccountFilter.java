package com.javaref.springboot.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/***************************************************
 * 用户权限处理
 */

@Component                      // 使这个类对象可以被Spring注入
@WebFilter(urlPatterns = "/*")  // urlPatterns = "/*"： 所有url都要被Filter匹配
public class AccountFilter implements Filter {

    // 不需要Filter的URI
    private final String[] ignoreURIs = {
            "/index",  "/css/", "/js/", "/images/",
            "/account/login",
            "/account/logOut",
            "/account/validateAccount"
    };

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        // 查看哪些资源的访问，经过了这个Filter
        HttpServletRequest  httpReq  = (HttpServletRequest)req;
        HttpServletResponse httpResp = (HttpServletResponse)resp;
        String uri = httpReq.getRequestURI();
        System.out.println("....... filter ........ "  + uri);

        // 当前访问的URI是不是在ignore列表中
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
            // 跳转登录页面
            httpResp.sendRedirect("/account/login");
            return;
        }

        //    1.1 找到、放行
        //    1.2 找不到、
        //    1.3 当前访问的URI是不是在ignore列表中


        chain.doFilter(req, resp);
    }

    private boolean isInIgnoreList(String uri) {
        for (String ignore : ignoreURIs) {
            if (uri.startsWith(ignore)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 这里可以添加代码：
        //  加载Filter启动需要的资源
        //  打印启动日志等

        System.out.println("....... Account Filter init........");
        Filter.super.init(filterConfig);
    }
}
