import React from 'react';
import { getHomeTimeline } from '../../actions/timeline';
import InfiniteScroll from 'react-infinite-scroller';
import { useDispatch, useMappedState } from 'redux-react-hook';
import Post from './components/post';
import styles from './index.module.scss';

const Home = () => {
    // (1) 对应于哪个reducer的数据格式
    //     step1：下面“useMappedState((state) => (state.timeline))”中看到是使用了RootReducer的timeline属性
    //     step2：“/src/reducers/index.js"中看到“import timeline from './timeline'”
    //     step3：“/src/reducers/timeline.js”就是对应的reducer
    // (2) posts 使用缺省值[]，home使用缺省值{}，避免初始空值时object undefine的错误
    //     另一种方式，在定义reducer时设置默认值，见src/reducers/timeline.js
    const { home: { posts = [], page = 0} = {} } = useMappedState((state) => (state.timeline));

    const dispatch = useDispatch();

    const handleInfiniteOnLoad = () => {
        dispatch(getHomeTimeline({ page: page + 1 }));
    };
    // <InfiniteScroll> 改为 initialLoad={true}，不再需要靠useEffect来完成初始加载
    // useEffect(() => {dispatch(getHomeTimeline({ page:1 }));}, []); 

    return(
        <div className={styles.container}>
            <InfiniteScroll
                initialLoad={true} //已经用了dispatch来初始加载、不需要为true
                pageStart={1} 
                loadMore={handleInfiniteOnLoad} //滚动到底部时触发的事件
                hasMore={true} //总是可以向下滚动
            >
            {
                posts.map(({
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
