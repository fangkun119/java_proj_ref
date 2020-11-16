# Weibo客户端项目

## 1 权限申请

微博公共平台：[https://open.weibo.com/](https://open.weibo.com/)

1.申请网站接入

> `微连接` -> `网站接入` -> `立即接入` -> `填写开发者基本信息` （开发者类型选“个人”）

2.开发者身份认证

> `页面右上角个人头像下拉列表` -> `开发者信息` -> `身份认证` （大约需要一天时间来审核）

3.创建应用：

> `微连接` -> `网站接入` -> `立即接入` -> `创建应用` (应用分类选“网页应用”，审核结果将在3个工作日内反馈）

4.OAuth2.0授权设置

> `我的应用` -> `应用链接` -> `接口管理` -> `授权机制` -> `OAuth2.0授权机制` 
> 
> 开发阶段希望回调页指向localhost，可以改/etc/hosts，让填入的网站域名指向localhost，例如：
> 
> * 授权回调页填写：`http://local.kenweiboapp.com:3000/login` 
> * 在/etc/hosts中配置：`127.0.0.1	local.kenweiboapp.com`

## 2. 创建项目

创建并启动项目

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/
> $ create-react-app weibo-app
> Creating a new React app in /Users/fangkun/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app.
> Installing packages. This might take a couple of minutes.
> ...
> Happy hacking!
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/
> $ cd weibo-app/
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app/
> $ npm start
> ~~~

代码（全部由`create-react-app`自动生成）：

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/ef7835970792162378b5d83af96077f0a9a268fd)

## 3. 微博OAuth回调测试

(1) 测试上一步本机`/etc/hosts`上的配置：

> `http://local.kenweiboapp.com:3000/`

(2) 测试weibo回调是否能成功：

> 微博.开放平台-开发文档-授权机制：[https://open.weibo.com/wiki/%E6%8E%88%E6%9D%83%E6%9C%BA%E5%88%B6#.E6.8E.88.E6.9D.83.E6.9C.89.E6.95.88.E6.9C.9F](https://open.weibo.com/wiki/%E6%8E%88%E6%9D%83%E6%9C%BA%E5%88%B6#.E6.8E.88.E6.9D.83.E6.9C.89.E6.95.88.E6.9C.9F)
>
	Web网站的授权
	1. 引导需要授权的用户到如下地址：
	https://api.weibo.com/oauth2/authorize?client_id=YOUR_CLIENT_ID&response_type=code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI
	2. 如果用户同意授权，页面跳转至 YOUR_REGISTERED_REDIRECT_URI/?code=CODE
	3. 换取Access Token
	URL：https://api.weibo.com/oauth2/access_token?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=authorization_code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE
> 
> 替换步骤1中`url`的`YOUR_CLIENT_ID`和`YOUR_REGISTERED_REDIRECT_URI`
>
> * `YOUR_CLIENT_ID`：在前面设置OAuth回调页面的url中可以找到）
> * `YOUR_REGISTERED_REDIRECT_URI`：前面配置的OAuth回调页`http://local.kenweiboapp.com:3000/login`
> 
> 访问该页面，登录后页面跳转地址形如 http://local.kenweiboapp.com:3000/login?code=9f53966a1029c4006882315152d8242e ，说明weibo回调成功，其中的code用于步骤3中的`AccessToken`换取

(3) 开发调试时可使用weibo提供的工具快速获取`AccessToken`并`调试接口`

> [https://open.weibo.com/tools/console](https://open.weibo.com/tools/console)
> 
> 例子如下：
> 
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/200_react/weibo_api_test_tools.jpg)
> 
> 页面上给出用于拼接请求的字段
> 
> ~~~txt
> 请求方式：get
> 请求URL：https://api.weibo.com/2/statuses/public_timeline.json
> 请求参数：access_token=2.00xrKsOCTXY2WB5aa2aaeb4261rqGC
> ~~~
> 
> 拼接后得到一个请求url如下，可以通过浏览器访问
> 
> ~~~txt
> https://api.weibo.com/2/statuses/public_timeline.json?access_token=2.00xrKsOCTXY2WB5aa2aaeb4261rqGC
> ~~~

## 4. 项目代码初始化

(1) 安装`react-router-dom`
 
> ~~~bash
> __________________________________________________________________
$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app/
> $ npm install react-router-dom -S
> ...
> + react-router-dom@5.2.0
> added 11 packages from 6 contributors and audited 1984 packages in 18.735s
> ...
> ~~~

(2) 裁剪项目成为一个仅包含`Home`页面的最简版本

> 代码 ：[git commit](https://github.com/fangkun119/java_proj_ref/commit/d09edeebdb00b0422c3e6eb4a86dab7e5e6af8a7) 
> 
> * [/src/index.js](https://github.com/fangkun119/java_proj_ref/blob/d09edeebdb00b0422c3e6eb4a86dab7e5e6af8a7/200_react/03_proj_weibo_client/weibo-app/src/index.js) 
> * [/src/App.js](https://github.com/fangkun119/java_proj_ref/blob/d09edeebdb00b0422c3e6eb4a86dab7e5e6af8a7/200_react/03_proj_weibo_client/weibo-app/src/App.js) 
> * [/src/router/index.js](https://github.com/fangkun119/java_proj_ref/blob/d09edeebdb00b0422c3e6eb4a86dab7e5e6af8a7/200_react/03_proj_weibo_client/weibo-app/src/router/index.js) 
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/d09edeebdb00b0422c3e6eb4a86dab7e5e6af8a7/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)

(3) 加入`axios`，增加`Ajax`、`拦截器`相关代码封装

> 安装`axios`
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app/
> $ npm install axios -S
> ...
> + axios@0.21.0
> ...
> ~~~
> 
> 代码：
> 
> [git commit](https://github.com/fangkun119/java_proj_ref/commit/cd6ba1c2de9ac6afe976874e354b165baa7b41cf) 
> 
> * [/src/utils/ajax.js](https://github.com/fangkun119/java_proj_ref/blob/cd6ba1c2de9ac6afe976874e354b165baa7b41cf/200_react/03_proj_weibo_client/weibo-app/src/utils/ajax.js)
> * [/src/utils/interceptors.js](https://github.com/fangkun119/java_proj_ref/blob/cd6ba1c2de9ac6afe976874e354b165baa7b41cf/200_react/03_proj_weibo_client/weibo-app/src/utils/interceptors.js)

## 5. 从`api.weibo.com`获取数据

### (1) 解决跨域问题

URL：步骤`3`用[https://open.weibo.com/tools/console](https://open.weibo.com/tools/console)，获取的access_token及url

> `https://api.weibo.com/2/statuses/public_timeline.json?access_token=2.00xrKsOCTXY2WB5aa2aaeb4261rqGC`
>
> * 在`access_token`有效期内，把这个url贴到浏览器，可以得到weibo返回的数据
> * 如果在代码中，直接用`ajax`访问，会得到`blocked by CORS policy`如下错误
> 
> ~~~txt
> Access to XMLHttpRequest at 'https://api.weibo.com/2/statuses/public_timeline.json?access_token=2.00xrKsOCTXY2WB5aa2aaeb4261rqGC' from origin 'http://localhost:3000' has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
> ~~~
>
> 需要解决跨域问题，下面的步骤使用`http-proxy-middleware`来跨域
 
步骤1：安装`http-proxy-middleware` 

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app/
> $ npm install http-proxy-middleware -D
> ...
> + http-proxy-middleware@1.0.6
> ...
> ~~~

步骤2：编写配置代码，使用跨域来访问weibo（需要重启）

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/d69ac12dc33f1a178fcf2ce6017a06c17a7d8101) 
> 
> * [/src/setupProxy.js](https://github.com/fangkun119/java_proj_ref/blob/d69ac12dc33f1a178fcf2ce6017a06c17a7d8101/200_react/03_proj_weibo_client/weibo-app/src/setupProxy.js)
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/d69ac12dc33f1a178fcf2ce6017a06c17a7d8101/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)
> 
> 重启配置生效后，先前被block的地址已经可以访问
> 
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/200_react/after_http_proxy_middleware.jpg)

### (2) 使用`injector`为所有HTTP请求统一设置`OAuth2 Token` 

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/f88b22654e91236ce4c1c3235b060a8154923045) 
> 
> * [/src/utils/interceptors.js](https://github.com/fangkun119/java_proj_ref/blob/f88b22654e91236ce4c1c3235b060a8154923045/200_react/03_proj_weibo_client/weibo-app/src/utils/interceptors.js) 

## 6. 用`redux`来触发`action`获取后端

(1) 安装redux

>~~~bash
>__________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app/
> $ npm install redux redux-thunk redux-react-hook -S
> ...
> + redux-thunk@2.3.0
> + redux-react-hook@4.0.3
> + redux@4.0.5
> added 4 packages from 3 contributors and audited 1982 packages in 15.527s
> ...
>~~~

(2) 添加`reducers`, `actions`, `store/state` 以及 `api` 

> 代码：
> 
> [git commit](https://github.com/fangkun119/java_proj_ref/commit/b466dcd3d0f7887bedfca229361a6de23a0c16fb) 
> 
> * action：[/src/actions/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/b466dcd3d0f7887bedfca229361a6de23a0c16fb/200_react/03_proj_weibo_client/weibo-app/src/actions/timeline.js)
> * api：[/src/api/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/b466dcd3d0f7887bedfca229361a6de23a0c16fb/200_react/03_proj_weibo_client/weibo-app/src/api/timeline.js) 
> * store/states: [/src/index.js](https://github.com/fangkun119/java_proj_ref/blob/b466dcd3d0f7887bedfca229361a6de23a0c16fb/200_react/03_proj_weibo_client/weibo-app/src/index.js)
> * view: [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/b466dcd3d0f7887bedfca229361a6de23a0c16fb/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)
> 
> 此时访问weibo后端已经由redux来触发

## 7. 用`redux`处理后端返回数据进行渲染

> 用`redux`分发`action`得到的数据、交给`reducer`处理、存入`store/state`并渲染页面
> 
> 代码：
> 
> [git commit](https://github.com/fangkun119/java_proj_ref/commit/244ce067bbbd45ac5426e69dcad80fbc76579042) 
> 
> * [/src/actions/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/244ce067bbbd45ac5426e69dcad80fbc76579042/200_react/03_proj_weibo_client/weibo-app/src/actions/timeline.js) : 修改action，让`action`分发`backend response` （用 `type: GET_HOME_TIMELINE`标识）
> * [/src/reducers/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/244ce067bbbd45ac5426e69dcad80fbc76579042/200_react/03_proj_weibo_client/weibo-app/src/reducers/timeline.js)：增加`reducer`，只处理`type`为`GET_HOME_TIMELINE`的数据
> * [/src/reducers/index.js](https://github.com/fangkun119/java_proj_ref/blob/244ce067bbbd45ac5426e69dcad80fbc76579042/200_react/03_proj_weibo_client/weibo-app/src/reducers/index.js)：将上面新增的`reducer`添加到`root reducer`中
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/244ce067bbbd45ac5426e69dcad80fbc76579042/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)：`view`，包含两部分代码：
> 	* 从外层的`store/state`（使用了`RootReduer`）挑选与该页面有关的数据 
>	* 上一小节的代码，`dispatch`发生变化时触发`action`从后端取数据 (?dispatch何时会发生变化）
>
> ~~~javascript
>     const { 
>         home = []  //初始值为空，后端返回数据后更新
>     } =  useMappedState((state) => (state.timeline)); // 数据由useMappedState从store/state中获取并根据传入的函数进行帅选
> 
>     const dispatch = useDispatch();
>     useEffect(() => {
>         dispatch(getHomeTimeline())
>     }, [dispatch]); //依赖[dispatch]：是一个规范，一旦dispatch发生变化，可以重新触发actions
> 
>     // 上面home数组中的数据可以用来渲染页面
> ~~~	
>
> * [git commit 2: update comment](https://github.com/fangkun119/java_proj_ref/commit/c44ff7f5d450305850c1dcc9ca574ecfe39aceb1) 

## 8. 用CSS预处理器`SASS`调整页面样式

(1) 安装相关的依赖

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app/
> $ npm install moment -S
> ...
> + moment@2.29.1
> ...
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app/
> $ npm install node-sass -D  # 只需要装`node-sass`到Dev依赖中、是因为create-react-app天生可以处理sass，不需要额外安装loader之类
> ...
> + node-sass@5.0.0
> ...
> ~~~
> 
> 尝试少量用`scss`编写的样式时、页面报错：

	Failed to compile.
	./src/pages/Home/index.module.scss (./node_modules/css-loader/dist/cjs.js??ref--5-oneOf-7-1!./node_modules/postcss-loader/src??postcss!./node_modules/resolve-url-loader??ref--5-oneOf-7-3!./node_modules/sass-loader/dist/cjs.js??ref--5-oneOf-7-4!./src/pages/Home/index.module.scss)
	Error: Node Sass version 5.0.0 is incompatible with ^4.0.0.

> 更换node-sass版本到^4.0.0，页面正常
> 
> ~~~bash
> npm uninstall node-sass
> npm install node-sass@4.14.1
> ~~~

(2) 调整样式

> `Chrome Dev Tool`的视图调成手机模式
> 
> 代码
> 
> [git commit](https://github.com/fangkun119/java_proj_ref/commit/9fe9cf820ea42bd9e0b011b93ee8681a927af071)
> 
> * [/src/index.js](https://github.com/fangkun119/java_proj_ref/blob/9fe9cf820ea42bd9e0b011b93ee8681a927af071/200_react/03_proj_weibo_client/weibo-app/src/index.js) 以及 [/src/index.module.scss](https://github.com/fangkun119/java_proj_ref/blob/9fe9cf820ea42bd9e0b011b93ee8681a927af071/200_react/03_proj_weibo_client/weibo-app/src/index.scss)：修改html标签`<ul>`和`<li>`的默认样式
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/9fe9cf820ea42bd9e0b011b93ee8681a927af071/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)：home页
> * [/src/pages/Home/index.module.scss](https://github.com/fangkun119/java_proj_ref/blob/9fe9cf820ea42bd9e0b011b93ee8681a927af071/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.module.scss)：home页样式
> * [/src/styles/mixin.scss](https://github.com/fangkun119/java_proj_ref/blob/9fe9cf820ea42bd9e0b011b93ee8681a927af071/200_react/03_proj_weibo_client/weibo-app/src/styles/mixin.scss)：公用样式（`mixin`）
> * [/src/styles/variable.scss](https://github.com/fangkun119/java_proj_ref/blob/9fe9cf820ea42bd9e0b011b93ee8681a927af071/200_react/03_proj_weibo_client/weibo-app/src/styles/variable.scss)：公用样式（`变量`）

## 9. 使用`Ant Design`等为页面增加组件

(1) 安装相关依赖

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app/
> $ npm install react-app-rewired customize-cra babel-plugin-import -D 
> ... 
> + customize-cra@1.0.0
> + babel-plugin-import@1.13.1
> + react-app-rewired@2.1.6
> ....
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app/
> $ npm install antd -S
> ...
> + antd@4.8.2
> ...
> ~~~
> 
> * `react-app-rewired customize-cra babel-plugin-import`是为了`antd`库的`css`按需加载，因为没有使用`LESS`，不需要为`customize-cra`配置`less-loader`
> * `antd`是`Ant Design`组件库
> 

(2) 编写`config-overrides.js`（放在项目最外层目录，而不是src目录）

> ~~~bash
> __________________________________________________________________
> > $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app/
> $ cat config-overrides.js
> const { override, fixBabelImports } = require('customize-cra');
> 
> module.exports = override(
>     fixBabelImports('antd', {
>         libraryDirectory: 'es',
>         style: 'css',
>     })
> )
> ~~~

(3) 替换`package.json`的`scripts`中的`react-scripts`为`react-app-rewired `

> ~~~json
>   "scripts": {
>     "start": "react-app-rewired start", 
>     "build": "react-app-rewired build",
>     "test": "react-app-rewired test", 
>     "eject": "react-app-rewired eject" 
>   },
> ~~~

(2) 修改样式：

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/aae15268e09ef6408edae9266f2f1d729a13852b)
> 
> * [/config-overrides.js](https://github.com/fangkun119/java_proj_ref/blob/aae15268e09ef6408edae9266f2f1d729a13852b/200_react/03_proj_weibo_client/weibo-app/config-overrides.js)
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/aae15268e09ef6408edae9266f2f1d729a13852b/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js) 
> * [/src/pages/Home/index.module.scss](https://github.com/fangkun119/java_proj_ref/blob/aae15268e09ef6408edae9266f2f1d729a13852b/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.module.scss)

## 10. 显式转帖

### (1) 从`Home`组件中抽离一个单独的`Post`组件

> 代码： [git commit](https://github.com/fangkun119/java_proj_ref/commit/61a7d3b09d6d5f8334db2eb51ceb09e544302121) 
> 
> * [/src/pages/Home/components/post/index.js](https://github.com/fangkun119/java_proj_ref/blob/61a7d3b09d6d5f8334db2eb51ceb09e544302121/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/post/index.js)，[/src/pages/Home/components/post/index.module.scss](https://github.com/fangkun119/java_proj_ref/blob/61a7d3b09d6d5f8334db2eb51ceb09e544302121/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/post/index.module.scss)：`Post`组件代码来自之前的`Home`组件
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/61a7d3b09d6d5f8334db2eb51ceb09e544302121/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)，[/src/pages/Home/index.module.scss](https://github.com/fangkun119/java_proj_ref/blob/61a7d3b09d6d5f8334db2eb51ceb09e544302121/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.module.scss)：拆分之后的`Home`组件
> * [/src/api/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/61a7d3b09d6d5f8334db2eb51ceb09e544302121/200_react/03_proj_weibo_client/weibo-app/src/api/timeline.js)：修一个bug，从获取广场微博，改为获取关注账号的微博，同时更容易看到转帖

### (2) 对于转帖的帖子，用一个`inner Card`显式原贴 

> 代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/ff96107ec2742ba52f70074243d13a50f8d5fe3d) 
> 
> * [/src/pages/Home/components/post/index.js](https://github.com/fangkun119/java_proj_ref/blob/ff96107ec2742ba52f70074243d13a50f8d5fe3d/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/post/index.js)

## 11. 支持自动翻页、滚动加载

### (1) 查看微博API是否支持翻页

微博API文档： [https://open.weibo.com/wiki/2/statuses/home_timeline](https://open.weibo.com/wiki/2/statuses/home_timeline) 

> 可用参数包括：`access_token`、`since_id`、`max_id`、`count`、`page`、`base_app`、`feature`、`trim_user`

### (2) 安装`react-infinite-scroller`库

> `react-infinite-scroller`能够监控滚动条，在滚动条滚动到底部时触发一些事件
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app/
> $ npm install react-infinite-scroller -S
> ...
> + react-infinite-scroller@1.2.4
> ...
> ~~~

### (3) 用`react-infinite-scroller`触发微博加载

> 代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/9744cc173a8f3a2674ee8a32656353ebe67f8ea8)
> 
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/9744cc173a8f3a2674ee8a32656353ebe67f8ea8/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)
> 
> 测试：滚动条拖到底部时、可以触发微博API调用，获取更多的微博，但是不能渲染在页面上，需要继续增加显示功能

### (4) 把`page`参数加入到`redux`单向链路

把`page`参数加入到`redux`单向链路，使得`scroller`每次滚动到底部时、可以请求再下一页

> 代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/51b66171aaaa0d0d69a7c2ec65b7cc763fe68243) 
> 
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/51b66171aaaa0d0d69a7c2ec65b7cc763fe68243/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)：view
> * [/src/actions/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/51b66171aaaa0d0d69a7c2ec65b7cc763fe68243/200_react/03_proj_weibo_client/weibo-app/src/actions/timeline.js)：action
> * [/src/reducers/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/51b66171aaaa0d0d69a7c2ec65b7cc763fe68243/200_react/03_proj_weibo_client/weibo-app/src/reducers/timeline.js)：reducer

### (5) 修复`Ajax`传参bug

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/b006e40e811a2483a6c280f0757bc1204fa18286)

>~~~javascript
>import ajax from "../utils/ajax";
> export function getHomeTimeline(params) {
>     return ajax.get('/proxy/2/statuses/home_timeline.json', {params}); // not {data: params}
> }
> ~~~




