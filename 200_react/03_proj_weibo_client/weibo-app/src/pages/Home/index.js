import React, { useEffect } from 'react';
import { useDispatch } from 'redux-react-hook';
import { getHomeTimeline } from '../../actions/timeline';

const Home = () => {
    const dispatch = useDispatch();

    useEffect(() => {
        dispatch(getHomeTimeline())
    }, []);

    return(
        <div>Home</div>
    );
};

export default Home;