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

### 1.2 登录登出功能

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

## 02 完整的分页器（前后端）

代码位置：[../demos/02_pagehelper_asynccrud_deploy/](../demos/02_pagehelper_asynccrud_deploy/)

功能：

* 完整的分页器
* BootStrap页面美化
* JQurey异步后端调用
* 部署方式（Jar包、War包）

### 2.1 分页器

> 在[`../../002_spring_boot/springboot_note/01_spring_boot_demos.md`](../../002_spring_boot/springboot_note/01_spring_boot_demos.md)中 如下章节的基础上继续开发
>
> 06 MyBatis：
>
> * 6.1项目搭建
>
> * 6.4 分页器（后端部分）

#### (1) 后端代码及返回数据

> Controller: [/src/main/java/.../controller/AccountController.java](../demos/02_pagehelper_asynccrud_deploy/src/main/java/com/javaref/springboot/controller/AccountController.java)
>
> 参数传递为：pageNum和pageSize，数据通过`Model model`返回给前端
>
> ```java
> @RequestMapping("/list")
> public String list(
>         @RequestParam(defaultValue = "1") int pageNum,
>         @RequestParam(defaultValue = "5") int pageSize,
>         Model model) {
>     PageInfo<Account> page = accountSrv.findByPage(pageNum, pageSize);
>     model.addAttribute("page", page); // 提供给前端页面的数据
>     return "/account/list"; // thymeleaf模板的路径
> }
> ```
>
> Service：[/src/main/.../service/AccountService.java](../demos/02_pagehelper_asynccrud_deploy/src/main/java/com/javaref/springboot/service/AccountService.java)
>
> ```java
> public PageInfo<Account> findByPage(int pageNum, int pageSize) {
>     // 设置AOP，它会影响底层数据库访问的操作
>     PageHelper.startPage(pageNum, pageSize);
> 
>     // 调用DAO层返回数据
>     AccountExample example = new AccountExample();
>     List<Account> accountList = accountMapper.selectByExample(example);
> 
>     // 构造PageInfo时，设置分页器只显示临近的5页（默认是8页）
>     return new PageInfo<>(accountList, 5);
> 
>     // 封装在PageInfo中的数据（也可以看出分页器API的设计思路）
>     // pageNum, pageSize, size, startRow, endRow, pages
>     // prePage, nextPage, isFirstPage, isLastPage,
>     // hasPreviousPage，hasNextPage，navigatePages，
>     // navigatepageNums, navigateFirstPage, pnavigateLastPage
> }
> ```

#### (2) 前端显示（thymeleaf + bootstrap）

> ```html
> <nav aria-label="Page navigation">
>     <ul class="pagination">
>         <!-- 上一页链接 -->
>         <!-- 在 https://v3.bootcss.com/components/#pagination 查到使用哪个css class，可以显式屏蔽提示符 -->
>         <!-- 在 thymeleaf 文档上查到如何只在没有上一页时，屏蔽这个链接 -->
>         <li th:class="${page.prePage} == 0 ? 'disabled' : ''">
>             <!-- 同理，在没有上一页时，不仅要显式屏蔽提示符，还要屏蔽页面链接      -->
>             <!-- 相比使用'#'屏蔽，使用javascript:void(0);可以阻止页面滚动回页头  -->
>             <a aria-label="Previous"
>                th:href="@{${page.prePage} == 0 ? 'javascript:void(0);' : '/account/list?pageNum=' + ${page.prePage}}">
>                 <span aria-hidden="true">&laquo;</span>
>             </a>
>         </li>
>         <!-- 临近页链接  -->
>         <li th:each="pageNum : ${page.navigatepageNums}">
>             <a th:href="@{'/account/list?pageNum=' + ${pageNum}}">[[${pageNum}]]</a>
>         </li>
>         <!-- 下一页链接 -->
>         <li th:class="${page.nextPage} == 0 ? 'disabled' : ''">
>             <a aria-label="Next"
>                th:href="@{${page.nextPage} == 0 ? 'javascript:void(0);':'/account/list?pageNum=' + ${page.nextPage}}">
>                 <span aria-hidden="true">&raquo;</span>
>             </a>
>         </li>
>     </ul>
> </nav>
> ```

### 2.2 JQuery异步调用后端

> ```javascript
> function deleteById(id) {
>     // 查看是否能调用到这个方法
>     console.log("id: " + id)
>     // showTip("haha", 5000, function() {});
>     // showMsg("haha", function() {});
>     showConfirm("确认要删除吗？", function () {
>         // 确认删除
>         var url = "/account/deleteById";
>         var args = {id: id};
>         // data：调用“/account/deleteById”之后，由后端Controller返回的RESPStat对象序列化成json后得到的数据
>         $.post(url, args, function (data) {
>             console.log(data)
>             if (data.code == 200) {
>                 //删除成功，刷新页面
>                 window.location.reload();
>             } else {
>                 alert(data.msg)
>             }
>         });
>     });
> }
> ```

### 2.3 前端页面美化

代码：

> * [/src/main/resources/templates/account/list.html](../demos/02_pagehelper_asynccrud_deploy/src/main/resources/templates/account/list.html)
> * [/src/main/resources/templates/account/login.html](../demos/02_pagehelper_asynccrud_deploy/src/main/resources/templates/account/login.html)
> * [/src/main/resources/templates/account/profile.html](../demos/02_pagehelper_asynccrud_deploy/src/main/resources/templates/account/profile.html)

参考：

> * Thymelef可参考本repo的其他文档，或官方文档、或《Spring In Action》
>
> * Bootstrap 现用现查参考官方文档的组件示例即可
>
> *  JQuery（过时了），简单翻一下《锋利的JQuery》就可以

### 2.4 部署方式

#### (1) Jar包部署方式（默认）

> ~~~bash
> # 生成内置tomcat可以直接运行的jar包
> mvn install 
> ~~~

#### (2) 老式war包部署方式（很少会使用）

> 1 在`<build></build>`子标签内，添加`<packaging>war</packaging>`
>
> 2 引入`spring-boot-starter-tomcat`依赖并让它在packaging阶段被exclude，避免tomcat相关的包被编译到war包中（而是在运行时由外部设施的tomcat来提供）
>
> ~~~xml
> <dependency>
> 	<groupId>org.springframework.boot</groupId>
>     <artifactId>spring-boot-starter-tomcat</artifactId>
>     <scope>provided</scope>
> </dependency>
> ~~~
>
> 3 修改入口类（拥有main函数的那个类），让它继承`SpringBootServletInitializer`，并重写configure方法，这样它可以在外部的tomcat启动时被调用注解
>
> ```java
> public class ServletInitializer extends SpringBootServletInitializer {
>    @Override
>    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
>       //此处的Application.class为带有@SpringBootApplication注解的启动类
>       return builder.sources(Application.class);
>    }
> }
> ```
>
> 4 `mvn install`，构建之后就会构建一个war包

## 03 项目配置

内容

> 使用`.yaml`编写项目配置
>
> 为开发、测试、生产环境指定不同的配置

代码位置：[../demos/03_yaml_projconf/](../demos/03_yaml_projconf/)

默认配置

> [/src/main/resources/application.properties](../demos/03_yaml_projconf/src/main/resources/application.properties)
>
> [/src/main/resources/application.yaml](../demos/03_yaml_projconf/src/main/resources/application.yaml)

开发、测试、生产环境配置

> [/src/main/resources/application-dev.properties](../demos/03_yaml_projconf/src/main/resources/application-dev.properties)
>
> [/src/main/resources/application-test.properties](../demos/03_yaml_projconf/src/main/resources/application-test.properties)
>
> [/src/main/resources/application-prod.properties](../demos/03_yaml_projconf/src/main/resources/application-prod.properties)

指定哪个配置文件生效

> ```properties
> # 指定哪个配置文件被激活:
> #   applicaiton-dev.properties
> #   applicaiton-prod.properties
> #   applicaiton-test.properties
> spring.profiles.active=dev
> ```

## 04 MyBatis多表访问

代码位置：[../demos/04_mybatis_multi_tables_ajaxcrud/](../demos/04_mybatis_multi_tables_ajaxcrud/)

> DAO：[/src/main/java/.../mapper/AccountMapper.java](../demos/04_mybatis_multi_tables_ajaxcrud/src/main/java/com/javaref/springboot/mapper/AccountMapper.java)
>
> ```java
> @Repository
> public interface AccountMapper extends MyBatisBaseDao<Account, Integer, AccountExample> {
>    // 在AccountMapper中创建selectByPermission方法
>    // 然后再到AccountMapper.xml中配置这个方法所有对应的SQL，由MyBatis来生成具体实现，包括
>    //    <selectByPermissionResultMap>
>    //    <select id="selectByPermission" resultMap="selectByPermissionResultMap">
>    List<Account> selectByPermission();
> }
> ```
>
> MyBatis配置：[/src/main/resources/mybatis/mapper/AccountMapper.xml](../demos/04_mybatis_multi_tables_ajaxcrud/src/main/resources/mybatis/mapper/AccountMapper.xml)
>
> ```xml
> <!--  自定义的ResultMap：包含一对多关系roleList和permissionList -->
> <resultMap type="com.javaref.springboot.entity.Account" id="selectByPermissionResultMap">
>     <!-- account表部分，注意重名字段要起别名 -->
>     <id column="aid" jdbcType="INTEGER" property="id"/>
>     <result column="login_name" jdbcType="VARCHAR" property="loginName"/>
>     <result column="password" jdbcType="VARCHAR" property="password"/>
>     <result column="nick_name" jdbcType="VARCHAR" property="nickName"/>
>     <result column="age" jdbcType="INTEGER" property="age"/>
>     <result column="location" jdbcType="VARCHAR" property="location"/>
>     <result column="role" jdbcType="VARCHAR" property="role"/>
>     <!--
>         role表部分，注意重名字段要起别名
>         * association 用于1:1映射； collection用于1:N映射
>         * property="roleList" 表示联表得到的数据存储到 Account.roleList 属性中
>         * ofType="...Role" 表示联表得到的数据对应的类型是"...Role"
>         为了避免出错，标签中的内容可以从RoleMapper.xml中拷贝一部分
>     -->
>     <collection property="roleList" ofType="com.javaref.springboot.entity.Role">
>         <id column="rid" jdbcType="INTEGER" property="id"/>
>         <result column="rname" jdbcType="VARCHAR" property="name"/>
>     </collection>
>     <!-- permission表部分，配置方法与role表部分类似，注意重名字段要起别名 -->
>     <collection property="permissionList" ofType="com.javaref.springboot.entity.Permission">
>         <id     column="pid" jdbcType="INTEGER" property="id"/>
>         <result column="uri" jdbcType="VARCHAR" property="uri"/>
>         <result column="pname" jdbcType="VARCHAR" property="name"/>
>         <result column="c" jdbcType="BIT" property="c"/>
>         <result column="r" jdbcType="BIT" property="r"/>
>         <result column="u" jdbcType="BIT" property="u"/>
>         <result column="d" jdbcType="BIT" property="d"/>
>     </collection>
> </resultMap>
> <!-- 自定义select语句，对应AccountMapper.selectByPermission()方法 -->
> <!--
>     所有重名的列，都必须其别名（例如下面的aid，rid，rname，pid，pname），ResultMap也要对应着修改
>     否则会出现select时只能select出来一条之类的奇怪问题
> -->
> <select id="selectByPermission" resultMap="selectByPermissionResultMap">
>    SELECT
>       a.id          as aid,
>       a.login_name, a.password, a.location,
>       r.id          as rid,
>       r.name        as rname,
>       p.id          as pid,
>       p.uri, p.c, p.u, p.d, p.r,
>       p.name        as pname
>     FROM account as a
>     inner join  account_role    as ar   on a.id = ar.account_id
>     inner join  role            as r    on ar.role_id = r.id
>     left join   role_permission as rp   on r.id = rp.role_id
>     left join   permission      as p    on p.id = rp.permission_id
> </select>
> <!-- 自定义部分结束 -->
> ```

## 05 Restful Controller

### 5.1 Restful Controller

代码位置

> [../demos/05_fileupload_restful_relationtable_allfeatures/](../demos/05_fileupload_restful_relationtable_allfeatures/)

Controllers

> [/src/main/.../controller/rest/PermissionManagerRestController.java](../demos/05_fileupload_restful_relationtable_allfeatures/src/main/java/com/javaref/springboot/controller/rest/PermissionManagerRestController.java)
> [/src/main/.../controller/rest/RoleManagerRestController.java](../demos/05_fileupload_restful_relationtable_allfeatures/src/main/java/com/javaref/springboot/controller/rest/RoleManagerRestController.java)

发送请求的的HTML和JQuery代码（以为“/api/v1/manager/permission/permission/add”例）

> ```html
> <form id="permissionForm">
>     <div>角色名称：<input type="text" name="name" th:value="${role}==null?'':${role.name}" disabled="true"></div>
>     <br/>
>     <div>角色ID：<input type="text" name="id" th:value="${role}==null?'':${role.id}"></div>
>     <br/>
>     <div>允许权限：</div>
>     <br/>
>     <!-- 先在Chrome DevTools上调好样式，再改下面的代码 -->
>     <div style="display:flex;">
>         <!-- 遍历后端设置到Model中的"pList":List<Permission>，为每个permission生成一个<span>标签 -->
>        <span style="padding-right:5px" th:each="permission:${pList}">
>             <!--
>                 为每个Persmission生成一个checkbox控件
>                 具体的代码，来自 http://icheck.fronteed.com/ 上Line skin的usage
>                 <input type="checkbox">
>                 <label>Checkbox 1</label>
>                 <input type="checkbox" checked>
>                 <label>Checkbox 2</label>
>             -->
>           <input type="checkbox" th:id="${permission.id}" name="permissions" th:value="${permission.id}">
>             <label>[[${permission.name}]]</label>
>        </span>
>     </div>
>     <br/>
> </form>
> <!-- 提交按钮 -->
> <a class="btn btn-default" href="javascript:add();">添加</a>
> 
> ...
> 
> <!-- 编写给角色设置权限的异步调用函数 -->
> <script type="text/javascript">
>     function add() {
>         var data = $("#permissionForm").serializeArray();
>         console.log(data);
>         console.log(JSON.stringify(data));
>         $.post("/api/v1/manager/role/permission/add", data, function (data) {
>             if (data != '1') {
>                 alert(data.message)
>             }
>             console.log(data)
>         })
>     }
> </script>
> ```

