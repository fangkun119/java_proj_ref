import React from 'react';
import { Row, Col, Card } from 'antd';
import styles from './index.module.less';

const Home = () => {
    // <Row><Col>：用'antd'栅格来排版
    // * <Row gutter={24}>: <Col>之间间隔是24px
    // * <Col lg={7} md={24}>: 没有用span={7}，是为了屏幕自适应，大屏是占7个栅格，中屏小屏时把24个栅格都占满
    // * <Card style={{marginBottom:24}}>: 让中小屏时、上下两个Col之间也能有24px的间隔
    return (
        <div className={styles.container}>
            <Row gutter={24}>
                <Col lg={7} md={24}>
                    <Card bordered={false} style={{marginBottom:24}}>
                        col1
                    </Card>
                </Col>
                <Col lg={17} md={24}>
                    <Card boarded={false}>col2</Card>
                </Col>
            </Row>
        </div>  
    )
};

export default Home;