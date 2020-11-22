import { message } from 'antd';
import * as api from 'api/comments';
import { GET_COMMENTS, RESET_COMMENTS, ADD_COMMENT } from 'constants/actions';

export function createComment(params = {}, isFirst) {
    return async (dispatch) => {
        try {
            const result = await api.createComment(params);
            if (result) {
                message.success('评论成功！');
                if (!isFirst) {
                    dispatch({ 
                        type: ADD_COMMENT,
                        payload: result,
                    })
                }
            } 
        } catch(e) {
            console.log(e);
            message.error(e.message || '评论失败！');
        }
    }
}

export function getComments(params = {}) {
    return async (dispatch) => {
        // 解构赋值、从response中取出comments和total_number // console.log(await api.getComments(params)); 
        const {comments = [], total_number = 0 } = await api.getComments(params); 
        dispatch({
            type: GET_COMMENTS, 
            payload: {
                comments, 
                params, 
                total: total_number, 
            }   
        });
    }
}

export function resetComments(payload = {}) {
    return async (dispatch) => {
        dispatch({
            type: RESET_COMMENTS, 
        });
    }
}
