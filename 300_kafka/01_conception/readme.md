<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Kafaka介绍](#kafaka%E4%BB%8B%E7%BB%8D)
    - [(1) 作用](#1-%E4%BD%9C%E7%94%A8)
    - [(2) 工作模式](#2-%E5%B7%A5%E4%BD%9C%E6%A8%A1%E5%BC%8F)
    - [(3) 零拷贝IO](#3-%E9%9B%B6%E6%8B%B7%E8%B4%9Dio)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Kafaka介绍

> 高吞吐消息队列

### (1) 作用

> 异步通信，用于系统间解耦，缓冲流量峰值（削峰填谷）

### (2) 工作模式

> * 按`topic`订阅
> * 按照`key`来拆分成`partition`
> * 相同`partition`的`record`有序组织
> * 费者各自维护不同的`offset`（读到了哪里），单台消费者吞吐能力不够时、可以以`消费组`的形式扩容

### (3) 零拷贝IO

> 传统的阻塞IO、共需要4次拷贝；Kafaka使用DMA内存读取协处理器，做到零拷贝




