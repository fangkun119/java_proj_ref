import { GET_COMMENTS, RESET_COMMENTS } from "../constants/actions";

const initState = {
    comments: []
};

export default function reducer(state = initState, action) {
    switch (action.type) {
        case GET_COMMENTS:
            const { comments, params: {page} = {}, total } = action.payload || {};
            return {
                ...state,
                // 合并新加载和已有的评论
                // 有时候因新评论加入影响page参数分页、会导致出现重复评论，需要去重
                comments: [...new Set([...state.comments, ...comments])], 
                // 已经加载到评论列表第几页
                page, 
                // 总共有多少
                total, 
            }
        case RESET_COMMENTS: 
            return initState;
        default: 
            return state;
    }
} 
