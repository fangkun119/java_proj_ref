import React, { useEffect } from 'react';import { Card } from 'antd';
import { RetweetOutlined, LikeOutlined, MessageOutlined } from '@ant-design/icons';
import { getHomeTimeline } from '../../actions/timeline';
import moment from 'moment';
import { useDispatch, useMappedState } from 'redux-react-hook';

import styles from './index.module.scss';

const Home = () => {
    const { 
        home = [] /*初始值为空，更新值从store中获取，其中会用到下面设置的函数来挑选与“信息流”有关的数据*/
    } =  useMappedState((state) => (state.timeline));

    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(getHomeTimeline())
    }, [dispatch]); //依赖[dispatch]：是一个规范，一旦dispatch发生变化，可以重新触发actions

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

    return(
        <div className={styles.container}>
            {
                home.map(({
                    id, 
                    text, 
                    user, 
                    created_at, 
                    source, 
                    pic_urls,
                    reposts_count,      // 转帖数
                    attitudes_count,    // 点赞数
                    comments_count,     // 评论数 
                }) => (
                    <Card 
                        className={styles.post} 
                        key={id} 
                        bordered={false} 
                        hoverable 
                        title={getPostTitle(user,created_at,source)}
                        actions={[ 
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
                ))
            }
        </div>
    );
};

export default Home;
