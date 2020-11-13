// ruducers/indes.js: 用于数据计算和存储

// 用来把多层reducer组合起来
import { combineReducers } from 'redux';
import profile from './profile';

const rootReducer = combineReducers({
    profile,
});

// export出去，由入口文件index.js来import
export default rootReducer;