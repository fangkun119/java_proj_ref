<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [React Note 02B](#react-note-02b)
  - [01 按需加载](#01-%E6%8C%89%E9%9C%80%E5%8A%A0%E8%BD%BD)
  - [02 Suspense](#02-suspense)
  - [03 栅格系统的屏幕自适应](#03-%E6%A0%85%E6%A0%BC%E7%B3%BB%E7%BB%9F%E7%9A%84%E5%B1%8F%E5%B9%95%E8%87%AA%E9%80%82%E5%BA%94)
  - [04 文本截断，CSS伪类和伪元素](#04-%E6%96%87%E6%9C%AC%E6%88%AA%E6%96%ADcss%E4%BC%AA%E7%B1%BB%E5%92%8C%E4%BC%AA%E5%85%83%E7%B4%A0)
  - [05 数组去重](#05-%E6%95%B0%E7%BB%84%E5%8E%BB%E9%87%8D)
  - [06 时间处理和展示](#06-%E6%97%B6%E9%97%B4%E5%A4%84%E7%90%86%E5%92%8C%E5%B1%95%E7%A4%BA)
  - [07 HTML Header中的`<meta ...>`标签](#07-html-header%E4%B8%AD%E7%9A%84meta-%E6%A0%87%E7%AD%BE)
  - [08 `Ajax`封装](#08-ajax%E5%B0%81%E8%A3%85)
  - [09 使用Redux在视图层触发action获取后端数据](#09-%E4%BD%BF%E7%94%A8redux%E5%9C%A8%E8%A7%86%E5%9B%BE%E5%B1%82%E8%A7%A6%E5%8F%91action%E8%8E%B7%E5%8F%96%E5%90%8E%E7%AB%AF%E6%95%B0%E6%8D%AE)
    - [(1) Redux经典链路：单向数据流](#1-redux%E7%BB%8F%E5%85%B8%E9%93%BE%E8%B7%AF%E5%8D%95%E5%90%91%E6%95%B0%E6%8D%AE%E6%B5%81)
    - [(2) 单向数据流（`react`） v.s 双向绑定（早期的`angular`、`vue`）](#2-%E5%8D%95%E5%90%91%E6%95%B0%E6%8D%AE%E6%B5%81react-vs-%E5%8F%8C%E5%90%91%E7%BB%91%E5%AE%9A%E6%97%A9%E6%9C%9F%E7%9A%84angularvue)
    - [(3) 引入`redux`、`axios`](#3-%E5%BC%95%E5%85%A5reduxaxios)
    - [(4) 代码编写步骤](#4-%E4%BB%A3%E7%A0%81%E7%BC%96%E5%86%99%E6%AD%A5%E9%AA%A4)
      - [步骤1：创建`store`并指定将其作用在哪个父组件上](#%E6%AD%A5%E9%AA%A41%E5%88%9B%E5%BB%BAstore%E5%B9%B6%E6%8C%87%E5%AE%9A%E5%B0%86%E5%85%B6%E4%BD%9C%E7%94%A8%E5%9C%A8%E5%93%AA%E4%B8%AA%E7%88%B6%E7%BB%84%E4%BB%B6%E4%B8%8A)
      - [步骤2：创建`Reducer`用于存储数据计算和修剪之后的数据结果](#%E6%AD%A5%E9%AA%A42%E5%88%9B%E5%BB%BAreducer%E7%94%A8%E4%BA%8E%E5%AD%98%E5%82%A8%E6%95%B0%E6%8D%AE%E8%AE%A1%E7%AE%97%E5%92%8C%E4%BF%AE%E5%89%AA%E4%B9%8B%E5%90%8E%E7%9A%84%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%9C)
      - [步骤3：编写视图层（`view`）代码并通过`dispatch`触发`action`执行](#%E6%AD%A5%E9%AA%A43%E7%BC%96%E5%86%99%E8%A7%86%E5%9B%BE%E5%B1%82view%E4%BB%A3%E7%A0%81%E5%B9%B6%E9%80%9A%E8%BF%87dispatch%E8%A7%A6%E5%8F%91action%E6%89%A7%E8%A1%8C)
      - [步骤4：编写`action`层代码用于获取数据](#%E6%AD%A5%E9%AA%A44%E7%BC%96%E5%86%99action%E5%B1%82%E4%BB%A3%E7%A0%81%E7%94%A8%E4%BA%8E%E8%8E%B7%E5%8F%96%E6%95%B0%E6%8D%AE)
  - [10 使用拦截器](#10-%E4%BD%BF%E7%94%A8%E6%8B%A6%E6%88%AA%E5%99%A8)
  - [11 SSO单点登录的实现](#11-sso%E5%8D%95%E7%82%B9%E7%99%BB%E5%BD%95%E7%9A%84%E5%AE%9E%E7%8E%B0)
    - [(a) 请求响应过程](#a-%E8%AF%B7%E6%B1%82%E5%93%8D%E5%BA%94%E8%BF%87%E7%A8%8B)
    - [(b) 使用Cookie的缺点和解决办法](#b-%E4%BD%BF%E7%94%A8cookie%E7%9A%84%E7%BC%BA%E7%82%B9%E5%92%8C%E8%A7%A3%E5%86%B3%E5%8A%9E%E6%B3%95)
    - [(c) Token保存方法：Cookie、localStorage、sessionStorage的区别](#c-token%E4%BF%9D%E5%AD%98%E6%96%B9%E6%B3%95cookielocalstoragesessionstorage%E7%9A%84%E5%8C%BA%E5%88%AB)
  - [12 Redux链路完整例子](#12-redux%E9%93%BE%E8%B7%AF%E5%AE%8C%E6%95%B4%E4%BE%8B%E5%AD%90)
    - [(1) dispatch后端调用action](#1-dispatch%E5%90%8E%E7%AB%AF%E8%B0%83%E7%94%A8action)
    - [(2) dispatch数据处理操作到reducer](#2-dispatch%E6%95%B0%E6%8D%AE%E5%A4%84%E7%90%86%E6%93%8D%E4%BD%9C%E5%88%B0reducer)
      - [(a) 在root reducer中添加sub reducer](#a-%E5%9C%A8root-reducer%E4%B8%AD%E6%B7%BB%E5%8A%A0sub-reducer)
      - [(b) 在sub reducer中处理数据](#b-%E5%9C%A8sub-reducer%E4%B8%AD%E5%A4%84%E7%90%86%E6%95%B0%E6%8D%AE)
      - [(c) redux storage](#c-redux-storage)
    - [(3) 让页面订阅redux storage，更新页面显示](#3-%E8%AE%A9%E9%A1%B5%E9%9D%A2%E8%AE%A2%E9%98%85redux-storage%E6%9B%B4%E6%96%B0%E9%A1%B5%E9%9D%A2%E6%98%BE%E7%A4%BA)
    - [(4) 在调试工具中查看Redux Store的数据](#4-%E5%9C%A8%E8%B0%83%E8%AF%95%E5%B7%A5%E5%85%B7%E4%B8%AD%E6%9F%A5%E7%9C%8Bredux-store%E7%9A%84%E6%95%B0%E6%8D%AE)
  - [13 Token过期或无效时引导用户到登录页面](#13-token%E8%BF%87%E6%9C%9F%E6%88%96%E6%97%A0%E6%95%88%E6%97%B6%E5%BC%95%E5%AF%BC%E7%94%A8%E6%88%B7%E5%88%B0%E7%99%BB%E5%BD%95%E9%A1%B5%E9%9D%A2)
  - [14 实现Immutable](#14-%E5%AE%9E%E7%8E%B0immutable)
  - [15 其他](#15-%E5%85%B6%E4%BB%96)
  - [参考](#%E5%8F%82%E8%80%83)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# React Note 02B

## 01 按需加载

对于模块化的组件库

> `antd`等组件库使用了`babel-plugin-import`实现了按需加载，可参考之前的Note

对于自己写的组件、使用`lazy import`，在`Chrome Dev Tools`中使用`Slow 3G`可以查看lazy import效果

> 普通import 
> 
> ~~~jsx
> import Home from '../pages/Home'
> import PersonalSetting from '../pages/PersonalSetting'
> import Login from '../pages/Login'
> import Register from '../pages/Register'
> ~~~
> 
> Lazy import 
> 
> ~~~jsx
> cost Home = lazy(() => import('../pages/Home'));
> cost PersonalSetting = lazy(() => import('../pages/PersonalSetting'));
> cost Login = lazy(() => import('../pages/Login'));
> cost Register = lazy(() => import('../pages/Register'));
> ~~~

在`Lazy import`过程中，可以使用`<Suspense>`来显示一个`loading...`页面

> ~~~jsx
> import React, {lazy, Suspense} from 'react';
> import { Route, Switch } from 'react-router-dom';
> import { Spin } from 'antd';
> import styles from './index.module.less';
> const Home     = lazy(() => import('../pages/Home'));
> const Login    = lazy(() => import('../pages/Login'));
> const Register = lazy(() => import('../pages/Register'));
> const Router = () => (
>     <Suspense 
>         fallback={
>             <div className={styles.spinWrap}>
>                 <Spin size="large" />
>             </div>    
>         }
>     >
>         <Switch>
>             <Route exact path="/" component={Home} />
>             <Route exact path="/login" component={Login} />
>             <Route exact path="/register" component={Register} />
>         </Switch>
>     </Suspense>
> );
> export default Router;
> ~~~

## 02 Suspense

> 在上面的例子中，异步加载尚未完成时，可以使用Suspense来展示一个“加载中”的图像
>
> 不论是路由、还是页面组件……，任何有异步加载需要fall back策略时，都可以用Suspense

类似的功能可以通过`componentDidMount`和`componentDidUpdate`声明周期函数来实现

> ~~~jsx
> class DynamicData extends Component {
> 	state = {loading:true,  error:null,  data:null};
> 	componentDidMount() {
> 		fetchData(this.props.id).then(
> 			data => {this.setState(loading:false, data)}; 
> 		).catch(
> 			error => {this.setState(loading:false,error:error.message)};
> 		)
> 	}
> 	componentDidUpdate(prevProps) {
> 		if (this.props.id !== prevProps.id) {
> 			this.setState(
> 				{loading:true}, 
> 				()=>{
> 					fetchData(this.props.id)
> 					.then(data. => {this.setState({loading : false, data});})
> 					.catch(error => {this.setState({loading : false, error : error.message});});
> 				}
> 			);
> 		}
> 	}
> 	render() {
> 		const {loading, error, data} = this.state;
> 		return loading ? (
> 			<p>Loading...</p>
> 		) : error ? (
> 			<p>Error: {error}</p>
> 		) : (
> 			<p>Data loaded </p>
> 		);
> 	}
> }	
> ~~~

但如果使用`<Suspense>`，可以更方便地用在所有子组件的异步操作上，将异步状态管理与UI组件分离：

> ~~~jsx
> class App extends Component. {
> 	render() {
> 		return (
> 			<Suspense fallback={<p>Loading...</p>}>
> 				<DeepNesting>
> 					<MaybeSomeAsyncComponent />
> 					<Suspense fallback={<p>Loading conent...</p>}>
> 						<ThereMightBeSeveralAsyncComponentsHere />
> 					</Supense>
> 					<Suspense fallback={<p>Loading footer...</p>}>
> 						<DeeplyNestedFooterTree />
> 					</Supsense>
> 				</DeepNesting>
> 			</Suspense>
> 		);
> 	};
> }
> ~~~

## 03 栅格系统的屏幕自适应

响应式栅格属性，值可以是`栅格数`，也可以是一个`包含其他属性的对象`

> | 属性 | 像素        |
> | ---- | ----------- |
> | xs   | <   576  px |
> | sm   | >= 576  px  |
> | md   | >= 768  px  |
> | lg   | >= 992  px  |
> | xl   | >= 1200 px  |
> | xxl  | >= 1600 px  |
>

代码例子

> ~~~jsx
> <div className={styles.container}> 
> 	<Row>
> 		<Col lg={7} md={24}>...</Col>
> 		<Col lg={17} md={24}>...</Col>
> 	</Row>
> </div>
> ~~~

## 04 文本截断，CSS伪类和伪元素

单行文本截断

> ~~~css
> .demo {
> 	white-space: nowrap;
> 	overflow:  hidden;
> 	text-overflow:  ellipsis;
> }
> ~~~
> 

两行显式文本、显示空间不够时截断

> ~~~css
> .demo {
> 	display: -webkit-box;
> 	overflow: hidden;
> 	-webkit-line-clamp:  2; //2行
> 	-webkit-box-orient: vertical;
> ~~~
> 

查看浏览器兼容性：[https://caniuse.com/](https://caniuse.com/)

伪类、伪元素：以上面的css class `.demo`为例子，

> * `.demo:hover`,  `.demo:active`，`.demo:focus`，`.demo:focus-within`，`.demo:visited`，`.demo:focus-visible`等就是它的`伪类`
> * `.demo::before`，`.demo::after`就是它的`伪元素`（假装`DOM`里面，该元素前面/后面，还有一个元素（其实没有 ）
> 

用伪元素实现双行文本显式时的截断（浏览器兼容性更好），但是当文本没有超长时，样式会有点奇怪，因此加一段过度色来改善

> ~~~css
> .demo {
> 	position: relative;
> 	line-height: 20px;
> 	height: 40px;
> 	overflow: hidden;
> }
> .demo::after {
> 	content: "...";
> 	position: absolute;
> 	bottom: 0;
> 	right: 0;
> }
> ~~~
> 
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/text_truncate.jpg" width="700" /></div>

## 05 数组去重

添加元素时进行去重检验

> ~~~jsx
> if (inputValue && !tags.concat(tempsTags).map( ({label}) => label ).includes(inputValue) ) {
> 	tempsTags = [...tempsTags, { key: `new-${tempsTags.length}`, label: inputValue }];
> }
> ~~~

对一个已有的数组去重

> 方法1： 适合简单数组（元素不是object）
> 
> ~~~jsx
> [...new Set(arr)]
> ~~~
> 
> 方法2：利用`reduce` + `includes`
> 
> ~~~jsx
> function unique(arr) {
> 	return arr.reduce((prev,  cur) => prev.includes(cur) ? prev : [...prev, cur],  []);
> }
> ~~~

## 06 时间处理和展示

库： [https//momentjs.com/](https//momentjs.com/)

> 比较方便，但是库比较大（72KB），建议按需加载，如果只是使用简单功能，自己实现也可以

## 07 HTML Header中的`<meta ...>`标签

`http-quiv`：设定浏览器行为、如cookie过期、禁止脱机浏览等

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/meta_quiv.jpg" width="700" /></div>

`charset`：告诉浏览器页面的编码方式 

> ~~~html
> <meta charset='utf-8'/>
> ~~~

`name`：主要给搜索引擎使用

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/meta_name.jpg" width="700" /></div>

## 08 `Ajax`封装

> [/src/utils/ajax.js](personal-app/src/utils/ajax.js)
>
> ~~~jsx
> // Axios封装
> import axios from 'axios';
> function getAxiosInstance(options) {
>  const instance = axios.create();
>  return instance;
> }
> function makeGet() {
>  return function(url, options) {
>      const instance = getAxiosInstance(options);
>      return instance({
>          url, 
>          method: 'get',
>          ...options
>      })
>  }
> }
> function makePost() {
>  return function(url, options) {
>      const instance = getAxiosInstance(options);
>      return instance({
>          url, 
>          method: 'post',
>          ...options
>      })
>  }
> }
> export default {
>  get: makeGet(),
>  post: makePost()
> };
> ~~~

## 09 使用Redux在视图层触发action获取后端数据

### (1) Redux经典链路：单向数据流

> `view`（界面发起请求）-> `actions` (获取数据）-> `reducer`（数据处理：过滤、修剪等） -> `stores/state`（全局数据存储）-> `View` ...
> 
> 用途：
> 
> * 没有`Redux`，组件之间的数据传递会变得复杂
> * 使用`Redux`，同一个的数据流，可以跨组件层级传递数据

### (2) 单向数据流（`react`） v.s 双向绑定（早期的`angular`、`vue`）

> * 单向数据流：优点是可追踪可记录、可维护、适合大型项目；缺点是代码步骤多、关注点分离
> * 双向绑定：有点是全自动、简单快捷、适合展示型项目；缺点是乱

### (3) 引入`redux`、`axios`

> 以使用`yarn`包管理器为例
> 
> ~~~bash
> yarn add redux redux-thunk redux-react-hook
> yarn add axios
> ~~~
> 
> `redux-thunk`：让redux变为异步的中间件
> `redux-react-hook`：连接redux和react的桥梁
> `axios`：http请求封装

### (4) 代码编写步骤

对应于上面`单向数据流`的四个节点（`view`, `actions`, `reducer`, `stores/state`）

#### 步骤1：创建`store`并指定将其作用在哪个父组件上

> [/src/index.js](personal-app/src/index.js)
>
> ~~~jsx
> ...
> import { createStore, applyMiddleware } from 'redux';  //用来注入reducers
> import thunk from 'redux-thunk';  //异步操作中间件 
> import { StoreContext, storeContext } from 'redux-react-hook';  //连接react和redux的桥
> ...
> import reducer from './reducers'; 
> 
> ...
> 
> const store = createStore(
> 	reducer, 				// 数据处理模块（src/reducers/index.js中编写的reducers）
> 	applyMiddleware(thunk) 	// 应用让redux变为异步的中间件
> )
> 
> ...
> 
> // 用<StoreContext.Provider value={store}>包裹<App>，这样App的各处代码都能取到store的数据
> ReactDOM.render(
>     <StoreContext.Provider value={store}>
>      <App />
>     </StoreContext.Provider>
>     ,
>     document.getElementById('root')
> );
> ~~~
>
> * `<StoreContext.Provider value={store}>`：有自己的钩子`useContext`、数据不必从最外层组件一层一层传递，内层的子组件可以用钩子拿到数据
> * 可以让`<StoreContext.Provider value={store}>`包裹最外层组件，在全局应用`redux`
> * 也可以让`<StoreContext.Provider value={store}>`包裹一个局部组件，在局部应用`redux`

#### 步骤2：创建`Reducer`用于存储数据计算和修剪之后的数据结果

> [/src/reducers/index.js](personal-app/src/reducers/index.js)
> 
> ~~~jsx
> import { combineReducers } from 'redux';
> const rootReducer = combineReducers({
> 	// 后续对项目数据分门别类存储时、可以在这里combine，挂在rootReducer下面
> });
> export default rootReducer;
> ~~~

#### 步骤3：编写视图层（`view`）代码并通过`dispatch`触发`action`执行

> [/src/pages/Register/index.js](personal-app/src/pages/Register/index.js)
>
> ~~~jsx
> ...
> import { useDispatch } from 'redux-react-hook'; 
> import { getCaptcha } from '../../actions/register';
> 
> ...
> 
> const Register = () => {
> 	const dispatch =  useDispatch();
> 	...
> 	const handleClickCaptcha = () => {
> 		form.validateFields(['username', 'email', 'password']) 
> 		.then(() => {
> 			console.log(form.getFieldsValue(['username', 'email', 'password']));
> 			// 触发actions执行
> 			dispatch(getCaptcha(form.getFieldsValue(['username', 'email', 'password'])));
> 		})
> 	}
> 	...
> }
> ~~~

#### 步骤4：编写`action`层代码用于获取数据

> [/src/actions/register.js](personal-app/src/actions/register.js)
> 
> ~~~jsx
> import { message } from 'antd';
> import * as api from '../api/register'; 
> export function getCaptcha(payload = {}) {
>     return async () =>  {
>         const response = await api.getCaptcha(payload);
>         const {data: {code, message:msg}} = response;
>         if (code === 20020) {
>             const {data: {data : { captcha }}} = response;
>             message.success(`${msg}, 验证码为${captcha}`);
>         } else {
>             message.error(`${msg}`);
>         }
>     }
> }
> ~~~
> 
> 这一小节只涉及`Redux`经典链路的前半段（`viewy` → `action`），后半段（`acition` → `reducer` → `store/state` → `view`）要在`home`页面的例子中才能看到

## 10 使用拦截器

用途举例

> (1) 后端返回大量的数据，但只有其中data字段包含的数据与前端展示有关，希望统一在一个地方对数据做选取
> (2) 登录成功后后端会返回一个token，前端需要在没有token时跳转到登录页面，有token时为所有后端请求都加上这个token，希望代码统一写在一处
> (3) token失效后端会返回一个错误码，希望在一个地方对该错误码做统一处理

方法：

> [/src/utils/ajax.js](personal-app/src/utils/ajax.js)：安装拦截器
>
> ~~~jsx
> import axios from 'axios'; // Axios封装
> import * as interceptors from './interceptors'; // 引入拦截器文件
> function getAxiosInstance(options) {
> const instance = axios.create();
> interceptors.install(instance, options); // 安装拦截器
> return instance;
> }
> ...
> ~~~
>
> [/src/utils/interceptors.js](personal-app/src/utils/interceptors.js)：定义拦截器，以及install方法

## 11 SSO单点登录的实现

> HTTP具有无状态的特性，导致路由/页面跳转/刷新时登录状态丢失，这是单点登录需要解决的问题

### (a) 请求响应过程

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/react_sso_cookie_approach.jpg" width="600px"/></div>
>
> 第一次请求：登录请求
>
> 第二次请求：页面请求

### (b) 使用Cookie的缺点和解决办法

缺点

> 1. 长度限制
> 2. 不安全、有篡改风险

解决方法

> 给用于存放登录信息的cookie设置如下三个属性：
>
> 1. HttpOnly：表示只能在http请求中携带，不能通过js操作
>
> 2. Secure：只能在https中携带
>
> 3. SameSite（取值范围：strict、lax、none）：只有同域（同网站）的请求可以读取这个cookie
>
>     * Strict：域名必须完全一致
>     * Lax：需要主域名一致
>     * None：无限制
>
>     但要注意taobao.com账号登录tmall.com，使用jd.com.cn账号登录 jd.hk这样的场景
>
>     另外注意浏览器是否支持该属性以及该值属性的默认值，例如Chrome版本小于8.0时该属性的默认值不会为None而是为空
>
> 在Chrome Dev Tool：Applation → Cookies页面，查看Cookie时可以看到这三个属性

### (c) Token保存方法：Cookie、localStorage、sessionStorage的区别

请求响应过程

> 登录成功、服务器生成并返回一个token → 客户端将token保存在本地 → 再次请求时会附带token给服务器 → 服务器收到请求后进行对比

token保存方法对比

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/react_sso_token_storage.jpg" width="800px"/></div>

demo中的代码：使用了local storage

> [personal-app/src/actions/account.js](personal-app/src/actions/account.js)
>
> ~~~jsx
> export function login(payload = {}) {
> 	return async () => {
> 		// data:{token} = {} 用来处理data为空的情况
> 		const {code, message:msg, data:{token}={}} = await api.login(payload); 
> 		if (code === 0) {
> 			// 将token保存在浏览器本地缓存中（也可以保存在cookies中等）
> 			// message.success(`${msg}`);
> 			window.localStorage.setItem('personal-app-token', token); 
> 			// 登录成功后跳转到首页，可以用下面的方法，也可以用react router提供的方法
> 			window.location.href="/";
> 		} else {
> 			message.error(`${msg}`);
> 		}
> 	}
> }
> ~~~
>
> [personal-app/src/utils/interceptors.js](personal-app/src/utils/interceptors.js)
>
> ~~~jsx
> // 用来处理request的拦截器，用数组存储，可以配多个
> const requestInterceptors = [
> 	{
> 		name: 'addHttpRequestHeader',
> 		success(config) {
> 			// 与后端协商http header中添加的token格式
> 			config.headers['Authorization'] = `Bearer ${window.localStorage.getItem('personal-app-token')}`;
> 			return config;
> 		},
> 		fail(err) {
> 			console.error('request error: ', err);
> 			return Promise.reject(err);
> 		}
> 	}
> ];
> ~~~

下一个项目使用weibo账号登录，会进一步介绍SSO

## 12 Redux链路完整例子

> Redux用于跨层组件间的数据传递。创建一个统一的单向数据流，任何一个子组件访问后端获取/更新数据，都可以通过存入到redux storage中的方法，供所有的组件使用。

### (1) dispatch后端调用action

页面dispatch用于执行后端调用的action

> 代码： [personal-app/src/pages/Home/index.js](personal-app/src/pages/Home/index.js)
>
> ~~~jsx
> const Home = () => {
> 	// 用来从view分发getUserProfile这个action
> 	const dispatch = useDispatch();
> 
> 	// 页面上有三个标签页（项目、文章 、工具）、这个state用来控制显示那个标签页的状态
> 	const [tabKey, setTabKey] = useState('projects');
> 	const onTabChange = (key) => {
> 		setTabKey(key);
> 	}
> 
> 	// 文档: https://zh-hans.reactjs.org/docs/hooks-effect.html
> 	// * 如果想执行只运行一次的 effect（仅在组件挂载和卸载时执行），可以传递一个空数组（[]）作为第二个参数。这就告诉 React 你的 effect 不依赖于 props 或 state 中的任何值，所以它永远都不需要重复执行。这并不属于特殊情况 —— 它依然遵循依赖数组的工作方式。
> 	// * 如果传入一个空数组（[]），effect 内部的 props 和 state 就会一直拥有其初始值。尽管传入 [] 作为第二个参数更接近大家更熟悉的 componentDidMount 和 componentWillUnmount 思维模式，但我们有更好的方式来避免过于频繁的重复调用 effect。除此之外，请记得 React 会等待浏览器完成画面渲染之后才会延迟调用 useEffect，因此会使得额外操作很方便。
> 	// * 如果传入非空的数组（[abc])，那么仅在 abc 更改时更新
> 	useEffect(() => {
> 		// getUserProfile返回一个方法、该方法提供参数以便让redux传入dispatch
> 		dispatch(getUserProfile());
> 	}, [dispatch]); // 把引入的变量dispatch放到依赖里，出于规范考虑
> 
> 	// 页面渲染
> 	return (
> 		<div className={styles.container}>
> 			...
> 		</div>  
> 	)
> };
> ~~~
>
> 被dispatch的action：[personal-app/src/actions/profile.js](personal-app/src/actions/profile.js)
>
> ~~~jsx
> /* 用来获取用户信息的action */
> import { message } from 'antd';
> import * as api from '../api/profile'; // ES Module语法：https://juejin.im/post/6844903993462751239
> import { GET_PROFILE } from '../constants/actions';
> 
> export function getUserProfile(payload = {}) {
> 	return async (dispatch /* redux框架传入的参数 */) =>  {
> 		const {code, message:msg, data} = await api.getUserProfile(payload); 
> 		if (code === 0) {
> 			// 获取用户信息成功，继续dispatch到root reducer，分发一个存储用户信息的操作
> 			dispatch({
> 				// 因为分发后先到达root-reducer
> 				// 需要提供一个type标识，以便能够进一步定位到具体执行该操作的sub-reducer
> 				type: GET_PROFILE,
> 				payload: data
> 			})
> 		} else {
> 			// 获取失败
> 			message.error(`${msg}`);
> 		}
> 	}
> }
> ~~~
>
> 被action调用的API：[personal-app/src/api/profile.js](personal-app/src/api/profile.js)
>
> ~~~jsx
> import ajax from '../utils/ajax';  
> import { HOST } from '../constants';
> 
> export function getUserProfile(params) {
> 	return ajax.post(`${HOST}/account/center`, {data: params});
> }
> ~~~
>
> 其中引用的变量`GET_PROFILE`：[personal-app/src/constants/actions.js](personal-app/src/constants/actions.js)
>
> ~~~jsx
> export const GET_PROFILE = 'profile/GET_PROFILE';
> ~~~

### (2) dispatch数据处理操作到reducer

后端调用action执行成功后，dispatch用于处理数据的action到Root Reducer，Root Reducer将action分发给具体的Sub Reducer，Sub Reducer将处理后的数据存储在Redux Store（Redux State）中

#### (a) 在root reducer中添加sub reducer

> 代码：[personal-app/src/reducers/index.js](personal-app/src/reducers/index.js)
>
> ~~~jsx
> // 用来把多层reducer组合起来
> import { combineReducers } from 'redux';
> import profile from './profile';
> 
> const rootReducer = combineReducers({
> 	// 用来处理用户信息的Reducer
> 	profile,
> });
> 
> // export出去，由入口文件index.js来import
> export default rootReducer;
> ~~~

#### (b) 在sub reducer中处理数据

> [personal-app/src/reducers/profile.js](personal-app/src/reducers/profile.js)
>
> ~~~jsx
> import {GET_PROFILE} from '../constants/actions';
> 
> const initState = {};
> 
> export default function reducer(state = initState, action) {
> 	switch (action.type) {
> 		// 当type为GET_PROFILE时执行下列操作
> 		case GET_PROFILE:
> 			return {
> 				// 把state展开
> 				...state,
> 				// 添加服务器返回的信息
> 				// 这里也可以做其他定制化的数据处理
> 				// 数据处理集中统一在reducer中做，职责划分更加清晰
> 				user: action.payload,
> 			}
> 		default:
> 			return state;
> 	}
> }
> ~~~

#### (c) redux storage

> `redux storege`存储结构分两层，第一层定义在root reducer中，第二层定义在sub reducer中
>
> 以上面存入的action.payload为例，获取它的方式是
>
> * 先使用`profile`在第一层级中查找
> * 再使用`user`在第二层级中查找
>
> 下面的小节是具体的例子

### (3) 让页面订阅redux storage，更新页面显示

代码： [personal-app/src/pages/Home/index.js](personal-app/src/pages/Home/index.js)

> ~~~jsx
> ...
> import { useDispatch, useMappedState } from 'redux-react-hook';
> ...
> 
> // 代码（1）：在redux storage第一层中查找profile
> // 给useMappedStete使用的方法，使用redux storage中的profile部分
> const mapState = (state) => (state.profile); 
> 
> ...
> 
> const Home = () => {
> 	...
> 
> 	// 代码（2）：在redux storage第二层中查找user	
> 	// 使用解构赋值查找user，同时指定初始值{}，避免后端返回数据之前加载页面时发生undefined error
> 	const { user = {} } = useMappedState(mapState);
> 
> 	// 然在已经通过state.profile.user拿到后端返回数据，可以用于页面展示
>     console.log(user);    
>     ...
> }
> ~~~
>
> 上面由使用方设置初始值的方法，容易发生漏写，更好的方法是在数据定义时就指定初始值
>
> [personal-app/src/reducers/profile.js](personal-app/src/reducers/profile.js)
>
> ~~~jsx
> ...
> 
> // 指定初始默认值
> const initState = {};
> export default function reducer(state = initState, action) {
> 	switch (action.type) {
> 		case GET_PROFILE:
> 			...
> 	}
> }
> ~~~

### (4) 在调试工具中查看Redux Store的数据

> 如果在开发时、想对上面的action、state来debug，需要使用Redux DevTools Chrome 插件，并使用`redux-devtools-extension`包
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/react_redux_devtool.jpg" width="700" /></div> 
>
> 安装插件后dev tool中会增加一个redux tab

## 13 Token过期或无效时引导用户到登录页面

使用拦截器统一处理，代码：[personal-app/src/utils/interceptors.js](personal-app/src/utils/interceptors.js)

> ~~~jsx
> // 用来处理response的拦截器，用数组存储，可以配多个
> const responseInterceptors = [
> 	...,
>     {
>         name: 'handleError', 
>         success(response) {
>             if (response.code === 70006 /*token失效的code*/) {
>                 window.location.href = '/login'; //跳转到登录页
>             } else {
>                 return response;
>             }
>         }
>     }
> ];
> ~~~

## 14 实现Immutable

React需要通过比较props、states等来确定是否需要重新渲染，使用immutable可以提高比较效率

> 例子1
>
> ~~~jsx
> const {code, message:msg} = await api.getUserInfo(payload);
> ~~~
>
> 例子2
>
> ~~~js
> const array = [1,2,3]; 
> // array.push(4)会破坏Immuatable，const也不能阻止
> // 下面的代码可以实现Immutable
> const newArray = [...array, 4];
> ~~~
>
> 数据量大时，可以使用immer.js、immutable等package来将数据转换为Immutable
>
> ~~~jsx
> import {GET_PROFILE} from '../constants/actions';
> 
> const initState = {};
> export default function reducer(state = initState, action) {
> 	switch (action.type) {
> 		case GET_PROFILE:
> 			return {
> 				...state,
> 				// 使用Immutable数据结构
> 				user: Immutable.Map(action.payload),
> 			}
> 		default:
> 			return state;
> 	}
> }
> ~~~

## 15 其他

(1) 点赞功能

> * 立即处理，批量处理
> * 引入`Redis`
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/nosql.jpg" width="700" /></div> 

(2)  应用Tab、项目Tab

> * 使用了`<List>`组件，可以根据屏幕大小自适应排版
> * 用`numeral`库来给数字加逗号之类
> * 自己写了个实现“万”的功能
> * 对于变化非常频繁的数字，最好使用“虚数”（例如 xxx万），而不是“确数”（例如 21323）
>
> 具体可参考[proj_02_personal_center_02.md](proj_02_personal_center_02.md)

(3) 前后端接口文档通常要说明

> * API 
> * 请求方式：POST/GET
> * 参数类型：JSON，...
> * 请求字段
> * 响应类型：JSON，...
> * 响应字段（含code约定）

(4)  在webpack中可以使用预加载等方式，例如DNSPrefetch等来

(5) `@babel/plugin-syntax-dynamic-import`：用来的动态加载某个包，例如这个包比较大，但仅在某个特定场景下采用用到，这是可以在代码当中（而不是代码头部）来import，例如

> ~~~jsx
> onClick = () => {
> 	import('moment').then((moment) => moment())
> }
> ~~~

(6) 动态polyfill：[http://polyfill.io](http://polyfill.io)

>根据浏览器动态加载一些polyfill，来提高代码的浏览器兼容

## 参考

查看浏览器兼容性：[https://caniuse.com/](https://caniuse.com/)

JavaScript学习和参考：[http://javascript.info](http://javascript.info)

查看一个包的体积：[http://bundlephobia.com](http://bundlephobia.com)


