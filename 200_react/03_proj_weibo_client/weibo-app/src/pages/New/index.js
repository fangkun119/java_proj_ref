import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useDispatch } from 'redux-react-hook';
import { LeftOutlined } from '@ant-design/icons';
import { Row, Affix, Input } from 'antd';
import { createComment } from '../../actions/comments';
import styles from './index.module.scss';

const { TextArea } = Input;

//const New = (props) => {
//  // 查看传入的props，这个props是`react router`传入的
//  // 从URL`/comments/${id}`传入参数id位于props.match.id
//  console.log(props); 
//}
const New = ({match}) => { //用解构赋值提取props.match
    const dispatch = useDispatch();
    const { params: { id }} = match;        //用解构赋值提取id
    const [value, setValue] = useState(''); //微博内容或评论内容
    const handleClick = (e) => {
        // 因为用于<a href>、要阻止默认的跳转href行为
        e.preventDefault(); 
        if(id) {
            // 接口文档：https://open.weibo.com/wiki/2/comments/create 
            // 参数access_token:由拦截器统一添加
            // Post请求要使用URLSearchParams来封装
            let param = new URLSearchParams();
            param.append('id', id);         // 被评论的微博id，来自url参数
            param.append('comment', value); // 评论内容
            dispatch(createComment(param));
        } 
    }

    return  (
        <div className={styles.container}>
            <Affix offsetTop={0}>
                <Row 
                    className={styles.appbar} 
                    justify="space-between" 
                    align="middle" 
                >
                    <Link to="/"><LeftOutlined className={styles.icon}/></Link>
                    <a 
                        className={styles.send} 
                        href="#!" 
                        onClick={handleClick}
                    >
                        {id ? '评论' : '发送'}
                    </a>
                </Row>
            </Affix>
            <div className={styles.content}>
                <TextArea
                    className={styles.textarea} 
                    placeholder={id ? "写评论" : "分享新鲜事"} 
                    onChange={(e) => setValue(e.target.value)} 
                />
            </div>
        </div>
    )
}

export default New;