import * as api from '../api/timeline';
import { GET_HOME_TIMELINE } from '../constants/actions';

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