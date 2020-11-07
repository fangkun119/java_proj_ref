import React from 'react';
import { Tag } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import styles from './index.module.less';

// 传参时使用解构赋值，传入一个object，得到object的属性tags
const TagList = ({ tags }) => {
    return (
        <div className={styles.tags}>
            <div className={styles.tagsTitle}>标签</div>
            {(tags || []).map((item) => (
                <Tag key={item.key}>{item.label}</Tag>
            ))}
            <Tag style={{ borderStyle : 'dashed'}}>
                <PlusOutlined/>
            </Tag>
        </div>
    )
} 

export default TagList;