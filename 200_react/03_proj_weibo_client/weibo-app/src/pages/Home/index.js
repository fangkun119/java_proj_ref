import React, { useEffect } from 'react';
import { getHomeTimeline } from '../../actions/timeline';
import { useDispatch, useMappedState } from 'redux-react-hook';
import Post from './components/post';

import styles from './index.module.scss';

const Home = () => {
    const { 
        home = [] /*初始值为空，更新值从store中获取，其中会用到下面设置的函数来挑选与“信息流”有关的数据*/
    } =  useMappedState((state) => (state.timeline));

    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(getHomeTimeline())
    }, [dispatch]); //依赖[dispatch]：是一个规范，一旦dispatch发生变化，可以重新触发actions

    return(
        <div className={styles.container}>
            {
                home.map(({
                    id, 
                    ...rest
                }) => (
                    <Post 
                        key={id} 
                        {...rest}
                    />
                ))
            }
        </div>
    );
};

export default Home;
