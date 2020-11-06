
import React from 'react';
import { Route, Switch } from 'react-router-dom';
import Home from '../pages/Home';
import Login from '../pages/Login';
import Register from '../pages/Register';

const Router = () => (
    // 定义路由，为不同的path指定不同的组件，匹配顺序是从上到下匹配，按照第一个能匹配的路由项来路由（如果去掉exact，部分匹配也会被路由）
    <Switch>
        <Route exact path="/" component={Home} />
        <Route exact path="/login" component={Login} />
        <Route exact path="/register" component={Register} />
    </Switch>
);

export default Router;