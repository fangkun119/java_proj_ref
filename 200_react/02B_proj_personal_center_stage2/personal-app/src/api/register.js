//对axio的封装
// export default {
//     get: makeGet(),
//     post: makePost()
// };
import ajax from '../utils/ajax';  

import { HOST } from '../constants';

export function getCaptcha(params) {
    return ajax.post(`${HOST}/user/getCaptcha`, {data: params});
}

export function register(params) {
    return ajax.post(`${HOST}/user/register`, { data: params});
}

