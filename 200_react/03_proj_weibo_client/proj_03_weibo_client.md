<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Weibo客户端项目](#weibo%E5%AE%A2%E6%88%B7%E7%AB%AF%E9%A1%B9%E7%9B%AE)
  - [01. 权限申请](#01-%E6%9D%83%E9%99%90%E7%94%B3%E8%AF%B7)
  - [02. 创建项目](#02-%E5%88%9B%E5%BB%BA%E9%A1%B9%E7%9B%AE)
  - [03. 微博OAuth回调测试](#03-%E5%BE%AE%E5%8D%9Aoauth%E5%9B%9E%E8%B0%83%E6%B5%8B%E8%AF%95)
  - [04. 项目代码初始化](#04-%E9%A1%B9%E7%9B%AE%E4%BB%A3%E7%A0%81%E5%88%9D%E5%A7%8B%E5%8C%96)
  - [05. 从`api.weibo.com`获取数据](#05-%E4%BB%8Eapiweibocom%E8%8E%B7%E5%8F%96%E6%95%B0%E6%8D%AE)
    - [(1) 解决跨域问题](#1-%E8%A7%A3%E5%86%B3%E8%B7%A8%E5%9F%9F%E9%97%AE%E9%A2%98)
    - [(2) 使用`injector`为所有HTTP请求统一设置`OAuth2 Token`](#2-%E4%BD%BF%E7%94%A8injector%E4%B8%BA%E6%89%80%E6%9C%89http%E8%AF%B7%E6%B1%82%E7%BB%9F%E4%B8%80%E8%AE%BE%E7%BD%AEoauth2-token)
  - [06. 用`redux`来触发`action`获取后端](#06-%E7%94%A8redux%E6%9D%A5%E8%A7%A6%E5%8F%91action%E8%8E%B7%E5%8F%96%E5%90%8E%E7%AB%AF)
  - [07. 用`redux`处理后端返回数据进行渲染](#07-%E7%94%A8redux%E5%A4%84%E7%90%86%E5%90%8E%E7%AB%AF%E8%BF%94%E5%9B%9E%E6%95%B0%E6%8D%AE%E8%BF%9B%E8%A1%8C%E6%B8%B2%E6%9F%93)
  - [08. 用CSS预处理器`SASS`调整页面样式](#08-%E7%94%A8css%E9%A2%84%E5%A4%84%E7%90%86%E5%99%A8sass%E8%B0%83%E6%95%B4%E9%A1%B5%E9%9D%A2%E6%A0%B7%E5%BC%8F)
  - [09. 使用`Ant Design`等为页面增加组件](#09-%E4%BD%BF%E7%94%A8ant-design%E7%AD%89%E4%B8%BA%E9%A1%B5%E9%9D%A2%E5%A2%9E%E5%8A%A0%E7%BB%84%E4%BB%B6)
  - [10. 完整显示转帖内容](#10-%E5%AE%8C%E6%95%B4%E6%98%BE%E7%A4%BA%E8%BD%AC%E5%B8%96%E5%86%85%E5%AE%B9)
    - [(1) 从`Home`组件中抽离一个单独的`Post`组件](#1-%E4%BB%8Ehome%E7%BB%84%E4%BB%B6%E4%B8%AD%E6%8A%BD%E7%A6%BB%E4%B8%80%E4%B8%AA%E5%8D%95%E7%8B%AC%E7%9A%84post%E7%BB%84%E4%BB%B6)
    - [(2) 对于转帖的帖子，用一个`inner Card`显式原贴](#2-%E5%AF%B9%E4%BA%8E%E8%BD%AC%E5%B8%96%E7%9A%84%E5%B8%96%E5%AD%90%E7%94%A8%E4%B8%80%E4%B8%AAinner-card%E6%98%BE%E5%BC%8F%E5%8E%9F%E8%B4%B4)
  - [11. 支持自动翻页、滚动加载](#11-%E6%94%AF%E6%8C%81%E8%87%AA%E5%8A%A8%E7%BF%BB%E9%A1%B5%E6%BB%9A%E5%8A%A8%E5%8A%A0%E8%BD%BD)
    - [(1) 查看微博API是否支持翻页](#1-%E6%9F%A5%E7%9C%8B%E5%BE%AE%E5%8D%9Aapi%E6%98%AF%E5%90%A6%E6%94%AF%E6%8C%81%E7%BF%BB%E9%A1%B5)
    - [(2) 安装`react-infinite-scroller`库](#2-%E5%AE%89%E8%A3%85react-infinite-scroller%E5%BA%93)
    - [(3) 用`react-infinite-scroller`触发微博加载](#3-%E7%94%A8react-infinite-scroller%E8%A7%A6%E5%8F%91%E5%BE%AE%E5%8D%9A%E5%8A%A0%E8%BD%BD)
    - [(4) 把`page`参数加入到`redux`单向链路](#4-%E6%8A%8Apage%E5%8F%82%E6%95%B0%E5%8A%A0%E5%85%A5%E5%88%B0redux%E5%8D%95%E5%90%91%E9%93%BE%E8%B7%AF)
    - [(5) 修复`Ajax`传参bug](#5-%E4%BF%AE%E5%A4%8Dajax%E4%BC%A0%E5%8F%82bug)
    - [(6) 将两个`action dispatch`合并成一个](#6-%E5%B0%86%E4%B8%A4%E4%B8%AAaction-dispatch%E5%90%88%E5%B9%B6%E6%88%90%E4%B8%80%E4%B8%AA)
    - [(7) 合并新旧信息流](#7-%E5%90%88%E5%B9%B6%E6%96%B0%E6%97%A7%E4%BF%A1%E6%81%AF%E6%B5%81)
  - [12 顶部的`App Header Bar`](#12-%E9%A1%B6%E9%83%A8%E7%9A%84app-header-bar)
    - [(1) `App Header Bar`样式](#1-app-header-bar%E6%A0%B7%E5%BC%8F)
    - [(2) OAuth登录1：登录按钮跳转](#2-oauth%E7%99%BB%E5%BD%951%E7%99%BB%E5%BD%95%E6%8C%89%E9%92%AE%E8%B7%B3%E8%BD%AC)
    - [(3) `App Header Bar`顶部固定](#3-app-header-bar%E9%A1%B6%E9%83%A8%E5%9B%BA%E5%AE%9A)
  - [13 `new post`页面](#13-new-post%E9%A1%B5%E9%9D%A2)
  - [14 `OAuth`登录2：获取weibo返回的登录`code`](#14-oauth%E7%99%BB%E5%BD%952%E8%8E%B7%E5%8F%96weibo%E8%BF%94%E5%9B%9E%E7%9A%84%E7%99%BB%E5%BD%95code)
  - [15. 发送微博评论功能](#15-%E5%8F%91%E9%80%81%E5%BE%AE%E5%8D%9A%E8%AF%84%E8%AE%BA%E5%8A%9F%E8%83%BD)
  - [16. 微博评论列表页面](#16-%E5%BE%AE%E5%8D%9A%E8%AF%84%E8%AE%BA%E5%88%97%E8%A1%A8%E9%A1%B5%E9%9D%A2)
    - [(1) 评论列表页面](#1-%E8%AF%84%E8%AE%BA%E5%88%97%E8%A1%A8%E9%A1%B5%E9%9D%A2)
    - [(2) 根据id获取微博原文、以便展示在评论列表页上](#2-%E6%A0%B9%E6%8D%AEid%E8%8E%B7%E5%8F%96%E5%BE%AE%E5%8D%9A%E5%8E%9F%E6%96%87%E4%BB%A5%E4%BE%BF%E5%B1%95%E7%A4%BA%E5%9C%A8%E8%AF%84%E8%AE%BA%E5%88%97%E8%A1%A8%E9%A1%B5%E4%B8%8A)
  - [17. `OAuth`登录3：用`login code`换取`access token`以使用其他API](#17-oauth%E7%99%BB%E5%BD%953%E7%94%A8login-code%E6%8D%A2%E5%8F%96access-token%E4%BB%A5%E4%BD%BF%E7%94%A8%E5%85%B6%E4%BB%96api)
  - [18. 微博评论列表页面（2）](#18-%E5%BE%AE%E5%8D%9A%E8%AF%84%E8%AE%BA%E5%88%97%E8%A1%A8%E9%A1%B5%E9%9D%A22)
    - [(1) 点击数量大于0的“评论按钮”时，可以获取并展开评论列表](#1-%E7%82%B9%E5%87%BB%E6%95%B0%E9%87%8F%E5%A4%A7%E4%BA%8E0%E7%9A%84%E8%AF%84%E8%AE%BA%E6%8C%89%E9%92%AE%E6%97%B6%E5%8F%AF%E4%BB%A5%E8%8E%B7%E5%8F%96%E5%B9%B6%E5%B1%95%E5%BC%80%E8%AF%84%E8%AE%BA%E5%88%97%E8%A1%A8)
    - [(2) 关闭评论列表功能](#2-%E5%85%B3%E9%97%AD%E8%AF%84%E8%AE%BA%E5%88%97%E8%A1%A8%E5%8A%9F%E8%83%BD)
    - [(3) 将`Home`组件中与评论列表相关的代码拆分到专用的`commentsList`组件](#3-%E5%B0%86home%E7%BB%84%E4%BB%B6%E4%B8%AD%E4%B8%8E%E8%AF%84%E8%AE%BA%E5%88%97%E8%A1%A8%E7%9B%B8%E5%85%B3%E7%9A%84%E4%BB%A3%E7%A0%81%E6%8B%86%E5%88%86%E5%88%B0%E4%B8%93%E7%94%A8%E7%9A%84commentslist%E7%BB%84%E4%BB%B6)
    - [(4) 将`Post`组件中与评论列表相关的代码迁移到`commentsList`组件中](#4-%E5%B0%86post%E7%BB%84%E4%BB%B6%E4%B8%AD%E4%B8%8E%E8%AF%84%E8%AE%BA%E5%88%97%E8%A1%A8%E7%9B%B8%E5%85%B3%E7%9A%84%E4%BB%A3%E7%A0%81%E8%BF%81%E7%A7%BB%E5%88%B0commentslist%E7%BB%84%E4%BB%B6%E4%B8%AD)
    - [(5) 评论分页加载功能](#5-%E8%AF%84%E8%AE%BA%E5%88%86%E9%A1%B5%E5%8A%A0%E8%BD%BD%E5%8A%9F%E8%83%BD)
    - [(6) Fix Compile Warnings](#6-fix-compile-warnings)
    - [(7) Fix Bug：当点击另一条微博的评论按钮时、重置`state/store`中的`comment list`](#7-fix-bug%E5%BD%93%E7%82%B9%E5%87%BB%E5%8F%A6%E4%B8%80%E6%9D%A1%E5%BE%AE%E5%8D%9A%E7%9A%84%E8%AF%84%E8%AE%BA%E6%8C%89%E9%92%AE%E6%97%B6%E9%87%8D%E7%BD%AEstatestore%E4%B8%AD%E7%9A%84comment-list)
  - [19. 增加微博评论](#19-%E5%A2%9E%E5%8A%A0%E5%BE%AE%E5%8D%9A%E8%AF%84%E8%AE%BA)
  - [20. 删除微博评论](#20-%E5%88%A0%E9%99%A4%E5%BE%AE%E5%8D%9A%E8%AF%84%E8%AE%BA)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Weibo客户端项目

## 01. 权限申请

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

## 02. 创建项目

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

代码（全部由`create-react-app`自动生成）

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/ef7835970792162378b5d83af96077f0a9a268fd)

## 03. 微博OAuth回调测试

(1) 测试上一步本机`/etc/hosts`上的配置

> `http://local.kenweiboapp.com:3000/`

(2) 测试weibo回调是否能成功

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
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/weibo_api_test_tools.jpg" width="1024" /></div>
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

## 04. 项目代码初始化

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

代码 ：[git commit](https://github.com/fangkun119/java_proj_ref/commit/d09edeebdb00b0422c3e6eb4a86dab7e5e6af8a7) 

> * [/src/index.js](https://github.com/fangkun119/java_proj_ref/blob/d09edeebdb00b0422c3e6eb4a86dab7e5e6af8a7/200_react/03_proj_weibo_client/weibo-app/src/index.js) 
> * [/src/App.js](https://github.com/fangkun119/java_proj_ref/blob/d09edeebdb00b0422c3e6eb4a86dab7e5e6af8a7/200_react/03_proj_weibo_client/weibo-app/src/App.js) 
> * [/src/router/index.js](https://github.com/fangkun119/java_proj_ref/blob/d09edeebdb00b0422c3e6eb4a86dab7e5e6af8a7/200_react/03_proj_weibo_client/weibo-app/src/router/index.js) 
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/d09edeebdb00b0422c3e6eb4a86dab7e5e6af8a7/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)

(3) 加入`axios`，增加`Ajax`、`拦截器`相关代码封装

安装`axios`

> ~~~bash
>__________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app/
> $ npm install axios -S
> ...
> + axios@0.21.0
> ...
> ~~~
> 

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/cd6ba1c2de9ac6afe976874e354b165baa7b41cf) 

> * [/src/utils/ajax.js](https://github.com/fangkun119/java_proj_ref/blob/cd6ba1c2de9ac6afe976874e354b165baa7b41cf/200_react/03_proj_weibo_client/weibo-app/src/utils/ajax.js)
>* [/src/utils/interceptors.js](https://github.com/fangkun119/java_proj_ref/blob/cd6ba1c2de9ac6afe976874e354b165baa7b41cf/200_react/03_proj_weibo_client/weibo-app/src/utils/interceptors.js)

## 05. 从`api.weibo.com`获取数据

### (1) 解决跨域问题

URL：步骤`3`用[https://open.weibo.com/tools/console](https://open.weibo.com/tools/console)获取的access_token及url

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

[git commit](https://github.com/fangkun119/java_proj_ref/commit/d69ac12dc33f1a178fcf2ce6017a06c17a7d8101) 

> * [/src/setupProxy.js](https://github.com/fangkun119/java_proj_ref/blob/d69ac12dc33f1a178fcf2ce6017a06c17a7d8101/200_react/03_proj_weibo_client/weibo-app/src/setupProxy.js)
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/d69ac12dc33f1a178fcf2ce6017a06c17a7d8101/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)
>
> 重启配置生效后，先前被block的地址已经可以访问
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/after_http_proxy_middleware.jpg" width="1024" /></div>

### (2) 使用`injector`为所有HTTP请求统一设置`OAuth2 Token` 

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/f88b22654e91236ce4c1c3235b060a8154923045) 

> * [/src/utils/interceptors.js](https://github.com/fangkun119/java_proj_ref/blob/f88b22654e91236ce4c1c3235b060a8154923045/200_react/03_proj_weibo_client/weibo-app/src/utils/interceptors.js) 

## 06. 用`redux`来触发`action`获取后端

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

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/b466dcd3d0f7887bedfca229361a6de23a0c16fb) 

> * action：[/src/actions/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/b466dcd3d0f7887bedfca229361a6de23a0c16fb/200_react/03_proj_weibo_client/weibo-app/src/actions/timeline.js)
> * api：[/src/api/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/b466dcd3d0f7887bedfca229361a6de23a0c16fb/200_react/03_proj_weibo_client/weibo-app/src/api/timeline.js) 
> * store/states: [/src/index.js](https://github.com/fangkun119/java_proj_ref/blob/b466dcd3d0f7887bedfca229361a6de23a0c16fb/200_react/03_proj_weibo_client/weibo-app/src/index.js)
> * view: [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/b466dcd3d0f7887bedfca229361a6de23a0c16fb/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)
> 
> 此时访问weibo后端已经由redux来触发

## 07. 用`redux`处理后端返回数据进行渲染

用`redux`分发`action`得到的数据、交给`reducer`处理、存入`store/state`并渲染页面

代码：[git commit 1](https://github.com/fangkun119/java_proj_ref/commit/244ce067bbbd45ac5426e69dcad80fbc76579042) 

> * [/src/actions/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/244ce067bbbd45ac5426e69dcad80fbc76579042/200_react/03_proj_weibo_client/weibo-app/src/actions/timeline.js) : 修改action，让`action`分发`backend response` （用 `type: GET_HOME_TIMELINE`标识）
> * [/src/reducers/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/244ce067bbbd45ac5426e69dcad80fbc76579042/200_react/03_proj_weibo_client/weibo-app/src/reducers/timeline.js)：增加`reducer`，只处理`type`为`GET_HOME_TIMELINE`的数据
> * [/src/reducers/index.js](https://github.com/fangkun119/java_proj_ref/blob/244ce067bbbd45ac5426e69dcad80fbc76579042/200_react/03_proj_weibo_client/weibo-app/src/reducers/index.js)：将上面新增的`reducer`添加到`root reducer`中
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/244ce067bbbd45ac5426e69dcad80fbc76579042/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)：`view`，包含两部分代码：
> 	* 从外层的`store/state`（使用了`RootReduer`）挑选与该页面有关的数据 
> 	* 上一小节的代码，`dispatch`发生变化时触发`action`从后端取数据 (?dispatch何时会发生变化）
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

代码：[git commit 2: update comment](https://github.com/fangkun119/java_proj_ref/commit/c44ff7f5d450305850c1dcc9ca574ecfe39aceb1) 

## 08. 用CSS预处理器`SASS`调整页面样式

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
>
> ~~~txt
> Failed to compile.
> ./src/pages/Home/index.module.scss (./node_modules/css-loader/dist/cjs.js??ref--5-oneOf-7-1!./node_modules/postcss-loader/src??postcss!./node_modules/resolve-url-loader??ref--5-oneOf-7-3!./node_modules/sass-loader/dist/cjs.js??ref--5-oneOf-7-4!./src/pages/Home/index.module.scss)
> Error: Node Sass version 5.0.0 is incompatible with ^4.0.0.
> ~~~
>
> 更换node-sass版本到^4.0.0，页面正常
>
> ~~~bash
> npm uninstall node-sass
> npm install node-sass@4.14.1
> ~~~

(2) 调整样式

> `Chrome Dev Tool`的视图调成手机模式
> 

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/9fe9cf820ea42bd9e0b011b93ee8681a927af071)

> * [/src/index.js](https://github.com/fangkun119/java_proj_ref/blob/9fe9cf820ea42bd9e0b011b93ee8681a927af071/200_react/03_proj_weibo_client/weibo-app/src/index.js) 以及 [/src/index.module.scss](https://github.com/fangkun119/java_proj_ref/blob/9fe9cf820ea42bd9e0b011b93ee8681a927af071/200_react/03_proj_weibo_client/weibo-app/src/index.scss)：修改html标签`<ul>`和`<li>`的默认样式
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/9fe9cf820ea42bd9e0b011b93ee8681a927af071/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)：home页
> * [/src/pages/Home/index.module.scss](https://github.com/fangkun119/java_proj_ref/blob/9fe9cf820ea42bd9e0b011b93ee8681a927af071/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.module.scss)：home页样式
> * [/src/styles/mixin.scss](https://github.com/fangkun119/java_proj_ref/blob/9fe9cf820ea42bd9e0b011b93ee8681a927af071/200_react/03_proj_weibo_client/weibo-app/src/styles/mixin.scss)：公用样式（`mixin`）
> * [/src/styles/variable.scss](https://github.com/fangkun119/java_proj_ref/blob/9fe9cf820ea42bd9e0b011b93ee8681a927af071/200_react/03_proj_weibo_client/weibo-app/src/styles/variable.scss)：公用样式（`变量`）

## 09. 使用`Ant Design`等为页面增加组件

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

(2) 修改样式

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/aae15268e09ef6408edae9266f2f1d729a13852b)

> * [/config-overrides.js](https://github.com/fangkun119/java_proj_ref/blob/aae15268e09ef6408edae9266f2f1d729a13852b/200_react/03_proj_weibo_client/weibo-app/config-overrides.js)
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/aae15268e09ef6408edae9266f2f1d729a13852b/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js) 
> * [/src/pages/Home/index.module.scss](https://github.com/fangkun119/java_proj_ref/blob/aae15268e09ef6408edae9266f2f1d729a13852b/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.module.scss)

## 10. 完整显示转帖内容

### (1) 从`Home`组件中抽离一个单独的`Post`组件

代码： [git commit](https://github.com/fangkun119/java_proj_ref/commit/61a7d3b09d6d5f8334db2eb51ceb09e544302121) 

> * [/src/pages/Home/components/post/index.js](https://github.com/fangkun119/java_proj_ref/blob/61a7d3b09d6d5f8334db2eb51ceb09e544302121/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/post/index.js)，[/src/pages/Home/components/post/index.module.scss](https://github.com/fangkun119/java_proj_ref/blob/61a7d3b09d6d5f8334db2eb51ceb09e544302121/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/post/index.module.scss)：`Post`组件代码来自之前的`Home`组件
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/61a7d3b09d6d5f8334db2eb51ceb09e544302121/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)，[/src/pages/Home/index.module.scss](https://github.com/fangkun119/java_proj_ref/blob/61a7d3b09d6d5f8334db2eb51ceb09e544302121/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.module.scss)：拆分之后的`Home`组件
> * [/src/api/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/61a7d3b09d6d5f8334db2eb51ceb09e544302121/200_react/03_proj_weibo_client/weibo-app/src/api/timeline.js)：修一个bug，从获取广场微博，改为获取关注账号的微博，同时更容易看到转帖

### (2) 对于转帖的帖子，用一个`inner Card`显式原贴 

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/ff96107ec2742ba52f70074243d13a50f8d5fe3d) 

> * [/src/pages/Home/components/post/index.js](https://github.com/fangkun119/java_proj_ref/blob/ff96107ec2742ba52f70074243d13a50f8d5fe3d/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/post/index.js)

## 11. 支持自动翻页、滚动加载

### (1) 查看微博API是否支持翻页

微博API文档： [https://open.weibo.com/wiki/2/statuses/home_timeline](https://open.weibo.com/wiki/2/statuses/home_timeline) 

> 可用参数包括：`access_token`、`since_id`、`max_id`、`count`、`page`、`base_app`、`feature`、`trim_user`

### (2) 安装`react-infinite-scroller`库

`react-infinite-scroller`能够监控滚动条，在滚动条滚动到底部时触发一些事件

> ~~~bash
>__________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app/
> $ npm install react-infinite-scroller -S
> ...
> + react-infinite-scroller@1.2.4
> ...
> ~~~

### (3) 用`react-infinite-scroller`触发微博加载

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/9744cc173a8f3a2674ee8a32656353ebe67f8ea8)

> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/9744cc173a8f3a2674ee8a32656353ebe67f8ea8/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)
> 

测试：滚动条拖到底部时、可以触发微博API调用，获取更多的微博，但是不能渲染在页面上，需要继续增加显示功能

### (4) 把`page`参数加入到`redux`单向链路

把`page`参数加入到`redux`单向链路，使得`scroller`每次滚动到底部时、可以请求再下一页

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/51b66171aaaa0d0d69a7c2ec65b7cc763fe68243) 

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

### (6) 将两个`action dispatch`合并成一个

代码中有两处可以触发`action`：一处是使用`useEffect`、一处使用`InfiniteScroll`；将它们统一合并在一处 

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/e2175627c52d9e23d70c984cdeefeeed6b481671) 

### (7) 合并新旧信息流

目前的代码，滚动到页面底部、触发自动翻页信息流获取后。`下一页的微博`会替换`当前微博`。修改`reducer`中的代码，使其改为`下一页的微博`追加在`当前微博`之后

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/bb586f46ce86150491e8e79edb04f7347faf219f) 

## 12 顶部的`App Header Bar`

### (1) `App Header Bar`样式 

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/ad67ecc62002a30bb0e1f7a94844abb8551dba3f). 

包含一个`<UserOutlined>`将用作登录按钮，`Weibo APP`标题，以及一个`<EditOutlined>`将用作发微博的按钮 

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/header_bar.jpg" width="500" /></div>

### (2) OAuth登录1：登录按钮跳转

点击登录按钮后，跳转到微博的登录界面，登录后跳转到开发平台指定的页面 

> ~~~txt
> // 微博授权机制Wiki：https://open.weibo.com/wiki/%E6%8E%88%E6%9D%83%E6%9C%BA%E5%88%B6#.E6.8E.88.E6.9D.83.E6.9C.89.E6.95.88.E6.9C.9F
> // 1. 引导需要授权的用户到如下地址：
> https://api.weibo.com/oauth2/authorize?client_id=${APP_KEY}&response_type=code&redirect_uri=${REDIRECT_URL}`
> ~~~

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/ca81a279b4fc0512965f97fcec4d81d8a0ecd591) 

> 登录后会跳转到`redirect_uri`参数所指向的页面（`login`），该页面目前还没有，第14步中添加

### (3) `App Header Bar`顶部固定

可以用`position:'fixed'`等来调CSS样式，也可以用`antd`组件`<Affix>`，下面使用`<Affix>`

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/7d7848d9fcff87e79955bf662cfcbc2afce6f481)

## 13 `new post`页面

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/b06243b1ccc0aeda76113efc5ad2eee45f58bf4c)

> * [/src/pages/New/index.js](https://github.com/fangkun119/java_proj_ref/blob/b06243b1ccc0aeda76113efc5ad2eee45f58bf4c/200_react/03_proj_weibo_client/weibo-app/src/pages/New/index.js)，[/src/pages/New/index.module.scss](https://github.com/fangkun119/java_proj_ref/blob/b06243b1ccc0aeda76113efc5ad2eee45f58bf4c/200_react/03_proj_weibo_client/weibo-app/src/pages/New/index.module.scss)：`new post`页面
> * [/src/router/index.js](https://github.com/fangkun119/java_proj_ref/blob/b06243b1ccc0aeda76113efc5ad2eee45f58bf4c/200_react/03_proj_weibo_client/weibo-app/src/router/index.js)：注册到`react router`中

##  14 `OAuth`登录2：获取weibo返回的登录`code`

> 该`code`后续可用于换取`Access Token`

(1) 安装`query-string`：用来提取url参数

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/03_proj_weibo_client/weibo-app/
> $ npm install query-string -S
> ...
> + query-string@6.13.7
> ...
> ~~~

(2) 代码

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/9babaeb0f52a474599ab18ca48ba903a9154316b) 
> 
> * [/src/pages/Login/index.js](https://github.com/fangkun119/java_proj_ref/blob/9babaeb0f52a474599ab18ca48ba903a9154316b/200_react/03_proj_weibo_client/weibo-app/src/pages/Login/index.js)：登录跳转页
> * [/src/router/index.js](https://github.com/fangkun119/java_proj_ref/commit/9babaeb0f52a474599ab18ca48ba903a9154316b) ：将登录跳转页添加到`react router`中

## 15. 发送微博评论功能

(1) 后端API

> Weibo API发评论接口：[https://open.weibo.com/wiki/2/comments/create](https://open.weibo.com/wiki/2/comments/create) 
> 
> 参数包括：`access_token`，`comment`（评论内容），`id` （被评论的微博id），`comment_ori`（如果是转发微博、是否评论给原微博），`rip`（开发者上报操作用户的真实IP）

(2) 发送添加评论请求到后端

代码：[git comment](https://github.com/fangkun119/java_proj_ref/commit/9f96ad7743eadd3013fac079bdbb3c81fd73ef50) 

> * [/src/actions/comments.js](https://github.com/fangkun119/java_proj_ref/blob/9f96ad7743eadd3013fac079bdbb3c81fd73ef50/200_react/03_proj_weibo_client/weibo-app/src/actions/comments.js)：action
> * [/src/api/comments.js](https://github.com/fangkun119/java_proj_ref/blob/9f96ad7743eadd3013fac079bdbb3c81fd73ef50/200_react/03_proj_weibo_client/weibo-app/src/api/comments.js)：api
> * [/src/pages/New/index.js](https://github.com/fangkun119/java_proj_ref/blob/9f96ad7743eadd3013fac079bdbb3c81fd73ef50/200_react/03_proj_weibo_client/weibo-app/src/pages/New/index.js)：view、复用发微博的组件
> * [/src/router/index.js](https://github.com/fangkun119/java_proj_ref/blob/9f96ad7743eadd3013fac079bdbb3c81fd73ef50/200_react/03_proj_weibo_client/weibo-app/src/router/index.js)：修改react router，发微博和发评论使用不同的url，但映射到相同的组件
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/9f96ad7743eadd3013fac079bdbb3c81fd73ef50/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)，[/src/pages/Home/components/post/index.js](https://github.com/fangkun119/java_proj_ref/blob/9f96ad7743eadd3013fac079bdbb3c81fd73ef50/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/post/index.js)：传入帖子id给用于发评论的组件
> * [/src/setupProxy.js](https://github.com/fangkun119/java_proj_ref/blob/9f96ad7743eadd3013fac079bdbb3c81fd73ef50/200_react/03_proj_weibo_client/weibo-app/src/setupProxy.js)：调试过程中尝试修改了proxy发送到微博后端的http header，但最终未使用该修改
> * [/src/utils/interceptors.js](https://github.com/fangkun119/java_proj_ref/blob/9f96ad7743eadd3013fac079bdbb3c81fd73ef50/200_react/03_proj_weibo_client/weibo-app/src/utils/interceptors.js)：对拦截器的修改，使请求成功/失败时的用户提示更友好

## 16. 微博评论列表页面

### (1) 评论列表页面 

请求后端获取评论列表（以便知道评论的格式），在`react router`中添加空的评论列表页面

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/5ccb0b58b9c5c590f7813821d3f3451c53422c6e) 

> [/src/actions/comments.js](https://github.com/fangkun119/java_proj_ref/blob/5ccb0b58b9c5c590f7813821d3f3451c53422c6e/200_react/03_proj_weibo_client/weibo-app/src/actions/comments.js)：action
>
> [/src/api/comments.js](https://github.com/fangkun119/java_proj_ref/blob/5ccb0b58b9c5c590f7813821d3f3451c53422c6e/200_react/03_proj_weibo_client/weibo-app/src/api/comments.js)：api
>
> [/jsconfig.json](https://github.com/fangkun119/java_proj_ref/blob/5ccb0b58b9c5c590f7813821d3f3451c53422c6e/200_react/03_proj_weibo_client/weibo-app/jsconfig.json)：用来缩短import的长度 
>
> ~~~json
> {
>     "_comment_1" : "配置文件，减少import文件时的路径长度，修改后重启生效",
>     "_comment_2" : "例如 /src/pages/Home/components/post/index.js 文件中的 import { getComments } from 'actions/comments';", 
>     "compilerOptions": {
>         "baseUrl":"src"
>     },
>     "include":["src"]
> }
> ~~~
>
> [/src/pages/Home/components/post/index.js](https://github.com/fangkun119/java_proj_ref/blob/5ccb0b58b9c5c590f7813821d3f3451c53422c6e/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/post/index.js)：因为使用了`jsconfig.json`，下面两种import是等价的
>
> ~~~javascript
> ...
> // import { getComments } from '../../../../actions/comments';
> import { getComments } from 'actions/comments'; 
> ...
> const Post = ({ id, text,  user,  created_at,  source,  pic_urls, reposts_count,  attitudes_count, comments_count,  retweeted_status,  type
> }) => {
>     const dispatch = useDispatch();
>     const handleClickComment = () => {
>         if (!comments_count) {
>             window.location.href = `/comments/${id}`;
>         } else {
>             dispatch(getComments({id}));  //解除这行的注释，可以触发action，看到后端返回数据的格式
>             window.location.href = `/details/${id}`;
>         }
>     };
>     ...
> }
> ~~~
>
> [/src/pages/Details/index.js](https://github.com/fangkun119/java_proj_ref/blob/5ccb0b58b9c5c590f7813821d3f3451c53422c6e/200_react/03_proj_weibo_client/weibo-app/src/pages/Details/index.js)：空的评论列表页面（还未添加样式组件）
>
> [/src/router/index.js](https://github.com/fangkun119/java_proj_ref/blob/5ccb0b58b9c5c590f7813821d3f3451c53422c6e/200_react/03_proj_weibo_client/weibo-app/src/router/index.js)：将评论列表页面添加到`react router`中

### (2) 根据id获取微博原文、以便展示在评论列表页上

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/75e0df13c058d7b620acf44d06bab344924db30d) 

> * [/src/actions/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/75e0df13c058d7b620acf44d06bab344924db30d/200_react/03_proj_weibo_client/weibo-app/src/actions/timeline.js)：action
> * [/src/api/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/75e0df13c058d7b620acf44d06bab344924db30d/200_react/03_proj_weibo_client/weibo-app/src/api/timeline.js)：api
> * [/src/pages/Details/index.js](https://github.com/fangkun119/java_proj_ref/blob/75e0df13c058d7b620acf44d06bab344924db30d/200_react/03_proj_weibo_client/weibo-app/src/pages/Details/index.js): view 
> 

测试时发现这个API只有授权用户才可以用，先暂停评论页面开发，转到其他功能

## 17. `OAuth`登录3：用`login code`换取`access token`以使用其他API

代码：[git commit 1](https://github.com/fangkun119/java_proj_ref/commit/f2240d400d78d4b2845e69630d9e1cfa157883e5) 

> * [/src/pages/Login/index.js](https://github.com/fangkun119/java_proj_ref/blob/f2240d400d78d4b2845e69630d9e1cfa157883e5/200_react/03_proj_weibo_client/weibo-app/src/pages/Login/index.js)：view，触发action
> * [/src/actions/account.js](https://github.com/fangkun119/java_proj_ref/blob/f2240d400d78d4b2845e69630d9e1cfa157883e5/200_react/03_proj_weibo_client/weibo-app/src/actions/account.js)：action
> * [/src/api/account.js](https://github.com/fangkun119/java_proj_ref/blob/f2240d400d78d4b2845e69630d9e1cfa157883e5/200_react/03_proj_weibo_client/weibo-app/src/api/account.js)：api
> * [/src/utils/interceptors.js](https://github.com/fangkun119/java_proj_ref/blob/f2240d400d78d4b2845e69630d9e1cfa157883e5/200_react/03_proj_weibo_client/weibo-app/src/utils/interceptors.js)：获取的access token由拦截器加入到每个http请求中
> * [/src/constants/index.js](https://github.com/fangkun119/java_proj_ref/blob/f2240d400d78d4b2845e69630d9e1cfa157883e5/200_react/03_proj_weibo_client/weibo-app/src/constants/index.js)：常量
> 
> ~~~javascript
> // 微博授权机制Wiki：https://open.weibo.com/wiki/%E6%8E%88%E6%9D%83%E6%9C%BA%E5%88%B6#.E6.8E.88.E6.9D.83.E6.9C.89.E6.95.88.E6.9C.9F
> // 1. 引导需要授权的用户到如下地址：
> export const LOGIN_URL = `https://api.weibo.com/oauth2/authorize?client_id=${APP_KEY}&response_type=code&redirect_uri=${REDIRECT_URL}`
> // 2. 如果用户同意授权，页面跳转至 YOUR_REGISTERED_REDIRECT_URI/?code=CODE
> // 3. 换取Access Token，URL：
> //    https://api.weibo.com/oauth2/access_token?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=authorization_code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE
> export const accessTokenUrl = (code) => (
>     `/proxy/oauth2/access_token?client_id=${APP_KEY}&client_secret=${APP_SECRET}&grant_type=authorization_code&redirect_uri=${REDIRECT_URL}&code=${code}` //有跨域问题、要使用proxy
> );
> // 4. 使用获得的Access Token调用API
> ~~~
> 

代码：[git commit 2](https://github.com/fangkun119/java_proj_ref/commit/d8fc9543caac3bdcd461fa8d32b39f46027e7e56) ，bug fix，写入localStorage时使用的key与拦截器保持一致

## 18. 微博评论列表页面（2）

> 因为没有获取单条微博的API权限，放弃`16. 微博评论列表页面`中用一个单独页面展示单条微博及其评论的方法，改为在`Home`页面下将评论列表展开显示

### (1) 点击数量大于0的“评论按钮”时，可以获取并展开评论列表

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/37e739d15fbc6a17ae0695d33985caef58bbfa36)

> * [/src/pages/Home/components/post/index.js](https://github.com/fangkun119/java_proj_ref/blob/37e739d15fbc6a17ae0695d33985caef58bbfa36/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/post/index.js)：view (`Post`)，点击Post组件的评论按钮、并且评论数大于0时，会触发两个action：(1) `getComments`获取评论列表 (2) `setCurrentPost`标记哪条微博要展开评论列表 
> * [/src/actions/comments.js](https://github.com/fangkun119/java_proj_ref/blob/37e739d15fbc6a17ae0695d33985caef58bbfa36/200_react/03_proj_weibo_client/weibo-app/src/actions/comments.js)：action从后端拿到comment列表后，继续dispatch给reducer
> * [/src/actions/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/37e739d15fbc6a17ae0695d33985caef58bbfa36/200_react/03_proj_weibo_client/weibo-app/src/actions/timeline.js)：
> 	* 删除之前的`getPost` action，没有获取单条微博API的权限
> 	* 替换为`setCurrentPost` action，该action不访问后端，只是将上游代码传入的post id分发给reducer，用来标记用户是想展开哪条微博的评论列表
> * [/src/reducers/comments.js](https://github.com/fangkun119/java_proj_ref/blob/37e739d15fbc6a17ae0695d33985caef58bbfa36/200_react/03_proj_weibo_client/weibo-app/src/reducers/comments.js)，[/src/reducers/index.js](https://github.com/fangkun119/java_proj_ref/blob/37e739d15fbc6a17ae0695d33985caef58bbfa36/200_react/03_proj_weibo_client/weibo-app/src/reducers/index.js)：用来处理`getComments`和`setCurrentPost`的两个reducer，处理数据并存入store
> * [/src/reducers/index.js](https://github.com/fangkun119/java_proj_ref/blob/37e739d15fbc6a17ae0695d33985caef58bbfa36/200_react/03_proj_weibo_client/weibo-app/src/reducers/index.js)：向root reducer中加入新建的reducer
> * [/weibo-app/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/37e739d15fbc6a17ae0695d33985caef58bbfa36/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)：view（`Home`），从`store/state`中拿到评论列表、以及评论所属的微博id，为该条微博展开评论列表

### (2) 关闭评论列表功能

功能：评论列表展开状态下，再次点击评论按钮，可以关闭评论列表

代码：[git commit 1](https://github.com/fangkun119/java_proj_ref/commit/e964d6b479e06edfe858223f307d17f8c6a769b0)

> * [/src/pages/Home/components/post/index.js](https://github.com/fangkun119/java_proj_ref/blob/e964d6b479e06edfe858223f307d17f8c6a769b0/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/post/index.js)：view，根据评论列表是否展开、在点击评论按钮时触发不同的action
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/e964d6b479e06edfe858223f307d17f8c6a769b0/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)：外层组件、传参给`Post`组件使其可以知道评论列表是否处于展开状态（因为现实评论列表是在`Home`组件中实现的
> 

代码：[git commit 2](https://github.com/fangkun119/java_proj_ref/commit/9d5e06c6bc98fb6a930dc9b50e4236e14ee03c78), bug fix

### (3) 将`Home`组件中与评论列表相关的代码拆分到专用的`commentsList`组件

代码：[git commit 1](https://github.com/fangkun119/java_proj_ref/commit/8159878d6617e3e6e98f0572e1fbbdada0353963) 

> * [/src/pages/Home/components/commentsList/index.js](https://github.com/fangkun119/java_proj_ref/blob/8159878d6617e3e6e98f0572e1fbbdada0353963/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/commentsList/index.js)，[/Home/components/commentsList/index.module.scss](https://github.com/fangkun119/java_proj_ref/blob/8159878d6617e3e6e98f0572e1fbbdada0353963/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/commentsList/index.module.scss)：`commentsList`组件
> * [/src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/8159878d6617e3e6e98f0572e1fbbdada0353963/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.js)，[/src/pages/Home/index.module.scss](https://github.com/fangkun119/java_proj_ref/blob/8159878d6617e3e6e98f0572e1fbbdada0353963/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/index.module.scss)：`Home`组件
> 

代码：[git commit 2](https://github.com/fangkun119/java_proj_ref/commit/b85012a1021eac523b237c68199a1e34e8f84040)，清理遗留的代码（已迁移到commentsList)

### (4) 将`Post`组件中与评论列表相关的代码迁移到`commentsList`组件中

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/913c49c9b9f9e27934da470b7fe7c087065e0e4f) 

### (5) 评论分页加载功能

评论展开后、每页最多增加5条评论，如果还有更多评论未展示，底部会显示一个“加载更多”按钮

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/3d5cffebf6e793d28a6c6534f8f2e763a571690a) 

> * [/src/actions/comments.js](https://github.com/fangkun119/java_proj_ref/blob/3d5cffebf6e793d28a6c6534f8f2e763a571690a/200_react/03_proj_weibo_client/weibo-app/src/actions/comments.js)：获取评论列表的action，在params中增加当前请求第几页`page`，另外增加总评论条数`total_number` 
> * [/src/reducers/comments.js](https://github.com/fangkun119/java_proj_ref/blob/3d5cffebf6e793d28a6c6534f8f2e763a571690a/200_react/03_proj_weibo_client/weibo-app/src/reducers/comments.js)：处理评论列表的reducer，合并并去重微博评论，传递page，total参数
> * [/Home/components/commentsList/index.js](https://github.com/fangkun119/java_proj_ref/blob/3d5cffebf6e793d28a6c6534f8f2e763a571690a/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/commentsList/index.js)：触发评论加载的action，从store/state中取数据渲染评论列表

### (6) Fix Compile Warnings

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/80fa291ed51b12bf21d312a8af852bfbcd8ad2f0) 

> ~~~javascript
>  // action获取评论列表后，会由reducer处理并存储在store/state中，这里从store/state提取评论列表
>  const { comments = [], page = 0, total } = useMappedState((state) => (state.comments)); 
>    
>     // dispatch
>      const dispatch = useDispatch();        
>    
>     // 滚动条滚到到底部时，触发获取下一页评论列表的action
>      // useCallback(() => {...}, [dispatch, id, page])
>     //    把内联回调函数及依赖项数组作为参数传入 useCallback，它将返回该回调函数的 memoized 版本，该回调函数仅在某个依赖项改变时才会更新
>     //    对这个例子：当disaptch,id,page之一发生变化时，该回调函数也会发生变化
>     // 参考（HookAPI索引）: https://zh-hans.reactjs.org/docs/hooks-reference.html#usecallback
>     const handleInfiniteOnLoad = useCallback(() => { // add 'useCallback' for fix warning "The 'handleInfiniteOnLoad' function makes the dependencies of useEffect Hook (at line 23) change on every render. To fix this, wrap the definition of 'handleInfiniteOnLoad' in its own useCallback() Hook"
>         dispatch(getComments({ id, page : page + 1, count: COMMENT_PAGESIZE }));
>     }, [dispatch, id, page]); // add dependencies for error "React Hook useCallback does nothing when called with only one argument. Did you forget to pass an array of dependencies?"
>    
>     // CommentList组件加载时，触发获取评论列表的action
>  useEffect(() => {
>         handleInfiniteOnLoad();
>     }, []);  
>     // }, [handleInfiniteOnLoad]); 
>     // 不希望 handleInfiniteOnLoad 发生变化时，加载下一页评论，将其从useEffect依赖中移除
>     // 否则会造成死循环（调用handleInfiniteOnLoad会更新page，而更新page会导致handleInfiniteOnLoad发生变化）
>    ~~~

### (7) Fix Bug：当点击另一条微博的评论按钮时、重置`state/store`中的`comment list`

代码：[git comment](https://github.com/fangkun119/java_proj_ref/commit/187e3801bd6a797f4f982b64c2d780387f0f31ad) 

> * [/src/actions/comments.js](https://github.com/fangkun119/java_proj_ref/blob/187e3801bd6a797f4f982b64c2d780387f0f31ad/200_react/03_proj_weibo_client/weibo-app/src/actions/comments.js)：增加`resetComments` action
> * [/src/reducers/comments.js](https://github.com/fangkun119/java_proj_ref/blob/187e3801bd6a797f4f982b64c2d780387f0f31ad/200_react/03_proj_weibo_client/weibo-app/src/reducers/comments.js)：reducer中增加对`resetComments` action的处理
> * [/src/actions/timeline.js](https://github.com/fangkun119/java_proj_ref/blob/187e3801bd6a797f4f982b64c2d780387f0f31ad/200_react/03_proj_weibo_client/weibo-app/src/actions/timeline.js)：由`setCurrentPost` action来触发`resetComments` action
> 
> ~~~javascript
> export function setCurrentPost(payload = {}) {
>     return async (dispatch) => {
>         // 组件更新顺序为
>         // 1. 用户点击“评论”按钮，触发setCurrentPost action
>         // 2. await dispatch(resetComments())：先将评论列表清空
>         // 3. dispatch payload，更新current的值为被点击的帖子的post_id
>         // 4. Home组件渲染时，在id === current的<Post>组件下方挂载<CommentsList>组件
>         // 5. CommentList组件触发getComments action获取评论列表
>         await dispatch(resetComments()); // 阻塞等待异步reset完成后，再dispatch payload
>         dispatch({
>             type: SET_CURRENT_POST, 
>             payload,
>         })
>     }
> }
> ~~~

## 19. 增加微博评论

展开评论列表时，同时显示一个输入框，用来输入微博评论


代码：[git commit 1](https://github.com/fangkun119/java_proj_ref/commit/ade5acb473d8695c1205acd3e72e37775b696dd7)，[git commit 2](https://github.com/fangkun119/java_proj_ref/commit/655f5c97e7baed41b404ee141e907aadde363f6d) 

> * [src/actions/comments.js](https://github.com/fangkun119/java_proj_ref/blob/655f5c97e7baed41b404ee141e907aadde363f6d/200_react/03_proj_weibo_client/weibo-app/src/actions/comments.js)：增加一个action、用来在comment列表（已有其他评论、即`isFirst === false`）新增评论
>* [/src/reducers/comments.js](https://github.com/fangkun119/java_proj_ref/blob/655f5c97e7baed41b404ee141e907aadde363f6d/200_react/03_proj_weibo_client/weibo-app/src/reducers/comments.js)：reducer中增加对`ADD_COMMENT`的处理，它将新创建的评论与已有的评论列表合并，以便能够触发页面渲染，将新建的comment也显示在评论页表中
> * [/src/pages/Home/components/commentsList/index.js](https://github.com/fangkun119/java_proj_ref/blob/655f5c97e7baed41b404ee141e907aadde363f6d/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/commentsList/index.js)：view，触发`ADD_COMMENT`  action，并清空输入框

## 20. 删除微博评论

代码: [git comment](https://github.com/fangkun119/java_proj_ref/commit/bea8aeb4572b591477b78cade80d695bf4eb9a4a) 

> * [/src/pages/Home/components/commentsList/index.js](https://github.com/fangkun119/java_proj_ref/blob/bea8aeb4572b591477b78cade80d695bf4eb9a4a/200_react/03_proj_weibo_client/weibo-app/src/pages/Home/components/commentsList/index.js)：为自己发的微博、显示“删除”链接、以及点击后的警示弹窗、确认后触发`deleteComment` action
> * [/src/actions/comments.js](https://github.com/fangkun119/java_proj_ref/blob/bea8aeb4572b591477b78cade80d695bf4eb9a4a/200_react/03_proj_weibo_client/weibo-app/src/actions/comments.js)：增加`deleteComment` action
> * [/src/api/comments.js](https://github.com/fangkun119/java_proj_ref/blob/bea8aeb4572b591477b78cade80d695bf4eb9a4a/200_react/03_proj_weibo_client/weibo-app/src/api/comments.js)：增加`deleteComment` api

辅助代码 

> * [/src/actions/account.js](https://github.com/fangkun119/java_proj_ref/blob/bea8aeb4572b591477b78cade80d695bf4eb9a4a/200_react/03_proj_weibo_client/weibo-app/src/actions/account.js)：在登录用的`getAccess`  action中增加保存登录用户id的操作 
> * [/src/constants/index.js](https://github.com/fangkun119/java_proj_ref/blob/bea8aeb4572b591477b78cade80d695bf4eb9a4a/200_react/03_proj_weibo_client/weibo-app/src/constants/index.js)：增加用于获取登录用户id的公用函数 


