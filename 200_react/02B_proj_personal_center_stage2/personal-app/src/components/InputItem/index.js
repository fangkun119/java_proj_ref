import React, { useEffect, useState } from 'react';
import { useDispatch } from 'redux-react-hook'; 
import { Input, Form, Button, Row, Col, message } from 'antd';
import { getCaptcha } from '../../actions/register';
import styles from './index.module.less';

//React.forwardRef: 引用传递
//  是一种通过组件向子组件自动传递 引用ref 的技术。
//  对于应用者的大多数组件来说没什么作用。但是对于有些重复使用的组件，可能有用。
//  例如某些input组件，需要控制其focus，本来是可以使用ref来控制，但是因为该input已被包裹在组件中，这时就需要使用Ref forward来透过组件获得该input的引用
//  https://www.jianshu.com/p/fac884647720
// Refs 使用场景：
// 处理焦点、文本选择或者媒体的控制，触发必要的动画，集成第三方 DOM 
const InputItem = React.forwardRef((props, ref) => {
    const {name, rules, ...rest} = props; // 解构赋值 + 数组展开语法
    // console.log(name);  // 例子： username
    // console.log(rest);  // 例子： {prefix: {…}, placeholder: "用户名", size: "large"}
    const [timing, setTiming] = useState(false);                  // 状态钩子：是否在倒计时
    const [count,  setCount ] = useState(props.countdown || 60);  // 状态钩子：倒计时秒数

    // react-redux的钩子，从Redux Store返回一个分发函数，用于分发action
    const dispatch =  useDispatch();

    const handleClickCaptcha = () => {
        message.success('成功获取验证码'); 
        dispatch(getCaptcha());  // 被分发的action写在src/actions/register.js中
        setTiming(true); 
    };

    // Effect钩子：
    // * 参数2是监听的状态变量、如果留空则useEffect会在每次重新渲染之后执行、如果传入空数组那么useEffect只会在初始化时执行一次；
    // * 参数1是状态变量发生变化时执行的操作
    useEffect(() => {
            // timeing被handleClickCaptcha置为true时，被会生成一个计时器，每一秒更新一次状态变量`count`
            let interval = 0; // 变量
            if (timing) {
                // 开启计时器，该计时器大约每秒（1000毫秒）唤醒一次并执行检查函数
                interval = window.setInterval(() => {
                    // 设置react状态(setCount)：如果传入是一个函数，函数参数就是state的旧值，返回的是stete的新值
                    setCount((preSecond) => {
                        if (preSecond <= 1) {
                            setTiming(false);              // 设置react状态(timing)，为false表示倒计时结束
                            clearInterval(interval);       // 停止每秒唤醒一次的计时器
                            return props.countdown || 60;  // 重置count为初始值
                        } else {
                            return preSecond - 1;  // 倒计时未结束，preSecond减1
                        }
                    })
                }, 1000);
            } 
            
            // 末尾的返回函数 () => clearInterval(timer) 会在组件被销毁时执行，用于清除计时器
            // * timing被倒计时结束时的setTiming设置为false时，会错过clearInterval(interval)调用
            // * 如果不用其他代码清除计时器，会造成内存泄漏
            // 下面的这个函数会在组件unmount时被执行，从而确保计时器一定被清楚
            return () => clearInterval(interval);
        }, 
        // Effect钩子要求把用到的props属性也添加到监听列表中，否则会报下面的Warning
        //      React Hook useEffect has a missing dependency: 'props.countdown'. 
        //      Either include it or remove the dependency array. 
        //      If 'setCount' needs the current value of 'props.countdown', you can also switch to useReducer 
        //      instead of useState and read 'props.countdown' in the reducer react-hooks/exhaustive-deps
        [timing, props.countdown]
    );

    if (name === 'captcha') {
        // 想让<Input>和<Button>显式在一行：(1)可以用css flex布局；（2)可以用Ant Design的栅格布局
        // * CSS Flex ：http://www.ruanyifeng.com/blog/2015/07/flex-grammar.html 
        // * antd 栅格 ：https://ant.design/components/grid-cn/ 
        // 这个例子使用Ant Design的栅格布局，ROW的总span是24，根据需要分配，Row的gutter属性是Col的间隔
        return (
            <Form.Item name={name} rules={rules}>
                <Row gutter={8}>
                    <Col span={16}><Input countdown={60} {...rest} /></Col>
                    <Col span={8}>
                        <Button 
                            className={styles.getCaptcha} 
                            size="large" 
                            onClick={handleClickCaptcha}
                            disabled={timing} 
                        >
                            {timing ? `${count}秒` : '获取验证码'}
                        </Button>
                    </Col>
                </Row>
            </Form.Item>
        )
    } else {
        return (
            <Form.Item name={name} rules={rules}>
                <Input ref={ref} {...rest} />
            </Form.Item>
        )
    }
});

export default InputItem;

