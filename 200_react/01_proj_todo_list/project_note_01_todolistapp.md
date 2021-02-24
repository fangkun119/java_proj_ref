<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [01 TO-DO List项目记录](#01-to-do-list%E9%A1%B9%E7%9B%AE%E8%AE%B0%E5%BD%95)
  - [1. 安装react（Mac）](#1-%E5%AE%89%E8%A3%85reactmac)
  - [2. 创建React App](#2-%E5%88%9B%E5%BB%BAreact-app)
  - [3. 项目目录结构](#3-%E9%A1%B9%E7%9B%AE%E7%9B%AE%E5%BD%95%E7%BB%93%E6%9E%84)
  - [4. package.json文件内容说明](#4-packagejson%E6%96%87%E4%BB%B6%E5%86%85%E5%AE%B9%E8%AF%B4%E6%98%8E)
    - [(1) package.json](#1-packagejson)
    - [(2) package-lock.json](#2-package-lockjson)
  - [5. 代码编写](#5-%E4%BB%A3%E7%A0%81%E7%BC%96%E5%86%99)
  - [6. 项目代码及注释说明](#6-%E9%A1%B9%E7%9B%AE%E4%BB%A3%E7%A0%81%E5%8F%8A%E6%B3%A8%E9%87%8A%E8%AF%B4%E6%98%8E)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# 01 TO-DO List项目记录

## 1. 安装react

> 开发环境为Mac

确认`nodejs`和`npm`已经安装

> ~~~bash
> $ $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/31_react/01_proj_todo_list/
> $ node -v
> v12.19.0
> __________________________________________________________________
> $ $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/31_react/01_proj_todo_list/
> $ npm -v
> 6.14.8
> ~~~

如果没有安装，可以使用如下命令进行安装

> ~~~bash
> $ # 安装node时会一同安装npm
> $ brew install node
> ~~~
>
> [https://nodejs.org/en/download/](https://nodejs.org/en/download/)
> [https://nodejs.org/en/download/package-manager/#macos](https://nodejs.org/en/download/package-manager/#macos)

安装React

> ~~~bash
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/31_react/01_proj_todo_list/
> $ sudo npm i create-react-app -g #-g表示全局安装
> Password:
> /usr/local/bin/create-react-app -> /usr/local/lib/node_modules/create-react-app/index.js
> + create-react-app@3.4.1
> added 97 packages from 45 contributors in 21.095s
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/31_react/01_proj_todo_list/
> $ create-react-app -V
> 3.4.1
> ~~~

安装之后可以尝试执行 `create-react-app todo-list`创建名为`todo-list`的demo 项目 

如果创建过程中报 `gyp: No Xcode or CLT version detected!`的错误，需要安装`xcode-select`

> ~~~bash
> $ sudo xcode-select --install 
> ~~~

参考文档

> * [https://segmentfault.com/a/1190000021394623](https://segmentfault.com/a/1190000021394623)
> * [https://blog.csdn.net/liubin9043/article/details/83856093](https://blog.csdn.net/liubin9043/article/details/83856093 )
> * [https://developer.apple.com/download/more/](https://developer.apple.com/download/more/) : query with keywords `Command Line Tools for Xcode`, download the dmg file and installs


## 2. 创建React App

创建一个名为 `todo-list`的React APP

> ~~~bash
> $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/31_react/01_proj_todo_list/
> $ create-react-app todo-list
> 
> Creating a new React app in /Users/fangkun/Dev/git/java_proj_ref/31_react/01_proj_todo_list/todo-list.
> 
> Installing packages. This might take a couple of minutes.
> Installing react, react-dom, and react-scripts with cra-template...
> 
> > fsevents@1.2.13 install /Users/fangkun/Dev/git/java_proj_ref/31_react/01_proj_todo_list/todo-list/node_modules/jest-haste-map/node_modules/fsevents
> > node install.js
> 
>   SOLINK_MODULE(target) Release/.node
>   CXX(target) Release/obj.target/fse/fsevents.o
>   SOLINK_MODULE(target) Release/fse.node
> 
> ...
> 
> + react-scripts@3.4.3
> + cra-template@1.0.3
> + react@16.14.0
> + react-dom@16.14.0
> added 1604 packages from 755 contributors and audited 1604 packages in 180.76s
> 
> 73 packages are looking for funding
>   run `npm fund` for details
> 
> found 1 high severity vulnerability
>   run `npm audit fix` to fix them, or `npm audit` for details
> 
> Removing template package using npm...
> 
> npm WARN tsutils@3.17.1 requires a peer of typescript@>=2.8.0 || >= 3.2.0-dev || >= 3.3.0-dev || >= 3.4.0-dev || >= 3.5.0-dev || >= 3.6.0-dev || >= 3.6.0-beta || >= 3.7.0-dev || >= 3.7.0-beta but none is installed. You must install peer dependencies yourself.
> 
> ...
> 
> 73 packages are looking for funding
>   run `npm fund` for details
> 
> found 1 high severity vulnerability
>   run `npm audit fix` to fix them, or `npm audit` for details
> 
> Success! Created todo-list at /Users/fangkun/Dev/git/java_proj_ref/31_react/01_proj_todo_list/todo-list
> Inside that directory, you can run several commands:
> 
>   npm start
>     Starts the development server.
> 
>   npm run build
>     Bundles the app into static files for production.
> 
>   npm test
>     Starts the test runner.
> 
>   npm run eject
>     Removes this tool and copies build dependencies, configuration files
>     and scripts into the app directory. If you do this, you can’t go back!
> 
> We suggest that you begin by typing:
> 
>   cd todo-list
>   npm start
> 
> Happy hacking!
> ~~~
>
> * `react version`：16.14.0
> * `cra-template`：is `reate-react-app`
> * `npm start`： developer mode of the project
> * `npm run build`:  package the project for production
> * `npm test`:  testing
> * `npm run eject`:  eject `create-react-app` configurations and script into the application directory

其中关于Typescript的Warning可以被忽略（即使手动安装TypeScript之后仍然可能会报这些错误）

> "tsutils@3.17.1 requires a peer of typescript@>=2.8.0 || >= 3.2.0-dev || >= 3.3.0-dev || >= 3.4.0-dev || >= 3.5.0-dev || >= 3.6.0-dev || >= 3.6.0-beta || >= 3.7.0-dev || >= 3.7.0-beta but none is installed" 
>
> 相关说明
>
> * [https://github.com/facebook/create-react-app/issues/8034](https://github.com/facebook/create-react-app/issues/8034) 
> * [https://github.com/facebook/create-react-app/issues/6834](https://github.com/facebook/create-react-app/issues/6834)
> * [https://igorkhromov.com/2020/06/25/create-react-app-on-the-mac](https://igorkhromov.com/2020/06/25/create-react-app-on-the-mac) 

启动APP之后，用浏览器访问 http://localhost:3000/ 可以看到项目页面

> ~~~bash
> # run npm start, it will open the browser and visit http://localhost:3000/
> npm start 
> ~~~

## 3. 项目目录结构

> * `node_modules`: 依赖项，例如 `react`, `react-dom`, ...
> * `public`: 静态资源, 例如 `index.html`, `logo192.png`, `manifest.json`, `robots.txt`, ...
> * `src`: 代码
>	* `App.css`, `App.js`:  index.js所使用的JSX Element
>	* `index.js`, `index.css`: 主页
>	* `serviceWorker.js`:  cache based PWA, register, listening, cache data, image into local store (可以离线访问）
>	* `App.test.js`, `setupTests.js`: 测试用例
>	* `logo.svg`
>	* `.gitignore`
> * `package-lock.json`, `package.json`
> * `README.md`

## 4. package.json文件内容说明

### (1) package.json

> ~~~json
> {
>   "name": "todo-list",
>   "version": "0.1.0",
>   "private": true,
>   "dependencies": {
>     "@testing-library/jest-dom": "^4.2.4",
>     "@testing-library/react": "^9.5.0",
>     "@testing-library/user-event": "^7.2.1",
>     "react": "^16.14.0",
>     "react-dom": "^16.14.0",
>     "react-scripts": "3.4.3"
>   },
>   "scripts": {
>     "start": "react-scripts start",
>     "build": "react-scripts build",
>     "test": "react-scripts test",
>     "eject": "react-scripts eject"
>   },
>   "eslintConfig": {
>     "extends": "react-app"
>   },
>   "browserslist": {
>     "production": [
>       ">0.2%",
>       "not dead",
>       "not op_mini all"
>     ],
>     "development": [
>       "last 1 chrome version",
>       "last 1 firefox version",
>       "last 1 safari version"
>     ]
>   }
> }
> ~~~
>
> * `dependencies`: 依赖的Libs以及版本
> * `scripts`:  当执行`npm start`命令时、其实执行的是这段代码中的 `react-scripts start`，这段代码可以更改，以便在APP启动时执行其他操作
> * `eslintConfig`: 代码样式检查标准
> * `browserslist`: Babel（用来转换高级别的javascript到ES5）相关的配置，指定需要支持哪些浏览器

### (2) package-lock.json

> 执行`npm install`时生成的文件，格式说明如下
>
> * `"version": "13.1.2"`:  版本必须是13.1.2
> * `"workbox-core": "^4.3.1"`:  使用4.\*.\*中的最大版本 (major version必须是4)
> * `"isarray": "~1.0.0"`: 使用1.0.*中的最大版本 (the major version必须是1.0)
>
> 当`git clone`并覆盖`package-lock.json`之后测试失败，可以回退`package-lock.json`到之前已经通过测试的版本
>
> 项目部署时应当使用包含`package-log.json`，这样production环境可以使用相兼容的依赖项

## 5. 代码编写

> 1. 启动APP并用浏览器访问 http://localhost:3000
>
>     ~~~bash
>     npm start
>     ~~~
>
> 2. 使用IDE（这个Demo使用Vissual Studio Code）打开项目
>
> 3. 当代码被修改时，浏览器的页面会自动刷新

## 6. 项目代码及注释说明

> `project`: [todo-list/](todo-list/)
>
> `main page`：[todo-list/public/index.html](todo-list/public/index.html)
>
> `App component`：[todo-list/src/App.js](todo-list/src/App.js)及[todo-list/src/App.css](todo-list/src/App.css)