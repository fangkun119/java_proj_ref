import React from 'react';
import ReactDOM from 'react-dom';

import { createStore, applyMiddleware } from 'redux'; //用来注入reducers
import thunk from 'redux-thunk'; //异步操作中间件 
import { StoreContext, storeContext } from 'redux-react-hook'; //连接react和redux的桥

import App from './App';
import reducers from './reducers';
import './index.less'; 

const store = createStore(
  reducer, //注入src/reducers/index.js中编写的reducers
  applyMiddleware(thunk) //应用异步中间件
)

// 用<StoreContext.Provider value={store}>包裹<App>，这样App的各处代码都能取到store的数据
ReactDOM.render(
  <StoreContext.Provider value={store}>
    <App />
  </StoreContext.Provider>
  ,
  document.getElementById('root')
);

