import ajax from '../utils/ajax';
import { accessTokenUrl } from '../constants';

export function getAccess({ code }) {
    // 接口文档：
    return ajax.post(accessTokenUrl(code));
}
