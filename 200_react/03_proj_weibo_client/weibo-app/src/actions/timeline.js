import * as api from '../api/timeline';

export function getHomeTimeline(payload = {}) {
    return async () => {
        const result = await api.getHomeTimeline(payload);
        console.log(result);
    }
}