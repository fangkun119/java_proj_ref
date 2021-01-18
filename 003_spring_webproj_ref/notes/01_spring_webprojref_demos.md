# Spring Web项目示例

[TOC]

##  01 拦截器及登录验证

代码位置：[../demos/01_filter_boostrap_mybatisgen/](../demos/01_filter_boostrap_mybatisgen/)

### 1.1 拦截器

> 实现拦截器之后，所有白名单以外的URI，都会被拦截器处理。拦截器检查 session中是否有登录时 生成 的account属性，如果没有则跳转到登录页面
>
> 代码：[/src/main/.../filter/AccountFilter.java](../demos/01_filter_boostrap_mybatisgen/src/main/java/com/javaref/springboot/filter/AccountFilter.java)
>
> ```java
> @Component  // 使这个类对象可以被Spring注入
> @WebFilter(urlPatterns = "/*" /*所有url都要被Filter匹配*/)
> public class AccountFilter implements Filter {
>     // doFilter方法执行过滤时，遇到白名单中的uri跳过不做处理
>     private final String[] IGNORE_URIS = {
>             "/index",  "/css/", "/js/", "/images/",  "/account/login", "/account/logOut", "/account/validateAccount"};
> 
>     private boolean isInIgnoreList(String uri) {
>         boolean isMatchIgnore = Stream.of(IGNORE_URIS).anyMatch((ignPrefix) -> uri.startsWith(ignPrefix));
>         return isMatchIgnore;
>     }
> 
>     @Override
>     public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
>             throws IOException, ServletException {
>         HttpServletRequest  httpReq  = (HttpServletRequest)req;
>         HttpServletResponse httpResp = (HttpServletResponse)resp;
> 
>         // 如果再忽略列表中，则跳过
>         String uri = httpReq.getRequestURI();
>         if (isInIgnoreList(uri)) {
>             chain.doFilter(req,resp);
>             return;
>         }
> 
>         // 从Session中找Account对象，看是否已经登录
>         // "account"是用户在访问"/validateAccount"时，被AccountController设置到session中的
>         Object account = httpReq.getSession().getAttribute("account");
>         System.out.println("get SessionAccount: " + account);
>         if (null == account) {
>             // 如果没有登录，则跳转登录页面
>             httpResp.sendRedirect("/account/login");
>             return;
>         }
> 
>         // 检查结束，放行，由下一个Filter（如果有）继续处理
>         chain.doFilter(req, resp);
>     }
>     
>     @Override
>     public void init(FilterConfig filterConfig) throws ServletException {
>         //  这里可以添加代码：加载Filter启动需要的资源，打印启动日志等
>         Filter.super.init(filterConfig);
>     }
> }
> ```

### 1.2  登录登出功能

> Controller：[/src/main/java/.../controller/AccountController.java](../demos/01_filter_boostrap_mybatisgen/src/main/java/com/javaref/springboot/controller/AccountController.java)
>
> 登录跳转：跳转到“account/login"页面
>
> ```java
> @Controller
> @RequestMapping("/account")
> public class AccountController {
>     @Autowired
>     AccountService accountSrv;
> 
>     // 登录跳转：跳转到“account/login"页面
>     @RequestMapping("login")
>     public String login() {
>         return "account/login";
>     }
> 
>     // 登出：在Session中删除account属性，然后跳转到首页 
>     @RequestMapping("/logOut")
>     public String logout(HttpServletRequest req) {
>         req.getSession().removeAttribute("account");
>         return "index";
>     }
>     
>     ...
> 
>     @RequestMapping("validateAccount")
>     @ResponseBody
>     public String validate(String loginName, String password, HttpServletRequest request) {
>         // 逻辑复杂或代码规范有要求时应当迁移到Service层中
>         Account account = accountSrv.findByLoginNameAndPassword(loginName, password);
>         if (null == account) {
>             return "fail";
>         }  else  {
>             // 把Account放到Session中 ，可以做更多事情，接口更加通用
>             request.getSession().setAttribute("account", account);
>             return "success";
>         }
>     }        
> }
> ```
>
> ”account/login"的`Thymeleaf模板`：[/src/main/resources/templates/account/login.html](../demos/01_filter_boostrap_mybatisgen/src/main/resources/templates/account/login.html)
>
> * 是一个使用了bootstrap做前端样式 ，jQuery做组件互动的页面
> * jQuery为登录按钮绑定了登录事件，将登录请求发送到"/account/validateAccount"
>
> ```javascript
> // 添加异步校验代码
> var url = "/account/validateAccount";
> var args = {loginName: loginName, password: password};
> $.post(url, args, function (data) {
>     if (data == "success") {
>         // 登录成功 跳转页面
>         window.location.href = "/";
>     } else {
>         // 登录失败，提示用户名密码错误
>         $(".tip").html("用户名或密码错误");
>         $(".tip").css("display", "block");
>     }
>     console.log(data)
> });
> ```
>
> 进而代码再次跳转到上面的`AccountController`中，它会调用service层对账号进行验证，并将登录成功的账号放入session

## 02 