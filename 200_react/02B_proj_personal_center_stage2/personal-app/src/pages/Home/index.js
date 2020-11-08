import React, {useState} from 'react';
import { Link } from 'react-router-dom';
import { Row, Col, Card, Divider, Avatar} from 'antd';
import { ContactsOutlined, ClusterOutlined, HomeOutlined } from '@ant-design/icons'
import Articles from '../../components/Articles';
import Projects from '../../components/Projects';
import Applications from '../../components/Applications';
import TagList from '../../components/TagList';
import styles from './index.module.less';

// 后端没有开发好，因此先引入预先构造好的假数据文件，用于调试
import {currentUser, fakeList} from './data.js';
const articleList = fakeList(10);
const applicationList = fakeList(10);
const projectList = fakeList(10);

const operationTabList = [{
    key: 'articles',
    tab: <span>文章<span>(10)</span></span>
},{
    key: 'applications',
    tab: <span>应用<span>(12)</span></span>
},{
    key: 'projects',
    tab: <span>项目<span>(18)</span></span>
}];

const renderChildrenByTabKey = (tabKey) => {
    switch (tabKey) {
        case 'applications':
            return <Applications list={applicationList} />;
        case 'projects': 
            return <Projects list={projectList} />;
        case 'articles': 
        default:
            return <Articles list={articleList} />;
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
    const [tabKey, setTabKey] = useState('projects');
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
                        <Divider dashed />
                        <TagList tags={currentUser.tags} />
                        <Divider dashed />
                        <div className={styles.team}>
                            <div className={styles.teamTitle}>团队</div>
                            <Row gutter={36}>
                                {
                                    // 图标：大屏(lg)1行1个，超大屏(xl)1行2个
                                    currentUser.notice && 
                                    currentUser.notice.map((item) => (
                                        <Col key={item.id} lg={24} xl={12}>
                                            <Link to="/setting">
                                                <Avatar size="small" src={item.logo}/>
                                                {item.member}
                                            </Link>
                                        </Col>
                                    ))
                                }
                            </Row>
                        </div>
                    </Card> 
                </Col>
                <Col lg={17} md={24}>
                    <Card 
                        bordered={false} 
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
