/**
 * 获取用户信息相关的action
 */
import { message } from 'antd';
import * as api from '../api/profile'; // ES Module语法：https://juejin.im/post/6844903993462751239
import { GET_PROFILE } from '../constants/actions';

export function getUserProfile(payload = {}) {
    return async (dispatch /* dispatch从参数传入 */) =>  {
        const {
            code, 
            message:msg, 
            data,
        } = await api.getUserProfile(payload); 
        if (code === 0) {
            dispatch({
                type: GET_PROFILE,
                payload: data
            })
        } else {
            message.error(`${msg}`);
        }
    }
}

