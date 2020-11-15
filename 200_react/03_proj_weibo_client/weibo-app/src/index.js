import React from 'react';
import ReactDOM from 'react-dom';
import { createStore, applyMiddleware } from 'redux';
import thunk from 'redux-thunk';
import { StoreContext } from 'redux-react-hook';
import reducers from './reducers'; // 是在"./reducers/index.js”中被“export default”的RootReducer
import App from './App';

const store = createStore(
  reducers,
  applyMiddleware(thunk) // 异步插件
);

ReactDOM.render(
  <StoreContext.Provider value={store} >
    <App />
  </StoreContext.Provider>
  ,
  document.getElementById('root')
);

