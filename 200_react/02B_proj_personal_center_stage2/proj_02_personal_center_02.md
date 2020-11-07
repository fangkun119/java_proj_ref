# 02 `个人中心`项目（下 ）

## 10. 在`react router`中用`lazy import`替换`import`

> `普通 import`会阻塞页面直到所有路由目标页面都加载完毕
> 
>  ~~~javascript
> import Home from '../pages/Home';
> import Login from '../pages/Login';
> import Register from '../pages/Register';
> ~~~
> 
> `lazy import`可以做到按需加载，配合`<Suspense>`标签还可以展示加载时的loading提示
> 
> ~~~javascript
> const Home = lazy(() => import('../pages/Home'));
> const Login = lazy(() => import('../pages/Login'));
> const Register = lazy(() => import('../pages/Register'));
> ~~~
> 
> ~~~javascript
> <Suspense fallback={<div className={styles.spinWrap}><Spin size="large" /></div>}>
>     <Switch>
>         <Route exact path="/" component={Home} />
>         <Route exact path="/login" component={Login} />
>         <Route exact path="/register" component={Register} />
>     </Switch>
> </Suspense>
> ~~~
> 
> 完整代码修改：[git commit](https://github.com/fangkun119/java_proj_ref/commit/a4866ceaf8db0a3b30748181eb2b18dbc7da39e4)
> 
>  * [src/router/index.js](https://github.com/fangkun119/java_proj_ref/blob/a4866ceaf8db0a3b30748181eb2b18dbc7da39e4/200_react/02B_proj_personal_center_stage2/personal-app/src/router/index.js)
>  * [src/router/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/a4866ceaf8db0a3b30748181eb2b18dbc7da39e4/200_react/02B_proj_personal_center_stage2/personal-app/src/router/index.module.less)

## 11. 