import * as api from '../api/timeline';
import { GET_HOME_TIMELINE, SET_CURRENT_POST } from 'constants/actions';
import { resetComments } from './comments';

export function getHomeTimeline(params = {}) {
    return async (dispatch) => {
        const result = await api.getHomeTimeline(params);
        console.log(result);
        dispatch({
            type: GET_HOME_TIMELINE,
            payload: result,
            params, //把params一路传下去，这样在redux单向链路的各步中就可以知道请求到第几页了
        })
    }
}

export function setCurrentPost(payload = {}) {
    return async (dispatch) => {
        // 组件更新顺序为
        // 1. 用户点击“评论”按钮，触发setCurrentPost action
        // 2. await dispatch(resetComments())：先将评论列表清空
        // 3. dispatch payload，更新current的值为被点击的帖子的post_id
        // 4. Home组件渲染时，在id === current的<Post>组件下方挂载<CommentsList>组件
        // 5. CommentList组件触发getComments action获取评论列表
        await dispatch(resetComments()); // 阻塞等待异步reset完成后，再dispatch payload
        dispatch({
            type: SET_CURRENT_POST, 
            payload,
        })
    }
}