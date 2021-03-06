[TOC]

# CH02 秒杀系统：商品详情页多级缓存 (1)

## 1 业务背景

相关流程

> 商家入驻流程，商品上线流程
>
> 资料图片上传，商品发布，商品维护、商品下架

商家后台功能

> 商品发布、商品修改、商品下架

## 2 商品模块的技术难点

### (1) 表复杂度

#### (a) 表如何设计

显示内容众多、包含图片、名字、价格、规格、评价、属性、……等

一张表合适、还是多张表合适？

> * 一张表：查询方便，但是存储复杂（不同类型商品的描述属性都不一样，例如Iphone有内存属性，衣服有面料属性）
> * 多张表：业务清晰维护方便，但是查询非常复杂

分析

> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/upload/sysdesign_ecom_ch02_data_analysis.png" width="400" /></div>

#### (b) 表设计演变

> 版本1：商品 + 类别
>
> 版本2：商品 + 类别 + 品牌
>
> 版本3：商品 + 类别 + 品牌 + 属性 
>
> 版本4：商品 + 类别 + 品牌 + 属性 + 规格

#### (c) 查询性能

> 展示和搜索都非常消耗性能
>
> 需要查很多张表
>
> 使用JMeter压测：500线程压测、每秒只能处理53个请求；800线程压测发生大量超时

### (2) 流量特点

> 热点少：商品详情页不需要登录也能打开
>
> 爬虫、比价网站抓取

## 3 如何优化性能

### (1) 方法1：生成可前置到CDN的静态页面

#### (a) 方法

> FreeMarker模板引擎：使用`模板`和` Java Object`生成静态html页面
>
> <div align="left"><img src="https://raw.githubusercontent.com/kenfang119/pics/main/upload/sysdesign_ecom_freemaker_controller.png" width="600" /></div>
>
> 最小化需要动态访问Tomcat的请求

#### (b) 触发时机

> 商品新增、商品信息修改、商品属性调整、模板更改、……

#### (c) 缺陷

静态页面数量庞大：商品数 * 机房数

> 只能用于商品较少的小型电商（例如小米）
>
> 对于大型电商（例如千万级商品数量），静态页面数量为亿级，任何一次模板更改（例如把“京东”改成“京东自营”）都会引发亿级的页面生成，需要消耗数天

需要大量同步：到各个国家和各个CDN节点

> 同样任何一次模板更新，都会消耗大量的时间和带宽

### (2) 方法2：使用Redis优化磁盘IO

> MySQL是磁盘IO，使用Redis虽然网络IO仍然不可避免，但是可以减少磁盘IO
