import * as api from '../api/account';
import {message} from 'antd';

export function getAccess(params = {}) {
    return async () => {
        try {
            const { access_token } = await api.getAccess(params);
            localStorage.setItem('weibo_app_access_token', access_token);
        } catch (e) {
            message.error('登录失败');
        }
        window.location.href = '/'; //跳转到首页
    }
}
