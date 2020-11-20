import React from 'react';
import { UserOutlined, EditOutlined } from '@ant-design/icons';
import { useDispatch, useMappedState } from 'redux-react-hook';
import InfiniteScroll from 'react-infinite-scroller';
import { Row, Affix, List, Avatar, Card } from 'antd';
import { Link } from 'react-router-dom';
import moment from 'moment';
import { getHomeTimeline } from '../../actions/timeline';
import { LOGIN_URL } from '../../constants';
import Post from './components/post';
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

    // 点击某条微博下面的评论时得到的该条微博的comment列表
    // (1) 这个状态是有子组件<Post>触发action获取的，通过react单向链路，所有组件（包括它的父组件Home）都可以获取
    const { comments = [] } = useMappedState((state) => (state.comments)); 

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
                            {...rest} 
                        />
                        {
                            current === id && 
                            // 代码来自：https://ant.design/components/list-cn/
                            <Card>
                                <List
                                    dataSource={comments}
                                    // 用解构赋值提取item的属性，减少内部代码取这些属性值时的复杂度
                                    renderItem={({user = {}, id, text, created_at}) => (
                                        <List.Item key={id}>
                                            <List.Item.Meta
                                                avatar={<Avatar src={user.avatar_hd}/>}
                                                title={
                                                    <div>
                                                        <span>{user.name}</span>
                                                        <span className={styles.extra}>
                                                            {moment(created_at).fromNow()}
                                                        </span>
                                                    </div>
                                                }
                                                description={text}
                                            />
                                        </List.Item>
                                    )}
                                />
                            </Card>
                        }
                    </div>
                ))
            }
            </InfiniteScroll>
        </div>
    );
};

export default Home;
