import * as api from '../api/timeline';
import { GET_HOME_TIMELINE } from '../constants/actions';

export function getHomeTimeline(payload = {}) {
    return async (dispatch) => {
        const result = await api.getHomeTimeline(payload);
        console.log(result);
        dispatch({
            type: GET_HOME_TIMELINE,
            payload: result,
        })
    }
}