import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import InputItem from '../../components/InputItem';
import SubmitButton from '../../components/SubmitButton';
import styles from './index.module.less';
import { Form, Popover, Progress, Row, Col, Select } from 'antd';

// 注册页面
const Register = () => {
    // antd Form 提供的钩子，用来拿到表单中的数据
    const [form] = Form.useForm(); 
    // 控制“popOver提示气泡”是否展示的状态钩子
    const [visible, setVisible] = useState(false);
    // 状态钩子，Register级别，给子组件InputItem在checkPassword时有机会从父组件Register开始全部重新渲染
    const [popOver, setPopOver] = useState(false);
    // 状态钩子，用于选择手机号码的区号
    const [prefix, setPrefix] = useState(86);

    // 表单提交时的回调函数
    const handleFinish = (values) => {
        console.log(values);
    };

    // 两个用来给自定义validator做密码验证的函数，
    // 参数1：当前表单项规则，'_'表示不关心其取值
    // 参数2：表单项的值
    // 详细文档：https://ant-design.gitee.io/components/form-cn/#Rule 
    const checkConfirm = (_, value) => {
        const promise = Promise; // new一个Promise
        if (value && value !== form.getFieldValue('password')) { //form.getFieldValue是antd提供的API
            return promise.reject('两次输入的密码不匹配');
        }
        return promise.resolve();
    }
    const checkPassword = (_, value) => {
        const promise = Promise; 
        // Password框：没有value、value为空串时
        if (!value) {
            setVisible(!!value);  //设置为false，阻止popOver显式
            return promise.reject('请输入密码'); 
        }
        // 如果password为非空
        if (!visible) {
            setVisible(true); //显式popOver
        }
        // 改变一下状态触发重新渲染（为了更新进度条）
        // popover定义在<Register>组件内的，是<Register>组件的state，当popover被更新之后，触发<Register>以及其下所有子组件(包括<Popover>和<Progress>)的重新渲染
        setPopOver(!popOver); 
        // 如果confirm也非空，触发confirm框的重新校验
        if (value && form.getFieldValue('confirm')) {
            form.validateFields(['confirm']); 
        }
        return promise.resolve();
    }

    // 密码强度样式后缀
    const getPasswordStatus = () => {
        const value = form.getFieldValue('password');
        if (value && value.length > 9) {
            return 'ok';
        } else if (value && value.length > 5) {
            return 'pass';
        } else {
            return 'poor';
        }
    }
    
    // 用颜色会发生变化的进度条来提示密码强度
    const renderPasswordProgress = () => {
        const passwordProgressMap = {ok : 'success', pass : 'normal', poor : 'exception'};
        const value = form.getFieldValue('password');
        const passwordStatus = getPasswordStatus();
        return value && value.length && (
            // Progress控件的文档： https://ant-design.gitee.io/components/progress-cn/  
            <div className={styles[`progress-${passwordStatus}`]}> 
                <Progress  
                    className={styles.progress}  
                    status={passwordProgressMap[passwordStatus]} 
                    type='line' strokeWidth={6} showInfo={false}
                    percent={value.length * 10 > 100 ? 100 : value.length * 10}
                />
            </div>
        )
    }

    // 输入控件包裹在<Form>中，同时给Form设置状态钩子form，以及提交时的回调函数handleFinish
    // <Popover>用于其他组件attach一个气球提示, placement是位置，visible用来控制该组件何时可见   
    const passwordStatusMap = {     // 用于显式密码强弱的文字提醒
        ok : (<div className={styles.success}>强度：强</div>),
        pass : (<div className={styles.warning}>强度：中</div>),
        poor : (<div className={styles.error}>强度：太短</div>),
    }
    return (
        <div className={styles.registerContainer}>
            <div className={styles.register}>
                <Form 
                    form={form} 
                    onFinish={handleFinish} 
                > 
                    <InputItem
                        name="mail" placeholder="邮箱" size="large" rules={[
                         {required: true, message: "请输入邮箱"},
                         {type:'email', message: "请填写正确的邮箱格式"}
                        ]}
                    />
                    <Popover placement='right' visible={visible} overlayStyle={{width:240}} 
                        content={visible && 
                            <div>
                                {passwordStatusMap[getPasswordStatus()]}
                                {renderPasswordProgress()}
                                <div>请至少输入6个字符，不要使用容易猜到的密码</div>
                            </div>
                        }
                    >
                        <InputItem
                            name="password" type="password" placeholder="至少六位密码、区分大小写" size="large" rules={[
                            {validator: checkPassword}
                            ]}
                        />
                    </Popover>
                    <InputItem
                        name="confirm" type="password" placeholder="确认密码" size="large" 
                        rules={[
                            {required: true, message: '请确认密码'},
                            {validator: checkConfirm}
                        ]}
                    />
                    <Row>
                        <Col span={6}>
                            <Select
                                size='large' 
                                value={prefix} 
                                onChange={(value)=>setPrefix(value)} 
                                style={{width:'100%'}}
                            >
                                <Select.Option value="86">+86</Select.Option>
                                <Select.Option value="87">+87</Select.Option>
                            </Select>
                        </Col>
                        <Col span={18}>
                            <InputItem
                                name="mobile" placeholder="手机号" size="large" 
                                rules={[
                                    {required: true, message: '请输入手机号'},
                                    {pattern: /^\d{11}/, message: '手机号格式错误'}
                                ]}
                            />
                        </Col>
                    </Row>
                    <InputItem 
                        name="captcha" 
                        size="large" 
                        rules={[
                            {required: true, message: "请输入验证码"}
                        ]} 
                        placeholder="验证码"
                    />
                    <Row justify="space-between" align="middle">
                        <Col span={8}>
                            <SubmitButton>注册</SubmitButton>
                        </Col>
                        <Col span={16}>
                            <Link className={styles.login} to="/login">使用已有账户登录</Link>
                        </Col>
                    </Row>
                </Form>
            </div>
        </div>
    );
};

export default Register;