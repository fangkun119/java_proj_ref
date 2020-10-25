// 从module中引入方法/类、并且不需要使用module前缀：
// 下面的 
//    import { useState } from 'react'; 
//    useState('');
// 等价于 
//    import useState from 'react';
//    react.useState(''); 
import React, {useEffect, useState} from 'react';
import './App.css';

const INIT_TODOS = [
  {text: 'Learn about React', isComplete:false},
  {text: 'Meet friend for lunch', isComplete:false},
  {text: 'Build todo app',  isComplete:false}
]

// 创建TodoItem Component时传入给回调函数使用的addToItem
function TodoForm({addTodoItem}) {
  // 用解构赋值来把数组元素赋给一组变量：
  // const [value, setValue] = useState('');
  //    state named "value", with function "setValue" for updating and an initial value ''
  //    * react.useState is a State Hook, for using state without writing a class
  //    * reference: https://zh-hans.reactjs.org/docs/hooks-state.html 
  // [a, b] = [1, 2]
  //    destracting assigment for assign variables from an array
  //    * reference: https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Operators/Destructuring_assignment
  //    * in this example, "useState" function returns an array
  const [value, setValue] = useState('');

  // 表单提交之后，页面会刷新，阻止页面刷新的三种方法
  // 1. 监听 input 的回车之后，也就不需要再进行表单提交了
  // 2. prevent default 方法可以阻止后续行为的执行，也同样可以阻止跳转
  // 3. 当 onsubmit 事件返回 false 之后，浏览器会认为表单未通过校验，从而实现不跳转
  const handleSubmit = (e) => {
    e.preventDefault();     //屏蔽默认的on submit页面刷新行为
    // console.log(value);  //调试，查看value的值
    if (!value) {           //过滤空值
      return;
    }
    addTodoItem(value);     //用外部组件传入的回调函数，处理submit事件
    setValue('')            //重置状态重新渲染，清空<input>
  }

  return (
    // className="input": 使用了Lib Bluma的CSS，该CSS在index.html被引入
    //    文档: http://bulma.zcopy.site/documentation
    //    CDN: https://cdnjs.com/libraries?q=bluma 
    // <input> onChange: 事件处理过程 
    //    input数据变化 -> event -> onChange(e) -> setValue(e.target.value) -> Component使用新的状态值重新渲染(value = {value})
    <form onSubmit={handleSubmit}>
      <input 
        value = {value}
        className="input"
        onChange={(e) => {
            // consold.log(e); console.log(e.target.value); //调试、查看e的数据
            setValue(e.target.value)
        }}
        />
    </form>
  )
}

// 使用第三方提供的图标，例如 https://www.iconfont.cn/search/index?searchType=icon&q=remove
// 1. 点击'copy svg'，粘贴到下面的变量之后
// 2. 把'class'更改为'className'，使用项目自己的CSS "icon"
const removeIcon     = <svg t="1603532667787" className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1282" width="200" height="200"><path d="M256 810.666667c0 46.933333 38.4 85.333333 85.333333 85.333333h341.333334c46.933333 0 85.333333-38.4 85.333333-85.333333V298.666667H256v512z m105.173333-303.786667l60.373334-60.373333L512 536.96l90.453333-90.453333 60.373334 60.373333L572.373333 597.333333l90.453334 90.453334-60.373334 60.373333L512 657.706667l-90.453333 90.453333-60.373334-60.373333L451.626667 597.333333l-90.453334-90.453333zM661.333333 170.666667l-42.666666-42.666667H405.333333l-42.666666 42.666667h-149.333334v85.333333h597.333334V170.666667z" p-id="1283" fill="#ffffff"></path></svg>
const completeIcon   = <svg t="1603598348171" className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="10172" data-spm-anchor-id="a313x.7781069.0.i28" width="200" height="200"><path d="M512 85.333333a426.666667 426.666667 0 0 1 426.666667 426.666667 426.666667 426.666667 0 0 1-426.666667 426.666667A426.666667 426.666667 0 0 1 85.333333 512 426.666667 426.666667 0 0 1 512 85.333333m-42.666667 618.666667l298.666667-298.666667-60.16-60.16L469.333333 583.253333l-131.84-131.413333L277.333333 512l192 192z" fill="#ffffff" p-id="10173" data-spm-anchor-id="a313x.7781069.0.i29" className="selected"></path></svg>
const unCompleteIcon = <svg t="1603598295484" className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="9979" width="200" height="200"><path d="M512 859.61428833A347.61428833 347.61428833 0 1 1 512 164.38571167a347.61428833 347.61428833 0 0 1 0 695.22857666z m0-77.24761979A270.36666854 270.36666854 0 1 0 512 241.63333146a270.36666854 270.36666854 0 0 0 0 540.73333708z" p-id="9980" fill="#ffffff"></path></svg>

// 用解构赋值来挑选一个object中的attributes，例如：
//    const obj = { name: 'Jenny', age: 18 };
//    const { name } = obj;
//    console.log(name); // Jenny
//    参考: https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Operators/Destructuring_assignment
// 下面方法，传入的object除了拥有属性index、todoItem、removeTotoItem、completeTodo, 其实还有一个属性key
// 使用解构赋值表示只需要指定的属性，不需要key
function TodoItem({index, todoItem, removeTodoItem, completeTodo}) {
  return ( 
    // 1. 变量removeIcon, completeIcon, unCompleteIcon的值是svg标签
    // 2. 把svg标签包裹在<a href="#!">标签中，可以应用CSS样式、绑定事件处理函数
    // 3. {todoItem.isComplete ? completeIcon : unCompleteIcon}: 根据条件选择渲染哪个标签
    // 4. 把{todo.*mpleteIcon}, {todoItem.text}包在一个<div>中 ，应用外层<a>的css的justify-content:space-between时不会向这两个元素之间插入space
    // 5. style={}：在{}内放一段控制样式的JavaScript，这个JavaScript是一个object（其属性textDecoration的值由todoItem.isComplete决定)
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
  )
}

// 返回JSX元素，其中的className="***"来自App.css
function App() {
  // react.useState是React的State Hook，用来在不用写React的情况下初始化React状态
  // 参考：https://reactjs.org/docs/hooks-reference.html#usestate，https://zh-hans.reactjs.org/docs/hooks-state.html
  const [todos, setTodos] = useState(
      JSON.parse(localStorage.getItem('todos')) || INIT_TODOS // 初始值
      );
 
  // react.useEffect(()=>{}, [])是React的Effect Hook
  // * 当参数2内部的元素发生改变时，调用参数1设置的回调函数
  // * 当参数2是空数组时，参数1的回调函数会在整个组件mount的时候执行，例如：
  //   useEffect(()=>{console.log("componentDidMount")}, [])
  // 参考：https://reactjs.org/docs/hooks-reference.html#useeffect，https://zh-hans.reactjs.org/docs/hooks-reference.html#useeffect
  useEffect(()=>{
    // console.log("todos change"); 
    // localStorage可存储少量数据（< 5 M）在浏览器本地，
    // 1. 在Chrome DevTool的console中可以运行和实验相关函数，例如：localStorage.clear(); localStorage.removeItem('todos')
    // 2. localStorage不能存储二进制数据，需要将其转化成JSON
    localStorage.setItem('todos', JSON.stringify(todos));
  }, [todos])

  // 传给<TodoForm>的3个回调函数
  const removeTodoItem = (e, index) => {
    e.preventDefault();           //屏蔽默认的url跳转
    e.stopPropagation();          //阻止事件向上传递，否则会从<a>向上传递到外层的<div>，触发该<div>的completeTodo回到函数
    // console.log(index);
    const newTodos = [...todos];  //拷贝列表
    newTodos.splice(index, 1);    //根据index删除列表元素
    setTodos(newTodos);           //更新状态重新渲染
  }

  const completeTodo = (index) => {
    // console.log("in completeTodo"); 
    const newTodos = [...todos];    //React的状态只能通过框架提供的方法来更改（触发重新渲染等）
    newTodos[index].isComplete = true;
    setTodos(newTodos);
  }

  const addTodo = (text) => {
    // {text}等价于{text:text, isComplete:false}, false是boolean的默认值
    const newTodos = [{text}, ...todos]; //在列表头部插入元素
    setTodos(newTodos);
  }

  // 返回一个JSX元素，内嵌JavaScrip包裹在{}中
  return (
    <div className="app">
      <div className="todo-list">
        <TodoForm addTodoItem={addTodo}/>
        { 
          todos.map((elem, index) => (
            <TodoItem 
              key={elem.text} 
              index={index} 
              todoItem={elem} 
              removeTodoItem={removeTodoItem} 
              completeTodo={completeTodo}
            />
          ))
        }
      </div> 
    </div>
  );
}

export default App;

