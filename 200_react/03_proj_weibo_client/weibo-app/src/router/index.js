import { Route, Switch } from 'react-router-dom';
import React, { lazy, Suspense } from 'react';

const Home  = lazy(() => import('../pages/Home'));
const Login = lazy(() => import('../pages/Login'));
const New   = lazy(() => import('../pages/New'));
const Details = lazy(() => import('../pages/Details'));

// 发评论（/comments/:id，id是帖子的id）与发微博（/new)可以共用同一个<New>组件
const Router = () => (
    <Suspense fallback="loading...">
        <Switch>
            <Route exact path="/" component={Home} />
            <Route exact path="/login" component={Login} />
            <Route exact path="/new" component={New} />
            <Route exact path="/comments/:id" component={New} />
            <Route exact path="/details/:id" component={Details} />
        </Switch>
    </Suspense>
);

export default Router;
