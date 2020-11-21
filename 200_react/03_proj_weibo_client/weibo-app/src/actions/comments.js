import { message } from 'antd';
import * as api from 'api/comments';
import { GET_COMMENTS } from 'constants/actions';

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
    return async (dispatch) => {
        // console.log(await api.getComments(params)); 
        // 解构赋值、从response中取出comments和total_number
        const {comments = [], total_number = 0 } = await api.getComments(params); 
        dispatch({
            type: GET_COMMENTS, 
            payload: {
                comments, 
                params, 
                total: total_number, 
            }   
        })
    }
}