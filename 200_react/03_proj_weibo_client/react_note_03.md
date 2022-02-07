# React Note 03：基于React做一个Weibo Client APP

## 00 课前Q&A 

官方文档 [https://redux-toolkit.js.org/usage/usage-guide](https://redux-toolkit.js.org/usage/usage-guide)

> 创建Reducer的新的方法，相比项目中使用的switch，会更加简洁
>
> ~~~react
> const todosReducer = createReducer([], (builder) => {
>   builder
>     .addCase('ADD_TODO', (state, action) => {
>       // "mutate" the array by calling push()
>       state.push(action.payload)
>     })
>     .addCase('TOGGLE_TODO', (state, action) => {
>       const todo = state[action.payload.index]
>       // "mutate" the object by overwriting a field
>       todo.completed = !todo.completed
>     })
>     .addCase('REMOVE_TODO', (state, action) => {
>       // Can still return an immutably-updated value if we want to
>       return state.filter((todo, i) => i !== action.payload.index)
>     })
> })
> ~~~
>
> 除了Redux官方，一些把Redux用作底层的第三方框架，也会提供类似的功能

## 01 开放平台申请及OAuth使用

### (1) 操作步骤

申请开发Client APP：[https://open.weibo.com/apps](https://open.weibo.com/apps)

> * 申请成功后会得到一个`App Key` 和`App Secret`，用来标识这个Client APP
>
> * 需要设置一个OAuth授权成功后的回调页地址（对url格式有要求），为了能够在本机能够测试，需要修改`/etc/hosts`来进行配置（批量修改DNS，可以使用Switch Host等软件）

测试授权回调是否成功[https://open.weibo.com/wiki/Oauth2/authorize](https://open.weibo.com/wiki/Oauth2/authorize)

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/react_03wb_test_oauth_redirect.jpg" width="600" /></div>
>
> 授权回调成功后，跳转到预设的回调页面，同时会看到这个页面url被增加了一个`code=xxx`的url参数

使用code访问weibo的`oauth2/access_token`接口、获取授权后的access token

### (2) OAuth授权过程

OAuth 2.0的授权过程

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/react_wbapp_oauth_proc.jpg" width="600" /></div>

在我们的例子中

> 右边的三个（用户服务器Resource Owener、授权服务器Authorization Server，资源服务器Resource Server）都是微博的
>
> * 用户服务器Resource Owner：是weibo登录界面（输入用户名密码的界面）所连接的服务器
> * 授权服务器Authorization Server：是weibo询问用户是否授权给这个第三方Client App的界面所连接的服务器
> * 资源服务器Resource Server：是weibo的后台服务器网关
>
> 过程：跳转到weibo的用户服务器（让用户输入用户名密码、以获取Grant Code）→ 跳转到weibo的授权服务器（询问用户是否同意授权、用Code换取Access Token）→ 访问资源服务器（使用Access Token调用微博的后台API） 

### (3) 使用调试用的“万能Access Token”进行开发

通常先开发其他内容，后期再实现登录功能，以方便调试，做法如下：

> (1) 后端也是自己开发，可以先不进行后端登录校验
>
> (2) 后端不可修改，以weibo为例，它为开发者提供了一个调试用的“万能Access Token”，将这个access token拼接到weibo REST API的url参数中，就可以进行开发调试

本项目采用如下的方法

> home/index.js
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/react_wbapp_append_accesstoken_homejs.jpg" width="1024" /></div>
>
> util/ajax.js
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/react_wbapp_append_accesstoken_ajaxjs.jpg" width="400" /></div>

但是直接拼接Access Token仍然不能成功访问weibo后端，还需要解决跨域问题

### (4) 解决跨域问题

跨域问题：来自于浏览器的同源策略，跨域访问会遇到如下问题

> * Cookie, LocalStorage和IndexDB无法读取
> * DOM和JS对象无法获得
> * AJAX请求不能发送

浏览器采用同源策略，主要是出于安全考虑

哪些是跨域，哪些不是

> 不是跨域：同一域名不同文件路径
>
> 属于跨域：端口不同、协议不同、域名v.s实际IP、主域名相同但是子域名不同、不同域名
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/react_wbapp_cors_examples.jpg" width="600" /></div>

针对跨域问题的解决方案较多，包括如下

>  1、通过jsonp跨域（常用）
>  2、 document.domain + iframe跨域 3、 location.hash + iframe
>  4、 window.name + iframe跨域
>  5、 postMessage跨域
>  6、 跨域资源共享(CORS)：常用，项目2中提过，让后端开放一个白名单，让两个域可以资源共享
>  7、 nginx代理跨域
>  8、 nodejs中间件代理跨域：常用，项目3（本项目）使用
>  9、 WebSocket协议跨域
>
> 其他的几种方法可以参考：[https://segmentfault.com/a/1190000011145364](https://segmentfault.com/a/1190000011145364)

搭建环境：用node中间件代理跨域

> 在项目3中，不可能让weibo的后端给localhost开一个跨域白名单，因此使用NodeJS中间件代理
>
> 代码如下
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/react_wbapp_nodejs_midware_cors.jpg" width="1024" /></div>
>
> 原理如下：
>
> 1.项目启动时nodejs会启一个dev server，访问localhost:3000会访问到这个server
>
> 2.在server中可以配置一个中间件，而不需要重头写一个
>
> 3.图片右下角的代码中，设置一个代理路径`/proxy`来访问`api.weibo.com`
>
> 4.图片左上角的代码，被改写成左下角的代码：不再直接访问`api.weibo.com`，而是改为访问`/proxy`
>
> 5.目前将对`/proxy/2/***`的访问定向到了`api.weibo.com/proxy/2/***`，因为weibo正确的路径是`api.weibo.com/2/***`，因此要在右下角图片代码中添加`rewrite('/proxy':'/')` 以消去多余的`/proxy`

为何代理之后，就不存在跨域问题

> 因为跨域问题是来自于浏览器的同源策略
>
> * 使用代理前：浏览器既与weibo服务器交互，又与我们自己的node server交互，产生跨域
> * 使用代理后：浏览器统一与node server交互，node server再决定是否要与weibo服务器交互
>
> 也是出于这类原因，上线时，一定要有一层node server层来解决跨域问题

### (5) 使用拦截器添加Token避免重复代码

> 
