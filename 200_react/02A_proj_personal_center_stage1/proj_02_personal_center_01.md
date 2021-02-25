<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [02 `个人中心`项目](#02-%E4%B8%AA%E4%BA%BA%E4%B8%AD%E5%BF%83%E9%A1%B9%E7%9B%AE)
  - [1. 介绍](#1-%E4%BB%8B%E7%BB%8D)
  - [2. 创建项目](#2-%E5%88%9B%E5%BB%BA%E9%A1%B9%E7%9B%AE)
  - [3. 使用`React Router`](#3-%E4%BD%BF%E7%94%A8react-router)
    - [(1) 导入`React Router`](#1-%E5%AF%BC%E5%85%A5react-router)
    - [(2) 使用`React Router`](#2-%E4%BD%BF%E7%94%A8react-router)
    - [(3) 定义`React Router`](#3-%E5%AE%9A%E4%B9%89react-router)
    - [(4) 创建Login/Home/Register页面的渲染组件](#4-%E5%88%9B%E5%BB%BAloginhomeregister%E9%A1%B5%E9%9D%A2%E7%9A%84%E6%B8%B2%E6%9F%93%E7%BB%84%E4%BB%B6)
    - [(5) 测试页面路由](#5-%E6%B5%8B%E8%AF%95%E9%A1%B5%E9%9D%A2%E8%B7%AF%E7%94%B1)
    - [(6) 为`src/App.js`添加公用的导航界面（下面代码的`<ul>...</ul>`部分）](#6-%E4%B8%BAsrcappjs%E6%B7%BB%E5%8A%A0%E5%85%AC%E7%94%A8%E7%9A%84%E5%AF%BC%E8%88%AA%E7%95%8C%E9%9D%A2%E4%B8%8B%E9%9D%A2%E4%BB%A3%E7%A0%81%E7%9A%84ulul%E9%83%A8%E5%88%86)
    - [(7) 总结: ` react-router-dom`中的一些组件](#7-%E6%80%BB%E7%BB%93--react-router-dom%E4%B8%AD%E7%9A%84%E4%B8%80%E4%BA%9B%E7%BB%84%E4%BB%B6)
  - [4. 使用`Ant Design UI`样式框架](#4-%E4%BD%BF%E7%94%A8ant-design-ui%E6%A0%B7%E5%BC%8F%E6%A1%86%E6%9E%B6)
    - [(1) 导入`Ant Design UI`](#1-%E5%AF%BC%E5%85%A5ant-design-ui)
    - [(2) 访问`Ant Design`文档，获取所需`UI组件`的样式代码和API](#2-%E8%AE%BF%E9%97%AEant-design%E6%96%87%E6%A1%A3%E8%8E%B7%E5%8F%96%E6%89%80%E9%9C%80ui%E7%BB%84%E4%BB%B6%E7%9A%84%E6%A0%B7%E5%BC%8F%E4%BB%A3%E7%A0%81%E5%92%8Capi)
    - [(3) 用`Ant Design`修改导航界面的样式](#3-%E7%94%A8ant-design%E4%BF%AE%E6%94%B9%E5%AF%BC%E8%88%AA%E7%95%8C%E9%9D%A2%E7%9A%84%E6%A0%B7%E5%BC%8F)
  - [5. 引入`CSS`预处理器`LESS`简化开发](#5-%E5%BC%95%E5%85%A5css%E9%A2%84%E5%A4%84%E7%90%86%E5%99%A8less%E7%AE%80%E5%8C%96%E5%BC%80%E5%8F%91)
    - [(1) 用途](#1-%E7%94%A8%E9%80%94)
    - [(2) 判断样式框架是否支持LESS](#2-%E5%88%A4%E6%96%AD%E6%A0%B7%E5%BC%8F%E6%A1%86%E6%9E%B6%E6%98%AF%E5%90%A6%E6%94%AF%E6%8C%81less)
    - [(3) 在`create-react-app`中加入`LESS`的两种方法](#3-%E5%9C%A8create-react-app%E4%B8%AD%E5%8A%A0%E5%85%A5less%E7%9A%84%E4%B8%A4%E7%A7%8D%E6%96%B9%E6%B3%95)
    - [(4) 将页面样式从`原始CSS`切换到`LESS`](#4-%E5%B0%86%E9%A1%B5%E9%9D%A2%E6%A0%B7%E5%BC%8F%E4%BB%8E%E5%8E%9F%E5%A7%8Bcss%E5%88%87%E6%8D%A2%E5%88%B0less)
      - [(a) 安装`react-app-rewired`和`customize-cra`](#a-%E5%AE%89%E8%A3%85react-app-rewired%E5%92%8Ccustomize-cra)
      - [(b) 修改`package.json`将`react-scripts`替换成`react-app-rewired`，](#b-%E4%BF%AE%E6%94%B9packagejson%E5%B0%86react-scripts%E6%9B%BF%E6%8D%A2%E6%88%90react-app-rewired)
      - [(c) 添加`config-overrides.js`文件](#c-%E6%B7%BB%E5%8A%A0config-overridesjs%E6%96%87%E4%BB%B6)
      - [(d) 安装less-loader](#d-%E5%AE%89%E8%A3%85less-loader)
      - [(e) 修改`App.js`由使用原始CSS改为使用LESS](#e-%E4%BF%AE%E6%94%B9appjs%E7%94%B1%E4%BD%BF%E7%94%A8%E5%8E%9F%E5%A7%8Bcss%E6%94%B9%E4%B8%BA%E4%BD%BF%E7%94%A8less)
    - [(5) 按需引入样式](#5-%E6%8C%89%E9%9C%80%E5%BC%95%E5%85%A5%E6%A0%B7%E5%BC%8F)
      - [(a) 问题](#a-%E9%97%AE%E9%A2%98)
      - [(b) 安装`babel-plugin-import`插件](#b-%E5%AE%89%E8%A3%85babel-plugin-import%E6%8F%92%E4%BB%B6)
      - [(c) 在personal-app/config-overrides.js中配置fixBableImports插件](#c-%E5%9C%A8personal-appconfig-overridesjs%E4%B8%AD%E9%85%8D%E7%BD%AEfixbableimports%E6%8F%92%E4%BB%B6)
      - [(d) 代码切换为按需引入样式](#d-%E4%BB%A3%E7%A0%81%E5%88%87%E6%8D%A2%E4%B8%BA%E6%8C%89%E9%9C%80%E5%BC%95%E5%85%A5%E6%A0%B7%E5%BC%8F)
    - [(6) 测试：使用LESS来编写组件样式](#6-%E6%B5%8B%E8%AF%95%E4%BD%BF%E7%94%A8less%E6%9D%A5%E7%BC%96%E5%86%99%E7%BB%84%E4%BB%B6%E6%A0%B7%E5%BC%8F)
  - [6. 使用`CSS Module`](#6-%E4%BD%BF%E7%94%A8css-module)
  - [7. 账号密码登录页](#7-%E8%B4%A6%E5%8F%B7%E5%AF%86%E7%A0%81%E7%99%BB%E5%BD%95%E9%A1%B5)
    - [(1) 编写页面代码](#1-%E7%BC%96%E5%86%99%E9%A1%B5%E9%9D%A2%E4%BB%A3%E7%A0%81)
    - [(2) 表单和提交按钮](#2-%E8%A1%A8%E5%8D%95%E5%92%8C%E6%8F%90%E4%BA%A4%E6%8C%89%E9%92%AE)
      - [(a) 创建通用的`InputItem`,`SubmitButtom`组件](#a-%E5%88%9B%E5%BB%BA%E9%80%9A%E7%94%A8%E7%9A%84inputitemsubmitbuttom%E7%BB%84%E4%BB%B6)
      - [(b) 为`/App.js`增加`InputItem`和`SubmitButton`控件](#b-%E4%B8%BAappjs%E5%A2%9E%E5%8A%A0inputitem%E5%92%8Csubmitbutton%E6%8E%A7%E4%BB%B6)
    - [(3) 小结：导入和使用CSS Module的方法](#3-%E5%B0%8F%E7%BB%93%E5%AF%BC%E5%85%A5%E5%92%8C%E4%BD%BF%E7%94%A8css-module%E7%9A%84%E6%96%B9%E6%B3%95)
      - [(a) 以module前缀的方式使用样式](#a-%E4%BB%A5module%E5%89%8D%E7%BC%80%E7%9A%84%E6%96%B9%E5%BC%8F%E4%BD%BF%E7%94%A8%E6%A0%B7%E5%BC%8F)
      - [(b) 解构赋值方式](#b-%E8%A7%A3%E6%9E%84%E8%B5%8B%E5%80%BC%E6%96%B9%E5%BC%8F)
  - [8. 手机验证码登录页](#8-%E6%89%8B%E6%9C%BA%E9%AA%8C%E8%AF%81%E7%A0%81%E7%99%BB%E5%BD%95%E9%A1%B5)
  - [9. 注册页面](#9-%E6%B3%A8%E5%86%8C%E9%A1%B5%E9%9D%A2)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# 02 `个人中心`项目

## 1. 介绍

> Web应用中用户页面展示：包含登录页、注册页、用户主页
>
> 使用技术包括：`React Router`,  `Ant Design UI`,  `Redux`

## 2. 创建项目

> 创建并启动项目
>
> ~~~bash
> $ create-react-app personal-app
> $ cd personal-app
> $ npm start
> ~~~
>
> 将原有的代码删除到最简、`src`目录中只保留`App.js`和`index.js`
>
> `App.js`：
>
> ~~~jsx
> function App() {
>   return (
>     <div className="App">
>       hello world
>     </div>
>   );
> }
> export default App;
> ~~~
>
> `index.js`
>
> ~~~jsx
> import ReactDOM from 'react-dom';
> import App from './App';
> ReactDOM.render(
>   <App /> ,
>   document.getElementById('root')
> );
> ~~~
>
> 备注：如果是从git获取的代码，需要手动运行`npm install react-app-rewired customize-cra -D`

## 3. 使用`React Router`

### (1) 导入`React Router`

> `npm install react-router-dom -S`：因为`react-router-dom`是项目中要用到的，所以使用`-S`参数将`react-router-dom`保存到`package.json`中
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app/
> $ npm install react-router-dom -S
> ...
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app/
> $ cat package.json | grep react-router-dom -C1
>     "react-dom": "^17.0.1",
>     "react-router-dom": "^5.2.0",
>     "react-scripts": "4.0.0",
> ~~~

### (2) 使用`React Router` 

> `src/App.js`
>
> ~~~jsx
> import React from 'react';
> import { BrowserRouter } from 'react-router-dom';
> import Router from './router'; //不能加{}
> function App() {
>   return (
>     <BrowserRouter>
>       <Router></Router>
>     </BrowserRouter>
>   );
> }
> export default App;
> ~~~

### (3) 定义`React Router`

> `src/router/index.js`
>
> ~~~jsx
> import React from 'react';
> import { Route, Switch } from 'react-router-dom';
> import Home from '../pages/Home';
> import Login from '../pages/Login';
> import Register from '../pages/Register';
> const Router = () => (
>     // 定义路由，为不同的path指定不同的组件
>     // 匹配顺序是从上到下匹配，按照第一个能匹配的路由项来路由（如果去掉exact，部分匹配也会被路由）
>     <Switch>
>         <Route exact path="/" component={Home} />
>         <Route exact path="/login" component={Login} />
>         <Route exact path="/register" component={Register} />
>     </Switch>
> );
> export default Router;
> ~~~
>
> 接下来创建Home，Login，Register这三个页面渲染组件

### (4) 创建Login/Home/Register页面的渲染组件

> 先不写详细代码，定义三个最简版本。 `src/pages/Home/index.js`代码如下，`src/pages/Login/index.js`，`src/pages/Register/index.js` 也类似
>
> ~~~jsx
> import React from 'react';
> const Home = () => {
>     return (<div>Home</div>);
> };
> export default Home;
> ~~~
>
> 每个页面在`src/pages/`目录下有一个单独的文件夹、主要是出于代码规范的考虑。当然也可以把三个组件都写在一个文件中。

### (5) 测试页面路由

分别访问在`src/router/index.js`被指定了component的三个路径，检查是否可以被正确路由

> * http://localhost:3000/
> * http://localhost:3000/register
> * http://localhost:3000/login

### (6) 为`src/App.js`添加公用的导航界面（下面代码的`<ul>...</ul>`部分）

> 这个导航界面被`src/router/index.js`中所有的页面共用
>
> ~~~jsx
> import React from 'react';
> import { BrowserRouter } from 'react-router-dom';
> import Router from './router';
> function App() {
>   return (
>     <BrowserRouter>
>       <ul>
>         <li><a href="/">Home</a></li>
>         <li><a href="/login">Login</a></li>
>         <li><a href="/register">Register</a></li>
>       </ul>
>       <Router></Router>
>     </BrowserRouter>
>   );
> }
> export default App;
> ~~~
>
> 再次访问下面三个页面，都增加了一个导航列表
>
> * http://localhost:3000/
> * http://localhost:3000/register
> * http://localhost:3000/login

### (7) 总结: ` react-router-dom`中的一些组件

> * `<Link>`：上面例子没有用到，是一个类似于`<a>`标签的组件，可以用于 React 单页应用内的导航
> * `<BrowserRouter>`：用于包裹React路由链接的组件，放置于最外层
> * `<Switch>`：类似编程语言中的switch，用来为url路径匹配一个`<Route>`进行渲染
> * `<Route>`：每条路由的路径及组件声明

## 4. 使用`Ant Design UI`样式框架

### (1) 导入`Ant Design UI`

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app/
> $ npm install antd -S
> ...
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app/
> $ cat package.json | grep antd -i --color -C1
>     "@testing-library/user-event": "^12.1.10",
>     "antd": "^4.7.3",
>     "react": "^17.0.1",
> ~~~

### (2) 访问`Ant Design`文档，获取所需`UI组件`的样式代码和API

> [https://ant.design/components/overview-cn/](https://ant.design/components/overview-cn/) 
>
> 读文档时选择的Ant Design版本应当与代码一致

### (3) 用`Ant Design`修改导航界面的样式

> * 把导航栏的`<a href=...>`替换成`react-router-dom`中的`NavLink`，好处是相比`<a href=...>`、`<NavLink to=...>`除了字符串、还可以传入Object
> * 搜索并参考`Ant Design`的文档（[https://ant.design/components/menu-cn/](https://ant.design/components/menu-cn/)）将导航界面改造成导航UI
>
> `src/App.js` 
>
> ~~~jsx
> import React from 'react';
> import { BrowserRouter, NavLink } from 'react-router-dom';
> import Router from './router';
> // 引入Ant Design的Menu组件 
> import { Menu } from 'antd';  
> // 引入Ant Design的样式文件，否则在Chrome Dev Tool可以看到对应的.css样式不存在，import路径可以在node_modules目录下查到
> import 'antd/dist/antd.css';  
> function App() { 
>   // 1. 相比<a href=...>，'react-router-dom'的<NavLink>还可以在to属性中指定object
>   // 2. <Menu>, <Menu.Item>使用方法参考https://ant.design/components/menu-cn/，注意选择与代码相同的lib版本
>   return (
>     <BrowserRouter> 
>       <Menu theme="dark">
>         <Menu.Item><NavLink to="/">Home</NavLink></Menu.Item>
>         <Menu.Item><NavLink to="/login">Login</NavLink></Menu.Item>
>         <Menu.Item><NavLink to="/register">Register</NavLink></Menu.Item>
>       </Menu>
>       <Router></Router>
>     </BrowserRouter>
>   );
> }
> export default App;
> ~~~
### (4) 导入样式组件的`4`种方法

> * `import { Menu } from 'antd'`:  结合解构便捷导入，最常用最简单，但是需要搭配优化插件才能实现按需加载
> * `import Menu from 'antd/lib/menu'`：按需加载导入，只引入所需要的模块，减小打包体积
> * `import { Menu } from 'antd/dist/antd'`：直接将整个包导入（导入了将打包好资源文件），所以也无法进行加载优化
> * `const { Menu } = require('antd')` ：nodejs的require导入方法，在 ES6 普及的当下，并不推荐在项目中使用这种写法（各类配置文件除外）

## 5. 引入`CSS`预处理器`LESS`简化开发

### (1) 用途

> `LESS`的开发效率比`CSS`高

### (2) 判断样式框架是否支持LESS

> * 在查看`node_modules/antd/dist/antd.css`文件时，发现同目录下还有一个`node_modules/antd/dist/antd.less`文件，说明`antd`也支持less
> * 可否引入这个less文件，来提升开发效率？
>
> ~~~jsx
> // 引入Ant Design的Menu组件 
> import { Menu } from 'antd';  
> // 引入Ant Design的样式文件 
> import 'antd/dist/antd.less';  // import 'antd/dist/antd.css';  
> ~~~
>
> 直接修改`import`还不够，需要下面一些列步骤

### (3) 在`create-react-app`中加入`LESS`的两种方法

> * 方法1：使用`npm run eject`来弹出`web pack配置`并在样式loader的配置位置加入less loader。缺点是web pack文件配置繁琐、暴露`web pack`配置是不可逆的操作
> * 方法2：使用`react-app-rewired`和`customize-cra`来载入`LESS`
>
> 下面的步骤使用方法2

### (4) 将页面样式从`原始CSS`切换到`LESS`

#### (a) 安装[`react-app-rewired`](https://github.com/timarney/react-app-rewired)和[`customize-cra`](https://github.com/arackaf/customize-cra) 

>`-D`表示安装到`Dev Dependencies`中，仅在开发阶段使用，不会部署到生成环境
>
>~~~bash
>__________________________________________________________________
>$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app/
>$ npm install react-app-rewired customize-cra -D
>...
>+ react-app-rewired@2.1.6
>+ customize-cra@1.0.0
>added 4 packages from 4 contributors and audited 2052 packages in 19.461s
>117 packages are looking for funding
>  run `npm fund` for details
>found 0 vulnerabilities
>__________________________________________________________________
>$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app/
>$ cat package.json | grep -E 'react-app-rewired|customize-cra' --color -C1
>  "devDependencies": {
>    "customize-cra": "^1.0.0",
>    "react-app-rewired": "^2.1.6"
>  }
>~~~

#### (b) 修改`package.json`将`react-scripts`替换成`react-app-rewired`，

> 使得`start`、`build`、`test`、`eject`命令都改用`react-app-rewired`执行
>
> ~~~json
> "scripts": {
>     "// Comment 1" : "npm install react-app-rewired customize-cra -D",
>     "// Comment 2" : "change react-scripts to react-app-rewired so that we can use LESS lib",
>     "start": "react-app-rewired start",
>     "build": "react-app-rewired build",
>     "test": "react-app-rewired test",
>     "eject": "react-app-rewired eject"
> },
> ~~~

#### (c) 添加`config-overrides.js`文件

切换到`react-app-rewired`后，`npm start`报错找不到`config-overrides.js`文件

> ~~~bash
> $ npm start
> > personal-app@0.1.0 start /Users/fangkun/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app
> > react-app-rewired start
> internal/modules/cjs/loader.js:834
>   throw err;
>   ^
> Error: Cannot find module '/Users/fangkun/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app/config-overrides'
> Require stack:
> - /Users/fangkun/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app/node_modules/react-app-rewired/config-overrides.js
> - /Users/fangkun/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app/node_modules/react-app-rewired/scripts/start.js
> ~~~

创建[`personal-app/config-overrides.js`](personal-app/config-overrides.js)文件并添加与LESS相关的配置

> ~~~jsx
> // nodejs的require导入方法
> // 在ES6普及的当下并不推荐在项目中使用这种写法（各类配置文件除外）
> const { override, addLessLoader } = require('customize-cra'); 
> module.exports = override (
>     addLessLoader({
> 		// LESS Loader配置   
>     }),
>     ... // 其他配置
> )
> ~~~

#### (d) 安装less-loader

再次`npm start`报错找不到`less-loader`，安装`less-loader`到`Dev Dependencies`，`npm start`成功

> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app/
> $ npm start
> personal-app@0.1.0 start /Users/fangkun/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app
> react-app-rewired start
> Cannot find module 'less-loader'
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app/
> npm install less-loader -D
> ...
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app/
> npm start
> ...
> ~~~

#### (e) 修改`App.js`由使用原始CSS改为使用LESS

> 页面提示找不到“Less”，安装“Less”后，页面提示“JavaScript没有启用”，可在`config-overrides.js`中添加“启用incline javascript”配置，页面正常展示

[`personal-app/src/App.js`](personal-app/src/App.js)修改

> ~~~jsx
> // import 'antd/dist/antd.css';  
> import 'antd/dist/antd.less';  
> ~~~

安装LESS到"Dev Dependencies"（之前装的是Less Loader，没有装LESS）

> ~~~bash
> npm install less -D
> ~~~

启动APP、页面提示没有“enable inline JavaScript”

> ~~~txt
> // https://github.com/ant-design/ant-motion/issues/44
> .bezierEasingMixin();
> ^
> Inline JavaScript is not enabled. Is it set in your options?
> ~~~

修改[personal-app/config-overrides.js](personal-app/config-overrides.js)如下

> ~~~jsx
> const { override, addLessLoader } = require('customize-cra'); 
> // 需要npm install less-loader -D来安装less-loader
> module.exports = override (
>     addLessLoader({
>         lessOptions : {
>             javascriptEnabled: true,
>         }
>     })
> )
> ~~~


页面可以正常访问

### (5) 按需引入样式

#### (a) 问题

> 之前使用`import 'antd/dist/antd.css'`或`import 'antd/dist/antd.less'`会引入所有`antd`的组件和样式，希望只引用用到的组件和样式。步骤如下：

#### (b) 安装`babel-plugin-import`插件

> 同样只安装到`Dev Denpencies`中，生产环境不需要
>
> ~~~bash
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app/
> $ npm install babel-plugin-import -D
> ...
> __________________________________________________________________
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/200_react/02_proj_personal_center/personal-app/
> $ cat package.json | grep babel-plugin-import -B1 -A5
>   "devDependencies": {
>     "babel-plugin-import": "^1.13.1",
>     "customize-cra": "^1.0.0",
>     "less": "^3.12.2",
>     "less-loader": "^7.0.2",
>     "react-app-rewired": "^2.1.6"
>   }
> ~~~

#### (c) 在[personal-app/config-overrides.js](personal-app/config-overrides.js)中配置fixBableImports插件

> ~~~js
> const { override, addLessLoader, fixBabelImports } = require('customize-cra'); 
> module.exports = override (
>     // 在node_modules/antd/dist/es/目录下可以找到各种antd组件的css
>     fixBabelImports('antd', {
>         libraryDirectory: 'es',
>         style: 'css'
>     }),
>     // 其他配置
>     ...
> )
> ~~~

#### (d) 代码切换为按需引入样式

注释掉[personal-app/src/App.js](personal-app/src/App.js)中引入全局样式的代码，关闭项目重新`npm start`，页面仍然能正常渲染样式

> ~~~jsx
> // import 'antd/dist/antd.less';  // 使用config-voerrides.js中的fixBabelImports配置来按需引入样式
> ~~~

### (6) 测试：使用LESS来编写组件样式

`src/App.less`

> ~~~less
> .menu {
>     width: 200px;
> }
> ~~~

`src/App.js`

> ~~~jsx
> import './App.less'; // 引入自己编写的LESS样式文件
> ...
> function App() { 
>   return (
>     <BrowserRouter> 
>       <Menu className="menu" theme="dark">
> 	...	
> ~~~


页面正常打开，但当前的问题是 ，如果其他被引入的样式文件也有`.menu`，两个`.menu`样式会作用在同 一个UI组件上，解决办法是使用`CSS Module`

## 6. 使用`CSS Module`

作用：使用`CSS Module`语法，让被引入的CSS只作用在引入的部分，而不会扩大做范围

步骤如下：

(1) 将`App.less`重命名为[`App.module.less`](personal-app/src/App.module.less)，内容不需要改变

> ~~~less
> .menu {
>     width: 200px;
> }
> ~~~


(2) 在`App.js`中修改`import`和`apply`样式的代码

> ~~~jsx
> // import './App.less';  
> import styles from './App.module.less';   //使用CSS Module引入样式
> ...
> function App() { 
>   return (
>     <BrowserRouter> 
>       <Menu className={styles.menu} theme="dark"> //用CSS Module的方式使用样式.menu
> ...
> ~~~


(3) 测试：在Chrome Dev Tool中可以看到，样式`menu`变成了`menu--NRYIR`，说明CSS Module生效

> ~~~html
> <ul class="ant-menu ant-menu-dark menu--NRYIR ant-menu-root ant-menu-vertical" role="menu">
> 	...
> </ul>
> ~~~
>
> 发现界面没有撑满整个页面，用`Chrome Dev Tool`查看发现是`<div id="root">`导致，为`#root`增加`CSS` 
>
> [`personal-app/src/App.js`](personal-app/src/App.js) 
>
> ~~~jsx
> import './index.less';
> ...
> ~~~
>
> [personal-app/src/index.less](personal-app/src/index.less)
>
> ~~~less
> #root {
>     height: 100%;
> }
> ~~~

## 7. 账号密码登录页

### (1) 编写页面代码

> 在`React Router`路由到的登录页面（`src/Login/index.js`）添加登录样式，并简单测试，样式代码来自[https://ant.design/components/tabs-cn/](https://ant.design/components/tabs-cn/)，进一步增加Less文件、修改样式

[`personal-app/src/pages/Login/index.js`](personal-app/src/pages/Login/index.js) 

> ~~~jsx
> import React from 'react';
> import {Tabs} from 'antd';
> import styles from './index.module.less'; 
> const { TabPane } = Tabs; //解构赋值，将Tabs.TabPane赋值给变量TabPane
> const Login = () => {
>     return (
>         // 1. Tab控件的代码参考来自：https://ant.design/components/tabs-cn/
>         // 2. {}里面用驼峰式、是JS的规范; 内容用来在.less文件中定位样式
>         <div className={styles.loginContainer}>
>             <div className={styles.login}>
>                 <div>
>                 <Tabs defaultActiveKey="1">
>                     <TabPane tab="账号密码登录" key="1"> tab1 content </TabPane>   
>                     <TabPane tab="手机登录" key="2"> tab2 content </TabPane>   
>                 </Tabs>
>                 </div>
>             </div>
>          </div>
>     );
> };
> ~~~

[`personal-app/src/pages/Login/index.module.less`](personal-app/src/pages/Login/index.module.less`)

> ~~~less
> .loginContainer {
>    	// 背景图片样式
>    	background-image: url('https://gw.alipayobjects.com/zos/rmsportal/TVYTbAXWheQpRcWDaDMu.svg');
>    	background-repeat: no-repeat;
>    	background-position: center 110px;
>    	background-size: 100%;
>    	// 高度撑满 
>    	height: 100%;
>    	.login {
>    		padding-top : 200px; //距离上边界200px
>    		width : 368px;
>    		margin : 0 auto;  //居中
>    		// 使用CSS Module之后，会自动给className加hash后缀，就失去了使用全局className的能力
>    		// 如果希望使用全局className，则要加上:global
>    		:global {
>    			// 希望两个tab水平居中，去掉下划线，
>    			// 用Chrome Dev Tool查看，发现是antd lib的三个样式在控制，覆盖他们的一些属性
>    			.ant-tabs .ant-tabs-nav {
>    				margin-bottom: 24px;
>    				text-align: center;
>    				&:before {
>    					border-bottom: 0;
>    				}
>    				.ant-tabs-nav-list {
>    					margin: 0 auto;
>    				}
>    				border-bottom: 0;
>    			}
>    		}
>    	}
> }
> ~~~


编写好之后访问`http://localhost:3000/login`测试

### (2) 表单和提交按钮 

#### (a) 创建通用的`InputItem`,`SubmitButtom`组件

[`personal-app/src/components/InputItem/index.js`](personal-app/src/components/InputItem/index.js)

> ~~~jsx
> import React from 'react';
> import { Input, Form } from 'antd';
> const InputItem = (props) => {
>     const {name, rules, ...rest} = props; // 解构赋值 + 数组展开语法
>     return (<Form.Item name={name} rules={rules}><Input {...props} /></Form.Item>)；
> };
> export default InputItem;
> ~~~

[`personal-app/src/components/SubmitButton/index.js`](personal-app/src/components/SubmitButton/index.js)

~~~jsx
import React from 'react';
import { Form, Button } from 'antd';
import styles from './index.module.less';
const SubmitButton = (props) => {
    const { children } = props;  // children是props自带的属性，不需显式地赋值就能传进来
    return (<Form.Item><Button className={styles.submit} type="primary" size="large" htmlType="submit">{children}</Button></Form.Item>)；
};
export default SubmitButton;
~~~
[personal-app/src/components/SubmitButton/index.modules.less](personal-app/src/components/SubmitButton/index.modules.less)

~~~less
.submit {
    width: 100%;
    margin-top: 24px; 
}
~~~

#### (b) 为`/App.js`增加`InputItem`和`SubmitButton`控件

[personal-app/src/App.js](personal-app/src/App.js)

> ~~~jsx
> ...
> import InputItem from '../../components/InputItem';
> import SubmitButton from '../../components/SubmitButton';
> ...
> const Login = () => {
>     // Ant Design Lib的Form组件的自定义钩子（用use开头的函数表示钩子）
>     const [form] = Form.useForm();
>     const handleFinish = (values) => {
>         console.log(values);
>     }
>     return (
>         ...
>                 <Form form={form} onFinish={handleFinish} >
>                     <Tabs defaultActiveKey="1"> 
>                         <TabPane tab="账号密码登录" key="1">
>                             <InputItem name="username" prefix = {<UserOutlined style={{ color : '#1890ff' }} />} placeholder="用户名" size="large" 
>                                 rules = {[{
>                                     required: true, 
>                                     message: "请输入用户名"
>                                 }]}
>                             />
>                             <InputItem name="password" type="password"  prefix = {<LockTwoTone style={{ color : '#1890ff' }} />} placeholder="密码" size="large" 
>                                 rules = {[{
>                                     required: true, 
>                                     message: "请输入密码"
>                                 }]}
>                             />
>                             <SubmitButton>登录</SubmitButton>
>                         </TabPane> 
>                         <TabPane tab="手机验证码登录" key="2"> tab2 content </TabPane>   
>                     </Tabs>
>                 </Form> 
>         ...
>         );
> }
> ~~~

### (3) 小结：导入和使用CSS Module的方法

> 假定 './index.module.less' 中有 .test 和 .hello 两个类样式

#### (a) 以module前缀的方式使用样式

> ~~~jsx
> import styles from './index.module.less';     	// 以`CSS Module`的方式直接导入
> <div className={styles.test} >Home</div>		// 使用用`CSS Module`导入的样式
> ~~~

#### (b) 解构赋值方式

> ~~~jsx
> import { test, hello } from './index.module.less';	// 解构赋值的方式导入
> <div className={test} >Home</div> 	    			// 使用单一样式
> <div className={`${test} ${hello}`} >Home</div>  	// 使用多个样式
> <div className={[test, hello]} >Home</div>	    	// 使用多个样式
> ~~~

## 8. 手机验证码登录页

手机验证码登录Tab

> * [`src/pages/Login/index.js`](personal-app/src/pages/Login/index.js)
> * [`src/pages/Login/index.module.less`](personal-app/src/pages/Login/index.module.less)
> 

`InputItem`控件支持验证码发送

> * [`src/components/InputItem/index.js`](personal-app/src/components/InputItem/index.js)
> * [`src/components/InputItem/index.module.less`](personal-app/src/components/InputItem/index.module.less)

## 9. 注册页面

注册页面代码

> * [src/pages/Register/index.js](personal-app/src/pages/Register/index.js) 
> * [src/pages/Register/index.module.less](personal-app/src/pages/Register/index.module.less)

其中

(1) 给`InputItem`加上`ref`，注册页面里的Popover会需要用到

[`person-app/src/components/InputItem/index.js`](personal-app/src/components/InputItem/index.js)

> ~~~jsx
> const InputItem = React.forwardRef((props, ref) => {
> 	const {name, rules, countDown, ...rest} = props; // 解构赋值 + 数组展开语法
> 	...
> 		<Input ref={ref} {...rest} />
> 	...
> });
> export default InputItem;
> ~~~

(2) 用颜色会发生变化的进度条来提示密码强度

[personal-app/src/pages/Register/index.js](personal-app/src/pages/Register/index.js) 

> ~~~jsx
>     // 密码强度样式后缀
>     const getPasswordStatus = () => {
>         const value = form.getFieldValue('password');
>         if (value && value.length > 9) {
>             return 'ok';
>         } else if (value && value.length > 5) {
>             return 'pass';
>         } else {
>             return 'poor';
>         }
>     }
>     // 用颜色会发生变化的进度条来提示密码强度
>     const renderPasswordProgress = () => {
>         const passwordProgressMap = {ok : 'success', pass : 'normal', poor : 'exception'};
>         const value = form.getFieldValue('password');
>         const passwordStatus = getPasswordStatus();
>         return value && value.length && (
>             // Progress控件的文档： https://ant-design.gitee.io/components/progress-cn/  
>             <div className={styles[`progress-${passwordStatus}`]}> 
>                 <Progress  
>                     className={styles.progress}  
>                     status={passwordProgressMap[passwordStatus]} 
>                     type='line' strokeWidth={6} showInfo={false}
>                     percent={value.length * 10 > 100 ? 100 : value.length * 10}
>                 />
>             </div>
>         )
>     }
> ~~~

[src/pages/Register/index.module.less](personal-app/src/pages/Register/index.module.less) 

> ~~~less
> .progress-pass > .progress {
>     :global {
>         .ant-progress-bg {
>             background-color: @warning-color,
>         }
>     }
> }
> ~~~

(3) 发送验证码功能

已经在 [`src/components/InputItem/index.js`](personal-app/src/components/InputItem/index.js)中写好、可以直接复用

[personal-app/src/pages/Register/index.js](personal-app/src/pages/Register/index.js) 

> ~~~jsx
> import InputItem from '../../components/InputItem';
> ...
> <InputItem name='captcha' size='large' placeholder='验证码' 
>     rules={[{required: true, message: '请输入验证码'}]} 
> />
> ~~~

(4) 注册按钮

同样可以复用  [`src/components/SubmitButton/index.js`](personal-app/src/components/SubmitButton/index.js) 

[personal-app/src/pages/Register/index.js](personal-app/src/pages/Register/index.js) 

> ~~~jsx
> <Row justify="space-between" align="middle">
> 	<Col span={8}><SubmitButton>注册</SubmitButton></Col>
> 	<Col span={16}>
> 		<Link className={styles.login} to="/login">使用已有账户登录</Link>
> 	</Col>
> </Row>
> ~~~

[personal-app/src/pages/Register/index.module.less](personal-app/src/pages/Register/index.module.less) 

> ~~~less
> .login {
>     float: right; // 右对齐
> }
> ~~~



