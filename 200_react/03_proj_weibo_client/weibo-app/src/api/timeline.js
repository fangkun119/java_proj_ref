import ajax from "../utils/ajax";

export function getHomeTimeline(params) {
    return ajax.get('/proxy/2/statuses/public_timeline.json', {data: params});
}
