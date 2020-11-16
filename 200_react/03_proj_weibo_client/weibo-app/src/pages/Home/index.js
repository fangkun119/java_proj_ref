import React, { useEffect } from 'react';
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
    const { home: { posts = [], page } = {} } = useMappedState((state) => (state.timeline));

    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(getHomeTimeline({ page: 1 })) //跟随函数调用查看代码，传参之前已经写好，传入的{page:1}可以被一路传递到ajax post操作发送给weibo后端
    }, [dispatch]); //依赖

    const handleInfiniteOnLoad = () => {
        console.log(page);
        dispatch(getHomeTimeline({ page: page + 1 }));
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
