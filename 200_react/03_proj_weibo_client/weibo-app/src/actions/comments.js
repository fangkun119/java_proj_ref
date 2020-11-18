import { message } from 'antd';
import * as api from '../api/comments';

export function createComment(params = {}) {
    return async () => {
        try {
            const { id } = await api.createComment(params);
            if (id) {
                message.success('评论成功！');
            } 
        } catch(e) {
            console.log(e);
            message.error(e.message || '评论失败！');
        }
    }
}

export function getComments(params = {}) {
    return async () => {
        const result = await api.getComments(params);
        // console.log(result); //用来知道API的返回格式
    }
}