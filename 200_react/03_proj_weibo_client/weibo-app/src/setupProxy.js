// 跨域用的代理
const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
    app.use(
        // 为以'/proxy'开头的请求使用代理
        '/proxy', 
        // 以服务器的形式（而不是浏览器的形式）发起请求
        createProxyMiddleware({
            target: 'https://api.weibo.com', // 请求目标地址
            pathRewrite: {'/proxy': '/'},    // 替换请求地址中的'/proxy‘为'/'
            changeOrigin: true,              // 表示是否已经换了一个域
        })
    )
}