import React, { useEffect } from 'react';
import { useDispatch } from 'redux-react-hook';
import { getPost } from '../../actions/timeline'; //借助了jsconfig.json的配置，定位到/src/actions/timeline.js

const Details = ({ match }) => { //用解构赋值提取props.match, props是react router设置的
    const dispatch = useDispatch();
    const { params: {id}} = match;
    useEffect(() => {
        // API文档：https://open.weibo.com/wiki/2/statuses/show 
        dispatch((getPost({id})));
    }, [dispatch]);

    return (
        <div>Details</div>
    )
}

export default Details;