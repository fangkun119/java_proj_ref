import React, { useEffect } from 'react';
import { List, Avatar, Card, Button } from 'antd';
import { useDispatch, useMappedState } from 'redux-react-hook';
import moment from 'moment';
// 因为在/jsconfig.json中配置了"baseUrl":"src"，下面两种导入方式等价
// import { getComments } from '../../../../actions/comments';  // 默认的import方式
import { getComments } from 'actions/comments';   // 借助jsconfig.json配置的import方式
import { COMMENT_PAGESIZE } from 'constants/index';
import styles from './index.module.scss';

const CommentsList = ({ id }) => {
    // action获取评论列表后，会由reducer处理并存储在store/state中，这里从store/state提取评论列表
    const { comments = [], page = 0, total } = useMappedState((state) => (state.comments)); 
    
    const dispatch = useDispatch();        
    const handleInfiniteOnLoad = () => {
        // 滚动条滚到到底部时，触发获取下一页评论列表的action
        dispatch(getComments({ id, page : page + 1, count: COMMENT_PAGESIZE }));
    }
    useEffect(() => {
        // 组件加载时、以及dispatch发生变化时，触发获取评论列表的action
        handleInfiniteOnLoad();
    }, []);

    const loadMore = page * COMMENT_PAGESIZE < total && (
        <div className={styles.loadMore}>
            <Button onClick={handleInfiniteOnLoad}>加载更多</Button>
        </div>
    );

    return (
        // <List> 代码来自：https://ant.design/components/list-cn/ 
        // <InfiniteScroll> 代码参考 <Home> 以及 https://ant.design/components/list-cn/ 
        <Card className={styles.comentsList}>
            <List
                loadMore={loadMore} 
                dataSource={comments}
                // 用解构赋值提取comments中item的属性，减少内部代码取这些属性值时的复杂度
                renderItem={({ user = {}, id, text, created_at }) => (
                    <List.Item key={"list_itme_" + id}>
                        <List.Item.Meta
                            avatar={<Avatar src={user.avatar_hd} />}
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
    )
}

export default CommentsList;
