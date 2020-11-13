// Axios封装
import axios from 'axios';
// 引入自己编写的拦截器文件
import * as interceptors from './interceptors'; // ES模块语法（ES modules)

function getAxiosInstance(options) {
    const instance = axios.create();
    // 安装拦截器
    interceptors.install(instance, options); 
    return instance;
}

function makeGet() {
    return function(url, options) {
        const instance = getAxiosInstance(options);
        return instance({
            url, 
            method: 'get',
            ...options
        })
    }
}

function makePost() {
    return function(url, options) {
        const instance = getAxiosInstance(options);
        return instance({
            url, 
            method: 'post',
            ...options
        })
    }
}

export default {
    get: makeGet(),
    post: makePost()
};
