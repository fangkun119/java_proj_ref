// 对axio的封装：src/utils/ajax.js
// export default {
//     get: makeGet(),
//     post: makePost()
// };
import ajax from '../utils/ajax';  
import { HOST } from '../constants';

export function getUserProfile(params) {
    return ajax.post(`${HOST}/account/center`, {data: params});
}
