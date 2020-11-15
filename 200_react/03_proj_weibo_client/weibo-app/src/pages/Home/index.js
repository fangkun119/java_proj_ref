import React, { useEffect } from 'react';
import ajax from '../../utils/ajax';

const Home = () => {
    useEffect(() => {
        ajax.get(
            //'https://api.weibo.com/2/statuses/public_timeline.json',
            '/proxy/2/statuses/public_timeline.json'
        );
    }, []);

    return(
        <div>Home</div>
    );
};

export default Home;