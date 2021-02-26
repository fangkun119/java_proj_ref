<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [02 `个人中心`项目（下）](#02-%E4%B8%AA%E4%BA%BA%E4%B8%AD%E5%BF%83%E9%A1%B9%E7%9B%AE%E4%B8%8B)
  - [10. 在`react router`中用`lazy import`替换`import`](#10-%E5%9C%A8react-router%E4%B8%AD%E7%94%A8lazy-import%E6%9B%BF%E6%8D%A2import)
  - [11. `Home`页面](#11-home%E9%A1%B5%E9%9D%A2)
    - [11.1 页面布局及屏幕尺寸排版自适应](#111-%E9%A1%B5%E9%9D%A2%E5%B8%83%E5%B1%80%E5%8F%8A%E5%B1%8F%E5%B9%95%E5%B0%BA%E5%AF%B8%E6%8E%92%E7%89%88%E8%87%AA%E9%80%82%E5%BA%94)
    - [11.2 右侧`<Card>`内的`<Tab>`导航栏](#112-%E5%8F%B3%E4%BE%A7card%E5%86%85%E7%9A%84tab%E5%AF%BC%E8%88%AA%E6%A0%8F)
    - [11.3 左侧`<Card>`](#113-%E5%B7%A6%E4%BE%A7card)
      - [11.3.1 用户信息展示](#1131-%E7%94%A8%E6%88%B7%E4%BF%A1%E6%81%AF%E5%B1%95%E7%A4%BA)
      - [11.3.2 用户所属team的标签展示](#1132-%E7%94%A8%E6%88%B7%E6%89%80%E5%B1%9Eteam%E7%9A%84%E6%A0%87%E7%AD%BE%E5%B1%95%E7%A4%BA)
      - [11.3.3 用户个人标签：显示标签](#1133-%E7%94%A8%E6%88%B7%E4%B8%AA%E4%BA%BA%E6%A0%87%E7%AD%BE%E6%98%BE%E7%A4%BA%E6%A0%87%E7%AD%BE)
      - [11.3.4 用户个人标签：增加新标签](#1134-%E7%94%A8%E6%88%B7%E4%B8%AA%E4%BA%BA%E6%A0%87%E7%AD%BE%E5%A2%9E%E5%8A%A0%E6%96%B0%E6%A0%87%E7%AD%BE)
    - [11.4 右侧`Card`：文章选项卡、应用选项卡、项目选项卡](#114-%E5%8F%B3%E4%BE%A7card%E6%96%87%E7%AB%A0%E9%80%89%E9%A1%B9%E5%8D%A1%E5%BA%94%E7%94%A8%E9%80%89%E9%A1%B9%E5%8D%A1%E9%A1%B9%E7%9B%AE%E9%80%89%E9%A1%B9%E5%8D%A1)
      - [11.4.1 文章选项卡](#1141-%E6%96%87%E7%AB%A0%E9%80%89%E9%A1%B9%E5%8D%A1)
      - [11.4.2 应用选项卡](#1142-%E5%BA%94%E7%94%A8%E9%80%89%E9%A1%B9%E5%8D%A1)
      - [11.4.3 项目选项卡](#1143-%E9%A1%B9%E7%9B%AE%E9%80%89%E9%A1%B9%E5%8D%A1)
  - [12. Redux，Axios安装、引入和使用](#12-reduxaxios%E5%AE%89%E8%A3%85%E5%BC%95%E5%85%A5%E5%92%8C%E4%BD%BF%E7%94%A8)
    - [12.1. 安装redux及axios](#121-%E5%AE%89%E8%A3%85redux%E5%8F%8Aaxios)
    - [12.2. 引入redux](#122-%E5%BC%95%E5%85%A5redux)
    - [12.3. 使用`redux`, `axio`实现验证码请求](#123-%E4%BD%BF%E7%94%A8redux-axio%E5%AE%9E%E7%8E%B0%E9%AA%8C%E8%AF%81%E7%A0%81%E8%AF%B7%E6%B1%82)
    - [12.4. 继续实现用户注册功能](#124-%E7%BB%A7%E7%BB%AD%E5%AE%9E%E7%8E%B0%E7%94%A8%E6%88%B7%E6%B3%A8%E5%86%8C%E5%8A%9F%E8%83%BD)
  - [13. 使用`Ajax`拦截器从后端Response中选取需要的数据](#13-%E4%BD%BF%E7%94%A8ajax%E6%8B%A6%E6%88%AA%E5%99%A8%E4%BB%8E%E5%90%8E%E7%AB%AFresponse%E4%B8%AD%E9%80%89%E5%8F%96%E9%9C%80%E8%A6%81%E7%9A%84%E6%95%B0%E6%8D%AE)
  - [14.登录功能实现](#14%E7%99%BB%E5%BD%95%E5%8A%9F%E8%83%BD%E5%AE%9E%E7%8E%B0)
    - [(1) 用`Redux`、`Ajax`发送登录请求并获取token](#1-%E7%94%A8reduxajax%E5%8F%91%E9%80%81%E7%99%BB%E5%BD%95%E8%AF%B7%E6%B1%82%E5%B9%B6%E8%8E%B7%E5%8F%96token)
    - [(2) 用`拦截器`将token添加到http请求头中，并跳转到首页](#2-%E7%94%A8%E6%8B%A6%E6%88%AA%E5%99%A8%E5%B0%86token%E6%B7%BB%E5%8A%A0%E5%88%B0http%E8%AF%B7%E6%B1%82%E5%A4%B4%E4%B8%AD%E5%B9%B6%E8%B7%B3%E8%BD%AC%E5%88%B0%E9%A6%96%E9%A1%B5)
  - [15.用`Redux`、`Ajax`完成用户信息页展示](#15%E7%94%A8reduxajax%E5%AE%8C%E6%88%90%E7%94%A8%E6%88%B7%E4%BF%A1%E6%81%AF%E9%A1%B5%E5%B1%95%E7%A4%BA)
    - [(1) `view`触发`action`执行、从后端获取数据](#1-view%E8%A7%A6%E5%8F%91action%E6%89%A7%E8%A1%8C%E4%BB%8E%E5%90%8E%E7%AB%AF%E8%8E%B7%E5%8F%96%E6%95%B0%E6%8D%AE)
    - [(2) 将`action`获取的数据`dispatch`给`reducer`进行加工](#2-%E5%B0%86action%E8%8E%B7%E5%8F%96%E7%9A%84%E6%95%B0%E6%8D%AEdispatch%E7%BB%99reducer%E8%BF%9B%E8%A1%8C%E5%8A%A0%E5%B7%A5)
    - [(3) 安装dev插件、查看reducer中的数据](#3-%E5%AE%89%E8%A3%85dev%E6%8F%92%E4%BB%B6%E6%9F%A5%E7%9C%8Breducer%E4%B8%AD%E7%9A%84%E6%95%B0%E6%8D%AE)
    - [(4) 让`store/state`订阅`reducer`的数据](#4-%E8%AE%A9storestate%E8%AE%A2%E9%98%85reducer%E7%9A%84%E6%95%B0%E6%8D%AE)
    - [(5) 切换页面渲染数据源到`store/state` (演示）](#5-%E5%88%87%E6%8D%A2%E9%A1%B5%E9%9D%A2%E6%B8%B2%E6%9F%93%E6%95%B0%E6%8D%AE%E6%BA%90%E5%88%B0storestate-%E6%BC%94%E7%A4%BA)
  - [16 `token`失效时跳转到登录页](#16-token%E5%A4%B1%E6%95%88%E6%97%B6%E8%B7%B3%E8%BD%AC%E5%88%B0%E7%99%BB%E5%BD%95%E9%A1%B5)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# 02 `个人中心`项目（下）

## 10. 在`react router`中用`lazy import`替换`import`

`普通 import`会阻塞页面直到所有路由目标页面都加载完毕

`lazy import`可以按需加载，配合`<Suspense>`标签可以展示加载时的loading提示

代码

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/a4866ceaf8db0a3b30748181eb2b18dbc7da39e4)
>
> [src/router/index.js](https://github.com/fangkun119/java_proj_ref/blob/a4866ceaf8db0a3b30748181eb2b18dbc7da39e4/200_react/02B_proj_personal_center_stage2/personal-app/src/router/index.js)
>
> [src/router/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/a4866ceaf8db0a3b30748181eb2b18dbc7da39e4/200_react/02B_proj_personal_center_stage2/personal-app/src/router/index.module.less)

## 11. `Home`页面

### 11.1 页面布局及屏幕尺寸排版自适应

代码

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/b5e1191e18bc3d995e6b2cb2069454112659334f)
>
> [src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/b5e1191e18bc3d995e6b2cb2069454112659334f/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.js)
>
> [src/pages/Home/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/b5e1191e18bc3d995e6b2cb2069454112659334f/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.module.less) 

代码中栅格的属性

> `lg`-表示大屏时所占的栅格；`md`-表示中小屏时所占的栅格</br>
>
> 关于`antd`响应式栅格的熔断点（尺寸切换阈值），见文档[https://ant.design/components/grid-cn/](https://ant.design/components/grid-cn/) ，他们拓展自[`boostrap`的规则](https://getbootstrap.com/docs/4.0/layout/overview/#responsive-breakpoints)

平板设备布局

> 在横屏状态下
> * 大多可以直接使用桌面布局，或只进行一些细微的调整
> * 还有常用的左右双栏布局是非常适合横屏模式下的平板
>
> 竖屏模式下，通常有两种常用解决方案：
> * 一是设计为较窄的双栏模式；
> * 二是将桌面端的布局由左右调整为上下，实现宽屏内容的竖向填充。 

### 11.2 右侧`<Card>`内的`<Tab>`导航栏

代码（commit 1）

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/10345ee1e50b22e8c8dd2547635f6ac72cb127d7) 
>
> [src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/10345ee1e50b22e8c8dd2547635f6ac72cb127d7/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.js)

代码（commit 2）

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/a779bb0833d67a8685cd4c62a5684c0982a4bc50) 
>
> [src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/a779bb0833d67a8685cd4c62a5684c0982a4bc50/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.js)
>
> [src/components/Projects/index.js](https://github.com/fangkun119/java_proj_ref/blob/a779bb0833d67a8685cd4c62a5684c0982a4bc50/200_react/02B_proj_personal_center_stage2/personal-app/src/components/Projects/index.js)
>
> [src/components/Articles/index.js](https://github.com/fangkun119/java_proj_ref/blob/a779bb0833d67a8685cd4c62a5684c0982a4bc50/200_react/02B_proj_personal_center_stage2/personal-app/src/components/Articles/index.js)

### 11.3 左侧`<Card>`

#### 11.3.1 用户信息展示

代码

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/8b70dcbd32e5828b42bb9cb89bb8e0b15c23e073) 
>
> [src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/8b70dcbd32e5828b42bb9cb89bb8e0b15c23e073/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.js) 
>
> [src/pages/Home/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/8b70dcbd32e5828b42bb9cb89bb8e0b15c23e073/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.module.less) 

#### 11.3.2 用户所属team的标签展示

代码

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/f632ffb3bdbc621d28c391ef9016a3c57a180561) 
>
> [src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/f632ffb3bdbc621d28c391ef9016a3c57a180561/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.js) 
>
> [src/pages/Home/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/f632ffb3bdbc621d28c391ef9016a3c57a180561/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.module.less)

#### 11.3.3 用户个人标签：显示标签

代码

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/f3f329a80e69d871e7dfe78e5368d856bdc9dd64)
>
> [src/components/TagList/index.js](https://github.com/fangkun119/java_proj_ref/blob/f3f329a80e69d871e7dfe78e5368d856bdc9dd64/200_react/02B_proj_personal_center_stage2/personal-app/src/components/TagList/index.js)
>
> [src/components/TagList/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/f3f329a80e69d871e7dfe78e5368d856bdc9dd64/200_react/02B_proj_personal_center_stage2/personal-app/src/components/TagList/index.module.less)

#### 11.3.4 用户个人标签：增加新标签

代码

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/142a7363cf6a4a0350aef1e3624beccfcd2e5a0d#diff-d662d58efdfce16c06bda8ac500bf86f2384fd6a7b33a7c97dfb69b1453720fa) 
>
> [src/components/TagList/index.js](https://github.com/fangkun119/java_proj_ref/blob/142a7363cf6a4a0350aef1e3624beccfcd2e5a0d/200_react/02B_proj_personal_center_stage2/personal-app/src/components/TagList/index.js)

### 11.4 右侧`Card`：文章选项卡、应用选项卡、项目选项卡

#### 11.4.1 文章选项卡

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/adeb607a4d02ffe0c9647c96fa402266713872d8#diff-40a13f60b39d0c791105efc4e0187ba1bb14f36074708cba318015c5a3c78311) 

Components updated

> [src/components/Articles/index.js](https://github.com/fangkun119/java_proj_ref/blob/adeb607a4d02ffe0c9647c96fa402266713872d8/200_react/02B_proj_personal_center_stage2/personal-app/src/components/Articles/index.js)
>
> [src/components/Articles/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/adeb607a4d02ffe0c9647c96fa402266713872d8/200_react/02B_proj_personal_center_stage2/personal-app/src/components/Articles/index.module.less)
>
> [src/components/ArticalListContent/index.js](https://github.com/fangkun119/java_proj_ref/blob/adeb607a4d02ffe0c9647c96fa402266713872d8/200_react/02B_proj_personal_center_stage2/personal-app/src/components/ArticalListContent/index.js)
>
> [src/components/ArticalListContent/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/adeb607a4d02ffe0c9647c96fa402266713872d8/200_react/02B_proj_personal_center_stage2/personal-app/src/components/ArticalListContent/index.module.less)

Bug Fix

> [/src/components/TagList/index.js](200_react/02B_proj_personal_center_stage2/personal-app/src/components/TagList/index.js)
>
> [/src/pages/Home/index.js](200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.js)
>
> [/src/pages/Home/index.module.less](200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.module.less)
> 
> [/personal-app/src/pages/Home/index.module.less](200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.module.less)

#### 11.4.2 应用选项卡

代码

> * [git commit](https://github.com/fangkun119/java_proj_ref/commit/a5d04569badc5995aa66c90ef3d428bb29e25744#diff-40a13f60b39d0c791105efc4e0187ba1bb14f36074708cba318015c5a3c78311) 
> * [src/components/Applications/index.js](https://github.com/fangkun119/java_proj_ref/blob/a5d04569badc5995aa66c90ef3d428bb29e25744/200_react/02B_proj_personal_center_stage2/personal-app/src/components/Applications/index.js)
> * [src/components/Applications/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/a5d04569badc5995aa66c90ef3d428bb29e25744/200_react/02B_proj_personal_center_stage2/personal-app/src/components/Applications/index.module.less)

#### 11.4.3 项目选项卡

代码

> * [git commit](https://github.com/fangkun119/java_proj_ref/commit/38976c4898c992eeb0ce1605f36d8dbc0936f537#diff-40a13f60b39d0c791105efc4e0187ba1bb14f36074708cba318015c5a3c78311) 
> * [src/components/Projects/index.js](https://github.com/fangkun119/java_proj_ref/blob/38976c4898c992eeb0ce1605f36d8dbc0936f537/200_react/02B_proj_personal_center_stage2/personal-app/src/components/Projects/index.js)

## 12. Redux，Axios安装、引入和使用

### 12.1. 安装redux及axios

> ~~~bash
> # redux
> # redux-thunk : 一个常用的redux-thunk插件、实现异步
> # redux-react-hook：用来衔接redux和react
> # -S:相当于--save，安装到编译依赖中
> npm install redux redux-thunk redux-react-hook -S
> 
> # 用来发HTTP请求
> npm install axios -S 
> ~~~

### 12.2. 引入redux

代码

> * [git commit](https://github.com/fangkun119/java_proj_ref/commit/3c5223199e856aea58732d839cd12e6ab9a3d50d) 
> * [src/reducers/index.js](https://github.com/fangkun119/java_proj_ref/commit/3c5223199e856aea58732d839cd12e6ab9a3d50d#diff-b9adb04f532c44638ffb071704c8b21aa11f161ef14923ba9677c4c8e8ec9a78)
> * [src/index.js](https://github.com/fangkun119/java_proj_ref/commit/3c5223199e856aea58732d839cd12e6ab9a3d50d#diff-2007805909cb3390d4f809d61f799b77caf51527b17150a87024e5d69fac047c)
> * [src/index.js (bug fix)](https://github.com/fangkun119/java_proj_ref/commit/d77a5e9cc563ea69f640886f2f377af18b7b52d7#diff-2007805909cb3390d4f809d61f799b77caf51527b17150a87024e5d69fac047c)

### 12.3. 使用`redux`, `axio`实现验证码请求

代码

> [git commit 1](https://github.com/fangkun119/java_proj_ref/commit/d77a5e9cc563ea69f640886f2f377af18b7b52d7) 
>
> * [view层（src/components/InputItem/index.js）：分发action](https://github.com/fangkun119/java_proj_ref/blob/d77a5e9cc563ea69f640886f2f377af18b7b52d7/200_react/02B_proj_personal_center_stage2/personal-app/src/components/InputItem/index.js) 
> * [注册action：src/actions/register.js](https://github.com/fangkun119/java_proj_ref/blob/d77a5e9cc563ea69f640886f2f377af18b7b52d7/200_react/02B_proj_personal_center_stage2/personal-app/src/actions/register.js)
> * [注册action使用的api：src/api/register.js](https://github.com/fangkun119/java_proj_ref/blob/d77a5e9cc563ea69f640886f2f377af18b7b52d7/200_react/02B_proj_personal_center_stage2/personal-app/src/api/register.js)
> * [公用，Axios封装：src/utils/ajax.js ](https://github.com/fangkun119/java_proj_ref/blob/d77a5e9cc563ea69f640886f2f377af18b7b52d7/200_react/02B_proj_personal_center_stage2/personal-app/src/utils/ajax.js)
> * [公用，常量：src/constants/index.js](https://github.com/fangkun119/java_proj_ref/blob/d77a5e9cc563ea69f640886f2f377af18b7b52d7/200_react/02B_proj_personal_center_stage2/personal-app/src/constants/index.js)
>
> [git commit 2: bug fix](https://github.com/fangkun119/java_proj_ref/commit/c477f930edcf336e269a43f6104e660218b280db)
>
> [git commit 3: bug fix](https://github.com/fangkun119/java_proj_ref/commit/754bfee1f634b3e13cbb7e6503280c8d7ad00227)

### 12.4. 继续实现用户注册功能

代码

> [git commit 1](https://github.com/fangkun119/java_proj_ref/commit/f7391c64a5717174de0b330c123ec1749b4b187f)
>
> * [view层、分发action：src/pages/Register/index.js](https://github.com/fangkun119/java_proj_ref/blob/f7391c64a5717174de0b330c123ec1749b4b187f/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Register/index.js)
> * [action：src/actions/register.js](https://github.com/fangkun119/java_proj_ref/blob/f7391c64a5717174de0b330c123ec1749b4b187f/200_react/02B_proj_personal_center_stage2/personal-app/src/actions/register.js)
> * [api: src/api/register.js](https://github.com/fangkun119/java_proj_ref/blob/f7391c64a5717174de0b330c123ec1749b4b187f/200_react/02B_proj_personal_center_stage2/personal-app/src/api/register.js)
>
> [git commit 2: 注册结果页面提示](https://github.com/fangkun119/java_proj_ref/commit/8924b58c2baa180b22beaee5c19cf891c79d8f8f) 

## 13. 使用`Ajax`拦截器从后端Response中选取需要的数据

代码

> [git commit 1：功能实现](https://github.com/fangkun119/java_proj_ref/commit/80c85d3180a1e04cf7598f8df871eac8d09954a5)
>
> * [拦截器代码：src/utils/interceptors.js](https://github.com/fangkun119/java_proj_ref/blob/80c85d3180a1e04cf7598f8df871eac8d09954a5/200_react/02B_proj_personal_center_stage2/personal-app/src/utils/interceptors.js)
> * [安装拦截器：src/utils/ajax.js](https://github.com/fangkun119/java_proj_ref/blob/80c85d3180a1e04cf7598f8df871eac8d09954a5/200_react/02B_proj_personal_center_stage2/personal-app/src/utils/ajax.js)
> * [使用拦截器之后的效果：src/actions/register.js](https://github.com/fangkun119/java_proj_ref/blob/80c85d3180a1e04cf7598f8df871eac8d09954a5/200_react/02B_proj_personal_center_stage2/personal-app/src/actions/register.js)
>
> [git commit 2：文件重命名](https://github.com/fangkun119/java_proj_ref/commit/65d8d2f2bcf9da712713744d3b3558fdf899439c)
>
>~~~bash
>renamed:    src/actions/register.js -> src/actions/account.js
>renamed:    src/api/register.js -> src/api/account.js
>modified:   src/pages/Register/index.js 
>~~~
> 

## 14.登录功能实现

### (1) 用`Redux`、`Ajax`发送登录请求并获取token

代码

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/6faeaf1659cd71e8382b098dfa233f9c9388bfb5) 
>
> * [view: src/pages/Login/index.js](https://github.com/fangkun119/java_proj_ref/blob/6faeaf1659cd71e8382b098dfa233f9c9388bfb5/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Login/index.js)
> * [actions：src/actions/account.js](https://github.com/fangkun119/java_proj_ref/blob/6faeaf1659cd71e8382b098dfa233f9c9388bfb5/200_react/02B_proj_personal_center_stage2/personal-app/src/actions/account.js)
> * [API：src/api/account.js](https://github.com/fangkun119/java_proj_ref/blob/6faeaf1659cd71e8382b098dfa233f9c9388bfb5/200_react/02B_proj_personal_center_stage2/personal-app/src/api/account.js)

### (2) 用`拦截器`将token添加到http请求头中，并跳转到首页

代码

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/336ba45a755c88610f593f5da04e0c0bdab8b610)
>
> * [/src/actions/account.js](https://github.com/fangkun119/java_proj_ref/blob/336ba45a755c88610f593f5da04e0c0bdab8b610/200_react/02B_proj_personal_center_stage2/personal-app/src/actions/account.js)
> * [/src/utils/interceptors.js](https://github.com/fangkun119/java_proj_ref/blob/336ba45a755c88610f593f5da04e0c0bdab8b610/200_react/02B_proj_personal_center_stage2/personal-app/src/utils/interceptors.js)

在Chrome Dev Tool中查看存储在localStorage中的token

> ~~~txt
>localStorage
> Storage {todos: "[{"text":"ccc"},{"text":"bbb","isComplete":true},{"text":"aaa"}]", personal-app-token: "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiO…I0NH0.I1pZcnY7SfkiquzY1pGNtW0NJLbmvZiujdOPDiOP9-A", length: 2}
> ~~~


HTTP Header中增加的token

> ~~~txt
> Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiI4ZmJkNTViMi0yZGEwLTRiYTItOTgwMS1hOTZlYTRkNjVlZGUiLCJzdWIiOiJ0ZXN0MzIxIiwiaXNzIjoiMDk4ZjZiY2Q0NjIxZDM3M2NhZGU0ZTgzMjYyN2I0ZjYiLCJpYXQiOjE2MDUyNTQyNDQsImF1ZCI6Imp6c2YiLCJleHAiOjE2MDY0NjM4NDQsIm5iZiI6MTYwNTI1NDI0NH0.I1pZcnY7SfkiquzY1pGNtW0NJLbmvZiujdOPDiOP9-A
> ~~~

## 15.用`Redux`、`Ajax`完成用户信息页展示

### (1) `view`触发`action`执行、从后端获取数据

`view` （trigger the action）→ `action`（retrieve data from backend）

代码

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/3ec2895d31324715cd6ab6c98016738f06f0aede) 
>
> * [view: /src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/3ec2895d31324715cd6ab6c98016738f06f0aede/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.js)
> * [actions: /src/actions/profile.js](https://github.com/fangkun119/java_proj_ref/blob/3ec2895d31324715cd6ab6c98016738f06f0aede/200_react/02B_proj_personal_center_stage2/personal-app/src/actions/profile.js)
> * [api: /src/api/profile.js](https://github.com/fangkun119/java_proj_ref/blob/3ec2895d31324715cd6ab6c98016738f06f0aede/200_react/02B_proj_personal_center_stage2/personal-app/src/api/profile.js) 
>
> [git commit 2: fix bug and warnings](https://github.com/fangkun119/java_proj_ref/commit/72f740aae54f9135aa6fb5955190e0c4a8e009c7) 
>
> [git commit 3: fix bug and warnings](https://github.com/fangkun119/java_proj_ref/commit/8180af40213736d127a02e80e9b005142db25903)

在console中打印出的后端Response

>  ~~~txt
>  {
>  	code: 0
>  	data: {username: "test321", userTagList: Array(1), articleList: Array(2)}
>  	message: "操作成功"
>  	__proto__: Object
>  }
>  ~~~

### (2) 将`action`获取的数据`dispatch`给`reducer`进行加工

代码：[git commit](https://github.com/fangkun119/java_proj_ref/commit/d3e0026c060c6b9a8410b586d80b98297830d17b)

[Root Reducer (/src/reducer/index.js)](https://github.com/fangkun119/java_proj_ref/blob/d3e0026c060c6b9a8410b586d80b98297830d17b/200_react/02B_proj_personal_center_stage2/personal-app/src/reducers/index.js)：用于挂载所有的Sub Reducer，分成多个`Sub Reducer`是为了便于数据的分类和存取，被`Store`包裹的组件下的所有子组件都可以通过`Root Reducer`来获取各个`Sub Reducer`的数据

> ~~~javascript
> import { combineReducers } from 'redux'; // 用来把多层reducer组合起来
> import profile from './profile';
> const rootReducer = combineReducers({
> 	profile, // 用于展示个人主页的reducer
> });
> 
> // export出去，由入口文件index.js来import
> export default rootReducer;
> ~~~
>

[Profile Reducer (/src/reducers/profile.js)](https://github.com/fangkun119/java_proj_ref/commit/d3e0026c060c6b9a8410b586d80b98297830d17b)：挂载在root reducer下

> ~~~javascript
>import {
> 	GET_PROFILE 
>} from '../constants/actions';
> 
>const initState = {};
> 
> export default function reducer(state = initState, action) {
> switch (action.type) {
>   	case GET_PROFILE:
>        	console.log('reducers/profile invoked'); // 临时代码，检查这个reducer函数是否被执行
>    		return {
>    			...state,
>    			user: action.payload,
>    		}
>  	default:
>    		return state;
>	}
> }
> ~~~


Profile Reducer的数据是由对应的Profile Action来dispatch得到

[Profile Action (/src/actions/profile.js)](/personal-app/src/actions/profile.js) 

> ~~~javascript
> import { message } from 'antd';
> import * as api from '../api/profile'; 
> import { GET_PROFILE } from '../constants/actions';
> export function getUserProfile(payload = {}) {
>  return async (dispatch /* dispatch从参数传入 */) =>  {
>    	const {
>    		code, 
>    		message:msg, 
>    		data,
>    	} = await api.getUserProfile(payload); 
>    		if (code === 0) {
>    			dispatch({
>    				type: GET_PROFILE,
>    				payload: data
>    			})
>    		} else {
>    			message.error(`${msg}`);
>    		}
>    	}
>    }
>    ~~~

### (3) 安装dev插件、查看reducer中的数据

安装插件

> ~~~bash
> # -S:  虽然是开发工具、但是因为代码中要引入它、因此还是要放到生产环境的dependency中
> npm install redux-devtools-extension -S
> ~~~
> 

同时修改`src/index.js`中创建`store`的代码，用dev tool extension来包裹reducer 

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/881e67ea4972e84e2d7640dcbac589c77c6d2e04) 
>
> ~~~javascript
> ...
> import { composeWithDevTools } from 'redux-devtools-extension';
> ...
> const store = createStore(
> 	// 注入src/reducers/index.js中编写的root reducers
> 	reducer, 
> 	// 用Dev Tools包裹：可以加一个判断，仅在开发环境才用Dev Tools包裹
> 	composeWithDevTools (
> 		applyMiddleware(thunk) // 异步中间件
> 	)
> )
> ~~~
>

如果需要在开发环境下查看reducer数据，需要另外安装Chrome插件

### (4) 让`store/state`订阅`reducer`的数据

> [git commit 1](https://github.com/fangkun119/java_proj_ref/commit/ddbef4e0a5340ddc166c0e8d3e33298d61687ae9)
>
> * [src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/ddbef4e0a5340ddc166c0e8d3e33298d61687ae9/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.js)
>
> [git commit 2: bug fix](https://github.com/fangkun119/java_proj_ref/commit/6df38dcbffe6bc77fb74a4e2334cc3e5a33f6c27)
>
> ~~~javascript
> ...
> import { useDispatch, useMappedState } from 'redux-react-hook';
> ...
> // 使用全局state中的profile部分，后续代码使用redux的useMappedState从中提取后端返回的数据
> const mapState = (state) => (state.profile); 
> ...
> const Home = () => {
>     // 用来从view分发getUserProfile这个action
>     const dispatch = useDispatch();
> 
>     // 用来从全局store/state中提取数据渲染view
>     // 其中useMappedState用来订阅reducer里的状态到mapState中
>     // 要使用的是rootReducer.profile.user (在reducers/profile.js中定义)
>     // * 在mappedState中：制定了使用state.profile
>     // * 在这里用解构赋值进一步制定使用state.profile.user
>     // "user = {}"给数据一个初始值、避免后端返回数据之前发生undefined error
>     const { user = {} } = useMappedState(mapState);
>     // 打印日志、确认已经能拿到后端数据
>     console.log(user);
> 	...
> }
> ~~~

### (5) 切换页面渲染数据源到`store/state` (演示）

> [git commit](https://github.com/fangkun119/java_proj_ref/commit/b7de088ba69f6c4faf0587aac4a80c5836f79cd9) 

## 16 `token`失效时跳转到登录页

> token失效时后端会返回一个约定好的code，可以在拦截器中捕捉并处理
>
> [git commit](https://github.com/fangkun119/java_proj_ref/commit/d24694282f50e0e7c7dde2e5afd82219dc697c11) 

