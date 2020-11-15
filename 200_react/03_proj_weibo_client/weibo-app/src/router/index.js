import { Route, Switch } from 'react-router-dom';
import React, { lazy, Suspense } from 'react';

const Home = lazy(() => import('../pages/Home'));

const Router = () => (
    <Suspense fallback="loading...">
        <Switch>
            <Route exact path="/" component={Home} />
        </Switch>
    </Suspense>
);

export default Router;
