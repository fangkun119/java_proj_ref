import React, { useEffect } from 'react';
import { List, Avatar, Card } from 'antd';
import { useDispatch, useMappedState } from 'redux-react-hook';
import moment from 'moment';
import styles from './index.module.scss';

const CommentsList = () => {
    // 点击某条微博下面的评论时得到的该条微博的comment列表
    // (1) 这个状态是有子组件<Post>触发action获取的，通过react单向链路，所有组件（包括它的父组件Home）都可以获取
    const { comments = [] } = useMappedState((state) => (state.comments)); 

    return (
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
    )
}

export default CommentsList;