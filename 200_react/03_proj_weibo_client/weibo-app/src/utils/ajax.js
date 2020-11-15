import axios from 'axios';
import * as interceptors from './interceptors'; // 拦截器

function getAxiosInstance(options) {
    const instance = axios.create();
    interceptors.install(instance, options); // 安装拦截器
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
    post: makePost(),
};
