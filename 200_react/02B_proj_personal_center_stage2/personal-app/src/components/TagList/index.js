import React, {useState} from 'react';
import { Tag, Input } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import styles from './index.module.less';
import SkeletonInput from 'antd/lib/skeleton/Input';

// 传参时使用解构赋值，传入一个object，得到object的属性tags
const TagList = ({ tags }) => {
    // 状态：是否展示输入框
    const [inputVisible, setInputVisible] = useState(true); 
    // 状态：输入框的输入内容
    const [inputValue, setInputValue] = useState('');
    // 状态：通过点击页面的“+”及输入，新增的Tag
    const [newTags, setNewTags] = useState([]); 

    // 回调函数：在显式输入框、还是显式"+"之间切换
    const showInput = () => {
        setInputVisible(true);
    }
    // 回调函数：在输入框中输入时、同步更新状态和界面
    const handleInputChange = (e) => {
        setInputValue(e.target.value); // 更新状态inputValue，重新渲染，更新input框的值
    }
    // 回调函数：添加tag
    const handleInputConfirm = (e) => {
        let tempsTags = [...newTags];
        if (
            // inputValue非空
            inputValue && 
            // 去重：用解构赋值和map函数，生成一个只包含label的数组，再调用数组的includes方法判断是否包含inputValue
            !tags.concat(tempsTags).map(({label}) => label).includes(inputValue) 
        ) {
            tempsTags = [...tempsTags, {key: `new-${tempsTags.length}`, label:inputValue}]
        }
        setNewTags(tempsTags);   // 更新newTags列表，加入新的tag
        setInputValue('');       // 清空输入框
        setInputVisible(false);  // 数据框隐藏、改为显示”+“按钮
    }

    // 组件渲染
    return (
        <div className={styles.tags}>
            <div className={styles.tagsTitle}>标签</div>
            {(tags || []).concat(newTags).map((item) => (
                <Tag key={item.key}>{item.label}</Tag>
            ))}
            {
                // 输入框：在inputVisible===true时才展现
                inputVisible && 
                <Input 
                    size="small"
                    style={{width:78}} 
                    value={inputValue} 
                    onChange={handleInputChange}  
                    onBlur={handleInputConfirm} 
                    onPressEnter={handleInputConfirm} 
                />
            }
            {
                // "+"按钮：在inputVisible===false时才展现
                !inputVisible && 
                <Tag onClick={showInput} style={{ borderStyle : 'dashed'}}>
                    <PlusOutlined/>
               </Tag>
            }
        </div>
    )
} 

export default TagList;