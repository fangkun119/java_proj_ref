import {
    GET_PROFILE 
} from '../constants/actions';

const initState = {};

export default function reducer(state = initState, action) {
    switch (action.type) {
        case GET_PROFILE:
            console.log('reducers/profile invoked'); // 临时代码，检查这个reducer函数是否被执行
            return {
                ...state,
                user: action.payload,
            }
        default:
            return state;
    }
}