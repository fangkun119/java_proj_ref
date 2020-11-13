// 用来处理response的拦截器，用数组存储，可以配多个
const responseInterceptors = [
    {
        name: 'formatResponse',
        success(response) {
            // 只需要response的data字段，其他字段丢弃
            return response.data; 
        }
    }
];

const interceptors = {
    response : responseInterceptors
};

function doInstall(instance, options = {}) {
    const { type } = options;   // 只使用options的type字段
    interceptors[type].forEach( // 根据type（例如response)，取出所有拦截器，遍历每个拦截器
        (interceptor) => {
            // 取出拦截器的success, fail字段所对应的方法
            const {success, fail} = interceptor; 
            // instance 来自于 ajax.create()
            // 根据axios文档（custom instance interceptors），以请求拦截器为例，采用如下方法安装
            //      var instance = axios.create();
            //      instance.interceptors.request.use(function () {/*...*/});
            instance.interceptors[type].use(success, fail);
        }
    )
}

// 参数：instance = ajax.create()
export function install(instance, options = {}) {
    doInstall(instance, {
        type: 'response'
    })
}
