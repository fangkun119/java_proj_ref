import React, {useEffect, useState} from 'react';
import { Link } from 'react-router-dom';
import { Row, Col, Card, Divider, Avatar} from 'antd';
import { useDispatch, useMappedState } from 'redux-react-hook';
import { ContactsOutlined, ClusterOutlined, HomeOutlined } from '@ant-design/icons'
import Articles from '../../components/Articles';
import Projects from '../../components/Projects';
import Applications from '../../components/Applications';
import TagList from '../../components/TagList';
import { currentUser, fakeList } from './data.js'; // 后端没有开发好时，先引入预先构造好的假数据文件，用于前端调试
import { getUserProfile } from '../../actions/profile';
import styles from './index.module.less';

// 假数据、在后端开发完成之前调成前端用
const articleList = fakeList(10);
const applicationList = fakeList(10);
const projectList = fakeList(10);

// 真数据、使用全局state中的profile部分
// 后续代码使用redux的useMappedState从中提取后端返回的数据
const mapState = (state) => (state.profile); 

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
const renderUserInfo = (user) => (
    <div className={styles.detail} >
        <p>
            <ContactsOutlined className={styles.userInfoIcon} />
            {user.title} 
        </p>
        <p>
            <ClusterOutlined className={styles.userInfoIcon} />
            {user.group}
        </p>
        <p>
            <HomeOutlined className={styles.userInfoIcon} /> 
            {(user.geographic || { province: {label: ''}}).province.label}
            {(user.geographic || { city: {label: ''}}).city.label}
        </p>  
    </div>
);

const Home = () => {
    // 用来从view分发getUserProfile这个action
    const dispatch = useDispatch();
    
    // 用来从全局store/state中提取数据渲染view
    // 其中useMappedState用来订阅reducer里的状态到mapState中
    // 要使用的是rootReducer.profile.user (在reducers/profile.js中定义)
    // * 在mappedState中：制定了使用state.profile
    // * 在这里用解构赋值进一步制定使用state.profile.user
    // "user = {}"给数据一个初始值、避免后端返回数据之前发生undefined error
    const { user = {} } = useMappedState(mapState);
    console.log(user);        // 打印日志、查看后端返回的数据
    console.log(currentUser); // 对比查看前端调试用的假数据
    
    // 切换数据源
    const userToRender = currentUser; //currentUser: 调试用假数据
    // const userToRender = user;     //user: 后端返回数据

    // 页面上有三个标签页、用来控制显示那个标签页的状态
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
                            <img alt="" src={userToRender.avatar} />
                            <div className={styles.name}>{userToRender.name}</div>
                            <div>{userToRender.signature}</div>
                        </div>
                        {renderUserInfo(userToRender)}
                        <Divider dashed />
                        <TagList tags={userToRender.tags} />
                        <Divider dashed />
                        <div className={styles.team}>
                            <div className={styles.teamTitle}>团队</div>
                            <Row gutter={36}>
                                {
                                    // 图标：大屏(lg)1行1个，超大屏(xl)1行2个
                                    userToRender.notice && 
                                    userToRender.notice.map((item) => (
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
