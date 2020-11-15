// Response 拦截器数组
const responseInterceptors = [
    /* { name: 'name', success(response) { return response; } }, */
];

// Request拦截器数组
const requestInterceptors = [
    /*{
        name: 'name', success(request) { return request; },
        fail(err) {console.error('request error: ', err); return Promise.reject(err);}
    }*/
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
