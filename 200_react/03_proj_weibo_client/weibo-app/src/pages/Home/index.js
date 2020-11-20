import React from 'react';
import { UserOutlined, EditOutlined } from '@ant-design/icons';
import { useDispatch, useMappedState } from 'redux-react-hook';
import InfiniteScroll from 'react-infinite-scroller';
import { Row, Affix } from 'antd';
import { Link } from 'react-router-dom';
import { getHomeTimeline } from 'actions/timeline';
import { LOGIN_URL } from '../../constants';
import Post from './components/post';
import CommentsList from './components/commentsList';
import styles from './index.module.scss';

const Home = () => {
    const dispatch = useDispatch();

    // 微博列表，与redux单向状态链路相关的笔记如下
    // (1) 对应于哪个reducer的数据格式
    //     step1：下面“useMappedState((state) => (state.timeline))”中看到是使用了RootReducer的timeline属性
    //     step2：“/src/reducers/index.js"中看到“import timeline from './timeline'”
    //     step3：“/src/reducers/timeline.js”就是对应的reducer
    // (2) posts 使用缺省值[]，home使用缺省值{}，避免初始空值时object undefine的错误
    //     另一种方式，在定义reducer时设置默认值，见src/reducers/timeline.js
    const { home: { posts = [], page = 0} = {}, current } = useMappedState((state) => (state.timeline));

    const handleInfiniteOnLoad = () => {
        dispatch(getHomeTimeline({ page: page + 1 }));
    };
    // <InfiniteScroll> 改为 initialLoad={true}，不再需要靠useEffect来完成初始加载
    // useEffect(() => {dispatch(getHomeTimeline({ page:1 }));}, []); 

    return(
        <div className={styles.container}>
            <Affix offsetTop={0} >
                <Row 
                    className={styles.appbar} 
                    justify="space-between" 
                    align="middle"
                >
                    <a href={LOGIN_URL}><UserOutlined className={styles.icon}/></a>
                    <div className={styles.appTitle}>Weibo APP</div>
                    <Link to="/new"><EditOutlined className={styles.icon}/></Link>
                </Row>
            </Affix>
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
                    <div key={id}>
                        <Post 
                            key={id} 
                            id={id} 
                            isCommentsExpanded={current === id} 
                            {...rest} 
                        />
                        {
                            current === id && 
                            <CommentsList id={id} />
                        }
                    </div>
                ))
            }
            </InfiniteScroll>
        </div>
    );
};

export default Home;
