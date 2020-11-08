# 02 `个人中心`项目（下 ）

## 10. 在`react router`中用`lazy import`替换`import`

> 代码：
>  * [git commit](https://github.com/fangkun119/java_proj_ref/commit/a4866ceaf8db0a3b30748181eb2b18dbc7da39e4)
>  * [src/router/index.js](https://github.com/fangkun119/java_proj_ref/blob/a4866ceaf8db0a3b30748181eb2b18dbc7da39e4/200_react/02B_proj_personal_center_stage2/personal-app/src/router/index.js)
>  * [src/router/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/a4866ceaf8db0a3b30748181eb2b18dbc7da39e4/200_react/02B_proj_personal_center_stage2/personal-app/src/router/index.module.less)
> 
> * `普通 import`会阻塞页面直到所有路由目标页面都加载完毕
> * `lazy import`可以按需加载，配合`<Suspense>`标签可以展示加载时的loading提示

## 11. `Home`页面

### 11.1 页面布局及屏幕尺寸排版自适应

> 代码：
> 
> * [git commit](https://github.com/fangkun119/java_proj_ref/commit/b5e1191e18bc3d995e6b2cb2069454112659334f)
> * [src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/b5e1191e18bc3d995e6b2cb2069454112659334f/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.js)
> * [src/pages/Home/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/b5e1191e18bc3d995e6b2cb2069454112659334f/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.module.less) 
> 
> 代码中栅格的属性：`lg`-表示大屏时所占的栅格；`md`-表示中小屏时所占的栅格</br>
> * 关于`antd`响应式栅格的熔断点（尺寸切换阈值），见文档[https://ant.design/components/grid-cn/](https://ant.design/components/grid-cn/) ，他们拓展自[`boostrap`的规则](https://getbootstrap.com/docs/4.0/layout/overview/#responsive-breakpoints)

> 平板设备布局：
> 
> * 在横屏状态下
> 	* 大多可以直接使用桌面布局，或只进行一些细微的调整
> 	* 还有常用的左右双栏布局是非常适合横屏模式下的平板
> * 竖屏模式下，通常有两种常用解决方案：
> 	* 一是设计为较窄的双栏模式；
> 	* 二是将桌面端的布局由左右调整为上下，实现宽屏内容的竖向填充。 

### 11.2 右侧`<Card>`内的`<Tab>`导航栏

> 代码（commit 1）：
> 
> * [git commit](https://github.com/fangkun119/java_proj_ref/commit/10345ee1e50b22e8c8dd2547635f6ac72cb127d7) 
> * [src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/10345ee1e50b22e8c8dd2547635f6ac72cb127d7/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.js)

> 代码（commit 2）：
> 
> * [git commit](https://github.com/fangkun119/java_proj_ref/commit/a779bb0833d67a8685cd4c62a5684c0982a4bc50) 
> * [src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/a779bb0833d67a8685cd4c62a5684c0982a4bc50/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.js)
> * [src/components/Projects/index.js](https://github.com/fangkun119/java_proj_ref/blob/a779bb0833d67a8685cd4c62a5684c0982a4bc50/200_react/02B_proj_personal_center_stage2/personal-app/src/components/Projects/index.js)
> * [src/components/Articles/index.js](https://github.com/fangkun119/java_proj_ref/blob/a779bb0833d67a8685cd4c62a5684c0982a4bc50/200_react/02B_proj_personal_center_stage2/personal-app/src/components/Articles/index.js)

### 11.3 左侧`<Card>`：用户信息展示、用户team标签、用户个人标签

#### 11.3.1 用户信息展示

> 代码：
> 
> * [git commit](https://github.com/fangkun119/java_proj_ref/commit/8b70dcbd32e5828b42bb9cb89bb8e0b15c23e073) 
> * [src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/8b70dcbd32e5828b42bb9cb89bb8e0b15c23e073/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.js) 
> * [src/pages/Home/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/8b70dcbd32e5828b42bb9cb89bb8e0b15c23e073/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.module.less) 

#### 11.3.2 用户所属team的标签展示

> 代码：
> 
> * [git commit](https://github.com/fangkun119/java_proj_ref/commit/f632ffb3bdbc621d28c391ef9016a3c57a180561) 
> * [src/pages/Home/index.js](https://github.com/fangkun119/java_proj_ref/blob/f632ffb3bdbc621d28c391ef9016a3c57a180561/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.js) 
> * [src/pages/Home/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/f632ffb3bdbc621d28c391ef9016a3c57a180561/200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.module.less)

#### 11.3.3 用户个人标签：显示标签

> 代码：
> 
> * [git commit](https://github.com/fangkun119/java_proj_ref/commit/f3f329a80e69d871e7dfe78e5368d856bdc9dd64)
> * [src/components/TagList/index.js](https://github.com/fangkun119/java_proj_ref/blob/f3f329a80e69d871e7dfe78e5368d856bdc9dd64/200_react/02B_proj_personal_center_stage2/personal-app/src/components/TagList/index.js)
> * [src/components/TagList/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/f3f329a80e69d871e7dfe78e5368d856bdc9dd64/200_react/02B_proj_personal_center_stage2/personal-app/src/components/TagList/index.module.less)

#### 11.3.4 用户个人标签：增加新标签

> 代码：
> 
> * [git commit](https://github.com/fangkun119/java_proj_ref/commit/142a7363cf6a4a0350aef1e3624beccfcd2e5a0d#diff-d662d58efdfce16c06bda8ac500bf86f2384fd6a7b33a7c97dfb69b1453720fa) 
> * [src/components/TagList/index.js](https://github.com/fangkun119/java_proj_ref/blob/142a7363cf6a4a0350aef1e3624beccfcd2e5a0d/200_react/02B_proj_personal_center_stage2/personal-app/src/components/TagList/index.js)

### 11.4 右侧`Card`：文章选项卡、应用选项卡、项目选项卡

#### 11.4.1 文章选项卡

> 代码
> 
> * [git commit](https://github.com/fangkun119/java_proj_ref/commit/adeb607a4d02ffe0c9647c96fa402266713872d8#diff-40a13f60b39d0c791105efc4e0187ba1bb14f36074708cba318015c5a3c78311) 
> 
> Components updated
> 
> * [src/components/Articles/index.js](https://github.com/fangkun119/java_proj_ref/blob/adeb607a4d02ffe0c9647c96fa402266713872d8/200_react/02B_proj_personal_center_stage2/personal-app/src/components/Articles/index.js)
> * [src/components/Articles/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/adeb607a4d02ffe0c9647c96fa402266713872d8/200_react/02B_proj_personal_center_stage2/personal-app/src/components/Articles/index.module.less)
> * [src/components/ArticalListContent/index.js](https://github.com/fangkun119/java_proj_ref/blob/adeb607a4d02ffe0c9647c96fa402266713872d8/200_react/02B_proj_personal_center_stage2/personal-app/src/components/ArticalListContent/index.js)
> * [src/components/ArticalListContent/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/adeb607a4d02ffe0c9647c96fa402266713872d8/200_react/02B_proj_personal_center_stage2/personal-app/src/components/ArticalListContent/index.module.less)
> 
> Bug Fix
> 
> * `200_react/02B_proj_personal_center_stage2/personal-app/src/components/TagList/index.js`
> * `200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.js`
> * `200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.module.less`
> * `200_react/02B_proj_personal_center_stage2/personal-app/src/pages/Home/index.module.less`

#### 11.4.2 应用选项卡

> 代码
> 
> * [git commit](https://github.com/fangkun119/java_proj_ref/commit/a5d04569badc5995aa66c90ef3d428bb29e25744#diff-40a13f60b39d0c791105efc4e0187ba1bb14f36074708cba318015c5a3c78311) 
> * [src/components/Applications/index.js](https://github.com/fangkun119/java_proj_ref/blob/a5d04569badc5995aa66c90ef3d428bb29e25744/200_react/02B_proj_personal_center_stage2/personal-app/src/components/Applications/index.js)
> * [src/components/Applications/index.module.less](https://github.com/fangkun119/java_proj_ref/blob/a5d04569badc5995aa66c90ef3d428bb29e25744/200_react/02B_proj_personal_center_stage2/personal-app/src/components/Applications/index.module.less)

#### 11.4.3 项目选项卡

> 代码
> 
> * [git commit](https://github.com/fangkun119/java_proj_ref/commit/38976c4898c992eeb0ce1605f36d8dbc0936f537#diff-40a13f60b39d0c791105efc4e0187ba1bb14f36074708cba318015c5a3c78311) 
> * [src/components/Projects/index.js](https://github.com/fangkun119/java_proj_ref/blob/38976c4898c992eeb0ce1605f36d8dbc0936f537/200_react/02B_proj_personal_center_stage2/personal-app/src/components/Projects/index.js)














