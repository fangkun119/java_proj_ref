import React, { useEffect, useCallback, useState } from 'react';
import { List, Avatar, Card, Button, Input, Row, Col, Modal} from 'antd';
import { useDispatch, useMappedState } from 'redux-react-hook';
import { ExclamationCircleOutlined } from '@ant-design/icons';
import moment from 'moment';
// 因为在/jsconfig.json中配置了"baseUrl":"src"，下面两种导入方式等价
// import { getComments } from '../../../../actions/comments';  // 默认的import方式
import { getComments, createComment, deleteComment } from 'actions/comments';   // 借助jsconfig.json配置的import方式
import { COMMENT_PAGESIZE, getUid } from 'constants/index';
import styles from './index.module.scss';

const uid = getUid();
const { confirm } = Modal; // 解构赋值

const CommentsList = ({ id }) => {
    // action获取评论列表后，会由reducer处理并存储在store/state中，这里从store/state提取评论列表
    const { comments = [], page = 0, total } = useMappedState((state) => (state.comments)); 
    
    // dispatch
    const dispatch = useDispatch();        

    // 滚动条滚到到底部时，触发获取下一页评论列表的action
    // useCallback(() => {...}, [dispatch, id, page])
    //    把内联回调函数及依赖项数组作为参数传入 useCallback，它将返回该回调函数的 memoized 版本，该回调函数仅在某个依赖项改变时才会更新
    //    对这个例子：当disaptch,id,page之一发生变化时，该回调函数也会发生变化
    // 参考（HookAPI索引）: https://zh-hans.reactjs.org/docs/hooks-reference.html#usecallback
    const handleInfiniteOnLoad = useCallback(() => { // add 'useCallback' for fix warning "The 'handleInfiniteOnLoad' function makes the dependencies of useEffect Hook (at line 23) change on every render. To fix this, wrap the definition of 'handleInfiniteOnLoad' in its own useCallback() Hook"
        dispatch(getComments({ id, page : page + 1, count: COMMENT_PAGESIZE }));
    }, [dispatch, id, page]); // add dependencies for error "React Hook useCallback does nothing when called with only one argument. Did you forget to pass an array of dependencies?"

    // CommentList组件加载时，触发获取评论列表的action
    useEffect(() => {
        handleInfiniteOnLoad();
    }, []);  
    // }, [handleInfiniteOnLoad]); 
    // 不希望 handleInfiniteOnLoad 发生变化时，加载下一页评论，将其从useEffect依赖中移除
    // 否则会造成死循环（调用handleInfiniteOnLoad会更新page，而更新page会导致handleInfiniteOnLoad发生变化）

    // 仅当还有未显示的评论时，才会出现“加载更多”按钮
    const loadMore = page * COMMENT_PAGESIZE < total && (
        <div className={styles.loadMore}>
            <Button onClick={handleInfiniteOnLoad}>加载更多</Button>
        </div>
    );

    // 发表评论
    const [commentValue, setCommentValue] = useState('');
    const handleSendComment = () => {
        // 接口文档：https://open.weibo.com/wiki/2/comments/create 
        // 参数access_token:由拦截器统一添加
        // Post请求要使用URLSearchParams来封装
        let param = new URLSearchParams();
        param.append('id', id);                 // 被评论的微博id，来自url参数
        param.append('comment', commentValue);  // 评论内容
        dispatch(createComment(param, false));  // 触发另一action，以便发送ajas请求
        setCommentValue('');                    // commentValue状态清零，以清空输入框
    };

    // 删除评论
    const handleDeleteComment  = (e, id) => {
        e.preventDefault();
        let param = new URLSearchParams(); 
        param.append('cid', id);
        confirm({
            title: '警告',
            icon: <ExclamationCircleOutlined />,
            content: '确定要删除这条评论吗？',
            onOk() {
                dispatch(deleteComment(param))
            }
        })
    }

    // 组件渲染
    return (
        // <List> 代码来自：https://ant.design/components/list-cn/ 
        // <InfiniteScroll> 代码参考 <Home> 以及 https://ant.design/components/list-cn/ 
        <Card className={styles.comentsList}>
            <Row>
                <Col span={20}>
                    <Input value={commentValue} onChange={(e) => setCommentValue(e.target.value)}/>
                </Col>
                <Col span={4}>
                    <Button onClick={handleSendComment} type="primary">评论</Button>
                </Col>
            </Row>
            <List
                loadMore={loadMore} 
                dataSource={comments}
                // 用解构赋值提取comments中item的属性，减少内部代码取这些属性值时的复杂度
                renderItem={({ user = {}, id, text, created_at }) => (
                    <List.Item 
                        key={id}
                        actions={
                            uid === user.idstr ? 
                                [<a href="#!" onClick={(e) => handleDeleteComment(e, id)}>删除</a>] : []
                            }
                    >
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
