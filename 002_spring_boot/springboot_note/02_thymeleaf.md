# Thymeleaf使用速查

[TOC]

## 01 概要

官方文档

> [https://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html](https://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html)

使用

> thymeleaf模板中引入`<html xmlns:th=*"http://www.thymeleaf.org"*>`
>
> 在后端代码中向model传入数据，在模板中标记要使用的数据，模板引擎会将其解析成HTML页面，例如
>
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/002_spring_boot/spring_thymeleaf.jpg)

例子

> [../demos/07_spring_boot_mvc_thymeleaf_jpa_bootstrap/src/main/resources/templates/register.html](../demos/07_spring_boot_mvc_thymeleaf_jpa_bootstrap/src/main/resources/templates/register.html)
>
> [../demos/07_spring_boot_mvc_thymeleaf_jpa_bootstrap/src/main/java/.../controller/MainController.java](../demos/07_spring_boot_mvc_thymeleaf_jpa_bootstrap/src/main/java/com/javaref/springbootmvc/springbootmvc03/controller/MainController.java)

以下是如何在thymeleaf文档中做各种标记

## 02 URL地址`@{...}`

### (1) 指向基于context-path的相对路径

> 例如当`context-path`配置为`/thymeleaf/user`时
>
> ~~~properties
> server.servlet.context-path=/thymeleaf/user
> ~~~
>
> * `@{userList}` 相对当前路径结果为：http://localhost/thymeleaf/user/userList
> * `@{./userList}` 相对当前路径结果为：http://localhost/thymeleaf/user/userList
> * `@{../tiger/home}` 相对当前路径结果为：http://localhost/thymeleaf/tiger/home

### (2) 指向绝对路径

> * `@{/tiger/home}` 相对应用根目录结果为：http://localhost/thymeleaf/tiger/home
> * `<link type="text/css" rel="stylesheet" th:href="@{/css/home.css}">`：`@` 以` "/"` 开头相对于应用的根目录

## 03 `th:href`

> ~~~html
> <body>
>     <a th:href="@{userList(id=9527)}">1、@{userList(id=9527)}</a>
>     <a th:href="@{userList(id=9527,name=华安)}">2、@{userList(id=9527,name=华安)}</a>
>     <a th:href="@{userList(id=9527,name=${userName})}">3、@{userList(id=9527,name=${userName})}</a>
> </body>
> ~~~

## 04 文本字面

> ~~~html
> <!--th:text用于文本替换，将该标签显示内容8替换为80 -->
> <p th:text="80">8</p>
> 
> <!-- 空格属于特殊字符，必须使用单引号包含整个字符串 -->
> <!-- 例如下面赋给的th:class和th:text的值-->
> <p class="css1 css2" th:class="'css1 css2'">样式</p>
> <p th:text="'Big China'">中国</p>
>  
> <!-- 上面的限制仅先于模板中的空格字面，与变量值里面的空格无关 -->
> <!-- 后端使用model.addAttribute("userName", "Lucy Zhang");返回数据 -->
> <p th:text="${userName}">userName</p>
> 
> <!-- 下面的例子体现出何时应当把空格包裹在单引号中，何时不用包裹 -->
> <!-- 使用 + 进行字符串连接-->
> <p th:text="'How are you, ' + ${userName} + '?'">userName</p>
> <p th:text="'small smile'+',very good.' + ${userName}">浅浅的微笑</p>
> ~~~

## 05 数字计算

> ~~~html
> <!--计算结果为 16 在进行替换-->
> <p th:text="8+8">8 + 8</p>
> <!--前面 8+8 计算结果为 16，然后字符串拼接上 Love，后面的 9+9也会被当做字符串拼接-->
> <p th:text="8+8+' Love '+9+9">8 + 8+' Love '+9+9</p>
> <!--前面 8+8 计算结果为 16，后面的 9+9因为有括号,所以也会计算结果，最后拼接 Love 字符串-->
> <p th:text="8+8+' Love '+(9+9)">8 + 8+' Love '+(9+9)</p>
> <!--后台传了一个：model.addAttribute("age", 35);取得结果后在进行计算-->
> <p th:text="100-${age}"></p>
> <!--其他-->
> <p th:text="15 * 4">值为 60 </p>
> <p th:text="15 * 4-100/10">值为 50 </p>
> <p th:text="100 % 8">值为 4</p>
> ~~~

## 06 boolean判断

> ~~~html
> <p th:text="true">布尔</p>
> 
> <!--true、false 是布尔值，and 是布尔运行符，and(与)，or(或)，not(非)、!(非)-->
> <p th:text="true and false">true and true</p>
>  
> <!--后台使用 model.addAttribute("isMarry", true); 传了值-->
> <!--th:if 表达式为 true，则显示标签内容，否则不显示-->
> <p th:if="${isMarry}">已结婚</p>
>  
> <!--后台传值：model.addAttribute("age", 35);-->
> <!--比较运算符：&gt;，&lt;，&gt; =，&lt;=（gt，lt，ge，le）-->
> <p th:if="${age}&gt;18">已成年</p>
> <p th:if="${age}&lt;18">未成年</p>
> ~~~

## 07 比较

> ~~~html
> <p th:if="5>3">5 大于 3</p>
> <p th:if="5 &gt;4">5 大于 4</p>
> <p th:if="10>=8 and 7 !=8">10大于等于8，且 7 不等于 8 </p>
> <p th:if="!${isMarry}">!false</p>
> <p th:if="not(${isMarry})">not(false)</p>
> ~~~

## 08 三元运算符

> ~~~html
> <p th:text="7&gt;5?'7大':'5大'">三元运算符</p>
> <!--后台控制器输出了：model.addAttribute("age", 35);-->
> <!--因为 ${xx}取值时，如果值为null，则默认整个标签不再显示-->
> <p th:text="${age}!=null?${age}:'age等于 null'"></p>
> <!--这里使用嵌套判断，嵌套的部分要使用括号-->
> <p th:text="${age}!=null?(${age}>=18?'成年':'未成年'):'age等于 null'"></p>
> <!--变量 age2 后台并没有输出，所以 age2 不存在，此时 age2 ==null-->
> <p th:text="${age2}!=null?${age2}:'age2等于 null'"></p>
>  
> <!--后台输出了：model.addAttribute("isMarry", true);-->
> <!--A>B?X:Y，这里的 Y 部分是省略的，此时如果 A>B 返回 false，则整个三元运算结果为 null-->
> <p th:class="${isMarry}?'css2':'css3'">已婚</p>
> ~~~

## 09 `th:utext`, `th:text`

> ~~~html
> <!--后端数据：map.addAttribute("china", "<b>Chian</b>,USA,UK"); -->
> <!--th:text用于文本替换，默认会进行转义 -->
> <p th:text="${china}">默认转义</p>
> <!--th:utext用于文本替换，但不会结果进行转义-->
> <p th:utext="${china}">不会转义</p>
> ~~~

## 10 设置属性

> `th:attr="${name}={val},..."`, `th:${name}=${val}`
>
> HTML5的所有属性，都可以th:*的形式进行设置
>
> ~~~html
> <!-- 设置单个属性 -->
> <a href="http://baidu.com" th:attr="title='百度'">百度</a>
> 
> <!--设置 title、href 多个属性-->
> <a href="" th:attr="title='前往百度',href='http://baidu.com'">前往百度</a>
> 
> <!--设置 href 属性-->
> <a href="userList.html" th:attr="href=@{/user/userHome}">用户首页</a>
> 
> <!--设置 id 属性，data-schoolName 属性 Html 本身是没有的，但允许用户自定义 -->
> <a href="#" th:attr="id='9527',data-target='user'">归海一刀</a>
> 
> <!--输出：<p abc123="华安">th:abc123="华安"</p>-->
> <p th:abc123="华安">th:abc123="华安"</p>
> ~~~

## 11 勾选框，下拉列表

> ~~~html
> <!--纯html-->
> <input type="checkbox" name="option1" checked/><span>是否已婚1？</span>
> <input type="checkbox" name="option2" checked="checked"/><span>是否已婚2？</span>
> 
> <!--后端Controller传递：model.addAttribute("isMarry", true);-->
> <!--option3、option4 会选中；option5 不会选中-->
> <input type="checkbox" name="option3" th:checked="${isMarry}" /><span>是否已婚3？</span>
> <input type="radio"    name="option4" th:checked="${isMarry}" /><span>是否本科？</span>
> <input type="radio"    name="option5" th:checked="!${isMarry}"/><span>是否应届生？</span>
> 
> <!--选择框-->
> <select>
> 	<option th:selected="${isMarry}">已婚</option>
> 	<option th:selected="${!isMarry}">未婚</option>
> </select>
> 
> <!-- autofocus -->
> <input type="text" th:autofocus="false">
> <input type="text" th:autofocus="true">
> <input type="text" th:autofocus="false">
> ~~~

## 12 日期格式化

> ~~~html
>  <span th:text="${#dates.format(date, 'yyyy-MM-dd HH:mm')}"></span>
> ~~~

## 13 循环

> JSTL 有一个 `<c:foreach>`，同理 Thymeleaf 也有一个`th:each`
>
> 作用都是一样的，都是用于遍历数组、List、Set、Map 等数据

### (1) 与select一起使用

> ~~~html
> <option th:each="city : ${list}" th:text="${city.name}" th:selected="${cityName} eq ${city.name}">横岗</option>
> ~~~

### (2) 状态变量（loopStatus）

> ~~~html
> <tr th:each="city,status : ${list}" th:style="${status.odd}?'background-color:#c2c2c2'">
> 	<td th:text = "${status.count}"></td>
> 	<td th:text = "${city.id}"></td>
> 	<td th:text = "${city.name}"></td>
> </tr>
> ~~~
>
> 如果不指定 为变量+Stat
>
> * index: 当前迭代对象的index（从0开始计算） 
> * count: 当前迭代对象的index(从1开始计算)  
> * size: 被迭代对象的大小   current:当前迭代变量  
> * even/odd: 布尔值，当前循环是否是偶数/奇数（从0开始计算） 
> * first: 布尔值，当前循环是否是第一个  
> * last: 布尔值，当前循环是否是最后一个

## 14 `if / else`

> ~~~html
> <p th:if="${isMarry}">已婚1</p>
> <p th:unless="${isMarry}">未婚</p>
> ~~~

## 15 `swich / case`多条件判断

> ~~~html
> <div th:switch="1">
>     <p th:case="0">管理员</p>
>     <p th:case="1">操作员</p>
>     <p th:case="*">未知用户</p>
> </div>
>  
> <!--数字类型：当没有 case 匹配时，取默认值，当有多个匹配，只取第一个-->
> <div th:switch="-1">
>     <p th:case="0">管理员</p>
>     <p th:case="*">操作员</p>
>     <p th:case="*">未知用户</p>
> </div>
>  
> <!--布尔类型，多个case满足时，只取第一个-->
> <div th:switch="${isMarry}">
>     <p th:case="true">已婚</p>
>     <p th:case="true">已成年</p>
>     <p th:case="false">未婚</p>
> </div>
>  
> <!--字符串类型-->
> <div th:switch="'For China'">
>     <p th:case="'For USA'">美国</p>
>     <p th:case="'For UK'">英国</p>
>     <p th:case="'For China'">中国</p>
>     <p th:case="*">未知国籍</p>
> </div>
> ~~~

## 16 内联表达式

> ~~~html
> <p>[[${china}]]</p>
> <p>[(${china})]</p>
> <p>[[Lo1ve]]</p>
> <p>[['I Love You Baby']]</p>
> <p>[(9527)]</p>
> ~~~
>
> [[...]] 等价于 th:text（结果将被 HTML 转义）
>
> [(...)] 等价于 th:utext（结果不会执⾏HTML转义）
>
> th:inline =“none” 来禁⽤内联

## 17 内联JavaScript

> ~~~html
> <script type="text/javascript" th:inline="javascript">
> 	var info = [[${info}]];
> 	var age = [[${age}]];
> 	var id = [[${id}]];
> 	var name = [[${name}]];
> 	console.log(id, name, age, info);
> </script>
> ~~~

## 18 URL

> ~~~html
> <p>${param.size()}=[[${param.size()}]]</p>
> <!--/*判断请求参数是否为空*/-->
> <p>${param.isEmpty()}=[[${param.isEmpty()}]]</p>
> <!--获取某个参数值，不存在时为null-->
> <p>${param.u_id}=[[${param.u_id}]]</p>
> ~~~

## 19 Session

> ~~~html
> <p>${session.size()}=[[${session.size()}]]</p>
> <!--/*判断请求参数是否为空*/-->
> <p>${session.isEmpty()}=[[${session.isEmpty()}]]</p>
> <!--获取某个参数值，不存在时为null-->
> <p>${session.user.id}=[[${session.user.id}]]</p>
> ~~~



