// 所有修改需要重启后才能生效

const { createProxyMiddleware } = require('http-proxy-middleware'); // 跨域用的代理

module.exports = function(app) {
    app.use( 
        // 为以'/proxy'开头的请求使用代理
        '/proxy', 
        // 浏览器打开一个网页（如 https://baidu.com），在这个域名下的脚本是无法请求其他域名下的资源的，这种请求叫做“跨域请求CORS”，这种限制叫做“同源策略”
        // 下面设置代理来解决跨域问题
        createProxyMiddleware({
            target: 'https://api.weibo.com', // 请求目标地址
            pathRewrite: {'/proxy': '/'},    // 替换请求地址中的'/proxy‘为'/'
            changeOrigin: true,              // 表示是否已经换了一个域
            // onProxyReq,
        })
    )
}

/*
// debug 过程中的代码（已经不需要）
function onProxyReq(proxyReq, req, res) {
    // 后端返回：{"error":"miss required parameter (comment), see doc for more info.","error_code":1001
    // 1.与PostMan可以成功发送的请求对比，差别在于`content-type`不一样
    // 2.将content-type添加到./api/comments.js的headers中，发现没有作用，应当是被proxy转发后丢失了
    // 3.将content-type添加到./setupProxy.js的header中，后端调用成功，然而发现请求内容被编码在了url中，形式如下
    //   http://local.baidu.com:3000/proxy/2/comments/create.json?id=4572635561992490&comment=test 
    //   并没有被POST，因此还是需要回到./api/comments.js中找原因
    // 4.问题的原因在于，/src/pages/New/index.js中没有使用URLSearchParams封装Post参数，而是直接装在一个js对象中传入，需要修改
    //   取消./setupProxy.js中的header、并修改/src/pages/New/index.js后，将params改成data:params后，请求数据变为正确的Post请求
    if (req.url.indexOf('/2/comments/create.json')) {
        proxyReq.setHeader('content-type','application/x-www-form-urlencoded');
    }
}
*/
