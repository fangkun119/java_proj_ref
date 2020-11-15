// 因为登录功能还没有开发、暂时先把ACCESS_TOKEN写到常量中
import { ACCESS_TOKEN } from '../constants';

// Response 拦截器数组
const responseInterceptors = [
    { 
        name: 'getResponseData', 
        success(response) { return response.data; } 
    }, 
];

// Request拦截器数组
const requestInterceptors = [
    {
        // 文档: https://open.weibo.com/wiki/%E6%8E%88%E6%9D%83%E6%9C%BA%E5%88%B6#.E6.8E.88.E6.9D.83.E6.9C.89.E6.95.88.E6.9C.9F
        // 使用OAuth2.0调用API接口有两种方式：
        // 1、 直接使用参数，传递参数名为 access_token
        //    https://api.weibo.com/2/statuses/public_timeline.json?access_token=abcd
        // 2、在header里传递，形式为在header里添加 Authorization:OAuth2空格abcd，这里的abcd假定为Access Token的值，其它接口参数正常传递即可。
        // 注：所有的微博开放平台接口都部署在weibo.com域下，仅有移动端的授权接口在open.weibo.cn域。
        name: 'addHttpRequestHeader', 
        success(req) { 
            req.headers['Authorization'] = `OAuth2 ${ACCESS_TOKEN}`
            return req; 
        },
        fail(err) {
            console.error('request error: ', err); 
            return Promise.reject(err);
        }
    }
];

const interceptors = {
    response : responseInterceptors,
    request : requestInterceptors
};

function doInstall(instance, options = {}) {
    const { type } = options;   // 只使用options的type字段
    interceptors[type].forEach( // 根据type（例如response)，取出所有拦截器，遍历每个拦截器
        (interceptor) => {
            const {success, fail} = interceptor; // 取出拦截器的success, fail字段所对应的方法
            // 根据axios文档（custom instance interceptors），以请求拦截器为例，采用如下方法安装
            //      var instance = axios.create();
            //      instance.interceptors.request.use(function () {/*...*/});
            instance.interceptors[type].use(success, fail);
        }
    )
};

// 参数：instance = ajax.create()
export function install(instance, options = {}) {
    doInstall(instance, {
        type: 'response'
    });
    doInstall(instance, {
        type: 'request'
    });
};
