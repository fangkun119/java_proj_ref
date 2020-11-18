import React, {useEffect} from 'react';
import queryString from 'query-string';

// 微博授权机制Wiki：https://open.weibo.com/wiki/%E6%8E%88%E6%9D%83%E6%9C%BA%E5%88%B6#.E6.8E.88.E6.9D.83.E6.9C.89.E6.95.88.E6.9C.9F
// 1. 引导需要授权的用户到如下地址（/src/constants/index.js)：
//    "export const REDIRECT_URL = 'http://${HOST_NAME}:3000/login'"
//    "export const LOGIN_URL = `https://api.weibo.com/oauth2/authorize?client_id=${APP_KEY}&response_type=code&redirect_uri=${REDIRECT_URL}`"
// 2. 如果用户同意授权，页面跳转至 YOUR_REGISTERED_REDIRECT_URI/?code=CODE
//    即跳转到这个页面
//    使用queryString.parseUrl可以提取出url中的参数code

const Login = () => {
    const {query: {code}} = queryString.parseUrl(window.location.href);
    return <div>{code}</div>;
}

export default Login;
