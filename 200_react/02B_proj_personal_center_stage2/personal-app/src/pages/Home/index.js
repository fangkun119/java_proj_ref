import React, {useState} from 'react';
import { Row, Col, Card } from 'antd';
import { ContactsOutlined, ClusterOutlined, HomeOutlined } from '@ant-design/icons'
import Articles from '../../components/Articles';
import Projects from '../../components/Projects';
import Applications from '../../components/Applications';
import styles from './index.module.less';

// 后端没有开发好，因此先引入预先构造好的假数据文件，用于调试
import {currentUser, fakeList} from './data.js';

const operationTabList = [{
    key: 'articles',
    tab: <Articles/>
},{
    key: 'applications',
    tab: <Applications/>
},{
    key: 'projects',
    tab: <Projects/>
}];

const renderChildrenByTabKey = (tabKey) => {
    switch (tabKey) {
        case 'articles': 
            return <Articles/>
        case 'applications':
            return <Applications/>
        case 'projects': 
            return <Projects/>
        default:
            return <p></p>
    }
};

// () => (<JSX_ELEM/>) 相当于 () => {return (<JSX_ELEM/>)}
// <p><图标/>文字</p>
const renderUserInfo = () => (
    <div className={styles.detail} >
        <p>
            <ContactsOutlined className={styles.userInfoIcon} />
            {currentUser.title} 
        </p>
        <p>
            <ClusterOutlined className={styles.userInfoIcon} />
            {currentUser.group}
        </p>
        <p>
            <HomeOutlined className={styles.userInfoIcon} /> 
            {(currentUser.geographic || { province: {label: ''}}).province.label}
            {(currentUser.geographic || { city: {label: ''}}).city.label}
        </p>  
    </div>
);

const Home = () => {
    const [tabKey, setTabKey] = useState('articles');
    const onTabChange = (key) => {
        setTabKey(key);
    }

    // <Row><Col>：用'antd'栅格来排版
    // * <Row gutter={24}>: <Col>之间间隔是24px
    // * <Col lg={7} md={24}>: 没有用span={7}，是为了屏幕自适应，大屏是占7个栅格，中屏小屏时把24个栅格都占满
    // * <Card style={{marginBottom:24}}>: 让中小屏时、上下两个Col之间也能有24px的间隔
    return (
        <div className={styles.container}>
            <Row gutter={24}>
                <Col lg={7} md={24}>
                    <Card bordered={false} style={{marginBottom:24}}>
                        <div className={styles.avatarHolder}>
                            <img alt="" src={currentUser.avatar} />
                            <div className={styles.name}>{currentUser.name}</div>
                            <div>{currentUser.signature}</div>
                        </div>
                        {renderUserInfo()}
                    </Card> 
                </Col>
                <Col lg={17} md={24}>
                    <Card 
                        boarded={false} 
                        tabList={operationTabList} 
                        activeTabKey={tabKey}
                        onTabChange={onTabChange}
                    >
                        {renderChildrenByTabKey(tabKey)}
                    </Card>
                </Col>
            </Row>
        </div>  
    )
};

export default Home;