// 登录功能完成之前、调试用的ACCESS_TOKEN，在`https://open.weibo.com/tools/console`生成
export const ACCESS_TOKEN = '2.00xrKsOCTXY2WB5aa2aaeb4261rqGC';

// 在`http://open.weibo.com`点击“我的应用”，点击应用，在”应用信息”-“基本信息”可以看到
export const APP_KEY = '1394113243';
export const APP_SECRET = '4fadf300962eb0cbd0f3aa75cbe01dde';

// 登录后重定向到“local.baidu.com:3000"，在/etc/hosts中配置”127.0.0.1 local.baidu.com“，使其指向"127.0.0.1:3000"
// * 使用"local.baidu.com:3000"是因为weibo api会屏蔽refer为baidu以外的图片请求
// * 在应用配置的”授权机制“-”授权回调页配置“，也要改为”http://local.baidu.com:3000/login“
// export const REDIRECT_URL = encodeURIComponent('http://local.baidu.com:3000/login');
export const REDIRECT_URL = 'http://local.baidu.com:3000/login';

// 微博授权机制Wiki：https://open.weibo.com/wiki/%E6%8E%88%E6%9D%83%E6%9C%BA%E5%88%B6#.E6.8E.88.E6.9D.83.E6.9C.89.E6.95.88.E6.9C.9F
// 1. 引导需要授权的用户到如下地址：
export const LOGIN_URL = `https://api.weibo.com/oauth2/authorize?client_id=${APP_KEY}&response_type=code&redirect_uri=${REDIRECT_URL}`
