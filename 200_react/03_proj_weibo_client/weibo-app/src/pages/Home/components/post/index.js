import React from 'react';
import { Card } from 'antd';
import { RetweetOutlined, LikeOutlined, MessageOutlined } from '@ant-design/icons';
import moment from 'moment';
import styles from './index.module.scss'

const getPostTitle = (user, created_at, source) => (
    <div className={styles.user} style={{display : 'flex'}}> 
        <img 
            src={user.profile_image_url} 
            className={styles.avatar} 
            alt={user.screen_name}
        />
        <div className={styles.userInfo}>
            <div>{user.screen_name}</div>
            <div className={styles.extra}>
                {moment(created_at).fromNow()} 来自 <span dangerouslySetInnerHTML={{ __html: source }} />
            </div>
        </div>
    </div>
);

const Post = ({
    text, 
    user, 
    created_at, 
    source, 
    pic_urls,
    reposts_count,      // 转帖数
    attitudes_count,    // 点赞数
    comments_count,     // 评论数 
    retweeted_status,   // 原贴（如果是转帖）
    type,               // 帖子类型，对于转帖，原贴的actions为空
}) => {
    // Ant Design的<Card/>有内置的type="inner"样式
    return (
        <Card 
            type={type} 
            className={styles.post} 
            bordered={false} 
            hoverable 
            title={getPostTitle(user,created_at,source)}
            actions={type ? [] : [ 
                <div>
                    <RetweetOutlined key="retweet"/>
                    <span>{reposts_count || ''}</span>
                </div>,
                <div>
                    <LikeOutlined key="like"/>
                    <span>{attitudes_count || ''}</span>
                </div>,
                <div>
                    <MessageOutlined key="message"/>
                    <span>{comments_count || ''}</span>
                </div>
            ]}
        >
            <div className={styles.content}>
                <div className={styles.text}>
                    {text}
                    {
                        retweeted_status && 
                        <Post type="inner" {...retweeted_status}/>
                    }
                </div>
                <ul className={styles.images}> 
                    {
                        // <li><ul>是无序列表，但是样式在src/index.scss中已经被修改了
                        pic_urls.map(({thumbnail_pic}) => (
                            <li key={thumbnail_pic} className={styles.imgWrapper}>
                                <div className={styles.imgContainer}>
                                    <img src={thumbnail_pic} alt={thumbnail_pic} />
                                </div>
                            </li>
                        ))
                    }
                </ul>
            </div>
        </Card>
    )
};

export default Post;
