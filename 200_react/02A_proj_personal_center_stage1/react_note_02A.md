<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [React Note 02-A](#react-note-02-a)
  - [01. `React Router`](#01-react-router)
    - [(1) 介绍](#1-%E4%BB%8B%E7%BB%8D)
    - [(2) 实现原理](#2-%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86)
    - [(3) 框架使用](#3-%E6%A1%86%E6%9E%B6%E4%BD%BF%E7%94%A8)
    - [(4) 浏览器输入地址到页面展示的过程](#4-%E6%B5%8F%E8%A7%88%E5%99%A8%E8%BE%93%E5%85%A5%E5%9C%B0%E5%9D%80%E5%88%B0%E9%A1%B5%E9%9D%A2%E5%B1%95%E7%A4%BA%E7%9A%84%E8%BF%87%E7%A8%8B)
  - [02. 通信协议：HTTP 1/2/3 以及 HTTPS](#02-%E9%80%9A%E4%BF%A1%E5%8D%8F%E8%AE%AEhttp-123-%E4%BB%A5%E5%8F%8A-https)
    - [(1) 状态码](#1-%E7%8A%B6%E6%80%81%E7%A0%81)
    - [(2) 常见字段](#2-%E5%B8%B8%E8%A7%81%E5%AD%97%E6%AE%B5)
    - [(3) Get / Post](#3-get--post)
    - [(4) 各个协议版本](#4-%E5%90%84%E4%B8%AA%E5%8D%8F%E8%AE%AE%E7%89%88%E6%9C%AC)
      - [HTTP 1.1 的优缺点](#http-11-%E7%9A%84%E4%BC%98%E7%BC%BA%E7%82%B9)
      - [HTTP v.s. HTTPS](#http-vs-https)
      - [`HTTP/1.1` v.s `HTTP/1.0`](#http11-vs-http10)
      - [`HTTP/2` v.s `HTTP/1.1`](#http2-vs-http11)
      - [`HTTP/3` v.s `HTTP/2`](#http3-vs-http2)
    - [(6) 常用HTTP请求方法及含义](#6-%E5%B8%B8%E7%94%A8http%E8%AF%B7%E6%B1%82%E6%96%B9%E6%B3%95%E5%8F%8A%E5%90%AB%E4%B9%89)
  - [03. `Ant Design`](#03-ant-design)
    - [(1) 特点](#1-%E7%89%B9%E7%82%B9)
    - [(2) 组件化开发](#2-%E7%BB%84%E4%BB%B6%E5%8C%96%E5%BC%80%E5%8F%91)
    - [(3) `InputItem`实现过程中的一些技巧](#3-inputitem%E5%AE%9E%E7%8E%B0%E8%BF%87%E7%A8%8B%E4%B8%AD%E7%9A%84%E4%B8%80%E4%BA%9B%E6%8A%80%E5%B7%A7)
      - [(a) 使用`{...props}`批量从外层组件传入props给内层组件](#a-%E4%BD%BF%E7%94%A8props%E6%89%B9%E9%87%8F%E4%BB%8E%E5%A4%96%E5%B1%82%E7%BB%84%E4%BB%B6%E4%BC%A0%E5%85%A5props%E7%BB%99%E5%86%85%E5%B1%82%E7%BB%84%E4%BB%B6)
      - [(b) 使用`antd`的`<Form.Item>`来获取`<Input/>`的值](#b-%E4%BD%BF%E7%94%A8antd%E7%9A%84formitem%E6%9D%A5%E8%8E%B7%E5%8F%96input%E7%9A%84%E5%80%BC)
      - [(c) 使用`antd`的表单API来操作状态`form`](#c-%E4%BD%BF%E7%94%A8antd%E7%9A%84%E8%A1%A8%E5%8D%95api%E6%9D%A5%E6%93%8D%E4%BD%9C%E7%8A%B6%E6%80%81form)
      - [(d) 其他`antd`的组件和函数](#d-%E5%85%B6%E4%BB%96antd%E7%9A%84%E7%BB%84%E4%BB%B6%E5%92%8C%E5%87%BD%E6%95%B0)
  - [04. `Redux`](#04-redux)
  - [05. CSS预处理器](#05-css%E9%A2%84%E5%A4%84%E7%90%86%E5%99%A8)
    - [(1) 用途](#1-%E7%94%A8%E9%80%94)
    - [(2) 市面上的预处理器](#2-%E5%B8%82%E9%9D%A2%E4%B8%8A%E7%9A%84%E9%A2%84%E5%A4%84%E7%90%86%E5%99%A8)
      - [(a) `SCSS`](#a-scss)
      - [(b) LESS](#b-less)
      - [(c) Stylus](#c-stylus)
    - [(3) 怎样在create-react-app中加入CSS预处理器](#3-%E6%80%8E%E6%A0%B7%E5%9C%A8create-react-app%E4%B8%AD%E5%8A%A0%E5%85%A5css%E9%A2%84%E5%A4%84%E7%90%86%E5%99%A8)
      - [方法1：使用`yarn eject` / `npm eject`之后手动修改webpack配置](#%E6%96%B9%E6%B3%951%E4%BD%BF%E7%94%A8yarn-eject--npm-eject%E4%B9%8B%E5%90%8E%E6%89%8B%E5%8A%A8%E4%BF%AE%E6%94%B9webpack%E9%85%8D%E7%BD%AE)
      - [方法2：override `create-react-app` (简称`cra`)、不需`eject`可以继续保持零配置](#%E6%96%B9%E6%B3%952override-create-react-app-%E7%AE%80%E7%A7%B0cra%E4%B8%8D%E9%9C%80eject%E5%8F%AF%E4%BB%A5%E7%BB%A7%E7%BB%AD%E4%BF%9D%E6%8C%81%E9%9B%B6%E9%85%8D%E7%BD%AE)
  - [06. 样式按需加载，避免全局引入](#06-%E6%A0%B7%E5%BC%8F%E6%8C%89%E9%9C%80%E5%8A%A0%E8%BD%BD%E9%81%BF%E5%85%8D%E5%85%A8%E5%B1%80%E5%BC%95%E5%85%A5)
  - [08. 使用`CSS modules`避免全局样式污染](#08-%E4%BD%BF%E7%94%A8css-modules%E9%81%BF%E5%85%8D%E5%85%A8%E5%B1%80%E6%A0%B7%E5%BC%8F%E6%B1%A1%E6%9F%93)
  - [09. 栅格系统](#09-%E6%A0%85%E6%A0%BC%E7%B3%BB%E7%BB%9F)
  - [10. effect钩子](#10-effect%E9%92%A9%E5%AD%90)
  - [11. 如何在大组件重新渲染时，避免内部的小组件重新渲染](#11-%E5%A6%82%E4%BD%95%E5%9C%A8%E5%A4%A7%E7%BB%84%E4%BB%B6%E9%87%8D%E6%96%B0%E6%B8%B2%E6%9F%93%E6%97%B6%E9%81%BF%E5%85%8D%E5%86%85%E9%83%A8%E7%9A%84%E5%B0%8F%E7%BB%84%E4%BB%B6%E9%87%8D%E6%96%B0%E6%B8%B2%E6%9F%93)
  - [12 用存JS实现计时器](#12-%E7%94%A8%E5%AD%98js%E5%AE%9E%E7%8E%B0%E8%AE%A1%E6%97%B6%E5%99%A8)
    - [12.1 用`setTimeout`实现计时器](#121-%E7%94%A8settimeout%E5%AE%9E%E7%8E%B0%E8%AE%A1%E6%97%B6%E5%99%A8)
    - [12.2 用`setInterval`实现计时器](#122-%E7%94%A8setinterval%E5%AE%9E%E7%8E%B0%E8%AE%A1%E6%97%B6%E5%99%A8)
    - [12.3 用`promise`实现计时器](#123-%E7%94%A8promise%E5%AE%9E%E7%8E%B0%E8%AE%A1%E6%97%B6%E5%99%A8)
  - [13 Promise](#13-promise)
  - [14. 其他功能点](#14-%E5%85%B6%E4%BB%96%E5%8A%9F%E8%83%BD%E7%82%B9)
  - [参考](#%E5%8F%82%E8%80%83)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# React Note 02-A


## 01. [`React Router`](http://reactrouter.com/)

### (1) 介绍

> 用途：根据不同的路径、渲染出不同的内容，例如[https://ant.design/components/form-cn/](https://ant.design/components/form-cn/)和[https://ant.design/components/input-cn/](https://ant.design/components/input-cn/) 
> 
> 例子：
> 
> * [https://reactrouter.com/web/example/basic](https://reactrouter.com/web/example/basic) 
> * [https://reactrouter.com/web/example/nesting](https://reactrouter.com/web/example/nesting)
> * ...
> 
> 新功能：[https://github.com/ReactTraining/react-router/releases](https://github.com/ReactTraining/react-router/releases) 

### (2) 实现原理

> ~~~jsx
> <Router>
> 	<div> 共用部分、导航栏等 </div>
> 	<Switch>
> 		{/*根据不同的路径、渲染不同的`component`*/}
> 		<Route path="/about"><About /></Route>
> 		<Route path="/users"><Users /></Route>
> 		<Route path="/"><Home /></Route>
> 	</Switch>
> </Router>
> ~~~

### (3) 框架使用

> 在样例（[https://reactrouter.com/web/example/](https://reactrouter.com/web/example/)）的基础上，使用样式组件（如`antd`组件）来修改界面

Sample Code：

[personal-app/src/App.js](personal-app/src/App.js) 

> ~~~jsx
> ...
> function App() { 
> return (
>  <BrowserRouter> 
>      <Menu className={styles.menu} theme="dark">
>         <Menu.Item><NavLink to="/">Home</NavLink></Menu.Item>
>         <Menu.Item><NavLink to="/login">Login</NavLink></Menu.Item>
>         <Menu.Item><NavLink to="/register">Register</NavLink></Menu.Item>
>       </Menu>
>       <Router></Router>
>     </BrowserRouter>
>    );
>    ...
>   ~~~

[personal-app/src/router/index.js](personal-app/src/router/index.js)

> ~~~jsx
> const Router = () => (
> 	<Switch>
> 		<Route exact path="/" component={Home} />
>    		<Route exact path="/login" component={Login} />
>    		<Route exact path="/register" component={Register} />
>    	</Switch>
>    );
>    ~~~

### (4) 浏览器输入地址到页面展示的过程

> 1. 浏览器输入地址
> 2.	DNS查询：浏览器缓存 → OS缓存 → 路由器缓存 → ISP DNS缓存 → 根域名服务器
> 	* 相关技术：DNS预加载、让浏览器预先加载页面中要用到的DNS
> 3.	浏览器建立到服务器的TCP连接
> 4.	浏览器发起HTTP请求
> 5.	服务端处理、返回结果
> 6.	关闭HTTP连接
> 7. 浏览器解析

## 02. 通信协议：HTTP 1/2/3 以及 HTTPS

### (1) 状态码

> `1xx`：提示信息、表示目前是协议处理的中间状态、还需要后续的操作
>
> `2xx`：成功、报文已经收到并被正确处理（常见：200；204；206）
>
> `3xx`：重定向、资源位置发生变动，需要客户端重新发送请求（常见：301；302；304）
>
> `4xx`：客户端错误，请求报文有误，服务器无法处理（常见：400-错误请求；403-禁止访问；404）
>
> `5xx`：服务器错误，服务器在处理请求时发生了内部错误（常见：500；501；502；503-Service不可用）

### (2) 常见字段

> `Keep-Alive`：建立可复用的持久连接、客户端主动关闭时才释放连接（兼容HTTP 1.1）
>
> `Accept` / `Accept-Encoding`：客户端接受什么格式 / 压缩格式
>
> `Content-Type` / `Content-Encoding`：服务器端返回什么格式 / 压缩格式

### (3) Get / Post

> `Get`	：(1) 应当用于请求资源； (2) 应当写成安全、幂等的； (3) 查询条件应当放在Query中；（4）可以被cache
>
> `Post`	：(1) 应当用于提交资源； (2) 非幂等；（3）传输数据应当放在Body中
>
> 从项目规范的角度来讲，要避免用Get做Post的事情、用Post做Get的事情

### (4) 各个协议版本

#### HTTP 1.1 的优缺点

> `Chrome Dev Tool`可以配置在network tab中查看protocal）：
> 简单灵活、跨平台（服务器、浏览器、APP、……）、无状态、明文传输

#### HTTP v.s. HTTPS 

> 风险：窃听（用秘钥解决）、冒充（用数字证书解决）、串改（用数字指纹解决）
>
> `HTTPS`：在`TCP`层和`HTTP`层之间加了`SSL/TLS`层用于加密解密
>
> 机密性 - 混合加密：
> * 通信建立前：用非对称加密（公钥、私钥）来安全交换秘钥
> * 通信建立后：用对称加密（会话秘钥）来传输数据

#### `HTTP/1.1` v.s `HTTP/1.0` 

> `HTTP/1.0`：短连接，收到响应才能发下一个请求
>
> `HTTP/1.1`：长连接，管道传输

#### `HTTP/2` v.s `HTTP/1.1`

`HTTP/1.1`的问题：

> 1. `Header`容易过大、特别是包含了Cookie之类的大字段、且不能压缩
> 2. 没有请求优先级控制：服务器按照请求的顺序响应，前面的请求响应慢时，会阻塞后面的请求

`HTTP/2`的改进点：

> 1. 头部压缩：使用`HPack`算法，在客户端和服务器同时维护一张头部信息表，生成一个索引号，如果发送同样的字段，只需要发送索引号
> 2. 二进制报文：节省开销，提升传输效率
> 3. 数据流：每个请求-回应的所有数据包属于同一个数据流，在数据流中有独一无二的ID。可以指定各条流的优先级
> 4. 多路复用：一个连接中可以并发多个请求-响应。并且也不需要串行回应这些请求，避免发生队列阻塞
> 5. 服务器推送（`Server Push`、又叫做`Cache Push`）：在html刚开始请求时，把JS、CSS等推送给客户端（而不需要客户端解析收到的html后发起二次请求），减少等待时间

另一个技术：`Websocket`：

> 服务器可以通过与客户端的长连接、发消息给客户端，例如用在游戏、页面聊天工具等。

#### `HTTP/3` v.s `HTTP/2`

`HTTP/2`的问题

> 1. `客户端-服务器`之间多个`请求-响应`，共用同一个TCP连接，`TCP`层丢包重传时，所有`请求-响应`都需要等待
>
> 2. `TCP + TLS`：一共需要6次握手

`HTTP/3`

> 用`UDP + QUIC`替代`TCP + TLS `：(1) `QUIC`弥补了`UDP`不可靠的缺点，同时只有丢包的流需要重传、不会影响其他流；(2) 只需要3次握手
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/quic_hs.jpg" width="700" /></div>

`HTTP 1.1` / `HTTPS` / `HTTP/2` / `HTTP/3`协议栈：

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/http123.jpg" width="700" /></div>

### (6) 常用HTTP请求方法及含义

> * `GET`：显示请求指定的资源，一般来说应该只用于数据的读取，而不应当用于会产生副作用的非幂等的操作中
> * `HEAD`：与`GET`方法一样，都是向服务器发出指定资源的请求。但是，服务器在响应HEAD请求时不会回传资源的内容部分，即响应主体。该 方法常被用于客户端查看服务器的性能
> * `POST`：向指定资源提交数据，请求服务器进行处理，如：表单数据提交、文件上传等；请求数据会被包含在请求体中。POST方法是非幂等的方法（可能会创建新的资源或/和修改现有资源）。
> * `PUT`：向指定资源位置上传其最新内容，PUT方法是幂等的方法。通过该方法客户端可以将指定资源的最新数据传送给服务器取代指定的资源的内容。
> * `DELETE`：请求服务器删除所请求URI（统一资源标识符，Uniform Resource Identifier）所标识的资源。DELETE请求后指定资源会被删除，DELETE方法也是幂等的。
> * `CONNECT`：`HTTP/1.1`协议预留方法，能够将连接改为管道方式的代理服务器。通常用于SSL加密服务器的链接与非加密的HTTP代理服务器的通信。
> * `OPTIONS`：与HEAD类似，一般也是用于客户端查看服务器的性能。 这个方法会请求服务器返回该资源所支持的所有HTTP请求方法，该方法会用'*'来代替资源名称，向服务器发送OPTIONS请求，可以测试服务器功能是否正常。JavaScript的XMLHttpRequest对象进行CORS跨域资源共享时，就是使用OPTIONS方法发送嗅探请求，以判断是否有对指定资源的访问权限。
> * `TRACE`：请求服务器回显其收到的请求信息，该方法主要用于HTTP请求的测试或诊断。
> * `PATCH`：一般用于资源的部分更新，而PUT一般用于资源的整体更新；当资源不存在时，PATCH会创建一个新的资源，而PUT只会对已在资源进行更新。

## 03. `Ant Design`

### (1) 特点

> 特点：相比`meterialUI`和`bootstrap`：`antd`的定位是企业级**中后台**产品，面向`to B`端或者面向公司内部部门，侧重于对表格、表单等的支持；但是包体积（[https://bundlephobia.com/result?p=antd@4.8.0](https://bundlephobia.com/result?p=antd@4.8.0)）要比另外两个大

### (2) 组件化开发

以登录页为例

> 代码
>
> * [personal-app/src/pages/Login/index.js][personal-app/src/pages/Login/index.js]
> * [personal-app/src/pages/Login/index.module.less][personal-app/src/pages/Login/index.module.less]
>
> 使用`antd`可以进行组件化开发
>
> `antd`提供了封装好的`react`组件，可以在官网上查找和使用，根据sample code和API来调整编写，例如
>
> * `Form`：[https://ant.design/components/form-cn/](https://ant.design/components/form-cn/) 
> * `Form.Item`：[https://ant.design/components/form-cn/#Form.Item](https://ant.design/components/form-cn/#Form.Item) 
> * `Tabs`：[https://ant.design/components/tabs-cn/](https://ant.design/components/tabs-cn/)
> * `Tabs.TabPane`：[https://ant.design/components/tabs-cn/#Tabs.TabPane](https://ant.design/components/tabs-cn/#Tabs.TabPane)

根据模块来组织components

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/folder_structure.jpg" width="800" /></div>
> 
> 其中的几个输入框（用户名、密码、手机号、验证码发送和填写）很相似，抽成一个组件`InputItem`

### (3) `InputItem`实现过程中的一些技巧

#### (a) 使用`{...props}`批量从外层组件传入props给内层组件

> ~~~jsx
> function InputItem (props) {
> 	return (<Input {...props} />);
> }
> ~~~
>
> 用`props`传递react组件，来自定义输入框头部的图标
>
> ~~~jsx
> <InputItem 
> 	prefix={ <UserOutlined style={{color:'#1890ff'}} className={styles.prefixIcon} />} 
> 	placeholder="用户名" 
> 	size="large"
> />
> <InputItem 
> 	prefix={ <LockTwoTone className={styles.prefixIcon} />} 
> 	placeholder="密码" 
> 	size="large"
> />
> ~~~

#### (b) 使用`antd`的`<Form.Item>`来获取`<Input/>`的值

> 使用`<Form>`可以获取其内部所有`<FormItem>`的值。相比自己写react状态更新代码会更加方便。
>
> ~~~jsx
> function InputItem (props) {
> 	const { name, ...rest } = props;
> 	// Form.Item的name属性用来区分同一个<Form>下的不同<Form.Item>
> 	return (
> 		<Form.Item name={name} >
> 			<Input (...rest) />
> 		</Form.Item>
> 	);
> }
> ~~~
>
> ~~~jsx
> // 用state hook得到的状态form会传递给<Form/>
> const [form] = Form.useForm();
> // 从values中可以得到<Form>内所有<Form.Item>的值
> const handleFinish = (values) => {
> 	console.log(values);
> }
> // 其中的InputItem内包含了<Form.Item>，从整体行形成<Form><Form.Item/>...</Form>嵌套结构
> return (
> 	<div>
> 		<Form form={form} onFinish={handleFinish}> 
> 			...
> 			<InputItem ... />  
> 			<InputItem ... />
> 			...
> 		</Form>
> 	</div>
> )
> ~~~

#### (c) 使用`antd`的表单API来操作状态`form`

> API文档：[https://ant.design/components/form-cn/#API](https://ant.design/components/form-cn/#API`)
>
> 例如如下例子（来自AntD文档 [Migrate Form to v4](https://ant.design/components/form/v3)）
>
> ~~~jsx
> onFinishFailed = ({ errorFields }) => {
>   form.scrollToField(errorFields[0].name);
> };
> ~~~

#### (d) 其他`antd`的组件和函数

> 1. `<Input ... type="password` ...>`：密码输入框
>
> 2. `<Form ... rules={[{required: true, message:'请输入用户名'}, ...]} ...>`：前端校验
>
> 3. `message.success('成功获取验证码'); `：全局提示，以函数的形式提供给代码（而非组件），可以在项目任何位置调用，并提供多种样式定制参数；类似的还有`notification`全局通知
>     * message: [https://ant.design/components/message-cn/](https://ant.design/components/message-cn/)
>     * notification: [https://ant.design/components/notification-cn/](https://ant.design/components/notification-cn/)
>
> 4. `<Row gutter={8}><Col>...</Col>...</Row>`，24格栅格系统，界面随着屏幕大小变化（Responsive），margin等属性值也都自动计算好（bootstrap是12格、antd是24格，用12或24是因为相除比较方便），gutter是Col之间间隔的像素值

## 04. `Redux`

用途

> 之前项目中（[` ../01_proj_todo_list/todo-list/src/App.js`]( ../01_proj_todo_list/todo-list/src/App.js)），回调函数`completeTodo`要定义在`App`这一层（因为要操作`App`的状态）、但却要一路传递给`TodoItem`来使用。
>
> `Readux`作为一个全局状态管理工具，用来解决此类问题，后续会详细介绍

## 05. CSS预处理器

### (1) 用途

> 为`CSS`（弱语言）增加编程特性（例如变量、继承、 Mixin、条件语句等等），提升开发效率、使代码容易维护

### (2) 市面上的预处理器

`SCSS/SASS`-份额66.0%；`LESS`-份额13.4%；`No Preprocessor`-份额13.5%；`Stylus`-份额4.2%；其他占2.9%的份额

#### (a) `SCSS`

支持嵌套编写，让CSS与DOM保持相同的嵌套层级，也能避免名称冲突（不再是满屏的全局样式）

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/scss_css_embedding.jpg" width="800" /></div>
>
> 

支持变量，例如将被用在很多地方的`color: #333`抽到一个单独的变量`$primary-color: #333`中，便于统一修改

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/scss_css_variable.jpg" width="800" /></div>

支持`mixin`，例如将`居中`、`旋转`等样式操作，抽到一个`minxi`中，统一作用在一个`.css_class`各处

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/scss_css_mixin.jpg" width="800" /></div>

支持if-else等编程语句

> ~~~css
> @if lightness($color) > 40% {
> 	background-color: #000;
> } @else {
> 	background-color: #fff;
> }
> ~~~

支持继承

> ~~~css
> .class1 {
> 	border: 1px solid #ddd;
> }
> .class2 {
> 	@extend .class1;
> 	font-size: 120%;
> }
> ~~~

#### (b) LESS

> 与SCSS的功能点比较类似

#### (c) Stylus 

> 特点是“能省则省”（大括号、分号……都可以省略），比较小众

### (3) 怎样在create-react-app中加入CSS预处理器

#### 方法1：使用`yarn eject` / `npm eject`之后手动修改webpack配置

弹出（eject）配置，使项目由一个`零配置`的React脚手架，变成一个配置文件全部暴露的项目

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/react_eject.jpg" width="800" /></div>

弹出后（在项目中可以看到`config`, `scripts`, 以及`package.json`中新增的各种依赖）

在`config`中找到webpack配置，可以看到`create-react-app`提供的`sass-loader`配置（支持`SCSS`和`SASS`），但是没有`less-loader`

如果想用`LESS`作为CSS预处理器，可以在`webpack配置`中，类似地添加`less-loader`

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/webpack_sass_loader.jpg" width="800" /></div> 

#### 方法2：override `create-react-app` (简称`cra`)、不需`eject`可以继续保持零配置

以使用`yarn`作为包管理器为例，步骤如下

1 . 安装`react-app-rewired`和`customize-cra` (`cra`就是`create-react-app`的缩写）

> ~~~bash
> yarn add react-app-rewired customize-cra -D
> ~~~
> 

2 . 修改package.json，把用于执行`start`/`build`/`test`/`eject`由原生的 `react-scripts`替换成`react-app-rewired` 

> ~~~js
> "scripts" : {
> 	"start" : "react-app-rewired start"
> 	"build" : "react-app-rewired build"
> 	"test" : "react-app-rewired test"
> 	"eject" : "react-app-rewired eject"
> }
> ~~~
> 

3 . 在根目录下创建`config-overrides.js`给上面的4个脚本读取

> ~~~javascript
> // 用node.js的写法，因为这个配置是用node.js来执行的 
> const { override, addLessLoader } = require('customize-cra'); 
> // 覆盖addLessLoader
> module.exports = override (
> 	addLessLoader( {
> 		// 提供一些配置给addLessLoader
> 		lessOptions: { 
> 			javascriptEnabled: true,
> 			localIdentName: '[local]--[hash:base64:5]'
> 		},
> 	}),
> )
> ~~~
> 

4 . 现在项目已经支持`LESS`了，可以把代码中的全局`antd.css`替换成`antd.less`

## 06. 样式按需加载，避免全局引入

使用`babel-plugin-import`来让`antd`按需加载，使用`yarn`作为包管理器为例，步骤如下

1 . 安装`babel-plugin-import`

> ~~~bash
> yarn add babel-plugin-import -D 
> ~~~
> 

2 . 在`config-overrides.js`中加入`babel-plugin-import`配置

> ~~~javascript
> const { override, addLessLoader } = require('customize-cra'); 
> module.exports = override (
> 	// `babel-plugin-import`配置
> 	fixBabelImports('antd', {
> 		libraryDirectory: 'es',
> 		style: 'css',
> 	}),
> 	addLessLoader( {
>		lessOptions: { 
>			javascriptEnabled: true,
> 			localIdentName: '[local]--[hash:base64:5]'
> 		},
> 	}),
> )
> ~~~

## 08. 使用`CSS modules`避免全局样式污染

不使用`CSS Modules`
> 
> ~~~javascript
> import './index.less';
> ...
> <div className="login">...</div>
> ~~~

使用`CSS Modules`

> `{styles.login}`会为className加后缀进行重命名避免全局样式污染 
> 
> ~~~javascript
> import styles from './index.module.less';  //文件命名约定来自于webpack配置
> ...
> <div className={styles.login}>  
> ...
> ~~~

其他方法：`CSS in JS`

> 例如下面的代码，或者使用一些`CSS in JS`的库
> 
> ~~~javascript
> ...
> const styles = {
> 	display: 'flex';
> 	...
> }
> ...
> <div ...  style={styles}>
> ~~~
> 
> 缺点是对代码的侵入性比较大（css与js写在一起）、另外css不能复用

## 09. 栅格系统 

> * `Antd`: 24格；`bootstrap`：12格
> * 布局layout实现方便，responsive响应式
> * 12/24这类数字有很多约数

可以用`span`指定占多少个栅格

> ~~~jsx
> <Row>
> 	<Col span={6}>
> 		<Select size='large' value={prefix} onChange={(value)=>setPrefix(value)} style={{width:'100%'}}>
> 			<Select.Option value="86">+86</Select.Option>
>      		<Select.Option value="87">+87</Select.Option>
>		</Select>
>    	</Col>
>	<Col span={18}>
>		<InputItem name="mobile" placeholder="手机号" size="large"  rules={[
>                                     {required: true, message: '请输入手机号'},
>                                     {pattern: /^\d{11}/, message: '手机号格式错误'}
>                                 ]}
>    		/>
>    	</Col>
></Row>
>~~~
>

也可以使用类似`flex 布局`里面的`justify`和`align`

> ~~~jsx
> <Row justify="space-between" align="middle">
> 	<Col span={8}>
> 		<SubmitButton>注册</SubmitButton>
> 	</Col>
>	<Col span={16}>
>		<Link className={styles.login} to="/login">使用已有账户登录</Link>
>    	</Col>
></Row>
>~~~

## 10. effect钩子

> ~~~javascript
>     // Effect钩子：
>     // * 参数2是监听的状态变量、如果留空则useEffect会在每次重新渲染之后执行、如果传入空数组那么useEffect只会在初始化时执行一次；
>     // * 参数1是状态变量发生变化时执行的操作
>     useEffect(() => {
>             // timeing被handleClickCaptcha置为true时，被会生成一个计时器，每一秒更新一次状态变量`count`
>             let interval = 0; // 变量
>             if (timing) {
>                 // 开启计时器，该计时器每秒（1000毫秒）唤醒一次并执行检查函数
>                 interval = window.setInterval(() => {
>                     // 设置react状态(setCount)：如果传入是一个函数，函数参数就是state的旧值，返回的是stete的新值
>                     setCount((preSecond) => {
>                         if (preSecond <= 1) {
>                             setTiming(false);              // 设置react状态(timing)，为false表示倒计时结束
>                             clearInterval(interval);       // 停止每秒唤醒一次的计时器
>                             return props.countDown || 60;  // 重置count为初始值
>                         } else {
>                             return preSecond - 1;  // 倒计时未结束，preSecond减1
>                         }
>                     })
>                 }, 1000);
>             } 
>             // 末尾的返回函数 () => clearInterval(timer) 会在组件被销毁时执行，用于清除计时器
>             // * timing被倒计时结束时的setTiming设置为false时，会错过clearInterval(interval)调用
>             // * 如果不用其他代码清除计时器，会造成内存泄漏
>             // 下面的这个函数会在组件unmount时被执行，从而确保计时器一定被清楚
>             return () => clearInterval(interval);
>         }, 
>         // Effect钩子要求把用到的props属性也添加到监听列表中，否则会报下面的Warning
>         //      React Hook useEffect has a missing dependency: 'props.countDown'. 
>         //      Either include it or remove the dependency array. 
>         //      If 'setCount' needs the current value of 'props.countDown', you can also switch to useReducer 
>         //      instead of useState and read 'props.countDown' in the reducer react-hooks/exhaustive-deps
>         [timing, props.countDown]
>     );
> ~~~

## 11. 如何在大组件重新渲染时，避免内部的小组件重新渲染

> 默认大组件重新渲染时、内部的小组件会一起重新渲染。如果想让小组件不会联动渲染，可以在`export`时用`memo`包裹。这样内部小组件是否重新渲染，只取决于小组件的state、props是否发生变化。例如：
> 
> ~~~javascript
> const SmallComponent = () => {
> 	...
> }
> export default memo(SmallComponent)
> ~~~
> 
> 相当于`class component`时代时的`PureComponent` 

## 12 用存JS实现计时器

### 12.1 用`setTimeout`实现计时器

> 错误方法
>
> ~~~javascript
> // 只能打印出5个5，不能打印1，2，3，4，5
> for (var i = 0; i < 5; i++)  {
> 	setTimeout(
> 		function() {console.log(i);}, 
> 		i * 1000 
> 	);
> }
> ~~~
>
> 正确方法
>
> ~~~javascript
> for (var i = 0; i < 5; i ++) {
> // 借助匿名函数立即执行，把传入的i值固定下来，分别为1，2，3，4，5
> 	(function(i) {
> 		setTimeout(
> 			function() {console.log(i);}, 
> 			i * 1000
> 		);}
> 	)(i);  // 匿名函数立即执行：(function f(){...})()
> }
> ~~~

### 12.2 用`setInterval`实现计时器

> ~~~javascript
> var count = 0;
> var a = setInterval(function() {
> 	console.log(count++)
> 	if (count == 5) {
> 		clearInterval(count);
> 	}
> }, 1000); 
> ~~~
>
> 但是如果count不是普通变量而是`react state`，就不能用这种方法，因为react state的更新是异步的，无法拿到count的最新值。此时仍然需要 [/src/components/InputItem/index.js](personal-app/src/components/InputItem/index.js) 中所使用的方法

### 12.3 用`promise`实现计时器

> ~~~javascript
> fn = (i) => {
> 	return new Promise((resolve, reject) => {
> 		setTimeout(() => {
> 			resolve(i);
> 		}, i + 1000)
> 	})
> }
> 
> Fn = async () => {
> 	for (let i =. 0; i <. 5; i++) {
> 		const rest = await fn(i);
> 		console.log(res);
> 	}
> }
> 
> Fn()
> ~~~

## 13 Promise

API：

> `resolve(then)`, `reject(catch)`, `race`（传入一个Promise数组，任意一个执行完毕就可以继续），`all`（传入一个Promise数组，全部执行完毕才可以继续）

状态：

> `pending`，`resolve(fullfilled)`，`rejected` 

手写一个Promise的polyfill（补丁：假定某种浏览器不支持Promise，手写一个来支持）

> [https://juejin.im/post/6844904022223110151](https://juejin.im/post/6844904022223110151) 

## 14. 其他功能点

注册框密码、密码校验两个控件的交叉验证（改“密码校验”时触发验证，改“密码”时触发验证）

> [src/pages/Register/index.js](../../02B_proj_personal_center_stage2/personal-app/src/pages/Register/index.js)

注册框密码强度检验

> [src/pages/Register/index.js](../../02B_proj_personal_center_stage2/personal-app/src/pages/Register/index.js)

## 参考

1. [https://bundlephobia.com/](https://bundlephobia.com/)：查看一个包大小
2. React Router：[https://reactrouter.com/web/example/nesting](https://reactrouter.com/web/example/nesting)
