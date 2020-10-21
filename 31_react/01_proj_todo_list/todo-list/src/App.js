import React, {useState} from 'react';
import './App.css';

// content of the TO-DO list
const INIT_TODOS = [
  'Learn about React', 
  'Meet friend for lunch', 
  'Build todo app'
]

// parameter: {todo_item}: 
// destructuring-assignment for getting attribute of an object
// for example
//    const obj = { name: 'Jenny', age: 18 };
//    const { name } = obj;
//    console.log(name); // Jenny
// notic that when passing in the parameter object, there are 2 attributes - key and todo_item
// here we use destructuring-assignment to get 'todo_item' attribute only
// reference: 
// https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Operators/Destructuring_assignment
function TodoItem({todo_item}) {
  return (
    // {v}: value of variable v
    // className="todo_css": sytle defined in app.css
    <div className="todo-item">{todo_item}</div>
  )
}

function TodoForm({}) {
  const handleSubmit = (e) => {
    e.preventDefault(); // prevent page-refresh on submit
    console.log(e);
  }
  return (
    <form onSubmit={handleSubmit}>
      <input className="input"/>
    </form>
  )
}

function App() {
  // App(): return a JSX element <div className="app">...</div>
  // <input className="inpu">: use the css lib bluma
  //    doc: bulma.zcopy.site/documentation
  //    cdn: https://cdnjs.com/libraries?q=bluma 
  //    import the css lib: index.html
  // <div className="app">
  // <div className="todo-list">
  //    css来自App.css
  return (
    <div className="app">
      <div className="todo-list">
        <TodoForm/>
        {
          //{}: js code
          INIT_TODOS.map((elem) => (
            <TodoItem key={elem} todo_item={elem} />
          ))
        }
      </div>
    </div>
  );
 
  // 1. 每次刷新页面之后，这些待办事项都会丢失，有没有什么神奇的办法可以让数据持久化呢？
  //    A. localStorage 很适合进行少量数据（< 5 M）的存取
  // 2. 表单提交之后，页面会刷新，如何阻止页面在提交后刷新呢？
  //    A. 监听 input 的回车之后，也就不需要再进行表单提交了
  //    B. prevent default 方法可以阻止后续行为的执行，也同样可以阻止跳转
  //    C. 当 onsubmit 事件返回 false 之后，浏览器会认为表单未通过校验，从而实现不跳转
}

export default App;

