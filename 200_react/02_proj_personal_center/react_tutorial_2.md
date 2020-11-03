# React Tutorial 2

## Q&A

#### React为什么需要key

> 用来增加渲染效率，提供一个`key`告诉react两个元素是一样
> 
~~~html
<ul>
	<li key="2015">Duke</li>
	<li key="2016">Villanova</li>
</ul>
~~~
>
>  react再重新渲染的时候，只需要渲染新增的`<li key="2014">Connecticut</li>` 
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
~~~
<ul>{list.map((v,idx)=><li key={idx}>{v}</li>)}</ul>
~~~
>

#### 为什么`setState`是一个异步过程（批量异步化）

> * 内部一致性：props、state全部都是异步的
> * 性能优化：所以要批量异步，而不是每次setState都立刻异步

> [https://github.com/facebook/react/issues/11527#issuecomment-360119710](https://github.com/facebook/react/issues/11527#issuecomment-360119710)

####  什么是Hooks
>
> 先看的`class component`写法  
>
~~~javascript
import. React, { Component } from 'react';
export default class Button. extends Component {
	constructor() {
		super();
		this.state = { buttonText: "Click me"};  //状态
		this.handleClick = this.handleClick.bind(this);
	}
	// 声明周期函数
	componentWillMount() {}
	componentDidMount() {console.log('mounted');}
	componentWillReceiveProps() {}
	componentDidUpdate() {}
	componentWillUnmount() {}
	// 回调函数，更新状态，触发重新渲染
	handleClick() {
		this.setState(() => {return {buttonText: "clicked"};});
	}
	// 渲染函数
	render() {
		const { buttonText } = this.state;
		return <button onClick={this.handleClick}>{buttonText}</button>;
	}
}
~~~
> 
> 以前的`functional component`只能用props，没有state也没有生命周期，复杂组件都只能 用`class component`来写
> 
> ~~~javascript
> function Welcome(props) {
> 	return <h1>Hello, {props.name}</h1>;
> }
> ~~~
> 
>  `functional. compoent`加上`hooks`之后，基本上等同于`class component`。
> 
> * 状态以及组件生命周期，都可以用`hooks`钩进来（例如：`useState()`，`useContext()`，`useReducer()`,  `useEffect()`）
> * 组件尽量写成纯函数，如果需要外部功能和副作用，就用`hooks`
