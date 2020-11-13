import { message } from 'antd';
import * as api from '../api/register'; //为何要用* as

export function getCaptcha(payload = {}) {
    return async () =>  {
        // 出于代码规范考虑，api定义在src/api/register.js，与action相对应
        // 用解构赋值把返回的result里面的 data:{code, message，data{captcha}} 提取出来，并将message重命名为msg
        const response = await api.getCaptcha(payload);
        console.log(response);
        const {code, message:msg} = response; // 未使用拦截器时的代码：const {data: {code, message:msg}} = response;
        if (code === 20020) {
            const { data : { captcha } } = response; // 未使用拦截器时的代码： const {data: {data : { captcha }}} = response;
            message.success(`${msg}, 验证码为${captcha}`);
        } else {
            message.error(`${msg}`);
        }
    }
}

export function register(payload = {}) {
    return async () =>  {
        const {code, message:msg} = await api.register(payload); // 未使用拦截器时的代码： const {data: {code, message:msg}} = await api.register(payload);
        if (code === 20023) {
            message.success(`${msg}`);
        } else {
            message.error(`${msg}`);
        }
    }
}

