# React Note 02A


## 1. [`React Router`](http://reactrouter.com/)

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
> 		{/*根据不同的路径、渲染不同的`component`}
> 		<Route path="/about"><About /></Route>
> 		<Route path="/users"><Users /></Route>
> 		<Route path="/"><Home /></Route>
> 	</Switch>
> </Router>
> ~~~

### (3) 框架使用

> 在样例（[https://reactrouter.com/web/example/](https://reactrouter.com/web/example/)）的基础上，使用样式组件（如`antd`组件）来修改界面

Sample Code：

> [personal-app/src/App.js](personal-app/src/App.js) 
> 
> ~~~javascript
> ...
> function App() { 
>   return (
>     <BrowserRouter> 
>       <Menu className={styles.menu} theme="dark">
>         <Menu.Item><NavLink to="/">Home</NavLink></Menu.Item>
>         <Menu.Item><NavLink to="/login">Login</NavLink></Menu.Item>
>         <Menu.Item><NavLink to="/register">Register</NavLink></Menu.Item>
>       </Menu>
>       <Router></Router>
>     </BrowserRouter>
>   );
> ...
> ~~~

> [personal-app/src/router/index.js](personal-app/src/router/index.js)
> 
> ~~~javascript
> const Router = () => (
>     <Switch>
>         <Route exact path="/" component={Home} />
>         <Route exact path="/login" component={Login} />
>         <Route exact path="/register" component={Register} />
>     </Switch>
> );
> ~~~

### (4) 浏览器输入地址到页面展示的过程

> 1. 浏览器输入地址
> 2.	DNS查询：浏览器缓存 -> OS缓存 -> 路由器缓存 -> ISP DNS缓存 -> 根域名服务器
> 	* 相关技术：DNS预加载、让浏览器预先加载页面中要用到的DNS
> 3.	浏览器建立到服务器的TCP连接
> 4.	浏览器发起HTTP请求
> 5.	服务端处理、返回结果
> 6.	关闭HTTP连接
> 7. 浏览器解析

## 2. 接口调用

## 3. 通信协议：HTTP 1/2/3 以及 HTTPS

### (1) 状态码

> * `1xx`：提示信息、表示目前是协议处理的中间状态、还需要后续的操作
> * `2xx`：成功、报文已经收到并被正确处理（常见：200；204；206）
> * `3xx`：重定向、资源位置发生变动，需要客户端重新发送请求（常见：301；302；304）
> * `4xx`：客户端错误，请求报文有误，服务器无法处理（常见：400-错误请求；403-禁止访问；404）
> * `5xx`：服务器错误，服务器在处理请求时发生了内部错误（常见：500；501；502；503-Service不可用）

### (2) 常见字段

> * `Keep-Alive`：建立可复用的持久连接、客户端主动关闭时才释放连接（兼容HTTP 1.1）
> * `Accept` / `Accept-Encoding`：客户端接受什么格式 / 压缩格式
> * `Content-Type` / `Content-Encoding`：服务器端返回什么格式 / 压缩格式

### (3) Get / Post

> * `Get`	：(1) 应当用于请求资源 (2) 应当写成安全、幂等的 (3) 查询条件应当放在Query中（4）可以被cache
> * `Post`	：(1) 应当用于提交资源 (2) 非幂等（3）传输数据应当放在Body中
> * 从项目规范的角度来讲，要避免用Get做Post的事情、用Post做Get的事情

### (4) 各个协议版本

#### HTTP 1.1 的优缺点

> `Chrome Dev Tool`可以配置在network tab中查看protocal）：
> 简单灵活、跨平台（服务器、浏览器、APP、……）、无状态、明文传输

#### HTTP v.s. HTTPS 

> * 风险：窃听（用秘钥解决）、冒充（用数字证书解决）、串改（用数字指纹解决）
> * HTTPS：在`TCP`层和`HTTP`层之间加了`SSL/TLS`层用于加密解密
> * 机密性 - 混合加密：
> 	* 通信建立前：用非对称加密（公钥、私钥）来安全交换秘钥
> 	* 通信建立后：用对称加密（会话秘钥）来传输数据

#### `HTTP/1.1` v.s `HTTP/1.0` 

> * `HTTP/1.0`：短连接，收到响应才能发下一个请求
> * `HTTP/1.1`：长连接，管道传输

#### `HTTP/2` v.s `HTTP/1.1`

`HTTP/1.1`的问题：
> 
> * `Header`容易过大、特别是包含了Cookie之类的大字段、且不能压缩
> * 没有请求优先级控制：服务器按照请求的顺序响应，前面的请求响应慢时，会阻塞后面的请求

`HTTP/2`的改进点：
> 
> * 头部压缩：使用`HPack`算法，在客户端和服务器同时维护一张头部信息表，生成一个索引号，如果发送同样的字段，只需要发送索引号
> * 二进制报文：节省开销，提升传输效率
> * 数据流：每个请求-回应的所有数据包属于同一个数据流，在数据流中有独一无二的ID。可以指定各条流的优先级
> * 多路复用：一个连接中可以并发多个请求-响应。并且也不需要串行回应这些请求，避免发生队列阻塞
> * 服务器推送（`Server Push`、又叫做`Cache Push`）：在html刚开始请求时，把JS、CSS等推送给客户端（而不需要客户端解析收到的html后发起二次请求），减少等待时间

另一个技术：`Websocket`：

> 服务器可以通过与客户端的长连接、发消息给客户端，例如用在游戏、页面聊天工具等。

#### `HTTP/3` v.s `HTTP/2`

`HTTP/2`的问题

> * `客户端-服务器`之间多个`请求-响应`，共用同一个TCP连接，`TCP`层丢包重传时，所有`请求-响应`都需要等待
> * `TCP + TLS`：一共需要6次握手

`HTTP/3`
 
> * 用`UDP + QUIC`替代`TCP + TLS `：(1) `QUIC`弥补了`UDP`不可靠的缺点，同时只有丢包的流需要重传、不会影响其他流；(2) 只需要3次握手
> 
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/200_react/quic_hs.jpg)

`HTTP 1.1` / `HTTPS` / `HTTP/2` / `HTTP/3`协议栈：

> ![https://raw.githubusercontent.com/kenfang119/pics/main/200_react/http123.png](https://raw.githubusercontent.com/kenfang119/pics/main/200_react/http123.jpg)

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

## 4. `Ant Design`

### (1) 特点

> 特点：相比`meterialUI`和`bootstrap`：`antd`的定位是企业级**中后台**产品，面向`to B`端或者面向公司内部部门，侧重于对表格、表单等的支持；但是包体积（[https://bundlephobia.com/result?p=antd@4.8.0](https://bundlephobia.com/result?p=antd@4.8.0)）要比另外两个大

### (2) 组件化开发

以登录页为例

> * [personal-app/src/pages/Login/index.js][personal-app/src/pages/Login/index.js]
> * [personal-app/src/pages/Login/index.module.less][personal-app/src/pages/Login/index.module.less]
> 
> 使用`antd`可以进行组件化开发，`antd`提供了封装好的`react`组件，可以在官网上查找和使用，根据sample code和API来调整编写
> 
> * `Form`：[https://ant.design/components/form-cn/](https://ant.design/components/form-cn/) 
> * `Form.Item`：[https://ant.design/components/form-cn/#Form.Item](https://ant.design/components/form-cn/#Form.Item) 
> * `Tabs`：[https://ant.design/components/tabs-cn/](https://ant.design/components/tabs-cn/)
> * `Tabs.TabPane`：[https://ant.design/components/tabs-cn/#Tabs.TabPane](https://ant.design/components/tabs-cn/#Tabs.TabPane)

> 根据模块来组织目录结构
> 
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/200_react/folder_structure.jpg)

> 其中的几个输入框（用户名、密码、手机号、验证码发送和填写）很相似，抽成一个组件`InputItem`

### (3) `InputItem`实现过程中的一些技巧

> 使用`{...props}`批量从外层组件传入props给内层组件

~~~javascript
function InputItem (props) {
	return (<Input {...props} />);
}
~~~

> 用`props`传递react组件，来自定义输入框头部的图标

~~~javascript
<InputItem 
	prefix={ <UserOutlined style={{color:'#1890ff'}} className={styles.prefixIcon} />} 
	placeholder="用户名" 
	size="large"
/>
<InputItem 
	prefix={ <LockTwoTone className={styles.prefixIcon} />} 
	placeholder="密码" 
	size="large"
/>
~~~

> 使用`<Form.Item>`来获取`<Input/>`的值，使用`<Form>`可以获取其内部所有`<FormItem>`的值。相比自己写react状态更新代码会更加方便。

~~~javascript
function InputItem (props) {
	const { name, ...rest } = props;
	// Form.Item的name属性用来区分同一个<Form>下的不同<Form.Item>
	return (
		<Form.Item name={name} >
			<Input (...rest) />
		</Form.Item>
	);
}
~~~

~~~javascript
// 用state hook得到的状态form会传递给<Form/>
const [form] = Form.useForm();
// 从values中可以得到<Form>内所有<Form.Item>的值
const handleFinish = (values) => {
	console.log(values);
}
// 其中的InputItem内包含了<Form.Item>，从整体行形成<Form><Form.Item/>...</Form>嵌套结构
return (
	<div>
		<Form form={form} onFinish={handleFinish}> 
			...
			<InputItem ... />  
			<InputItem ... />
			...
		</Form>
	</div>
)
~~~

> 可以使用`https://ant.design/components/form-cn/#API`中的API来操作状态`form`，例如如下例子（来自AntD文档 [Migrate Form to v4](https://ant.design/components/form/v3)）

~~~javascript
onFinishFailed = ({ errorFields }) => {
  form.scrollToField(errorFields[0].name);
};
~~~

> 其他`antd`的组件和函数：
> 
> * `<Input ... type="password` ...>`：密码输入框
> * `<Form ... rules={[{required: true, message:'请输入用户名'}, ...]} ...>`：前端校验
> * `message.success('成功获取验证码'); `：全局提示，以函数的形式提供给代码（而非组件），可以在项目任何位置调用，并提供多种样式定制参数；类似的还有`notification`全局通知
> 	* message: [https://ant.design/components/message-cn/](https://ant.design/components/message-cn/)
> 	* notification: [https://ant.design/components/notification-cn/](https://ant.design/components/notification-cn/)
> * `<Row gutter={8}><Col>...</Col>...</Row>`，24格栅格系统，界面随着屏幕大小变化（Responsive），margin等属性值也都自动计算好（bootstrap是12格、antd是24格，用12或24是因为相除比较方便），gutter是Col之间间隔的像素值

## 5. `Redux`

> 用途：之前项目中（[` ../01_proj_todo_list/todo-list/src/App.js`]( ../01_proj_todo_list/todo-list/src/App.js)），回调函数`completeTodo`要定义在`App`这一层（因为要操作`App`的状态）、但却要一路传递给`TodoItem`来使用。`Readux`作为一个全局状态管理工具，用来解决此类问题

## 6. CSS预处理器

### (1) 用途

> 为`CSS`（弱语言）增加编程特性（例如变量、继承、 Mixin、条件语句等等），提升开发效率、使代码容易维护

### (2) 市面上的预处理器

`SCSS/SASS`-份额66.0%；`LESS`-份额13.4%；`No Preprocessor`-份额13.5%；`Stylus`-份额4.2%；其他占2.9%的份额

#### `SCSS`

支持嵌套编写，让CSS与DOM保持相同的嵌套层级，也能避免名称冲突（不再是满屏的全局样式）

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/200_react/scss_css_embedding.jpg)

支持变量，例如将被用在很多地方的`color: #333`抽到一个单独的变量`$primary-color: #333`中，便于统一修改

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/200_react/scss_css_variable.jpg)

支持`mixin`，例如将`居中`、`旋转`等样式操作，抽到一个`minxi`中，统一作用在一个`.css_class`各处

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/200_react/scss_css_mixin.jpg)

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

#### LESS

> 与SCSS的功能点比较类似

#### Stylus 

> 特点是“能省则省”（大括号、分号……都可以省略），比较小众

### (3) 怎样在create-react-app中加入CSS预处理器

#### 方法1：使用`yarn eject` / `npm eject`,  ... 然后改webpack配置

弹出（eject）配置，使项目由一个`零配置`的React脚手架，变成一个配置文件全部暴露的项目

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/200_react/react_eject.jpg)

弹出后（在项目中可以看到`config`, `scripts`, 以及`package.json`中新增的各种依赖），在`config`中找到webpack配置，可以看到`create-react-app`提供的`sass-loader`配置（支持`SCSS`和`SASS`），但是没有`less-loader`
 
> * 如果想用`LESS`作为CSS预处理器，可以在`webpack配置`中，类似地添加`less-loader`
> 
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/200_react/webpack_sass_loader.jpg) 

#### 方法2：override `create-react-app` (简称`cra`)，不需`eject`、继续保持零配置

> 以`yarn`引入`LESS`为例：
> 
> (1) 安装`react-app-rewired`和`customize-cra` (`cra`就是`create-react-app`的缩写）
> 
> ~~~bash
> yarn add react-app-rewired customize-cra -D
> ~~~
> 
> (2) 修改package.json，把用于执行`start`/`build`/`test`/`eject`由原生的 `react-scripts`替换成`react-app-rewired` 
> 
> ~~~json
> "scripts" : {
> 	"start" : "react-app-rewired start"
> 	"build" : "react-app-rewired build"
> 	"test" : "react-app-rewired test"
> 	"eject" : "react-app-rewired eject"
> }
> ~~~
> 
> (3) 在根目录下创建`config-overrides.js`给上面的4个脚本读取
> 
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
> (4) 现在项目已经支持`LESS`了，可以把代码中的全局`antd.css`替换成`antd.less`

## 7. 样式按需加载，避免全局引入

> 使用`babel-plugin-import`来让`antd`按需加载
> 
> (1) 安装`babel-plugin-import`（以项目使用`yarn`为例）
> 
> ~~~bash
> yarn add babel-plugin-import -D 
> ~~~
> 
> (2) 在`config-overrides.js`中加入`babel-plugin-import`配置
>
>~~~javascript
> const { override, addLessLoader } = require('customize-cra'); 
> module.exports = override (
> 	// `babel-plugin-import`配置
> 	fixBabelImports('antd', {
> 		libraryDirectory: 'es',
> 		style: 'css',
> 	}),
> 	addLessLoader( {
> 		lessOptions: { 
> 			javascriptEnabled: true,
> 			localIdentName: '[local]--[hash:base64:5]'
> 		},
> 	}),
> )
>~~~

## 8. 使用`CSS modules`避免全局样式污染

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
> const styles. = {
> 	display: 'flex';
> 	...
> }
> ...
> <div ...  style={styles}>
> ~~~
> 
> 缺点是对代码的侵入性比较大

## 附录

1. [https://bundlephobia.com/](https://bundlephobia.com/)：查看一个包大小
2. React Router：[https://reactrouter.com/web/example/nesting](https://reactrouter.com/web/example/nesting)