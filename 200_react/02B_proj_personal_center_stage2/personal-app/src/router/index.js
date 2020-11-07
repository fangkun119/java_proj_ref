
import React, {lazy, Suspense} from 'react';
import { Route, Switch } from 'react-router-dom';
import { Spin } from 'antd';
import styles from './index.module.less';

// 用lazy(()=>import(...)来替换直接import，可以避免页面打开后要阻塞直到所有import都完成
//      import Home from '../pages/Home';
//      import Login from '../pages/Login';
//      import Register from '../pages/Register';
const Home     = lazy(() => import('../pages/Home'));
const Login    = lazy(() => import('../pages/Login'));
const Register = lazy(() => import('../pages/Register'));

const Router = () => (
    // 定义路由，为不同的path指定不同的组件，匹配顺序是从上到下匹配，按照第一个能匹配的路由项来路由（如果去掉exact，部分匹配也会被路由）
    // 在lazy import完成之前，显式loading界面，"Chrome Dev Tool"的Network->Preset->Slow3G中可以模拟网络慢的场景，看到loading页面
    <Suspense 
        fallback={
            <div className={styles.spinWrap}>
                <Spin size="large" />
            </div>    
        }
    >
        <Switch>
            <Route exact path="/" component={Home} />
            <Route exact path="/login" component={Login} />
            <Route exact path="/register" component={Register} />
        </Switch>
    </Suspense>
);

export default Router;