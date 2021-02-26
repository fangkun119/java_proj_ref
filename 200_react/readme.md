<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [项目1：todo-list](#%E9%A1%B9%E7%9B%AE1todo-list)
- [项目2：personal-center](#%E9%A1%B9%E7%9B%AE2personal-center)
- [项目3：weibo-client](#%E9%A1%B9%E7%9B%AE3weibo-client)
- [常用网站及参考](#%E5%B8%B8%E7%94%A8%E7%BD%91%E7%AB%99%E5%8F%8A%E5%8F%82%E8%80%83)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## 项目1：todo-list

项目介绍：支持增删查改的todo-list应用

项目代码：[01_proj_todo_list/todo-list/](01_proj_todo_list/todo-list/)

项目文档：[01_proj_todo_list/project_note_01_todolistapp.md](01_proj_todo_list/project_note_01_todolistapp.md)

技术笔记：[01_proj_todo_list/react_note_01.md](01_proj_todo_list/react_note_01.md)

> 1. Angular, React, VUE技术比较
> 
> 2. 脚手架`create-react-app`
> 
> 3. 用于快速学习和上手的项目：代码及注释链接
> 
> 4. React Hook：useState hook、useEffect hook
> 
> 5. 屏蔽表单提交时的默认操作；阻止点击实现向上传递
> 
> 6. 矢量图icon的使用
> 
> 7. 为列表元素指定key的作用，根据index来删除列表元素
> 
> 8. Local Storage的使用
> 
> 9. 解构赋值；ES6 Spread语法；使用style属性来定义样式
> 
> 10. React改进可复用性的历史：Mixin → HOC → Render Props → Hooks
> 
> 11. CSS盒模型布局，CSS弹性布局

项目截图：

<div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/react_proj_todo_list.jpg" width="600" /></div>


## 项目2：personal-center

项目介绍：个人中心展示页面，包括头像/标签/团队/文章/应用/项目等

项目代码：[02B_proj_personal_center_stage2/personal-app/](02B_proj_personal_center_stage2/personal-app/)

项目文档：

(1) 阶段1：[02A_proj_personal_center_stage1/proj_02_personal_center_01.md](02A_proj_personal_center_stage1/proj_02_personal_center_01.md)

(2) 阶段2：[02B_proj_personal_center_stage2/proj_02_personal_center_02.md](02B_proj_personal_center_stage2/proj_02_personal_center_02.md)

技术笔记：

(1) 阶段1：[02A_proj_personal_center_stage1/react_note_02A.md](02A_proj_personal_center_stage1/react_note_02A.md)

>1. 使用React Router
>
>2. HTTP
>
>3. UI样式库的使用：以Ant Design为例
>
>4. CSS预处理器：SCSS、LESS等
>
>5. 样式按需加载：使用babel-plugin-import
>
>6. 使用CSS modules避免全局样式污染
>
>7. Ant Design样式库的栅格系统
>
>8. React effect Hook
>
>9. 在大组件重新渲染、阻止未发生变化的小组件被联动渲染
>
>10. JS计时器实现

(2) 阶段2：[02B_proj_personal_center_stage2/react_note_02B.md](02B_proj_personal_center_stage2/react_note_02B.md)

>1. 使用lazy import实现组件按需加载
>
>2. 使用`<Suspense>`来处理按需加载等待时的界面展示问题
>
>3. 栅格系统屏幕自适应
>
>4. 文本截断的实现方式、数组去重，时间处理库`momentjs`
>
>5. HTML Header中`<meta>`标签与用途
>
>6. Ajax封装
>
>7. Redux单向数据链路
>
>    (1) 链路搭建：[02B_proj_personal_center_stage2/react_note_02B.md#09-使用redux在视图层触发action获取后端数据](02B_proj_personal_center_stage2/react_note_02B.md#09-使用redux在视图层触发action获取后端数据)
>
>    (2) 完整例子：[02B_proj_personal_center_stage2/react_note_02B.md#12-redux%E9%93%BE%E8%B7%AF%E5%AE%8C%E6%95%B4%E4%BE%8B%E5%AD%90](02B_proj_personal_center_stage2/react_note_02B.md#12-redux%E9%93%BE%E8%B7%AF%E5%AE%8C%E6%95%B4%E4%BE%8B%E5%AD%90)
>
>8. 使用拦截器
>
>9. SSO单点登录的实现
>
>10. Token过期或无效时引导用户到登录页面
>
>11. Immutable

项目截图：

<div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/react_proj2_personal_center.jpg" width="800" /></div>

## 项目3：weibo-client

项目介绍：微博的web客户端，支持发微博/评论/滚屏功能，使用微博开放API作为后台

项目代码：[03_proj_weibo_client/weibo-app/](03_proj_weibo_client/weibo-app/)

项目笔记：[03_proj_weibo_client/proj_03_weibo_client.md](03_proj_weibo_client/proj_03_weibo_client.md)

技术笔记：03_proj_weibo_client/react_note_03.md  (to do)

项目截图：

<div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/200_react/react_proj_weibo_app_2.jpg" width="800" /></div>

## 常用网站及参考

> * JavaScript：[http://javascript.info/](http://javascript.info/)
> * 解构赋值：[reference/destructing_assignment.pdf](reference/destructing_assignment.pdf)
> * CSS选择器优先级：[reference/css_selector.pdf](reference/css_selector.pdf)
> * CSS选择器（含实例操作）：[https://www.runoob.com/cssref/css-selectors.html](https://www.runoob.com/cssref/css-selectors.html)
> * CSS盒模型（含实例操作）：[https://www.runoob.com/css/css-boxmodel.html](https://www.runoob.com/css/css-boxmodel.html)
> * CSS弹性布局模型（含实例操作）：[https://www.runoob.com/css3/css3-flexbox.html](https://www.runoob.com/css3/css3-flexbox.html)
> * 查看浏览器兼容性：[https://caniuse.com/](https://caniuse.com/)
> * 查看一个包的体积：[http://bundlephobia.com](http://bundlephobia.com)
> * React Router：[https://reactrouter.com/web/example/nesting](https://reactrouter.com/web/example/nesting)

