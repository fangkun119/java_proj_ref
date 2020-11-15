# React Note 02B

## 01 按需加载

对于模块化的组件库

> `antd`等组件库使用了`babel-plugin-import`实现了按需加载，可参考之前的Note

对于自己写的组件、使用`lazy import`，在`Chrome Dev Tools`中使用`Slow 3G`可以查看lazy import效果

> 普通import 
> 
> ~~~javascript
> import Home from '../pages/Home'
> import PersonalSetting from '../pages/PersonalSetting'
> import Login from '../pages/Login'
> import Register from '../pages/Register'
> ~~~
> 
> Lazy import 
> 
> ~~~javascript
> cost Home = lazy(() => import('../pages/Home'));
> cost PersonalSetting = lazy(() => import('../pages/PersonalSetting'));
> cost Login = lazy(() => import('../pages/Login'));
> cost Register = lazy(() => import('../pages/Register'));
> ~~~

在`Lazy import`过程中，可以使用`<Suspense>`来显示一个`loading...`页面

> ~~~javascript
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

> 不论是路由、还是页面组件……，任何有异步加载需要fall back策略时，都可以用Suspense
 
类似的功能可以通过`componentDidMount`和`componentDidUpdate`声明周期函数来实现

> ~~~javascript
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
~~~

但如果使用`<Suspense>`，可以更方便地用在所有子组件的异步操作上，将异步状态管理与UI组件分离：

> ~~~javascript
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

属性 	| 像素  
------	| ---------
xs   	| <   576  px
sm  	| >= 576  px
md  	| >= 768  px
lg   		| >= 992  px
xl    	| >= 1200 px
xxl  	 	| >= 1600 px

> ~~~javascript
> <div className={styles.container}> 
> 	<Row>
> 		<Col lg={7} md={24}>...</Col>
> 		<Col lg={17} md={24}>...</Col>
> 	</Row>
> </div>
> ~~~

## 04 文本截断，CSS伪类和伪元素

> 单行文本截断
> 
> ~~~css
> .demo {
> 	white-space: nowrap;
> 	overflow:  hidden;
> 	text-overflow:  ellipsis;
> }
> ~~~
> 
> 两行显式文本、显式不下时截断
> 
> ~~~css
> .demo {
> 	display: -webkit-box;
> 	overflow: hidden;
> 	-webkit-line-clamp:  2; //2行
> 	-webkit-box-orient: vertical;
> ~~~
> 
> 查看浏览器兼容性：[https://caniuse.com/](https://caniuse.com/)
> 
> 伪类、伪元素：以上面的css class `.demo`为例子，
> 
> * `.demo:hover`,  `.demo:active`，`.demo:focus`，`.demo:focus-within`，`.demo:visited`，`.demo:focus-visible`等就是它的`伪类`
> * `.demo::before`，`.demo::after`就是它的`伪元素`（假装`DOM`里面，该元素前面/后面，还有一个元素（其实没有 ）
> 
> 用伪元素实现双行文本显式时的截断（浏览器兼容性更好），但是当文本没有超长时，样式会有点奇怪
> 
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
> 加一段过度色来改善
> 
> ![](https://raw.githubusercontent.com/kenfang119/pics/main/200_react/text_truncate.jpg)

## 05 数组去重

添加元素时进行去重检验

> ~~~javascript
> if (inputValue && !tags.concat(tempsTags).map( ({label}) => label ).includes(inputValue) ) {
> 	tempsTags = [...tempsTags, { key: `new-${tempsTags.length}`, label: inputValue }];
> }
> ~~~

对一个已有的数组去重

> 方法1： 适合简单数组（元素不是object）
> 
> ~~~javascript
> [...new Set(arr)]
> ~~~
> 
> 方法2：利用`reduce` + cccc`includes`
> 
> ~~~javascript
> function unique(arr) {
> 	return arr.reduce((prev,  cur) => prev.includes(cur) ? prev : [...prev, cur],  []);
> }
> ~~~

## 06 时间处理和展示

库： [https//momentjs.com/](https//momentjs.com/)

> 比较方便，但是库比较大（72KB），建议按需加载，如果只是使用简单功能，自己实现也可以

## 07 HTML `<header>`中的`<meta ...>`标签

`http-quiv`：设定浏览器行为、如cookie过期、禁止脱机浏览等

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/200_react/meta_quiv.jpg)

`charset`：告诉浏览器页面的编码方式 

> ~~~html
> <meta charset='utf-8'/>

`name`：主要给搜索引擎使用

> ![](https://raw.githubusercontent.com/kenfang119/pics/main/200_react/meta_name.jpg)

## 07 `Ajax`封装

> [/src/utils/ajax.js](personal-app/src/utils/ajax.js)
>
> ~~~javascript
> // Axios封装
> import axios from 'axios';
> function getAxiosInstance(options) {
>     const instance = axios.create();
>     return instance;
> }
> function makeGet() {
>     return function(url, options) {
>         const instance = getAxiosInstance(options);
>         return instance({
>             url, 
>             method: 'get',
>             ...options
>         })
>     }
> }
> function makePost() {
>     return function(url, options) {
>         const instance = getAxiosInstance(options);
>         return instance({
>             url, 
>             method: 'post',
>             ...options
>         })
>     }
> }
> export default {
>     get: makeGet(),
>     post: makePost()
> };
~~~

## 08 使用`Redux`在视图层触发`action`发起请求、获取数据

(1) Redux经典链路：单向数据流

> `view`（界面发起请求）-> `actions` (获取数据）-> `reducer`（数据处理：过滤、修剪等） -> `stores/state`（全局数据存储）-> `View` ...
> 
> 用途：
> 
> * 没有`Redux`，组件之间的数据传递会变得复杂
> * 使用`Redux`，同一个的数据流，可以跨组件层级传递数据

(2) 单向数据流（`react`） v.s 双向绑定（早期的`angular`、`vue`）

> * 单向数据流：优点是可追踪可记录、可维护、适合大型项目；缺点是代码步骤多、关注点分离
> * 双向绑定：有点是全自动、简单快捷、适合展示型项目；缺点是乱

(3) 引入`redux`、`axios`

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

(4) 代码步骤：对应于上面`单向数据流`的四个节点（`view`, `actions`, `reducer`, `stores/state`）

步骤1：创建`store`，并指定该`store`作用在哪个父组件上

> [/src/index.js](personal-app/src/index.js)
> 
> ~~~javascript
> ...
> import { createStore, applyMiddleware } from 'redux';  //用来注入reducers
> import thunk from 'redux-thunk';  //异步操作中间件 
> import { StoreContext, storeContext } from 'redux-react-hook';  //连接react和redux的桥
> ...
> import reducer from './reducers'; 
> ...
> const store = createStore(
>   reducer, 					// 数据处理模块（src/reducers/index.js中编写的reducers）
>   applyMiddleware(thunk) 	// 应用让redux变为异步的中间件
> )
> ...
> // 用<StoreContext.Provider value={store}>包裹<App>，这样App的各处代码都能取到store的数据
> ReactDOM.render(
>   <StoreContext.Provider value={store}>
>     <App />
>   </StoreContext.Provider>
>   ,
>   document.getElementById('root')
> );
> ~~~
> 
> * `<StoreContext.Provider value={store}>`：有自己的钩子`useContext`、数据不必从最外层组件一层一层传递，内层的子组件可以用钩子拿到数据
> * 可以让`<StoreContext.Provider value={store}>`包裹最外层组件，在全局应用`redux`
> * 也可以让`<StoreContext.Provider value={store}>`包裹一个局部组件，在局部应用`redux`

步骤2：创建`Reducer`，这里存储数据计算和修剪之后的结果

> [/src/reducers/index.js](personal-app/src/reducers/index.js)
> 
> ~~~javascript
> import { combineReducers } from 'redux';
> const rootReducer = combineReducers({
> 	// 后续对项目数据分门别类存储时、可以在这里combine，挂在rootReducer下面
> });
> export default rootReducer;
> ~~~

步骤3：视图层（`view`）代码，这里会通过`dispatch`触发`action`的执行

> [/src/pages/Register/index.js](personal-app/src/pages/Register/index.js)
> 
> ~~~javascript
> ...
> import { useDispatch } from 'redux-react-hook'; 
> import { getCaptcha } from '../../actions/register';
> ...
> const Register = () => {
> 	const dispatch =  useDispatch();
> 	...
> 	const handleClickCaptcha = () => {
>		form.validateFields(['username', 'email', 'password']) 
>		.then(() => {
>			console.log(form.getFieldsValue(['username', 'email', 'password']));
>			// 触发actions执行
>			dispatch(getCaptcha(form.getFieldsValue(['username', 'email', 'password'])));
>		})
>	}
>	...
> }

步骤4：`action`层代码，获取数据的操作

> [/src/actions/register.js](personal-app/src/actions/register.js)
> 
> ~~~javascript
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
> 这一小节只涉及`Redux`经典链路的前半段（`view`->`action`），后半段（`acition`->`reducer`->`store/state`->`view`）要在`home`页面的例子中才能看到

## 10 使用拦截器

用途举例：

> (1) 后端返回大量的数据，但只有其中data字段包含的数据与前端展示有关，希望统一在一个地方对数据做选取
> (2) 登录成功后后端会返回一个token，前端需要在没有token时跳转到登录页面，有token时为所有后端请求都加上这个token，希望代码统一写在一处
> (3) token失效后端会返回一个错误码，希望在一个地方对该错误码做统一处理

方法：

> [/src/utils/ajax.js](personal-app/src/utils/ajax.js)：安装拦截器
> 
> ~~~javascript
> import axios from 'axios'; // Axios封装
> import * as interceptors from './interceptors'; // 引入拦截器文件
> function getAxiosInstance(options) {
>    const instance = axios.create();
>    interceptors.install(instance, options); // 安装拦截器
>    return instance;
> }
> ...
> ~~~
> 
> [/src/utils/interceptors.js](personal-app/src/utils/interceptors.js)：定义拦截器，以及install方法

## 11 项目结构

[/src/constants]()：常量
[/src/actions](): redux单向数据流中的action步骤
[/src/api]()：调用ajax访问后端的各个api
[//]()：




## 99 其他

1.实现个人标签的展示和添加功能

2.点赞功能

> * 立即处理，批量处理
> * 引入`Redis`

![](https://raw.githubusercontent.com/kenfang119/pics/main/200_react/nosql.jpg) 

3.应用Tab、项目Tab

> * 使用了`<List>`组件，可以根据屏幕大小自适应排版
> * 用`numeral`库来给数字加逗号之类
> * 自己写了个实现“万”的功能
> * 对于变化非常频繁的数字，最好使用“虚数”（例如 xxx万），而不是“确数”（例如 21323）

4.前后端接口文档

> * API 
> * 请求方式：POST/GET
> * 参数类型：JSON，...
> * 请求字段
> * 响应类型：JSON，...
> * 响应字段（含code约定）

5.redux用于跨层组件间的数据传递，但是如果只是单个组件内部的状态，并不需要redux，毕竟redux开发比较复杂

## 参考资料：

*  查看浏览器兼容性：[https://caniuse.com/](https://caniuse.com/)




