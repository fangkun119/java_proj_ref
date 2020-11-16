import ajax from "../utils/ajax";

export function getHomeTimeline(params) {
    return ajax.get('/proxy/2/statuses/home_timeline.json', {params});
}
