import * as api from '../api/register'; //为何要用* as

export function getCaptcha(payload = {}) {
    return async () =>  {
        console.log('test');
        // 出于代码规范考虑，api定义在src/api/register.js，与action相对应
        const result =  await api.getCaptcha(payload);
        console.log(result);
    }
}