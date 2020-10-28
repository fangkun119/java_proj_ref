# 01 `TO-DO List`项目

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

## 2. 环境搭建

> 环境搭建，项目目录结构，package.json，[`01_env_setup.md`](01_env_setup.md)

## 3. create-react-app

官方提供的脚手架、创建单页应用程序、零配置构建。使用下面4种方法之一（取决于使用哪种包管理工具）：

~~~bash
create-react-app my-app 		# 全局安装create-react-app的情况
npx create-react-app my-app  	# npx来自npm 5.2+或更高版本
npm init react-app my-app  		# npm init <initializer> 在 npm 6+可用
yarn create react-app my-app 	# yarn create在Yarn 0.25+中可用
~~~

项目创建之后（以使用npm包管理工具为例，yarn差别也不大），有四条命令可以用

> `npm start`：启动项目、可以在浏览器中查看
> `npm run build`:  构建，把JSX语法、高阶ES语法等转义成浏览器能用的格式，压缩JS等
> `npm test`:  运行单元测试等
> `npm run eject`: 将零配置项目、改成暴露配置的形式（该操作不可逆），不满意React提供的默认配置时，可以用这个命令

##4. 项目代码

###(1) 组件(`Component`)

> * 小组件组装成大组件、大组件组装成更大的组件、……，提高代码复用
> * Trade Off：组件化缺乏 v.s 过度组件化

###(2) 代码及注释链接

首页：[todo-list/public/index.html](todo-list/public/index.html)

> 载入CSS Lib，使用定义在`to-dolist/src/App.js`中的`App Component`作为该应用的主Component

~~~html
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bulma/0.9.1/css/bulma.min.css" />
~~~

`App Component`：[todo-list/src/App.js](todo-list/src/App.js)及[todo-list/src/App.css](todo-list/src/App.css)

> 主Component：实现方法及相关React使用要点，见代码注释

###(3) 语言点摘录

> * `React`的状态(`state`)
> * `useState钩子`
> 
~~~javascript
const [value, setValue] = useState('');
~~~
> 
> *  `<Form></Form>`表单提交时的默认操作及屏蔽方法
>
~~~javascript
  const handleSubmit = (e) => {
    e.preventDefault();     //屏蔽默认的on submit页面刷新行为
    ...c
  }
~~~
>
> *  `setState`：异步提交、以及event loop批量统一渲染
> 
~~~javascript
onChange={(e) => {
     console.log('before: ', value); 
     setValue(e.target.value);       // 异步操作，只会把value变化提交给event loop
     console.log('after: ', value);   // 输出的仍然是旧的value
}}
~~~
> 
> * `funtional component` 函数式编程,  `class component`在16.6 Hook出现之前处于主导地位
> * 列表元素key：`React`要求列表元素提供key属性、避免不必要的重新渲染以及渲染元素的唯一性。同时不推荐用index当做key，会导致诸如“表格插入行时重新渲染所有行”的情况。
> 
~~~javascript
<div className="todo-list">
    <TodoForm addTodoItem={addTodo}/>
    { 
        todos.map((elem, index) => (
         <TodoItem 
              key={elem.text}  // React使用key来做视图更新，避免不必要的重新渲染
              index={index} todoItem={elem} removeTodoItem={removeTodoItem} completeTodo={completeTodo}
          />))
    }
</div> 
~~~
> 
> * 插入icon并绑定事件处理：（1）几种格式有什么特点：`png`不可编程而`svg`可编程；`png`支持透明度而`jpg`文件更小；（2）用什么做icon：需要矢量用`svg`；需要透明度用`png`或`svg`；需要高彩色图像用`jpg`或`png`；（3）除了`<a>`也可以绑定在其他可点击的html标签中
> 
~~~
// 使用第三方提供的图标，例如 https://www.iconfont.cn/search/index?searchType=icon&q=remove
// 1. 点击'copy svg'，粘贴到下面的变量之后
// 2. 把'class'更改为'className'以符合JSX的要求（HTML to JSX）
const removeIcon     = <svg t="1603532667787" className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1282" width="200" height="200"><path d="...." p-id="1283" fill="#ffffff"></path></svg>
const completeIcon   = <svg t="1603598348171" className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="10172" data-spm-anchor-id="a313x.7781069.0.i28" width="200" height="200"><path d="M512 85.333333a426.666667 426.666667 0 0 1 426.666667 426.666667 426.666667 426.666667 0 0 1-426.666667 426.666667A426.666667 426.666667 0 0 1 85.333333 512 426.666667 426.666667 0 0 1 512 85.333333m-42.666667 618.666667l298.666667-298.666667-60.16-60.16L469.333333 583.253333l-131.84-131.413333L277.333333 512l192 192z" fill="#ffffff" p-id="10173" data-spm-anchor-id="a313x.7781069.0.i29" className="selected"></path></svg>
const unCompleteIcon = <svg t="1603598295484" className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="9979" width="200" height="200"><path d="M512 859.61428833A347.61428833 347.61428833 0 1 1 512 164.38571167a347.61428833 347.61428833 0 0 1 0 695.22857666z m0-77.24761979A270.36666854 270.36666854 0 1 0 512 241.63333146a270.36666854 270.36666854 0 0 0 0 540.73333708z" p-id="9980" fill="#ffffff"></path></svg>
...
...
<a href="#!" onClick={(e)=>removeTodoItem(e, index)} className='todo-item-remove'>{removeIcon}</a>
~~~
> 

JavaScript

> * 解构赋值：(1) 将对象属性赋给一组变量；(2) 将数组元素赋给一组变量
> * 结构赋值用在函数参数时的简写方式
> 
~~~javascript
function TodoForm({addTodoItem}) {
	...
}
~~~
> 
> 等价于
> 
~~~javascript
function TodoForm(props) {
	{addTodoItem} = props;  // 将props.addTodoItems赋值给变量addTodoItems
	...
}
~~~
> 
> * ES6 Spread语法： [https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Operators/Spread_syntax](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Operators/Spread_syntax) 
> 
~~~javascript
const newTodos = [{text}, ...todos]; // 在列表头部插入元素
~~~
> 
> * 根据index删除列表元素：
> 
~~~javascript
newTodos.splice(index, 1);    //根据index删除列表元素
~~~
> 
> * 用style属性来定义样式：(1)优先级高于css class、用Chrome Dev Tool可以看出style样式是加在`element.style`上的；(2)外层{}用来引入style的属性值、内层{}表示一个object
> 
~~~html
<div className="todo-item-content" style={{textDecoration: todoItem.isComplete ? 'line-through' : ''}}`></div>
~~~
> 
> * **点击事件向上传递**：Javascript的点击事件会向上传递。子组件`<a href="#!", onClick=...>{removeIcon}</a>`的点击事件除了会触发资深的`onClick`，还会触发父组件`<div>`的`onClick`，昌盛两个异步的事件处理。因此子组件的`onClick`要使用`e.stopPropagation()`来阻止事件向上传播。
> 
~~~html
    <div onClick={()=>completeTodo(index)} className="todo-item">
      <div 
        className="todo-item-content" 
        style={{
          textDecoration: todoItem.isComplete ? 'line-through' : ''
        }}
      >
        {todoItem.isComplete ? completeIcon : unCompleteIcon}
        {todoItem.text}
      </div>
      <a href="#!" onClick={(e)=>removeTodoItem(e, index)} className='todo-item-remove'>{removeIcon}</a>
    </div>
~~~
> 
~~~javascript
  const removeTodoItem = (e, index) => {
    e.preventDefault();           //屏蔽<a>标签默认的url跳转（否则地址栏url会变）
    e.stopPropagation();          //阻止事件向上传递，否则会从<a>向上传递到外层的<div>，触发该<div>的completeTodo回到函数
    // console.log(index);
    const newTodos = [...todos];  //拷贝列表
    newTodos.splice(index, 1);    //根据index删除列表元素
    setTodos(newTodos);           //更新状态重新渲染
  }
~~~

###(4) 资料 
 
> * JavaScript：[http://javascript.info/](http://javascript.info/)
> * 解构赋值：[reference/destructing_assignment.pdf](reference/destructing_assignment.pdf)
> * CSS选择器优先级：[reference/css_selector.pdf](reference/css_selector.pdf)


