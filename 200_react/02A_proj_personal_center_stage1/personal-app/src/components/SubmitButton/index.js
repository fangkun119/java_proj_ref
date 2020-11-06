import React from 'react';
import { Form, Button } from 'antd';
import styles from './index.module.less';

const SubmitButton = (props) => {
    // children是props自带的属性，不需显式地赋值就能传进来
    const { children } = props;
    return (
        <Form.Item>
            <Button className={styles.submit} type="primary" size="large" htmlType="submit">
                {children}
            </Button>
        </Form.Item>
    );
};

export default SubmitButton;