import React, { useState } from 'react';
import { Link } from 'react-router-dom';            // to属性可以传入object
import { Tabs, Form, Checkbox, Row } from 'antd';   // 参考：https://ant.design/components/tabs-cn/
import { 
    UserOutlined, LockTwoTone, MobileTwoTone, 
    MailTwoTone, AlipayCircleOutlined, TaobaoCircleOutlined,
    WeiboCircleOutlined
} from '@ant-design/icons'; // 为啥要用@
import { useDispatch } from 'redux-react-hook';
import InputItem from '../../components/InputItem';
import SubmitButton from '../../components/SubmitButton';
import { login } from '../../actions/account';
import styles from './index.module.less';  

const { TabPane } = Tabs;   // 解构赋值，将Tabs.TabPane赋给变量TabPane

const Login = () => {
    const dispatch = useDispatch();
    const [autoLogin, setAutoLogin] = useState(true);
    const [form] = Form.useForm(); // Ant Design Lib的Form组件的自定义钩子（用use开头的函数表示钩子）
    const handleFinish = (values) => {
        console.log(values);
        dispatch(login(values));
    };
    return (
        // 1. Tab控件的代码参考来自：https://ant.design/components/tabs-cn/
        // 2. {}里面用驼峰式、是JS的规范; 内容用来在.less文件中定位样式
        // 3. <UserOutlined/>是来自于and-design图标库组件标签
        
        // todo: 查找InputItem和UserOutlined的使用方法

        <div className={styles.loginContainer}>
            <div className={styles.login}>
                <Form 
                    form={form} 
                    onFinish={handleFinish} 
                >
                    <Tabs defaultActiveKey="1"> 
                        <TabPane tab="账号密码登录" key="1">
                            <InputItem 
                                name="username" 
                                prefix = {<UserOutlined style={{ color : '#1890ff' }} />}
                                placeholder="用户名" 
                                size="large" 
                                rules = {[{
                                    required: true, 
                                    message: "请输入用户名"
                                }]}
                            />
                            <InputItem 
                                name="password" 
                                type="password" 
                                prefix = {<LockTwoTone style={{ color : '#1890ff' }} />}
                                placeholder="密码" 
                                size="large" 
                                rules = {[{
                                    required: true, 
                                    message: "请输入密码"
                                }]}
                            />
                            <SubmitButton>登录</SubmitButton>
                        </TabPane> 
                        <TabPane tab="手机验证码登录" key="2"> 
                            <InputItem 
                                name="mobile" 
                                prefix = {<MobileTwoTone/>}
                                placeholder="手机号" 
                                size="large" 
                                rules = {[{
                                    required: true, 
                                    message: "请输入手机号"
                                }]}
                            />
                            <InputItem 
                                name="captcha"  
                                prefix = {<MailTwoTone style={{ color : '#1890ff' }} />}
                                placeholder="验证码" 
                                size="large" 
                                rules = {[{
                                    required: true, 
                                    message: "请输入验证码"
                                }]}
                            />
                            <SubmitButton>登录</SubmitButton>
                        </TabPane>   
                    </Tabs>
                    <Row justify="space-between">
                        <Checkbox
                            checked={autoLogin} 
                            onChange={(e) => setAutoLogin(e.target.checked)}
                        >自动登录</Checkbox>
                        <a href="#!">忘记密码</a>
                    </Row>
                    <div className={styles.other}>
                            其他登录方式
                            <AlipayCircleOutlined className={styles.icon} />
                            <TaobaoCircleOutlined className={styles.icon} />
                            <WeiboCircleOutlined  className={styles.icon} />
                            <Link className={styles.register} to="/register">注册账户</Link>
                    </div>
                </Form> 
            </div>
         </div>
    );
};

export default Login;
