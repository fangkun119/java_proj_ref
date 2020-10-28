# 01 TO-DO List Project

## 1. Install React

check nodejs and rpm 

~~~bash
$ $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/31_react/01_proj_todo_list/
$ node -v
v12.19.0
__________________________________________________________________
$ $ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/31_react/01_proj_todo_list/
$ npm -v
6.14.8
~~~

intall node (including npm) if they are not installed

~~~
brew install node
~~~

> [https://nodejs.org/en/download/](https://nodejs.org/en/download/)
> [https://nodejs.org/en/download/package-manager/#macos](https://nodejs.org/en/download/package-manager/#macos)

install react

~~~bash
$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/31_react/01_proj_todo_list/
$ sudo npm i create-react-app -g #-g表示全局安装
Password:
/usr/local/bin/create-react-app -> /usr/local/lib/node_modules/create-react-app/index.js
+ create-react-app@3.4.1
added 97 packages from 45 contributors in 21.095s
$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/31_react/01_proj_todo_list/
$ create-react-app -V
3.4.1
~~~

> after that we can try creating a react application with `create-react-app todo-list` in next section

make sure `xcode-select` has been installed

> if having error `gyp: No Xcode or CLT version detected!` when creating react application, `xcode-select` need to be installed

~~~
sudo xcode-select --install 
~~~

> reference: 
> 
> * [https://segmentfault.com/a/1190000021394623](https://segmentfault.com/a/1190000021394623)
> * [https://blog.csdn.net/liubin9043/article/details/83856093](https://blog.csdn.net/liubin9043/article/details/83856093 )
> * [https://developer.apple.com/download/more/](https://developer.apple.com/download/more/) : query with keywords `Command Line Tools for Xcode`, download the dmg file and installs


## 2. Create React App

create a react-app named as `todo-list`

~~~bash
$ /fangkundeMacBook-Pro/ fangkun@fangkundeMacBook-Pro.local:~/Dev/git/java_proj_ref/31_react/01_proj_todo_list/
$ create-react-app todo-list

Creating a new React app in /Users/fangkun/Dev/git/java_proj_ref/31_react/01_proj_todo_list/todo-list.

Installing packages. This might take a couple of minutes.
Installing react, react-dom, and react-scripts with cra-template...

> fsevents@1.2.13 install /Users/fangkun/Dev/git/java_proj_ref/31_react/01_proj_todo_list/todo-list/node_modules/jest-haste-map/node_modules/fsevents
> node install.js

  SOLINK_MODULE(target) Release/.node
  CXX(target) Release/obj.target/fse/fsevents.o
  SOLINK_MODULE(target) Release/fse.node

...

+ react-scripts@3.4.3
+ cra-template@1.0.3
+ react@16.14.0
+ react-dom@16.14.0
added 1604 packages from 755 contributors and audited 1604 packages in 180.76s

73 packages are looking for funding
  run `npm fund` for details

found 1 high severity vulnerability
  run `npm audit fix` to fix them, or `npm audit` for details

Removing template package using npm...

npm WARN tsutils@3.17.1 requires a peer of typescript@>=2.8.0 || >= 3.2.0-dev || >= 3.3.0-dev || >= 3.4.0-dev || >= 3.5.0-dev || >= 3.6.0-dev || >= 3.6.0-beta || >= 3.7.0-dev || >= 3.7.0-beta but none is installed. You must install peer dependencies yourself.

...

73 packages are looking for funding
  run `npm fund` for details

found 1 high severity vulnerability
  run `npm audit fix` to fix them, or `npm audit` for details

Success! Created todo-list at /Users/fangkun/Dev/git/java_proj_ref/31_react/01_proj_todo_list/todo-list
Inside that directory, you can run several commands:

  npm start
    Starts the development server.

  npm run build
    Bundles the app into static files for production.

  npm test
    Starts the test runner.

  npm run eject
    Removes this tool and copies build dependencies, configuration files
    and scripts into the app directory. If you do this, you can’t go back!

We suggest that you begin by typing:

  cd todo-list
  npm start

Happy hacking!
~~~
 
> * `react version`：16.14.0
> * `cra-template`：is `reate-react-app`
> * `npm start`： developer mode of the project
> * `npm run build`:  package the project for production
> * `npm test`:  testing
> * `npm run eject`:  eject `create-react-app` configurations and script into the application directory

there are warnings like 

> "tsutils@3.17.1 requires a peer of typescript@>=2.8.0 || >= 3.2.0-dev || >= 3.3.0-dev || >= 3.4.0-dev || >= 3.5.0-dev || >= 3.6.0-dev || >= 3.6.0-beta || >= 3.7.0-dev || >= 3.7.0-beta but none is installed" 

and vulnerability related with it during the creation (even after installed typescript manually), but seems like they can be ignored according to below links

> * [https://github.com/facebook/create-react-app/issues/8034](https://github.com/facebook/create-react-app/issues/8034) 
> * [https://github.com/facebook/create-react-app/issues/6834](https://github.com/facebook/create-react-app/issues/6834)
> * [https://igorkhromov.com/2020/06/25/create-react-app-on-the-mac](https://igorkhromov.com/2020/06/25/create-react-app-on-the-mac) 

testing: un npm start, it will open the browser and open the site http://localhost:3000/

~~~bash
# run npm start, it will open the browser and visit http://localhost:3000/
npm start 
~~~

## 3. Project Directory Structure

> * `node_modules`: dependencies, such as `react`, `react-dom`, ...
> * `public`: static resources, such as `index.html`, `logo192.png`, `manifest.json`, `robots.txt`, ...
> * `src`: code
>	* `App.css`, `App.js`: jsx element used  by index.js
>	* `index.js`, `index.css`: index page
>	* `serviceWorker.js`:  cache based PWA, register, listening, cache data,image into local store (可以离线访问）
>	* `App.test.js`, `setupTests.js`: test cases
>	* `logo.svg`: 
>	* .gitignore
> * `package-lock.json`, `package.json`
> * `README.md`

## 4. package.json

~~~json
{
  "name": "todo-list",
  "version": "0.1.0",
  "private": true,
  "dependencies": {
    "@testing-library/jest-dom": "^4.2.4",
    "@testing-library/react": "^9.5.0",
    "@testing-library/user-event": "^7.2.1",
    "react": "^16.14.0",
    "react-dom": "^16.14.0",
    "react-scripts": "3.4.3"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject"
  },
  "eslintConfig": {
    "extends": "react-app"
  },
  "browserslist": {
    "production": [
      ">0.2%",
      "not dead",
      "not op_mini all"
    ],
    "development": [
      "last 1 chrome version",
      "last 1 firefox version",
      "last 1 safari version"
    ]
  }
}
~~~

> * `dependencies`: dependended libs and versions
> * `scripts`: when type in `npm start`, it will execute `react-scripts start`
> * `eslintConfig`: standard of code style check
> * `browserslist`: babel related config, babel is a tool for transform higher level code to ES5 javascript, indict which browsers must be supported

package-lock.json: generated by `npm install`

> for lock down versions of the dependencies
> 
> * `"version": "13.1.2"`:  the version must be 13.1.2
> * `"workbox-core": "^4.3.1"`: use the largest version under 4.\*.\* (the major version must be 4)
> * `"isarray": "~1.0.0"`: use the largest version under 1.0.* (the major version must be 1.0)

> if someone `git clone` your project and overwrite the `lock file` according to the `package.json`, but fails on test, he can fall back to the previouse `lock file` from git which has already passed the test

this `package-lock.json` file should be included when deploying, so that the production install will use the compitable dependencies 

## 5. Start Coding

> 1. start the application and open the page in http://localhost:3000

~~~bash
npm start
~~~

> 2. open the project with an IDE, for example Vissual Studio Code 
> 3. when code is changed, the page will refresed into the browser, we can inspect 
