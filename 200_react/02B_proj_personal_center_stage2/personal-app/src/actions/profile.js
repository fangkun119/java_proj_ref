/**
 * 获取用户信息相关的action
 */

import { message } from 'antd';
import * as api from '../api/profile'; // ES Module语法：https://juejin.im/post/6844903993462751239

export function getUserProfile(payload = {}) {
    return async () =>  {
        const response = await api.getUserProfile(payload);
        console.log(response); // 查看是否能从后端拿到数据
        const {code, message:msg} = response; 
        if (code === 0) {
            message.success(`${msg}`);
        } else {
            message.error(`${msg}`);
        }
    }
}