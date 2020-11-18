import { Route, Switch } from 'react-router-dom';
import React, { lazy, Suspense } from 'react';

const Home  = lazy(() => import('../pages/Home'));
const Login = lazy(() => import('../pages/Login'));
const New   = lazy(() => import('../pages/New'));

const Router = () => (
    <Suspense fallback="loading...">
        <Switch>
            <Route exact path="/" component={Home} />
            <Route exact path="/login" component={Login} />
            <Route exact path="/new" component={New} />
        </Switch>
    </Suspense>
);

export default Router;
