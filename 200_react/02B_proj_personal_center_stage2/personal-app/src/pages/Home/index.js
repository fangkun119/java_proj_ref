import React, {useEffect, useState} from 'react';
import { Link } from 'react-router-dom';
import { Row, Col, Card, Divider, Avatar} from 'antd';
import { useDispatch } from 'redux-react-hook';
import { ContactsOutlined, ClusterOutlined, HomeOutlined } from '@ant-design/icons'
import Articles from '../../components/Articles';
import Projects from '../../components/Projects';
import Applications from '../../components/Applications';
import TagList from '../../components/TagList';
import {currentUser, fakeList} from './data.js'; // 后端没有开发好时，先引入预先构造好的假数据文件，用于前端调试
import { getUserProfile } from '../../actions/profile';
import styles from './index.module.less';

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
    const dispatch = useDispatch();
    const [tabKey, setTabKey] = useState('projects');
    const onTabChange = (key) => {
        setTabKey(key);
    }
    
    // 文档: https://zh-hans.reactjs.org/docs/hooks-effect.html
    // * 如果想执行只运行一次的 effect（仅在组件挂载和卸载时执行），可以传递一个空数组（[]）作为第二个参数。这就告诉 React 你的 effect 不依赖于 props 或 state 中的任何值，所以它永远都不需要重复执行。这并不属于特殊情况 —— 它依然遵循依赖数组的工作方式。
    // * 如果传入一个空数组（[]），effect 内部的 props 和 state 就会一直拥有其初始值。尽管传入 [] 作为第二个参数更接近大家更熟悉的 componentDidMount 和 componentWillUnmount 思维模式，但我们有更好的方式来避免过于频繁的重复调用 effect。除此之外，请记得 React 会等待浏览器完成画面渲染之后才会延迟调用 useEffect，因此会使得额外操作很方便。
    // * 如果传入非空的数组（[abc])，那么仅在 abc 更改时更新
    useEffect(() => {
        dispatch(getUserProfile());
    }, [dispatch]); // 把引入的变量dispatch放到依赖里，出于规范考虑
    
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
