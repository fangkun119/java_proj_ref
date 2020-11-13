import React from 'react';
import { Avatar } from 'antd';
import moment from 'moment';
import sytles from './index.module.less';

const ArticalListContent = ({
    data: {content, avatar, owner, href, updateAt} // 解构赋值
}) => (
    <div>
        <div>{content}</div>
        <div className={sytles.extra} >
            <Avatar src={avatar} size="small" />
            <a href={href}>{owner}</a>发布在<a href={href}>{href}</a>
            <em>{moment(updateAt).format('YYYY-MM-DD HH:mm')}</em>
        </div>
    </div>
);

export default ArticalListContent;
