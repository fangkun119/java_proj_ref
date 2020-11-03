# React Note 1

## 1. 三个前端框架

> * `Angular`: Google，特点是提供丰富的内置组件（`build-in`）
> * `React`: Facebook，开源出来的内容非常少、最小化专注于界面（`UI-building`）
> * `Vue`：独立开发者（前谷歌员工），特点介于两者之间

| Feature         	| Angular	| React 	| Vue    	|
| -----------	| ------	| ---- 	| ----- 	|
| UI/DOM操作 	| 支持		| 支持 	| 支持 	|
| 状态管理     	| 支持	 	| 较弱	| 支持    	|
| Routing      	| 支持	 	| 		| 支持    	|
| 表单验证/处理  	| 支持	 	| 		|           	|
| Http客户端  	| 支持	 	| 		|           	|

语法比较：

> * `Angular`：类Html静态模板形式，代码只要关注动态部分即可
> * `React`：JSX组件形式，动态模板。代码较少，但状态变化触发重新渲染，为此`React`引入`Virtual Dom`、通过以下方法来降低渲染开销 (`Angular`,`Vue`后续也引入了`Virtual Dom`)
> 	* 比较、选择性渲染
> 	* 批量Dom操作、在event loop的结尾（一组短时间内连续发生的Dom操作）统一渲染
> * `Vue`：Html静态模板部分类似`Angular`

框架学习曲线：

> * `Vue`：平滑、上手快、中小型公司经常使用
> * `React`：初期JSX语法有一定难度
> * `Angular`：三个框架学习成本最高

框架功能/性能差别：

> 功能差别已经不大了（生态都已经建立起来）；性能也很难比较（三足鼎立状态、没有哪一款可以绝对优于其他2款，一些特定操作的测评与实际应用差别较大也不具备代表性）

框架流行度：

> `React`占明显优势，国外`React`和`Angular`，国内`React`和`Vue`

## 2. create-react-app

官方提供的脚手架、创建单页应用程序、零配置构建。使用下面4种方法之一（取决于使用哪种包管理工具）：

> ~~~bash
> create-react-app my-app 		# 全局安装create-react-app的情况
> npx create-react-app my-app  	# npx来自npm 5.2+或更高版本
> npm init react-app my-app  		# npm init <initializer> 在 npm 6+可用
> yarn create react-app my-app 	# yarn create在Yarn 0.25+中可用
> ~~~

项目创建之后（以使用npm包管理工具为例，yarn差别也不大），有四条命令可以用

> `npm start`：启动项目、可以在浏览器中查看
> `npm run build`:  构建，把JSX语法、高阶ES语法等转义成浏览器能用的格式，压缩JS等
> `npm test`:  运行单元测试等
> `npm run eject`: 将零配置项目、改成暴露配置的形式（该操作不可逆），不满意React提供的默认配置时，可以用这个命令

## 3.组件化思想

> * 小组件组装成大组件、大组件组装成更大的组件、……，提高代码复用
> * Trade Off：组件化缺乏 v.s 过度组件化

## 4. 用于快速上手的项目

### (1) 文档

> 环境搭建，项目目录结构，package.json，代码：[`proj_01_todolistapp.md`](proj_01_todolistapp.md)

### (2) 代码及注释链接

[todo-list/public/index.html](todo-list/public/index.html)

> 载入CSS Lib，使用定义在`to-dolist/src/App.js`中的`App Component`作为该应用的主Component

~~~html
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bulma/0.9.1/css/bulma.min.css" />
~~~
[todo-list/src/App.js](todo-list/src/App.js)及[todo-list/src/App.css](todo-list/src/App.css)

> 主Component：实现方法及相关React使用要点，见代码注释

## 5. 项目知识点

### (1)  什么是钩子（Hook）
>
> 先看的`class component`写法  
>
>~~~javascript
> import. React, { Component } from 'react';
> export default class Button. extends Component {
> 	constructor() {
> 		super();
> 		this.state = { buttonText: "Click me"};  //状态
> 		this.handleClick = this.handleClick.bind(this);
> 	}
> 	// 声明周期函数
> 	componentWillMount() {}
> 	componentDidMount() {console.log('mounted');}
> 	componentWillReceiveProps() {}
> 	componentDidUpdate() {}
> 	componentWillUnmount() {}
> 	// 回调函数，更新状态，触发重新渲染
> 	handleClick() {
> 		this.setState(() => {return {buttonText: "clicked"};});
> 	}
> 	// 渲染函数
> 	render() {
> 		const { buttonText } = this.state;
> 		return <button onClick={this.handleClick}>{buttonText}</button>;
> 	}
> }
>~~~
> 
> 以前的`functional component`只能用props，没有state也没有生命周期，复杂组件都只能 用`class component`来写
> 
> ~~~javascript
> function Welcome(props) {
>  	return <h1>Hello, {props.name}</h1>;
> }
> ~~~
> 
>  `functional. compoent`加上`hooks`之后，基本上等同于`class component`。
> 
> * 状态以及组件生命周期，都可以用`hooks`钩进来（例如：`useState()`，`useContext()`，`useReducer()`,  `useEffect()`）
> * 组件尽量写成纯函数，如果需要外部功能和副作用，就用`hooks`

### (2) `useState`钩子

> ~~~javascript
> const [value, setValue] = useState(''); //状态钩子
> ~~~
> 
> `setState`：异步提交，在`event loop`中批量统一渲染
> 
> ~~~javascript
> onChange={(e) => {
>      console.log('before: ', value); 
>      setValue(e.target.value);       // 异步操作，只会把value变化提交给event loop
>      console.log('after: ', value);   // 输出的仍然是旧的value
> }}
> ~~~
> 
> 为什么`setState`是一个异步过程（批量异步化）
> 
> * 内部一致性：props、state全部都是异步的
> * 性能优化：所以要批量异步，而不是每次setState都立刻异步
> * 参考：[https://github.com/facebook/react/issues/11527#issuecomment-360119710](https://github.com/facebook/react/issues/11527#issuecomment-360119710)
> 
> `funtional component` 函数式编程,  `class component`在16.6 Hook出现之前处于主导地位

### (3) `useEffect`钩子 

> ~~~javascript
> // react.useEffect(()=>{}, [])是React的Effect Hook
> // * 当参数2内部的元素发生改变时，调用参数1设置的回调函数
> // * 当参数2是空数组时，参数1的回调函数只在整个组件挂载和卸载时执行，例如：
> //   useEffect(()=>{console.log("componentDidMount")}, [])
> // * 当参数2没有提供时，参数1的回调函数在整个组件每轮渲染后执行
> useEffect(()=>{
> 	  // console.log("todos change"); 
>     // localStorage可存储少量数据（< 5 M）在浏览器本地，
>     // 1. 在Chrome DevTool的console中可以运行和实验相关函数，例如：localStorage.clear(); localStorage.removeItem('todos')
>     // 2. localStorage不能存储二进制数据，需要将其转化成JSON
>     localStorage.setItem('todos', JSON.stringify(todos));
> }, [todos])
> ~~~
>
> 参考1：[https://zh-hans.reactjs.org/docs/hooks-reference.html#useeffect](https://zh-hans.reactjs.org/docs/hooks-reference.html#useeffect) <br/>
> 参考2：[https://zh-hans.reactjs.org/docs/hooks-effect.html](https://zh-hans.reactjs.org/docs/hooks-effect.html)

### (4) 屏蔽表单提交时的默认操作及屏蔽方法

> ~~~javascript
>   const handleSubmit = (e) => {
>     e.preventDefault();  //屏蔽默认的on submit页面刷新行为
>     ...c
>   }
> ~~~
> 
> 类似的还有链接点击，默认操作会做链接跳转（浏览器里的url发生变化）

### (5) 屏蔽点击事件向上传递

> Javascript的点击事件会向上传递。
> 
> 子组件`<a href="#!", onClick=...>{removeIcon}</a>`的点击事件除了会触发自身的`onClick`，还会触发父组件`<div>`的`onClick`，产生两个异步的事件处理函数调用。
> 
> 因此子组件的`onClick`要使用`e.stopPropagation()`来阻止事件向上传播
> 
> ~~~html
>     <div onClick={()=>completeTodo(index)} className="todo-item">
>       <div 
>         className="todo-item-content" 
>         style={{
>           textDecoration: todoItem.isComplete ? 'line-through' : ''
>         }}
>       >
>         {todoItem.isComplete ? completeIcon : unCompleteIcon}
>         {todoItem.text}
>       </div>
>       <a href="#!" onClick={(e)=>removeTodoItem(e, index)} className='todo-item-remove'>{removeIcon}</a>
>     </div>
> ~~~
> 
> ~~~javascript
>   const removeTodoItem = (e, index) => {
>     e.preventDefault();           //屏蔽<a>标签默认的url跳转（否则地址栏url会变）
>     e.stopPropagation();          //阻止事件向上传递，否则会从<a>向上传递到外层的<div>，触发该<div>的completeTodo回到函数
>     // console.log(index);
>     const newTodos = [...todos];  //拷贝列表
>     newTodos.splice(index, 1);    //根据index删除列表元素
>     setTodos(newTodos);           //更新状态重新渲染
>   }
> ~~~

### (6) 插入icon并绑定事件处理

图标文件格式：

> * `png`不可编程而`svg`可编程
> * `png`支持透明度而`jpg`文件更小

用什么格式做icon：

> * 需要矢量用`svg`
> * 需要透明度用`png`或`svg`
> * 需要高彩色图像用`jpg`或`png`

代码如下：

> 用`<a>`标签包裹（需要使用`e.preventDefault()`屏蔽`<a>`标签的点击时默认的url跳转操作），也可以绑定在其他可点击的html标签中
>
> ~~~javascript
> // 使用第三方提供的图标，例如 https://www.iconfont.cn/search/index?searchType=icon&q=remove
> // 1. 点击'copy svg'，粘贴到下面的变量之后
> // 2. 把'class'更改为'className'以符合JSX的要求（HTML to JSX）
> const removeIcon     = <svg t="1603532667787" className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1282" width="200" height="200"><path d="...." p-id="1283" fill="#ffffff"></path></svg>
> const completeIcon   = <svg t="1603598348171" className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="10172" data-spm-anchor-id="a313x.7781069.0.i28" width="200" height="200"><path d="M512 85.333333a426.666667 426.666667 0 0 1 426.666667 426.666667 426.666667 426.666667 0 0 1-426.666667 426.666667A426.666667 426.666667 0 0 1 85.333333 512 426.666667 426.666667 0 0 1 512 85.333333m-42.666667 618.666667l298.666667-298.666667-60.16-60.16L469.333333 583.253333l-131.84-131.413333L277.333333 512l192 192z" fill="#ffffff" p-id="10173" data-spm-anchor-id="a313x.7781069.0.i29" className="selected"></path></svg>
> const unCompleteIcon = <svg t="1603598295484" className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="9979" width="200" height="200"><path d="M512 859.61428833A347.61428833 347.61428833 0 1 1 512 164.38571167a347.61428833 347.61428833 0 0 1 0 695.22857666z m0-77.24761979A270.36666854 270.36666854 0 1 0 512 241.63333146a270.36666854 270.36666854 0 0 0 0 540.73333708z" p-id="9980" fill="#ffffff"></path></svg>
> ...
> ...
> <a href="#!" onClick={(e)=>removeTodoItem(e, index)} className='todo-item-remove'>{removeIcon}</a>
> ~~~


### (7) 列表元素key：

> `React`要求列表元素提供key属性、避免不必要的重新渲染以及渲染元素的唯一性。同时不推荐用index当做key，会导致诸如“表格插入行时重新渲染所有行”的情况。
> 
> ~~~javascript
> <div className="todo-list">
>     <TodoForm addTodoItem={addTodo}/>
>     { 
>         todos.map((elem, index) => (
>          <TodoItem 
>               key={elem.text}  // React使用key来做视图更新，避免不必要的重新渲染
>               index={index} todoItem={elem} removeTodoItem={removeTodoItem} completeTodo={completeTodo}
>           />))
>     }
> </div> 
> ~~~
> 
> `React`为什么需要key: 用来增加渲染效率，提供一个`key`告诉react两个元素是一样。例如下面的标签：
> 
>~~~html
><ul>
>	<li key="2015">Duke</li>
>	<li key="2016">Villanova</li>
></ul>
>~~~
>
> 当插入新的`<li>`之后，react再次重新渲染，只需要渲染新增的`<li key="2014">Connecticut</li>` 
>
>~~~html
><ul>
>	<li key="2014">Connecticut</li>
>	<li key="2015">Duke</li>
>	<li key="2016">Villanova</li>	
></ul>
>~~~
> 
> 不推荐使用index作为key，例如下面代码，list重排时，会导致所有元素全部重新渲染
> 
>~~~
><ul>{list.map((v,idx)=><li key={idx}>{v}</li>)}</ul>
>~~~
>

### (8) Local Storage

> 在浏览器本地存储数据
> 
> ~~~javascript
> > localStorage.clear();
> undefined
> > localStorage.setItem('a', '123');
> undefined
> > localStorage
> Storage {a: "123", length: 1}
> > localStorage.getItem('a');
> "123"
> > localStorage.removeItem('a');
> undefined
> > localStorage
Storage {length: 0}
> ~~~

### (7) 解构赋值：

> 用途：1. 将对象属性赋给一组变量；2. 将数组元素赋给一组变量
> 
> 结构赋值用在函数参数时的简写方式
> 
> ~~~javascript
> function TodoForm({addTodoItem}) {
> 	...
> }
> ~~~
> 
> 等价于
> 
> ~~~javascript
> function TodoForm(props) {
> 	{addTodoItem} = props;  // 将props.addTodoItems赋值给变量addTodoItems
> 	...
> }
> ~~~
> 
> 参考文档：[reference/destructing_assignment.pdf](reference/destructing_assignment.pdf)

### (8) ES6 Spread语法

> 参考：[https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Operators/Spread_syntax](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Operators/Spread_syntax) 
> 
> ~~~javascript
> const newTodos = [{text}, ...todos]; // 在列表头部插入元素
> ~~~

### (9) 用style属性来定义样式

> * 优先级高于css class、用Chrome Dev Tool可以看出style样式是加在`element.style`上的
> * 外层{}用来引入style的属性值、内层{}表示一个object
> 
> ~~~html
> <div className="todo-item-content" style={{textDecoration: todoItem.isComplete ? 'line-through' : ''}}`></div>
> ~~~
> 
> 参考文档：CSS选择器优先级：[reference/css_selector.pdf](reference/css_selector.pdf)

### (10) 根据index删除列表元素

> ~~~javascript
> newTodos.splice(index, 1);    //根据index删除列表元素
> ~~~

## 6. Hooks背后的理念：可复用性 

> `React`改进可复用性的历史：`Mixin` -> `HOC` -> `Render Props` -> `Hooks`

Mixin：
>  
> * 原自于OOP的思想，将比较通用的代码提取出来
> * 缺陷：
> 	* 隐式依赖：代码复杂后，调用一个class方法 ，但是不知道这个class来自于哪些mixin
> 	* 可能产生冲突：引入多个mixin之后，出现多个同样的方法，或者多个不同方法作用于同一个变量
> 	* 引入mixin会倾向于产生更多状态，过多的mixin使得状态越来越复杂
> * 已经被淘汰
> 
> ~~~javascript
> // 公用代码：提供计时器功能的Mixin
> var SetIntervalMixin = {
> 	componentWillMount: function() {
> 		this.intervals = [];
> 	},
> 	setInterval: function() {
> 		this.intervals.push(setInterval.apply(null, arguments)); 
> 	},
> 	componentWillUnmount: function() {
> 		this.intervals.forEach(clearInterval); // 计时器销毁
> 	}
> };
> var createReactClass = require('create-react-class');
> var TickTock = createReactClass({
> 	mixins:  [SetIntervalMixin], // 使用Mixin
> 	getInitialState: function() {
> 		return {seconds: 0};
> 	},
> 	componentDidMount: function() {
> 		// 本来TickTock类没有setInterval这个方法，Mixin使它具有了这个方法
> 		this.setInterval(this.tick, 1000); //  调用Mixin的方法 
> 	},
> 	tick: function() {
> 		this.setState({seconds: this.state.seconds +. 1});
> 	},
> 	render: function() {
> 		return (<p>React has been running. for {this.state.seconds} seconds</p>);
> 	}
> });
> ~~~

HOC: 高阶组件

> * 编写转换函数，把通用性低但是相似的基础组件、组装成通用性高的组件
> * 优点：通过层级结构（通用的部分在转换函数中，不通用的部分在参数中）、降低了复杂度
> * 缺点：
> 	* 扩展性受限：需要改基础组件、让它能够与其他组件结构类似
> 	* Ref传递问题：因为包裹了一层，ref属性的指向会发生问题，没有指向被包裹的组件
> 	* `Wrapper  Hell`：`高阶组件`作为其他`高阶组件`的`基础组件`，包裹套包裹 ，复杂性叠加

> 两个基础组件：`CommonList`、`BlogSpot`
>
> ~~~javascript
> class CommonList extends React.Component {
> 	constructor(props) {
> 		super(props);
> 		this.handleChange = this.handleChange.bind(this);
> 		this.state = {.  comments: DataSource.getComments() };  
> 	}
> 	componentDidMount() {
> 		DataSource.addChangeListener(this.handleChange);
> 	}
> 	componentWillUnmount() {
> 		DataSource.removeChangeListener(this.handleChange);
> 	}
> 	handleChange() {
> 		this.setState({comments: DataSource.getComments()});
> 	}
> 	render() {
> 		return (<div>{this.state.comments.map((c) => {<Comment c={c} key={c.id} />})}</div>);
> 	}
> }
> ~~~
> 
> ~~~javascript
> class BlogPost extends. React.Component {
> 	constructor(props) {
> 		super(props);
> 		this.handleChange = this.handleChange.bind(this);
> 		this.state = { blogPost: DataSource.getBlogPost(props.id) };
> 	}
> 	componentDidMount() {
> 		DataSource.addChangeListener(this.handleChange);
> 	}
> 	componentWillUnmount() {
> 		DataSource.removeChangeListener(this.handleChange);
> 	}
> 	handleChange() {
> 		this.setState({blogPost: DatSource.getBlogPost(this.props.id)});
> 	}
> 	render() {
> 		return (<TextBlock text={this.state.blogPost} />);
> 	}
> }
> ~~~
> 
> 写一个更加通用的函数（业务低耦合），把基础组件转换成高阶组件
> 
> ~~~javascript
> function withSubscription(WrappedComponent, selectDataFunc) {
> 	return class extends React.Component. {
> 		constructor(props) {
> 			super(props);
> 			this.handleChange = this.handleChange.bind(this);
> 			this.state = {data:  selectDataFunc(DataSource, props)};
> 		}
> 		componentDidMount() {
> 			DataSource.addChangeListener(this.handleChange);
> 		}
> 		componentWillUnMount() {
> 			DataSource.removeChangeListener(this.handleChagne);
> 		}
> 		handleChagne() {
> 			this.setState({data: selectDataFunc(DataSource, this.props)});
> 		}
> 		render() {
> 			return <WrappedComponent data={this.state.data} {...this.props} />;
> 		}
>	 }
> };
> ~~~
>
> `withSubscription`函数生成的高阶组件通用性更强，可以用在高复用上下文中
> 
> ~~~javascript
> const CommentListWithSubscription = withSubscription(CommentList, (DataSource). => DataSource.getComments());
> const BlogPostWithSubscription = withSubscription(BlogPost, (DataSource, props) => DataSource.getBlogPost(props.id);
> ~~~

Render Props： 		

> 要定制化的部分`{this.props.render(this.state);}`空出来由外部代码传入
> 
> ~~~javascript
> class Mouse extends React.Component {
> 	constructor(props) {
> 		super(props);
> 		this.handleMouseMove = this.handleMouseMove.bind(this);
> 		this.state = {x:0, y:0};
> 	}
> 	handleMouseMove(event) {
> 		this.setState({x:event.clientX, y:event.clientY});
> 	}
> 	render() {
> 		return (
> 			<div style=({height:'100vh'}} onMouseMove={this.handleMouseMove}>
> 				{this.props.render(this.state) //把渲染函数通过属性传入}
> 			</div>
> 		);
> 	}
> }
> ~~~
> 
> 外部代码传入定制化区域的渲染函数
> 
> ~~~javascript
> class Cat extends React.Component {
> 	render() {
> 		const mouse = this.props.mouse;
> 		return (<img src="/cat.jpg" styles={{position: 'absolute', left:mouse.x, top.mouse.y }} />
> 	};
> }
> 
> class MouseTracker extends React.Component {
> 	render() {
> 		return (
> 			<div>
> 				<h1>move the mouse around</h1>
> 				//把渲染函数通过属性传入
> 				<Mouse render={mouse => (<Cat mouse={mouse}/>)}/>
> 			</div>
> 		);
> 	}
> }
> ~~~

`Mixin`，`HOC`，`Render Props`都需要编写公共组件才能复用，有没有可复用性更好的组件呢？

**Hooks** 

把公共部分抽出来变成一个方法，就可以了。不再需要以组件为粒度来写公共代码。例子如下：
 
> 公用部分写在`自定义hook`（使用官方hook的函数）中：`useFriendStatus`
> 
> ~~~javascript
> import { useState, useEffect } from 'react';
> 
> function useFriendStatus(friendID) {
> 	const [isOnline, setIsOnline] = useState(null);
> 	useEffect(() => {
> 		function handleStatusChange(status) { 
> 			setIsOnline(status.isOnline); 
> 		}
> 		// 组件渲染时注册回调函数给ChatAPI（因为useEffect第二个参数为空）
> 		ChatAPI.subscribeToFriendStatus(friendID, handleStatusChange);
> 		// 组件卸载时解除注册（通过返回一个函数来实现）
> 		return () => {ChatAPI.unsubscribeFromFriendStatus(friendID, handleStatusChange};
> 	}); 
> 	return isOnline;
> }
> ~~~
> 
> 两个组件`FriendStatus`, `FriendListItem`都可以复用上线的钩子
> 
> ~~~javascript
> function Friendstatus(props) {
> 	const isOnline = useFriendStatus(props.friend.id);
> 	if (isOnline === null) {
> 		return 'loading...';
> 	}
> 	return isOnline ? 'Online' : 'Offline';
> }
> 
> function FriendListItem(props) {
> 	const isOnline = useFriendStatus(props.friend.id);
> 	return (
> 		<li style={{color:isOnline ? 'green' : 'black'}}>
> 			{props.friend.name}
> 		</li>
> 	)
> }
> ~~~
> 
> 文档：[https://zh-hans.reactjs.org/docs/hooks-effect.html](https://zh-hans.reactjs.org/docs/hooks-effect.html) 

| 理论基础  | 方案   | 缺陷   |
| -------------	| --------------- | --------------- |
| 借鉴OOP复用模式	| Mixin| 组件复杂度陡升、难以理解 |
| 声明式优于命令式、组合优于继承 | 高阶组件（Higher-Order Components)，Render Props   |  多重抽象导致Warpper Hell   |
| 借鉴函数式思想（纯粹、职责单一、没副作用、幂等）| Hooks | 写法限制、学习成本等        |

## 7. CSS




## 附录：参考文档

> * JavaScript：[http://javascript.info/](http://javascript.info/)
> * 解构赋值：[reference/destructing_assignment.pdf](reference/destructing_assignment.pdf)
> * CSS选择器优先级：[reference/css_selector.pdf](reference/css_selector.pdf)

