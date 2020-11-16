import React, { useEffect } from 'react';
import { getHomeTimeline } from '../../actions/timeline';
import InfiniteScroll from 'react-infinite-scroller';
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
    }, [dispatch]); //依赖
    
    const handleInfiniteOnLoad = () => {
        dispatch(getHomeTimeline())
    };

    return(
        <div className={styles.container}>
            <InfiniteScroll
                initialLoad={false} //已经用了dispatch来初始加载、不需要为true
                pageStart={1} 
                loadMore={handleInfiniteOnLoad} //滚动到底部时触发的事件
                hasMore={true} //总是可以向下滚动
            >
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
            </InfiniteScroll>
        </div>
    );
};

export default Home;
