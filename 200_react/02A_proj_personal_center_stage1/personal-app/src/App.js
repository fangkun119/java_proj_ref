import React  from 'react';
import Router from './router';

import { Menu }          from 'antd';    
import { BrowserRouter } from 'react-router-dom';
import { NavLink }       from 'react-router-dom'; 

// 导入组件的4种方法：
// import { Menu } from 'antd'            结合解构方便捷导入，最常用最简单，但是需要搭配摇树优化插件才能实现按需加载
// import Menu from 'antd/lib/menu'       按需加载导入，只引入所需要的模块，减小打包体积
// import { Menu } from 'antd/dist/antd'  直接将整个包导入（导入了将打包好资源文件），所以也无法进行加载优化
// const { Menu } = require('antd')       是nodejs的require导入方法，在 ES6 普及的当下，并不推荐在项目中使用这种写法（各类配置文件除外）

// 引入Ant Design的样式文件: (1) 在Chrome Dev Tool可以看到对应的.css样式不存在; (2) import路径可以在node_modules目录下查到
// import 'antd/dist/antd.css';            //方法1：引入所有的css
// import 'antd/dist/antd.less';           //方法2：引入所有的less，需要npm install less -D以及npm install less-loader -D
// import './App.less';                    //方法3：普通方式按需引入less，该文件中指定了用哪个样式class，还可以追加自定义的样式，需要在config-voerrides.js中配置了fixBabelImports按需引入
import styles from './App.module.less';    //方法4：方法3基础上、使用CSS Module引入样式，避免class名称冲突

function App() { 
  // 1. 相比<a href=...>，'react-router-dom'的<NavLink>还可以在to属性中指定object
  // 2. <Menu>, <Menu.Item>使用方法参考https://ant.design/components/menu-cn/，注意选择与代码相同的lib版本
  // 3. <Menu className="menu" ...>用普通的方式引入Less样式
  //    <Menu className={styles.menu} ...>用CSS Module来引入Less样式
  //    编译后的class会增加一个hash字符串（如：menu--NRYIR）来唯一标识，这样就不会和其他的.menu冲突
  
  return (
    <BrowserRouter> 
      <Menu className={styles.menu} theme="dark">
        <Menu.Item><NavLink to="/">Home</NavLink></Menu.Item>
        <Menu.Item><NavLink to="/login">Login</NavLink></Menu.Item>
        <Menu.Item><NavLink to="/register">Register</NavLink></Menu.Item>
      </Menu>
      <Router></Router>
    </BrowserRouter>
  );
  /*
  return (
    <BrowserRouter> 
      <Router></Router>
    </BrowserRouter>
  );
  */
}

export default App;
