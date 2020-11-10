// Axios封装
import axios from 'axios';

function getAxiosInstance(options) {
    const instance = axios.create();
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
