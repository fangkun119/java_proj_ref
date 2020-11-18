import ajax from '../utils/ajax';

export function createComment(params) {
    // 接口文档：https://open.weibo.com/wiki/2/comments/create
    return ajax.post('/proxy/2/comments/create.json', {
        // 后端返回：{"error":"miss required parameter (comment), see doc for more info.","error_code":1001
        // 1.与PostMan可以成功发送的请求对比，差别在于Content Type不一样
        // 2.将content-type添加到./api/comments.js的headers中，发现没有作用，应当是被proxy转发后丢失了
        // 3.将content-type添加到./setupProxy.js的header中，后端调用成功，然而发现请求内容被编码在了url中，形式如下
        //   http://local.baidu.com:3000/proxy/2/comments/create.json?id=4572635561992490&comment=test 
        //   并没有被POST，因此还是需要回到./api/comments.js中找原因
        // 4.问题的原因在于，/src/pages/New/index.js中没有使用URLSearchParams封装Post参数，而是直接装在一个js对象中传入，需要修改
        //   取消./setupProxy.js中的header、并修改/src/pages/New/index.js后，将params改成data:params后，请求数据变为正确的Post请求
        data: params, 
        // params, 
        // headers: {'content-type':'application/x-www-form-urlencoded'}
    });
}

export function getComments(params) {
    // 接口文档：https://open.weibo.com/wiki/2/comments/show 
    return ajax.get('/proxy/2/comments/show.json', {
        params,
    });
}
