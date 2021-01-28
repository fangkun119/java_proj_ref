<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Git Pro](#git-pro)
  - [Chapter 01：介绍及安装](#chapter-01%E4%BB%8B%E7%BB%8D%E5%8F%8A%E5%AE%89%E8%A3%85)
    - [1.0. 原理部分（CH1.1、1.2、1.3）](#10-%E5%8E%9F%E7%90%86%E9%83%A8%E5%88%86ch111213)
    - [1.1. 安装及首次配置: P10](#11-%E5%AE%89%E8%A3%85%E5%8F%8A%E9%A6%96%E6%AC%A1%E9%85%8D%E7%BD%AE-p10)
  - [CH02 基础操作](#ch02-%E5%9F%BA%E7%A1%80%E6%93%8D%E4%BD%9C)
    - [2.1. 获取仓库](#21-%E8%8E%B7%E5%8F%96%E4%BB%93%E5%BA%93)
      - [场景1：用本地已有的代码生成git仓库](#%E5%9C%BA%E6%99%AF1%E7%94%A8%E6%9C%AC%E5%9C%B0%E5%B7%B2%E6%9C%89%E7%9A%84%E4%BB%A3%E7%A0%81%E7%94%9F%E6%88%90git%E4%BB%93%E5%BA%93)
      - [场景2：从远端clone到git仓库到本地](#%E5%9C%BA%E6%99%AF2%E4%BB%8E%E8%BF%9C%E7%AB%AFclone%E5%88%B0git%E4%BB%93%E5%BA%93%E5%88%B0%E6%9C%AC%E5%9C%B0)
    - [2.2. 记录变更](#22-%E8%AE%B0%E5%BD%95%E5%8F%98%E6%9B%B4)
      - [2.2.1/4/5/6 查看当前文件状态](#221456-%E6%9F%A5%E7%9C%8B%E5%BD%93%E5%89%8D%E6%96%87%E4%BB%B6%E7%8A%B6%E6%80%81)
      - [2.2.2/3/4 跟踪、修改、提交新文件](#22234-%E8%B7%9F%E8%B8%AA%E4%BF%AE%E6%94%B9%E6%8F%90%E4%BA%A4%E6%96%B0%E6%96%87%E4%BB%B6)
    - [2.3 查看提交历史](#23-%E6%9F%A5%E7%9C%8B%E6%8F%90%E4%BA%A4%E5%8E%86%E5%8F%B2)
    - [2.4 撤销](#24-%E6%92%A4%E9%94%80)
    - [2.5 使用远程仓库](#25-%E4%BD%BF%E7%94%A8%E8%BF%9C%E7%A8%8B%E4%BB%93%E5%BA%93)
    - [2.6 Tags](#26-tags)
    - [2.7 别名(alias)](#27-%E5%88%AB%E5%90%8Dalias)
  - [CH03 分支](#ch03-%E5%88%86%E6%94%AF)
    - [3.0 原理（图解演示见P43-P44)](#30-%E5%8E%9F%E7%90%86%E5%9B%BE%E8%A7%A3%E6%BC%94%E7%A4%BA%E8%A7%81p43-p44)
    - [3.1 新建 & 切换分支 （图解演示见P44-P47)](#31-%E6%96%B0%E5%BB%BA--%E5%88%87%E6%8D%A2%E5%88%86%E6%94%AF-%E5%9B%BE%E8%A7%A3%E6%BC%94%E7%A4%BA%E8%A7%81p44-p47)
    - [3.3 分支合并（图解演示见P48-P55）](#33-%E5%88%86%E6%94%AF%E5%90%88%E5%B9%B6%E5%9B%BE%E8%A7%A3%E6%BC%94%E7%A4%BA%E8%A7%81p48-p55)
    - [3.4 分支有关的工作流（P57-P59）](#34-%E5%88%86%E6%94%AF%E6%9C%89%E5%85%B3%E7%9A%84%E5%B7%A5%E4%BD%9C%E6%B5%81p57-p59)
    - [3.5 远程分支（图解演示见P60-P66）](#35-%E8%BF%9C%E7%A8%8B%E5%88%86%E6%94%AF%E5%9B%BE%E8%A7%A3%E6%BC%94%E7%A4%BA%E8%A7%81p60-p66)
      - [3.5.0 原理，添加远端仓库到本地](#350-%E5%8E%9F%E7%90%86%E6%B7%BB%E5%8A%A0%E8%BF%9C%E7%AB%AF%E4%BB%93%E5%BA%93%E5%88%B0%E6%9C%AC%E5%9C%B0)
      - [3.5.1 推送本地分支到远程](#351-%E6%8E%A8%E9%80%81%E6%9C%AC%E5%9C%B0%E5%88%86%E6%94%AF%E5%88%B0%E8%BF%9C%E7%A8%8B)
      - [3.5.2 跟踪远端分支的变化](#352-%E8%B7%9F%E8%B8%AA%E8%BF%9C%E7%AB%AF%E5%88%86%E6%94%AF%E7%9A%84%E5%8F%98%E5%8C%96)
  - [CH04 Git服务器搭建和配置](#ch04-git%E6%9C%8D%E5%8A%A1%E5%99%A8%E6%90%AD%E5%BB%BA%E5%92%8C%E9%85%8D%E7%BD%AE)
  - [CH05 Git工作流](#ch05-git%E5%B7%A5%E4%BD%9C%E6%B5%81)
    - [5.1 几种分布式工作流](#51-%E5%87%A0%E7%A7%8D%E5%88%86%E5%B8%83%E5%BC%8F%E5%B7%A5%E4%BD%9C%E6%B5%81)
      - [集中式工作流](#%E9%9B%86%E4%B8%AD%E5%BC%8F%E5%B7%A5%E4%BD%9C%E6%B5%81)
      - [集成管理者工作流](#%E9%9B%86%E6%88%90%E7%AE%A1%E7%90%86%E8%80%85%E5%B7%A5%E4%BD%9C%E6%B5%81)
      - [司令官与副官工作流](#%E5%8F%B8%E4%BB%A4%E5%AE%98%E4%B8%8E%E5%89%AF%E5%AE%98%E5%B7%A5%E4%BD%9C%E6%B5%81)
    - [5.2 开发者为项目做贡献](#52-%E5%BC%80%E5%8F%91%E8%80%85%E4%B8%BA%E9%A1%B9%E7%9B%AE%E5%81%9A%E8%B4%A1%E7%8C%AE)
      - [5.2.1 提交准则](#521-%E6%8F%90%E4%BA%A4%E5%87%86%E5%88%99)
      - [5.2.2 私有小型团队（集中式工作流）](#522-%E7%A7%81%E6%9C%89%E5%B0%8F%E5%9E%8B%E5%9B%A2%E9%98%9F%E9%9B%86%E4%B8%AD%E5%BC%8F%E5%B7%A5%E4%BD%9C%E6%B5%81)
      - [5.2.3 大型私有团队（每个小组一个专属branch）](#523-%E5%A4%A7%E5%9E%8B%E7%A7%81%E6%9C%89%E5%9B%A2%E9%98%9F%E6%AF%8F%E4%B8%AA%E5%B0%8F%E7%BB%84%E4%B8%80%E4%B8%AA%E4%B8%93%E5%B1%9Ebranch)
      - [5.2.4 派生一个开源项目（图解演示：P111-P112)](#524-%E6%B4%BE%E7%94%9F%E4%B8%80%E4%B8%AA%E5%BC%80%E6%BA%90%E9%A1%B9%E7%9B%AE%E5%9B%BE%E8%A7%A3%E6%BC%94%E7%A4%BAp111-p112)
      - [5.2.5 通过电子邮件接收补丁的开源项目（图解演示：P113-P115）](#525-%E9%80%9A%E8%BF%87%E7%94%B5%E5%AD%90%E9%82%AE%E4%BB%B6%E6%8E%A5%E6%94%B6%E8%A1%A5%E4%B8%81%E7%9A%84%E5%BC%80%E6%BA%90%E9%A1%B9%E7%9B%AE%E5%9B%BE%E8%A7%A3%E6%BC%94%E7%A4%BAp113-p115)
    - [5.3 远程仓库维护者（Owner）（图解演示：P115-P126）](#53-%E8%BF%9C%E7%A8%8B%E4%BB%93%E5%BA%93%E7%BB%B4%E6%8A%A4%E8%80%85owner%E5%9B%BE%E8%A7%A3%E6%BC%94%E7%A4%BAp115-p126)
      - [5.3.1 使用主题分支来合并贡献者提交的变更](#531-%E4%BD%BF%E7%94%A8%E4%B8%BB%E9%A2%98%E5%88%86%E6%94%AF%E6%9D%A5%E5%90%88%E5%B9%B6%E8%B4%A1%E7%8C%AE%E8%80%85%E6%8F%90%E4%BA%A4%E7%9A%84%E5%8F%98%E6%9B%B4)
      - [5.3.2 应用来自电子邮件的补丁](#532-%E5%BA%94%E7%94%A8%E6%9D%A5%E8%87%AA%E7%94%B5%E5%AD%90%E9%82%AE%E4%BB%B6%E7%9A%84%E8%A1%A5%E4%B8%81)
      - [5.3.3 检出远程分支](#533-%E6%A3%80%E5%87%BA%E8%BF%9C%E7%A8%8B%E5%88%86%E6%94%AF)
      - [5.3.4 确定会引入哪些内容](#534-%E7%A1%AE%E5%AE%9A%E4%BC%9A%E5%BC%95%E5%85%A5%E5%93%AA%E4%BA%9B%E5%86%85%E5%AE%B9)
      - [5.3.5 整合贡献者提交的变更](#535-%E6%95%B4%E5%90%88%E8%B4%A1%E7%8C%AE%E8%80%85%E6%8F%90%E4%BA%A4%E7%9A%84%E5%8F%98%E6%9B%B4)
      - [5.3.6 为发布版打标签](#536-%E4%B8%BA%E5%8F%91%E5%B8%83%E7%89%88%E6%89%93%E6%A0%87%E7%AD%BE)
      - [5.3.7 生成构建编号](#537-%E7%94%9F%E6%88%90%E6%9E%84%E5%BB%BA%E7%BC%96%E5%8F%B7)
      - [5.3.8/9 发布及简报](#5389-%E5%8F%91%E5%B8%83%E5%8F%8A%E7%AE%80%E6%8A%A5)
  - [CH06 GitHub](#ch06-github)
  - [CH07 Git工具](#ch07-git%E5%B7%A5%E5%85%B7)
    - [7.1 在git命令中指定commit](#71-%E5%9C%A8git%E5%91%BD%E4%BB%A4%E4%B8%AD%E6%8C%87%E5%AE%9Acommit)
      - [7.1.1 指定单个commit](#711-%E6%8C%87%E5%AE%9A%E5%8D%95%E4%B8%AAcommit)
      - [7.1.2 指定一批提交](#712-%E6%8C%87%E5%AE%9A%E4%B8%80%E6%89%B9%E6%8F%90%E4%BA%A4)
    - [7.2 交互式暂存（git add）](#72-%E4%BA%A4%E4%BA%92%E5%BC%8F%E6%9A%82%E5%AD%98git-add)
      - [7.2.1 暂存和取消文件暂存（以文件为粒度）](#721-%E6%9A%82%E5%AD%98%E5%92%8C%E5%8F%96%E6%B6%88%E6%96%87%E4%BB%B6%E6%9A%82%E5%AD%98%E4%BB%A5%E6%96%87%E4%BB%B6%E4%B8%BA%E7%B2%92%E5%BA%A6)
    - [7.3 储藏与清理（git stash）](#73-%E5%82%A8%E8%97%8F%E4%B8%8E%E6%B8%85%E7%90%86git-stash)
    - [7.4 签署工作](#74-%E7%AD%BE%E7%BD%B2%E5%B7%A5%E4%BD%9C)
    - [7.5 搜索](#75-%E6%90%9C%E7%B4%A2)
      - [7.5.1 git grep](#751-git-grep)
      - [7.5.2 git日志搜索](#752-git%E6%97%A5%E5%BF%97%E6%90%9C%E7%B4%A2)
    - [7.6 重写历史](#76-%E9%87%8D%E5%86%99%E5%8E%86%E5%8F%B2)
      - [7.6.1 重写最近一次commit](#761-%E9%87%8D%E5%86%99%E6%9C%80%E8%BF%91%E4%B8%80%E6%AC%A1commit)
      - [7.6.2 修改多个commit message](#762-%E4%BF%AE%E6%94%B9%E5%A4%9A%E4%B8%AAcommit-message)
      - [7.6.3 重排commit的顺序，或者删除某个commit](#763-%E9%87%8D%E6%8E%92commit%E7%9A%84%E9%A1%BA%E5%BA%8F%E6%88%96%E8%80%85%E5%88%A0%E9%99%A4%E6%9F%90%E4%B8%AAcommit)
    - [7.7 重置（git reset)与检出（git checkout)](#77-%E9%87%8D%E7%BD%AEgit-reset%E4%B8%8E%E6%A3%80%E5%87%BAgit-checkout)
      - [7.7.1 三棵树（三个区域）](#771-%E4%B8%89%E6%A3%B5%E6%A0%91%E4%B8%89%E4%B8%AA%E5%8C%BA%E5%9F%9F)
    - [7.7.2 关于HEAD，index，工作目录的工作流（图解演示P119-P202)](#772-%E5%85%B3%E4%BA%8Eheadindex%E5%B7%A5%E4%BD%9C%E7%9B%AE%E5%BD%95%E7%9A%84%E5%B7%A5%E4%BD%9C%E6%B5%81%E5%9B%BE%E8%A7%A3%E6%BC%94%E7%A4%BAp119-p202)
      - [7.7.4 用git reset回滚某个文件或目录](#774-%E7%94%A8git-reset%E5%9B%9E%E6%BB%9A%E6%9F%90%E4%B8%AA%E6%96%87%E4%BB%B6%E6%88%96%E7%9B%AE%E5%BD%95)
      - [7.7.5 用git reset将多个commit压缩为一个](#775-%E7%94%A8git-reset%E5%B0%86%E5%A4%9A%E4%B8%AAcommit%E5%8E%8B%E7%BC%A9%E4%B8%BA%E4%B8%80%E4%B8%AA)
    - [7.7.6 git checkout与git reset的区别](#776-git-checkout%E4%B8%8Egit-reset%E7%9A%84%E5%8C%BA%E5%88%AB)
      - [作用在分支上时](#%E4%BD%9C%E7%94%A8%E5%9C%A8%E5%88%86%E6%94%AF%E4%B8%8A%E6%97%B6)
      - [作用在文件/目录上时](#%E4%BD%9C%E7%94%A8%E5%9C%A8%E6%96%87%E4%BB%B6%E7%9B%AE%E5%BD%95%E4%B8%8A%E6%97%B6)
    - [7.8 高级合并(merge)技巧](#78-%E9%AB%98%E7%BA%A7%E5%90%88%E5%B9%B6merge%E6%8A%80%E5%B7%A7)
        - [二、复杂冲突：两个分支分叉后各自修改较复杂，冲突原因不容易定位](#%E4%BA%8C%E5%A4%8D%E6%9D%82%E5%86%B2%E7%AA%81%E4%B8%A4%E4%B8%AA%E5%88%86%E6%94%AF%E5%88%86%E5%8F%89%E5%90%8E%E5%90%84%E8%87%AA%E4%BF%AE%E6%94%B9%E8%BE%83%E5%A4%8D%E6%9D%82%E5%86%B2%E7%AA%81%E5%8E%9F%E5%9B%A0%E4%B8%8D%E5%AE%B9%E6%98%93%E5%AE%9A%E4%BD%8D)
    - [7.8.3 其他类型合并](#783-%E5%85%B6%E4%BB%96%E7%B1%BB%E5%9E%8B%E5%90%88%E5%B9%B6)
    - [7.9 rerere](#79-rerere)
    - [7.10 使用Git调试](#710-%E4%BD%BF%E7%94%A8git%E8%B0%83%E8%AF%95)
      - [7.10.1 git blame](#7101-git-blame)
      - [7.10.2 git biset 二分查找定位问题](#7102-git-biset-%E4%BA%8C%E5%88%86%E6%9F%A5%E6%89%BE%E5%AE%9A%E4%BD%8D%E9%97%AE%E9%A2%98)
    - [7.11 git submodule 子模块](#711-git-submodule-%E5%AD%90%E6%A8%A1%E5%9D%97)
      - [7.11.1 向repo中添加submodule](#7111-%E5%90%91repo%E4%B8%AD%E6%B7%BB%E5%8A%A0submodule)
      - [7.11.2 克隆含有子模块的项目](#7112-%E5%85%8B%E9%9A%86%E5%90%AB%E6%9C%89%E5%AD%90%E6%A8%A1%E5%9D%97%E7%9A%84%E9%A1%B9%E7%9B%AE)
      - [7.11.3 开发含有子模块的项目](#7113-%E5%BC%80%E5%8F%91%E5%90%AB%E6%9C%89%E5%AD%90%E6%A8%A1%E5%9D%97%E7%9A%84%E9%A1%B9%E7%9B%AE)
        - [1.拉取子模块上游的变更](#1%E6%8B%89%E5%8F%96%E5%AD%90%E6%A8%A1%E5%9D%97%E4%B8%8A%E6%B8%B8%E7%9A%84%E5%8F%98%E6%9B%B4)
        - [2.使用子模块：P239-241](#2%E4%BD%BF%E7%94%A8%E5%AD%90%E6%A8%A1%E5%9D%97p239-241)
        - [3.发布子模块的变更：P241-P242](#3%E5%8F%91%E5%B8%83%E5%AD%90%E6%A8%A1%E5%9D%97%E7%9A%84%E5%8F%98%E6%9B%B4p241-p242)
        - [4.合并子模块的变更：P242-P244](#4%E5%90%88%E5%B9%B6%E5%AD%90%E6%A8%A1%E5%9D%97%E7%9A%84%E5%8F%98%E6%9B%B4p242-p244)
      - [7.11.4 子模块的技巧：P245-P246](#7114-%E5%AD%90%E6%A8%A1%E5%9D%97%E7%9A%84%E6%8A%80%E5%B7%A7p245-p246)
        - [1.foreach命令](#1foreach%E5%91%BD%E4%BB%A4)
        - [2.alias](#2alias)
      - [7.11.5 子模块的问题：P246-P247](#7115-%E5%AD%90%E6%A8%A1%E5%9D%97%E7%9A%84%E9%97%AE%E9%A2%98p246-p247)
        - [1.主模块分支切换问题](#1%E4%B8%BB%E6%A8%A1%E5%9D%97%E5%88%86%E6%94%AF%E5%88%87%E6%8D%A2%E9%97%AE%E9%A2%98)
        - [问题2：把主模块的子目录转换为子模块](#%E9%97%AE%E9%A2%982%E6%8A%8A%E4%B8%BB%E6%A8%A1%E5%9D%97%E7%9A%84%E5%AD%90%E7%9B%AE%E5%BD%95%E8%BD%AC%E6%8D%A2%E4%B8%BA%E5%AD%90%E6%A8%A1%E5%9D%97)
    - [7.12 打包](#712-%E6%89%93%E5%8C%85)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Git Pro

## Chapter 01：介绍及安装

### 1.0. 原理部分（CH1.1、1.2、1.3）

* Git检出到本地的不是某个version下的代码镜像，而是整个代码仓库（任何一个客户端都是一个完整的仓库备份）
	
	* Git提交代码版本：可以理解为是由一组指针（链接）组成的快照

	* 大部分情况下，Git操作可以在本地进行，做好之后再与远端同步

	* 大部分提交，对本地的Git仓库来说，都是添加数据，因此安全性好

* 文件的三个状态

	* 已修改：改了工作区的代码文件
	
	* 已暂存：执行git add ${file_or_dir}命令时，所有的更改都会存储到暂存区

	* 已提交：执行git commit命令后，暂存区的更改会存储在本地.git的新增版本中

### 1.1. 安装及首次配置: P10

命令  | 说明
------------- | -------------
git config --system  | 修改/etc/gitconfig
git config --global  | 修改~/.gitconfig或~/.config/git/config
git config  | 修改当前项目的.git/config

git config --global user.name "John Doe"

git config --global user.email johndoe@examlle.com

git config --global core.editor emacs

git config --list

git config user.name


## CH02 基础操作

获取仓库；记录变更；查看提交历史；撤销操作；远端仓库；标签；命令别名

### 2.1. 获取仓库

#### 场景1：用本地已有的代码生成git仓库

命令 | 说明
------------- | -------------
git init | 在本地生成.git目录
git add *.c | 把工作区所有的*.c文件加入到暂存区
git add LICENSE | 把名为LICENSE的文件加入到暂存区
git commit -m "xxx" | 把之前记录在暂存区的变更提交到.git目录中

#### 场景2：从远端clone到git仓库到本地

命令 | 说明
------------- | -------------
git clone https://github.com/libgit2/libgit2 | 克隆到本地同名目录中
git clone https://github.com/libgit2/libgit2 mylibgit | 克隆到本地改名后的目录mylibgit

### 2.2. 记录变更

#### 2.2.1/4/5/6 查看当前文件状态

命令 | 说明
-------------------- | -------------
git status | 查看被修改的文件，哪些没有被暂存(add)，哪些没有被提交(commit)，当前的本地分支是什么
git status -s | 用两个字母来标记上面的内容，第一个对应于暂存区，第二个对应于提交区，M表示修改，A表示增加，?表示是未被跟踪的文件
git diff | 查看工作区代码与暂存区代码的diff
git diff --staged 或 git diff --cached| 查看暂存区代码与提交区代码的区别
git difftool | 使用emerge、vimdiff等软件查看上述的diff
git difftool --tool-help | 查看有哪些diff-tool可用

.gitignore文件：标记哪些文件不要被跟踪，可使用正则表达式（P19），例子参考[https://github.com/github/gitignore](https://github.com/github/gitignore)

#### 2.2.2/3/4 跟踪、修改、提交新文件

**跟踪（2.2.2）**

~~~
git add MY_NEW_FILE
git status
~~~

**修改并暂存（2.2.3）**

~~~
vim MY_NEW_FILE
git status
git add MY_NEW_FILE
git status
~~~

**提交到本地仓库（2.2.7）**

~~~
git commit
git commit -m "Story 182: fix benchmarks for speed"
~~~

**跳过暂存直接提交（2.2.8）**

~~~
git status
git commit -a -m "Story 182: fix benchmarks for speed"
~~~

**删除文件（2.2.9）**

如果文件没有已暂存的修改

~~~
git status
git rm MY_NEW_FILE #从工作区、以及占存区的跟踪列表中删除
~~~

如果文件有已暂存的修改

~~~
git status
git rm -f MY_NEW_FILE #因为还会把已暂存的修改会随rm丢失、要用--force
~~~

把文件保留在工作目录，仅从暂存区的跟踪列表删除

~~~
git status
git rm --cached MY_NEW_FILE
~~~

删除一批文件（使用通配符来表示BLOB）

~~~
git rm log/\*log #"\"是给shell用的转义符，转义成"log/*log"后传给git
git rm \*~  #被shell转义成*~之后传给git，表示以~结尾的文件
~~~

**移动文件（2.2.10）**

~~~
git mv FILE_FROM FILE_TO #等价于mv, git rm, git add
~~~

### 2.3 查看提交历史

git log常用示例

~~~
git log #显示提交历史
git log -p #附加显示每次提交的差异
git log -2 #只显示最近2次提交
git log --stat #附加显式每次提交的变更统计
git log --pretty=oneline #每行只显式一个提交
git log --pretty=format:"%h-%an,%ar:%s" #自定义摘要格式，参数见P27 (
git log --pretty=format:"%h %s" --graph #--graph可以显式提交分支及合并的历史
~~~

git log常用显式选项

选项 | 说明
---------- | ----------
-p | 按补丁格式显式每次提交引入的变更
--stat | 统计信息
--name-only | 附加显式每次提交被更改的文件列表
--name-status | 上一个选项基础上海显式“已更改/新增/删除"统计信息
--abbrv-commit | SHA-1部分只显式前几个字符串
--relative-date | 使用相对日期如”两周前“
--graph | 提交历史旁边显式ASCII图表，表示分支及合并的历史信息
--pretty | 指定显式格式，包括oneline、short、full、fuller、format:"xxx"

git log常用范围选项

~~~
git log --since=2.weeks #"--since"等价于"--after"
git log --before=2008-01-15 #"--before"等价于"--until"
git log --since=-"2 year 1 day 3 minutes ago" --until=2.weeks
git log --author="John" #作者与John匹配
git log --committer=”John" #提交者与John匹配
git log --grep="Story 128" #提交信息中包含“Story 128"
git log --author="John" --grep="Story 128" #满足2条件任意一个的提交
git log --author="John" --grep="Story 128" #同时满足所有条件
git log -SsomeFuncName #添加或删除SsomeFuncName字符串的变更
~~~

### 2.4 撤销

合并到上一次的commit中（避免创建新的commit，使用--amend来commit）

~~~
vim my_file 
git commit -am #第一次修改已经comit 
vim my_file
git add my_file
git commit --amend #以amend的方式将第二次修改合并到前一次commit中
~~~

将已经暂存的更改从暂存区移除，但在工作区仍然保留

~~~
vim file_to_roll_back #更改进入"not stage"状态
git add file_to_roll_back #更改进入"to be commited"状态
git reset HEAD file_to_roll_back #把对file_to_roll_back的修改移出暂存区
git status #更改回退到"not stage"状态
~~~

将更改彻底移除（不论是暂存区，还是工作区都移除）

~~~
vim file_change_to_discard #更改进入"not stage"状态
git add file_change_to_discard #更改进入"to be commited"状态
git checkout -- file_change_to_discard #撤销上面两个操作的所有更改
git status #文件与上次commit的内容一样，新的更改全部丢弃
~~~

备注：git checkout -- somefile是条危险的命令，工作中暂存和撤销文件修改，最好使用后面介绍的git stash命令

### 2.5 使用远程仓库

查看本地都有哪些仓库，仓库名是什么，跟踪的远程仓库的地址是什么

~~~
$ cd grit
$ git remote -v 
origin git@github.com:mojombo/grit.git (fetch)
origin git@github.com:mojombo/grit.git (push)
cho45 https://github.com/cho45/grit (fetch)
cho45 https://github.com/cho45/grit (push)
...
~~~

查看当前切换到是哪个仓库（git checkout命令是用来切换当前仓库的）

~~~
$ git remote
origin
~~~

克隆远程仓库到本地（clone的远程仓库，仓库名默认为origin，目录与url末尾同名)

~~~
$ git clone https://github.com/schacon/ticgit
$ cd ticgit
$ git remote
origin
~~~

添加一个远程仓库进来（只是添加仓库，并不会添加内容）

~~~
$ git remote -v
origin https://github.com/schacon/ticgit (fetch)
origin https://github.com/schacon/ticgit (push)
$ git remote add ticgit_paulboone https://github.com/paulboone/ticgit
$ git remote -v
origin https://github.com/schacon/ticgit (fetch)
origin https://github.com/schacon/ticgit (push)
ticgit_paulboone https://github.com/schacon/paulboone (fetch)
ticgit_paulboone https://github.com/schacon/paulboone (push)
~~~

把某个（很可能是新添加的）仓库内容（包括master在内的所有分支）下载到本地

~~~
$ git fetch ticgit_paulboone
备注：
1.该命令只是把远程仓库所有内容下载到.git目录中，并不会修改当前工作目录
2.git fetch之后，本地就拥有了所有分支的副本，可以用checkout命令切换到各个本地分支上，修改数据，合并分支等
3.本地分支如何跟踪了一个远程分支，用git pull可以从远程仓库得到当前本地分支的最新内容
~~~

把修改推送到远程仓库

~~~
$ git push origin master #推送到名为origin远程仓库的master分支
备注：如果别人已经push，必须先pull这个人的变更，merge之后再尝试push
~~~

查看远程仓库

~~~
$ git remote show ticgit_paulboone
备注：查看远程仓库有哪些分支，与本地仓库的关系如何（需要pull的分支，需要push的分支，没有track的分支，需要删除的分支...，P35）
~~~

删除远程仓库

~~~
$ git remote rm ticgit_paulboone #不跟踪这个远程仓库了
~~~

重命名远程仓库

~~~
$ git remote rename ticgit_paulboone ticgit_pb
$ git remote
origin
ticgit_pb
备注：分支名称也会被一同更改，例如ticgit_paulboone/master被改为ticgit_pb/master
~~~

### 2.6 Tags

查看有哪些tags

~~~
$ git tag #所有tag
v0.1
v0.3
v1.0
v1.8.5
v1.8.5-rc0
$ git tag -l "v1.8.5*" #v1.8.5开头的tag
v1.8.5
v1.8.5-rc0
~~~

创建标签：git有两种标签

* light-weight标签：只是一个只想某次提交的指针，用于临时标签等场景
* annotated标签：生成一个包含详细信息（校验和、提交信息、tag-message、签名等）的完成对象，存储在git数据库中，多数情况下都推荐使用annotated标签（用-a参数来创建标签）

~~~
$ git tag -a v1.4 -m "my version 1.4" #创建annotated标签
$ git tag v1.4-lw #创建light-weight标签
$ git tag -a v1.3 9fceb02 #用SHA-1为9fceb02*的commit创建tag
$ git tag #查看有哪些tag
v1.3
v1.4
v1.4-lw
~~~

查看标签数据，可以看到v1.4-lw的输出内容要比v1.4少 P37，P38

~~~
$ git show v1.4
$ git show v1.4-lw
~~~

把tag推送到远端仓库，供其他人使用

~~~
$ git push origin v1.5 #把tag v1.5推送到名为origin的远端仓库
$ git push origin --tags #把本地所有的tag推送到名为origin的远端仓库
~~~

把远端仓库的tag取下来，并用其创建分支

~~~
$ git checkout -b version2 v2.0.0
~~~

### 2.7 别名(alias)

配置别名，简化日常操作

~~~
$ git config --global alias.co checkout
$ git config --global alias.br branch
$ git config --global alias.ci commit
$ git config --global alias.st status
$ git config --global alias.unstage 'reset HEAD --'
$ git config --global alias.last 'log -1 HEAD'
$ git config --global alias.visual '!gitk' #!表示后面跟着外部命令
~~~

## CH03 分支

### 3.0 原理（图解演示见P43-P44)

* **git commit**

	只是创建一个Commit Object，内含快照指针、作者姓名、邮箱地址、commit message、父commit指针等（第一次commit没有父commit，普通commit只有一个父commit，merge commit有2个或多个父commit），指向Tree Object的指针

* **git add**

	为add的代码文件计算校验和、把新版本的文件以Blob对象的方式保存在本地Git仓库中，同时把校验和以及add操作记录添加到暂存区
	
	稍后git commit时，git会计算每个子目录的校验和，用这些校验和生成表示目录树的Tree Object
	
* **git的轻量级分支机制**

	git的分支，只是一些指向Commit Object的指针（commit object再指向tree object，tree object指向各个代码版本的blob object)。创建分支只是相当于创建指针，与SVN不同没有代码拉取和拷贝、可以在瞬间完成。两个不同的分支，完全可以指向同一个Commit Object。

### 3.1 新建 & 切换分支 （图解演示见P44-P47)

**新建分支**

~~~
$ git branch testing #创建一个分支对象，指向当前HEAD所指的commit object
~~~

**查看当前HEAD所指的commit object，同时被哪些分支所指**

~~~
$ git log --oneline --decorate | head -n1
f30ab (HEAD,master,testing) add feature #32 - ability to add new
~~~

**切换分支**

~~~
$ git checkout master #切换到master分支，即让HEAD指向master所指的commit object
$ git checkout testing #切换到testing分支，即让HEAD指向testing所指的commit object
~~~

**构造一个有分支冲突的场景**

~~~
$ git checkout testing
$ vim test.rb
$ git commit -a -m "made a change"
$ git checkout master
$ vim test.rb
$ git commit -a -m "made other changes"
$ git log --oneline --decorate --graph --all #注意下面的master,testing,HEAD字符串
* c2b9e (HEAD, master) made other changes
| * 87ab2 (testing) made a change
| /
* f30ab add feature #32 - ability to add new formates to the
~~~

### 3.3 分支合并（图解演示见P48-P55）

场景1：在本地master分支上工作时，收到通知，优先修复issue#53

~~~
$ git checkout -b iss53 #创建并切换到分支iss53
$ vim index.html #修复issue#53
$ git commit -a -m 'add a new footer [issue 53]' #被电话打断、保存进度到本地分支
~~~

场景2：接到电话，有一个hot-fix要立即进行

~~~
$ git checkout master #切换回master分支
$ git checkout -b hotfix #从master所指的Commit Object创建并切换到hotfix分支
$ vim index.html #hot fix
$ git commit -a -m 'fixed the broken email address'
~~~

场景3：hot-fix修复完毕，合并到master分支以便上线

~~~
$ git checkout master #切换回master
$ git merge hotfix #因hotfix由master创建，只需指针fast-forward，从终端输出可看到提示
$ git branch -d hotfix #hotfix已经不需要使用了、删除
~~~

场景4：继续修复issue#53

~~~
git checkout iss53 #切换到iss53分支
vim index.html #继续修改
git commit -a -m 'finished the new footer [issue 53]' #追加(-a)修改到前一commit
~~~

场景5：iss53修改完毕，合并到master以便可以上线

假设没有冲突（git merge时用到的三方可并机制 P52, P53）

~~~
$ git checkout master #切换回master分支
$ git merge iss53 #Git自动合并master与iss53
$ git branch -d iss53 #iss53不用了，删除
备注：这次的合并、不能fast-forward，会有两个父提交
用git log --oneline --decorate --graph --all可看到
~~~

假设有冲突（比如两个分支修改了index.html的同一行）

~~~
$ git checkout master #切换回master分支
$ git merge iss53 #合并master与iss53，Git报冲突，无法自动合并
CONFLICT (content): Merge conflict in index.html
$ git status #查看哪些文件没有自动合并
Unmerged paths:
  (use "git add <file> .." to mark resolution)
  both modified: index.html
$ vim index.html #修复冲突，也可以用git mergetool调用图形化工具来修复
备注：git会在index.html加入冲突标记，例如
<<< HEAD:index.html
HEAD版本内容
= ====
iss53版本内容
>>> iss53:index.html 
$ git add index.html #标记index.html文件内的冲突已经解决
$ git status #这次显式已经没有冲突文件了
$ git commit #提交
~~~

###3.3 分支管理

列出所有分支的名称

~~~
git branch #*开头的为HEAD所指向的分支（当前分支）
~~~

列出所有分支的名称、以及最新的commit

~~~
git branch -v
~~~

列出已经merge到当前分支的所有分支

~~~
$ git branch --merged
  iss53
* master
~~~

列出所有没merge到当前分支的所有分支

~~~
$ git branch --no-merged
  testing
$ git branch -d testing #没merge的分支默认不能删除，除非用-D
error: The branch 'testing' is not fully merged
If you are sure you want to delete it, run 'git branch -D testing'
~~~

### 3.4 分支有关的工作流（P57-P59）

上述三个分支稳定性渐进递增，修改从稳定性低的分支merge到稳定性高的分支

* **master（长期）**：只存放稳定版本代码（已发布、或即将发布）
* **develop/next分支（长期）**：用于平行开发或测试，一旦代码稳定，就合并到master
* **toic分支（短期）**：主题分支，短期存在，用于开发新的feature

备注：

~~~
1.上述分支都是存在与本地，还没有涉及到远端服务器上的分支
2.目前只介绍少量概念用于3.5节，CH04详细介绍各种工作流
~~~

### 3.5 远程分支（图解演示见P60-P66）

#### 3.5.0 原理，添加远端仓库到本地

内容 | 说明
--- | ---
**远程分支表示形式** | ${remote\_repo\_name}/${branch\_name}
**查看仓库名及对应的url** | 使用之前的git remote -v命令
**跟踪分支** | tracking branch，又叫上游分支(upstream branch)，用git checkout -b iss53 origin/iss53创建的本地分支iss53，就是远程分支origin/iss53的跟踪分支。
**对跟踪分支执行git push或者git pull** | git会自动找到对应的远程分支origin/iss53进行数据传输

**添加远端仓库：**

~~~
git clone https://github.com/libgit2/libgit2 #仓库名为origin
git remote add pb https://github.com/paulboone/ticgit #仓库名为pb
~~~

#### 3.5.1 推送本地分支到远程
**推送到远端仓库：** git push ${remote\_repo\_name} ${branch\_name}：将本地分支push到远端

~~~
git push origin iss53 #将iss53分支push到远端的同名分支上、以便于他人协作开发iss53
git push origin ref/heads/iss53:ref/heads/iss53 #与上一条命令相同，CH10详解
git push origin iss53:team_web_iss53 #将iss53分支push到远端的team_web_iss53分支上
~~~

#### 3.5.2 跟踪远端分支的变化

**更新本地仓库：**

git fetch ${remote\_repo\_name} 会将远程的分支同步/增加到本地仓库，但注意这仅仅是更新本地".git/"目录中的数据（CommitObject, TreeObject, Blob等对象）并不会创建本地分支

~~~
git fetch origin #从名称为origin的远端仓库获取最新的数据，更新到本地仓库
git fetch ${remote_repo_name} #更新指定名称的远程仓库数据到本地仓库
~~~

一条命令从远端更新所有的仓库

**使用远程分支来合并/新建本地分支**

~~~
git merge origin/iss53 #将远端分支origin/iss53合并到当前的本地分支
git checkout -b iss53 origin/iss53：新建本地分支iss53，基于远端origin/iss53分支
~~~

**创建跟踪分支**

在本地仓库新建跟踪分支，跟踪远程分支：git checkout -b ${local\_tracking\_branch\_name} ${repo\_name}/$
{remote\_br\_name}

~~~
git checkout -b iss53 origin/iss53 #指定跟踪分支和远程分支的名称
git checkout --track origin/iss53 #简写形式，跟踪分支同名，是iss53
git checkout iss53 #简写形式，远程分支是origin/iss53，跟踪分支是iss53
git checkout -b issue53 origin/iss53 #指定名称时，也可以不同名
~~~

**更改已存在本地分支所指向的远程分支**

上面命令中的-b替换成-u或者--set-upstream-to选项

~~~
git checkout -u origin/iss53
git checkout -u issue53 origin/iss53
~~~

**查看本地有哪些跟踪分支，都跟踪了哪些远程分支**

~~~
git branch -vv
~~~

**从远端拉取跟踪分支的数据**

~~~
git pull #相当于执行两条命令：git fetch; git pull
~~~

###3.6 变基（rebase）(图解演示见P66-P75)

####3.6.0 原理

* **git两个分支整合的方法：**（1）merge基于三方合并（2）rebase基于分支commit差集重放
* **git merge \${branch\_to\_merge\_in}：**用合并三方（当前分支Head，合入分支的Head，两分支最近的共有commit）的方法，新建一个CommitObject。该CommitObject有两个父Commit分别来两个分支
* **git rebase \${branch\_to\_merge\_in}：**用在${branch\_to\_merge\_in}上重放当前分支独有commit的方法，新建一个CommitObject，该CommitObject只有一个父Commit即当前分支的Head（但是代码已经包含了\${branch\_to\_merge\_in}的变更），并且让\${branch\_to\_merge\_in}指向这个新建的CommitObject
* **方法选择：**本地（还没有push到远端）的分支都适合用rebase来合并，好处是得到的commit历史记录是线性的、简洁干净，并且push到远端以后，远端仓库维护者不用做任何整合工作。对于已经push到远端的commit，不要使用rebase，会有意想不到的麻烦（后面介绍）

####3.6.1 用rebase整和两个分支

将分支exp并入到master

~~~
$ git checkout exp #切换到exp分支
$ git rebase master #将当前分支独有的commit重放到master上
$ git checkout master #切换到master分支
$ git merge exp #因为rebase的重放，这步只是通过fast-forward将master的HEAD指向重放时生成的CommitObject
~~~

####3.6.2 用rebase在三个分支间对commit有选择地整合

**相关命令：**

* **git rebase \${base\_branch}：**将当前分支独有的commit重放在base分支上
* **git rebase \${base\_branch} \${new\_func\_branch}：**将new_func分支独有的commit重放在base分支上
* **git rebase --onto \${onto_branch} \${base\_branch} \${new\_func\_branch}：**将new_func分支独有的commit重放在onto分支上
* **备注：**上面说的XX分支独有的commit，都是相对于base分支，即XX分支有base分支没有

**场景1：**三个分支master、server、client，在client分支包含了一部分server的commit的情况下，希望先让client合入master上线

~~~
$ git rebase --onto master server client #将client有server没有的Commit重放在master上
$ git checkout master #切换到master分支
$ git merge cliet #master在已经重放client提交历史的基础下fast-forward
$ git branch -d client #client分支已经不需要了
~~~

**场景2：**随后server分支开发完成，rebase到master上上线

~~~
$ git rebase master server #把server有master无的commit重放在server分支上
$ git checkout master #切换到master分支
$ git merge server #master在已经重放server提交历史的基础下fast-forward
$ git branch -d server #server分支已经不需要了
~~~

###3.6.3/4 rebase操作的潜在危害和应对办法

**错误产生场景：** A Push了一组commit到远端，B fetch这些commit并与B本地分支merge，A（也可以是C、B或任何人）对先前已经Push到远端的commit做变基（rebase）操作，B再次fetch并且merge这个commit。

**现在产生如下问题：** B merge了两次，这两次可能会包含同一个Commit，第一次是A在原始commit时产生，第二次是A rebase时产生，两次都会出现在B的git log历史记录中，引发git log输出混乱。

**更糟糕的问题：** 类似的问题会滚雪球般继续累积，最终导致git log记录混乱不堪

**预防方法：只对存在与本地仓库，还没有push的commit做rebase操作，对于已经push到远端的commit，仅可以做merge操作** 

**解决方法：**如果别人对远端的commit做了rebase，自己在更新本地分支时，不要使用merge，而是使用基于rebase的git命令。这些命令可以自动检查重复的commit并且从git history log中去重

~~~
git pull -rebase #使用基于rebase的git pull
这条命令相当于
git fetch
git rebase ${remote_repo_name}/${remote_branch_name}
~~~

也可以把git pull的默认整合方法从merge改为rebase

~~~
git config --global pull.rebase true
~~~

## CH04 Git服务器搭建和配置
略

## CH05 Git工作流

开发人员可以是节点（向其他repo贡献代码），也可以是中枢（维护公共repo供他人使用和提交代码）。工作流种类多样，可选择一种，也可以混搭。

### 5.1 几种分布式工作流

#### 集中式工作流

* 只有一个起中枢作用的远程仓库，大家都把代码修改push到这个仓库中，类似svn

#### 集成管理者工作流

* 一个主仓库，多个远程仓库，
* 开发人员对自己的远程仓库有读写权限，对别人的远程仓库只有读权限
* 开发人员从主仓库获取官方的代码版本，在本地修改，提交到自己的公开仓库，并通知主仓库的管理员合并。主仓库管理员将开发人员的代码修改合并到主仓库中，以便其他开发者也可以使用到。

好处：开发人员不用等主仓库管理员合并，可以直接用自己的远程仓库代码构建项目，上线；管理员可以将优秀并且测试通过的新功能合入主仓库，共享给其他开发人员。

#### 司令官与副官工作流

角色：

* 司令官：负责维护主仓库，从副官的仓库中拉取变更，合并到主仓库
* 多个副官：每个副官维护一个副官仓库，负责项目中某一部分的维护
* 开发者：每个开发者有一个自己的远程仓库，对齐有写权限，同时对于主仓库以及其他开发者的仓库有读权限

工作流：

* 开发者以主仓库最新的master为基准，拉取到本地，创建主题分支增加新功能，rebase并push到开发者仓库的master上
* 副官将拉取主仓库的master，同时将开发者的master并入自己的master中
* 司令官将副官的master并入主仓库master中 --> 回到第一步，所有开发者以最新的master为基准创建主题分支、开发新功能、变基(rebase)

适用场景：超大型项目，高度层次化的项目

### 5.2 开发者为项目做贡献

项目的不确定性：贡献者数量、工作流、有权限的仓库

#### 5.2.1 提交准则

关于提交项目补丁的建议：Git源代码中Documentation/SubmittingPatches

1.检查以确保不要有与空白字符相关的错误： 

~~~
git diff --check
~~~

2.每个补丁只包含一个变更，如果多个补丁修改到同一个文件，用git add --patch命令来部分占存文件的变更（第7.2节）

3.提交信息符合规范 P99

#### 5.2.2 私有小型团队（集中式工作流）

成员A

~~~
git clone a@githost:proj.git
cd proj
vim lib/code.rb
git commit -am 'msg a'
~~~

成员B

~~~
git clone b@githost:proj.git
cd proj
vim TODO
git commit -am 'msg b'
~~~

成员A

~~~
git push origin master
~~~

成员B：此时不能直接push，必须先在本地仓库merge才能提交

~~~
git fetch origin
git merge origin/master
git push origin master
~~~

成员A：此时A刚好在本地建了名为issue54的主题分支，准备把issue54合并到master，但合并之前，他想先看看B向远端push了什么样的变更

~~~
git fetch origin #同步新的变更到本地仓库
git log --no-merges issue54..origin/master #master有issue54没有的commit
git checkout master #切换到master
git merge issue54 #合并本地分支issue54，Git可以直接用Fast-Foward
git merge origin/master #合并远端的master
git push origin master #push到远端
~~~

#### 5.2.3 大型私有团队（每个小组一个专属branch）

团队分成若干小组共同开发一个项目，一个人有可能同时在不同的小组中与不同的组员合作，每个小组都有一个专属的分支用于组内协作

例子中J与A一组开发FeatureA，J与B一组开发FeatureB，协作过程如下

J开发FeatureA

~~~
git clone J@githost:proj.git
git check -b featureA #创建分支featureA
vim lib/code.rb
git commit -am "msg 1 J@FeatureA" 
git push origin master #推送到远端的FeatureA分支，供小组A使用
~~~

J开发FeatureB

~~~
git fetch origin #同步远端仓库到本地
git checkout -b featureB origin/master #基于远端master创建featureB分支（不能带入featureA的内容）
vim lib/code.rb
git commit -am "msg 2 J@FeatureB"
~~~

J收到B的邮件，说B有一个关于FeatureB的分支branchB要合并进来，内含了前续功能的代码，之后也使用branchB

~~~
git fetch origin #同步远端仓库到本地
git checkout featureB #确保切换到了featureB
git merge origin/branchB #合并同事B的分支
git push -u origin featureB:branchB 
-u表示--set-upstream, 当前分支跟踪的远程分支也被改为branchB
这条命令表示将featureB中合并过的工作内容推送到branchB中，详见$10.5
~~~

J收到A的邮件，说A已经向远端分支branchA推送了新的变更

~~~
git fetch origin #同步远端仓库到本地
git log fetureA..origin/featureA #看相比本地featureA，远端featureA做了哪些变更
git checkout featureA #切换到分支featureA
git merge origin/featureA #合并远端featureA的修改
vim lib/code.rb #做少量修改
git commit -am "msg 3 J@FeatureA"
git push #推送到远端
~~~

#### 5.2.4 派生一个开源项目（图解演示：P111-P112)

#### 5.2.5 通过电子邮件接收补丁的开源项目（图解演示：P113-P115）

### 5.3 远程仓库维护者（Owner）（图解演示：P115-P126）

#### 5.3.1 使用主题分支来合并贡献者提交的变更

#### 5.3.2 应用来自电子邮件的补丁

#### 5.3.3 检出远程分支

#### 5.3.4 确定会引入哪些内容

#### 5.3.5 整合贡献者提交的变更

#### 5.3.6 为发布版打标签

#### 5.3.7 生成构建编号

#### 5.3.8/9 发布及简报

## CH06 GitHub

略

## CH07 Git工具

### 7.1 在git命令中指定commit

#### 7.1.1 指定单个commit

**用SHA-1指定单个commit**

可以使短格式简写的SHA-1，前n个字符，字符数大于4且没有歧义就可以

~~~
$ git log #或者git log --oneline得到历史提交的SHA-1值
$ git show 1c002dd4b536e749fe34593e72e6c6c1819e53b
$ git show 1c002dd4b536e749f
$ git show 1c002d #上面3个命令都相同，查看同一个commit的diff
$ git log --abbrev-commit --pretty=oneline #返回没有歧义的短格式SHA-1
~~~

**用分支名称指定分支头部的commit**

~~~
$ git show iss54
$ git show ca82a6dff8 #与指定分支头部Commit的SHA-1效果相同
$ git rev-parse iss54 #该命令会返回分支iss54头部commit的SHA-1值
~~~

**用reflog查看最近几个月HEAD和分支都引用过哪些Commit**

注意reflog信息只存放于本地，远端仓库中并没有

~~~
$ git reflog #返回一个列表
$ git show HEAD@{5} #查看仓库HEAD之前第5次的diff（从0开始计数)
$ git show master@{yesterday} #master分支昨天都有哪些commit，diff是什么
$ git log -g master #查看master分支的git log时，附加上reflog命令输出的信息
~~~

**指定某个提交的父提交、祖先提交**

~~~
$ git log --pretty=format:'%h %s' --graph #查看commit间父子关系
$ git show HEAD^ #查看HEAD父提交的diff
$ git show d921970^ #查看d921970*的父提交
$ git show d921970^ #查看d921970*是分支合并，有两个父提交）的第2个父提交
$ git show HEAD~3 #HEAD父父父提交（父提交的父提交的父提交）
$ git show HEAD^^^ #与上面命令相同
$ git show HEAD~3^2 #HEAD父父提交（假定是个分支合并提交）的第二个父提交
~~~

#### 7.1.2 指定一批提交

**双点号A..B：**表示B有、但是A没有的提交（从A到B，即以A为基准，把B合并进来，还差哪些提交）

~~~
git log master..experiment #experiment分支有、master分支没有的提交
git log experiment..master #master分支有、experiment分支没有的提交
git log origin/master..HEAD #本地HEAD有、远程仓库origin的master分支没有的提交
备注：HEAD取决于当前版本指向哪个Commit
远程仓库origin：是指git fetch时从远程同步到本地的origin仓库镜像、如果之后有人更新了远端的origin，需要再次git fetch
git log origin/master.. #与上一条命令相同，双点号一边如果是HEAD就可以省略
~~~

**^branch或--not branch：**明确表示不在某个分支（或commit指针）上的提交

~~~
git log refA..refB #与下面两条命令等价
git log ^refA refB
git log refB --not refA
~~~

该命令的好处是，可以用于在多个分支（或commit指针）中选择提交

~~~
git log refA refB ^refC #refA、refB有，refC没有的提交
git log refA refB --not refC #与上面的命令等价
~~~

**三点号A...B：**查看(A)∩(B)在(A)∪(B)中的补集、即只在A中或只在B中但不会同时出现在A和B中的提交

~~~
git log master...experiment #只在master分支或只在experiment分支中的提交
git log --left-right master...experiment #只在一方中的提交，并且标明是在哪一方
~~~

### 7.2 交互式暂存（git add）

用途：通过git add -i或git add --interactive进入交互模式，有选择地对修改进行暂存，以便接下来将一对修改分类到若干个有针对性的commit中

#### 7.2.1 暂存和取消文件暂存（以文件为粒度）

~~~
$ git add -i
1: status 2: update 3: revert ... 5: patch 6: diff
What now> 2 #选择操作2:update 
        staged unstaged path  #第2列表示暂存区，第3列表示工作区，+m/-n表示增删行数
  1: unchanged    +0/-1 TODO
  2: unchanged    +1/-1 index.html
  3: unchanged    +5/-1 lib/simplegit.rb
Update>> 1,2 #选择文件1、2，注意">"数量以前前面操作提示符的变化
Update>> #再按一次回车确认上面的选择
What now> 1 #查看最近状态
What now> 3 #选择revert
Revert >> 1 #选择文件1
Revert >> #再次回车确认选择
What now> 1 #选择查看最新状态
What now> 6 #对比文件差异
Review diff>> 1 #选择文件1
~~~

####7.2.2 暂存文件中一部分修改（以hunk，即diff块为粒度）

在交互式暂存（git add -i）过程中选择命令5:patch
更可以使用git add -patch；git reset --patch；git stash save -patch；git checkout --patch；等命令开启部分暂存

~~~
$ git add -i
1: status 2: update 3: revert ... 5: patch 6: diff
What now> 5 #选择操作5:patch
diff --git a/lib/code.rb b/lib/code.rb #会依次显式各个diff块(hunk)
... 
Stage this hunk [y,n,a,d,/,j/J/g,e,?]? ? #输入?会显式操作提示 
y - stage this hunk
n - do not stage this hunk
a - stsger this all the remaining hunks in the file
...
What now> 1 #status
        staged unstaged path  
  1: unchanged    +0/-1 TODO
  2: unchanged    +1/-1 index.html
  3: +1/-1        +4/-0 lib/code.rb #这个文件staged和unstaged都有diff，说明只对一部分内容作了暂存
~~~

### 7.3 储藏与清理（git stash）

用途：git stash命令可以把当前做了一半的工作保存起来，然后清理工作区（将其重置），以便开始另一项工作。稍后可以用储藏的数据恢复工作区，继续从事先前被打断的工作。

**储藏（并重置）工作区及暂存区，查看和删除先前的储藏**

~~~
$ git status #可能有已add等待commit的变更，也可能有正在等待add的变更
$ git stash  #储藏
$ git status #已经没有变更了
$ git stash list #可看到先前所有使用stash储藏的工作进度，以及这些工作进度属于哪个分支，在分支的哪个Commit基础上工作
stash@{0}: WIP on master: 049d078 added the index file
stash@{1}: WIP on master: c264051 Revert "added file_size"
stash@{2}: WIP on master: 21d80a5 added number to log
$ git stash drop stash@{0} #丢弃stash@{0}
~~~

**只储藏（并重置）还没暂存的变更：**用来让已暂存（index）的变更先commit和上线，而其他内容临时储藏并重置，待暂存区commit之后，再恢复并继续之前的工作

~~~
$ git status -s
M  index.html  #已经在暂存区也修改了
 M lib/code.rb #只是在工作区被修改，没有add
$ git stash --keep-index #只储藏工作区，保持暂存区（又叫index）不变
$ git status -s
M  index.html  #提交到暂存区的变更没有被重置
~~~

**储藏（并重置）还没有被track的新文件**

~~~
$ git status -s
M  index.html
 M lib/code.rb
?? new-file.txt
$ git stash -u #等价于--include-untracked
git status -s
$ #空的，new-file.txt也被储藏并重置了
~~~

**交互式储藏（并重置）：**以hunk（diff块）的粒度决定哪些储藏哪些内容

~~~
$ git stash --patch
diff --git a/lib/code.rb blib/code.rb
...
Stash this hunk [y,n,q,a,d,/,e,?]? y
...
~~~

**用储藏的工作区进度创建新分支**：有时候直接恢复、会遇到merge冲突（比如某个文件在stash之后，又发生了新的变更），这时将储藏恢复到一个新分支上，然后就可以用分支merge/rebase来处理这些冲突

~~~
$ git stash branch some_branch_name stash@{1} 
创建一个分支，分支名称为some_branch_name，
分支内容基于创建stash@{1}时工作区所在的Commit
然后重放stash@{1}存储的变更，如果操作成功，stash@{1}会被删除
$ git stash branch some_branch_name #stash参数缺失时默认使用最新的一个stash即stash@{0}
$ git stash --help #查看该命令详细说明
~~~

**清理工作目录**

用来清理IDE、外部工具等产生的临时文件
有多种方法、执行的操作有差别，为了不误删代码文件，要小心使用

除了使用git stash -all，另一种比较安全的方式是用git clean时，先用-n检查、确认安全后再把-n改成-f

~~~
git clean #递归目录树，清理所有untracked的文件（未被git add的新代码文件会被误删）
git stash -all #作用同上，但是会在stash区域留一份备份
git clean -d -f
	#-d：表示不仅清理所有untracked文件，untrancked目录也被清理 (小心误删）
	#-f：表示强制执行
git clean -d -n
	#-n：表示预演一下，显示哪些文件会被删除，
git clean -d -f -x
	#-x：连.gitignore中匹配的文件也一起删除
git clean -d -x -i
	#-i: 以交互式的方式逐个文件确认
~~~

### 7.4 签署工作

从互联网上接手他人工作时，需要验证提交源是否可信，Git可以利用GPG来签署和验证工作

内容略，见P186-P189

### 7.5 搜索

用来快速查找代码和commit

#### 7.5.1 git grep

在工作目录，或者指定分支的代码中，查找指定的字符串，可用--and, --or设定多个字符串匹配条件，可定制输出格式

~~~~
$ git grep -n gmtime_r 
	#在工作目录中查找字符串gmtime_r，-n表示输出行号
$ git grep --count gmtime_r 
	#输出统计信息，工作目录中每个文件匹配该字符串多少次
$ git grep -p gmtime_r *.c 
	#在工作目录下所有*.c文件中查找字符串gmtime_r，并输出匹配位置属于哪个方法或函数
$ git grep --break --heading \ #美化输出格式
	-n \ #输出行号
	-e \ #接下来的参数是pattern表达式
	'#define' --and \( -e LINK -e BUF_MAX \) #同时匹配两个字符串的代码行
	v1.8.0 #在分支v1.8.0的代码中查找
~~~~

#### 7.5.2 git日志搜索

~~~
$ git log -S ZLIB_BUF_MAX --oneline 
	#增加/删除过ZLIB_BUF_MAX的commit(用来查找使用过这个变量的commit)
$ git log -L :git_default_bound:zlib.c
	#查看zlib.c中git_default_bound这个代码行(或函数)的修改历史
	#git会先把git_default_bound当做函数名，尝试找到函数的起止位置
$ git log -L '/unsigned long git_deflate_bound/',/^}/:zlib.c
	#与上一条命令效果相同，传入正则表达式告诉git如何定位函数起止位置
~~~

### 7.6 重写历史

#### 7.6.1 重写最近一次commit

git commit --amend 

会将新的commit合并到最新的一次commit中，但是也会改变这次commit的SHA-1值，更重要的是，与rebase原理相同，不要--amend已经push到远端仓库的commit

#### 7.6.2 修改多个commit message

用rebase -i进入交互式rebase模式，例如要修改最近三次提交及HEAD、HEAD^、HEAD^^，将这三个提交中最早的那次的父提交即HEAD^^^(等价于HEAD~3或HEAD~2^)传给git rebase -i命令

注意这是变基命令，HEAD、HEAD^、HEAD^^中任何一个都可能会被重写，因此不能用于已经push到远端的commit（会产生相同commit的不同版本，引发混乱）

~~~
 $ git rebase -i HEAD~2^ #重写最近3次提交HEAD,HEAD^,HEAD^^
 # 进入一个文本编辑器，显式如下内容（按从早到晚的提交顺序显式）
 # 要重写哪个commit，就把对应的"pick"改成edit
 pick f7f4f6d message_of_change_178 #按从早到晚顺序显式3次提交
 pick 23df21e message_of_change_179
 pick se1d3ag message_of_change_180
 # Commands: p, pick; r, reword; e, edit; s, squash; f, fixup; x, exec
 # 关闭编辑器之后，根据对编辑器的修改，继续交互过程
 # git会在标记为edit的commit前停下，此时
 #  执行git commit --amend可以修改commit message，
 #  执行git rebase --continue继续处理后面的commit
~~~

#### 7.6.3 重排commit的顺序，或者删除某个commit

~~~
 $ git rebase -i HEAD~2^ #希望重排HEAD,HEAD^,HEAD^^，将HEAD^^的父提交传入
 # 进入一个文本编辑器，显示如下内容（按从早到晚的提交顺序显式）
 pick f7f4f6d message_of_change_178
 pick 23df21e message_of_change_179
 pick se1d3ag message_of_change_180
 # commands: p, pick; r, reword; e, edit; s, squash; f, fixup; x, exec
 # 将其改为
 pick se1d3ag message_of_change_180
 pick 23df21e message_of_change_179
 # 关闭编辑器
 # git会按照se1d3ag(180)，23df21e(179)的顺序对HEAD^^重放commit patch
 # 以达到重排commit顺序的目的，而f7f4f6d(178）则被丢弃
~~~

注意事项同7.6.2：只能应用在还没push到远端的commit，一旦push就不能执行rebase

####7.6.4 压缩多个commit为一个

~~~
 $ git rebase -i HEAD~2^ #希望重排HEAD,HEAD^,HEAD^^，将HEAD^^的父提交传入
 # 进入一个文本编辑器，显示如下内容（按从早到晚的提交顺序显式）
 pick f7f4f6d message_of_change_178
 pick 23df21e message_of_change_179
 pick se1d3ag message_of_change_180
 # commands: p, pick; r, reword; e, edit; s, squash; f, fixup; x, exec
 # 将其改为
 pick f7f4f6d message_of_change_178
 squash 23df21e message_of_change_179
 squash se1d3ag message_of_change_180
 # 关闭编辑器
 # git会对f7f4f6d(178)的父commit依次重放178、179、180的patch
 # 并打开一个编辑器让你输出合并后commit的message
 # 编辑器关闭后合并完成，3个commit被合并成一个
~~~

注意事项同7.6.2：只能应用在还没push到远端的commit，一旦push就不能执行rebase

####7.6.5 将一个commit拆分为多个

方法：用交互式rebase撤销这个提交，然后使用stash多次部分暂存和提交

~~~
 $ git rebase -i HEAD~2^ #希望拆分HEAD,HEAD^,HEAD^^中的一个提交，将HEAD^^的父提交传入
 # 进入一个文本编辑器，显示如下内容（按从早到晚的提交顺序显式）
 pick f7f4f6d message_of_change_178
 pick 23df21e message_of_change_179
 pick se1d3ag message_of_change_180
 # commands: p, pick; r, reword; e, edit; s, squash; f, fixup; x, exec
 # 将要拆分的提交(179)改为edit
 pick f7f4f6d message_of_change_178
 edit 23df21e message_of_change_179
 pick se1d3ag message_of_change_180
 # 关闭编辑器
 # git会对f7f4f6d(178)的父commit依次重放f7f4f6d(178)、然后再179暂停，依次输入如下命令
 $ git reset HEAD^ #180是HEAD，179就是HEAD^，reset命令后面讲
 $ git add README #拆分出的第一个commit只包含对README的更改
 $ git commit -m "message_of_change_179_1"
 $ git add lib/code.rb #拆分出的第二个commit只包含对code.rb的更改
 $ git commit -m "message_of_change_179_2"
 $ git rebase --continue #继续重放后面的commit
 # 这组命令执行完之后，3个commit被拆分成4个，用git log可以查看
 $ git log -4 --pretty=format:"%h %s"
~~~

注意事项同7.6.2：只能应用在还没push到远端的commit，一旦push就不能执行rebase

###7.6.6 用于大面积修改历史记录的filter-branch命令

注意：与7.6.2/3/4/5一样，这条命令会大面积修改历史commit的SHA-1，除非项目还没有公开，也没人打算在被重写的commit上开展工作，否则不该使用此命令

**从所有commit中删除某个文件**：例如一个意外提交的巨大二进制文件、或者包含了密码的文件，同时又准备把这个项目开源

~~~
$ git filter-branch --tree-filter 'rm -f passwords.txt' HEAD
备注：
1.--tree-filter会把HEAD所有前置的commit都checkout出来，执行一次rm -f passwords.txt命令，再提交进去
2.最好在测试分支上执行上面的命令，确定没问题，再重置master

$ git filter-branch --tree-filter --all 'rm -f passwrods.txt' HEAD 
在所有分支上执行历史记录修改
~~~

**将子目录设置成新的根目录**：例如从svn代码库迁移过来的项目，想让trunk目录成为之后每次提交的根目录，与trunk无关的提交都删除

~~~
$ git filter-branch --subdirectory-filter trunk HEAD
~~~

**全面修改电子邮件地址**：例如项目要开源，需要把邮箱地址改成非公司内部邮箱

~~~
$ git filter-branch --commit-filter '
	if [ "$GIT_AUTHOR_EMAIL" = "schacon@localhost" ];
	then
		GIT_AUTHOR_NAME="Scott Chacon";
		GIT_AUTHOR_EMAIL="schacon@example.com";
		git commit-tree "$@";
	else
		git commit-tree "$@";
	fi' HEAD
~~~

要用--commit-filter参数，并且要小心，只修改自己的邮箱地址，但是所有历史commit的SHA-1都会被修改。

### 7.7 重置（git reset)与检出（git checkout)

git reset与git checkout的区别是git最令人困惑部分之一，本节介绍

#### 7.7.1 三棵树（三个区域）

对后面几小节常提到的概念做一个说明：

* **HEAD**：当前分支引用的指针，指向该分支上最后一次commit，也是下一个commit的父commit
* **索引（index）**：暂存区，git checkout时暂存区的内容会和HEAD的内容相同，随后git add时，会把更改写在暂存区，执行各种git命令时，输出中的index指的就是暂存区
* **工作目录**：除去.git目录，余下的就是工作目录，即写代码的哪些目录

~~~
$ git cat-file -p HEAD #输出HEAD的内容可证明HEAD是分支最后一个commit的指针
tree cfda3br379e4f8dba8717dee55aab78aef7f4daf
author Scott Chacon 1301511835 -0700
commiter Scott Chacon 1301511825 -0700

initial commit

$ git ls-tree -r HEAD #查看HEAD指向的树都有什么Object
100644 blob a906cb2a5a904a152... README
100644 blob 8f94139338f9404f2... Rakefile
040000 tree 99f1a6d12cb4b6f19... lib

$ git ls-files -s #查看index（暂存区）都有哪些文件
100644 a90cb2a4a904a152e80877d4088654daad0c859 0 README
100644 8f94139338f9404f26296bef88755fc2598c289 0 Rakefile
100644 47c6340d6459e05787f644c2447d2595f5d3a54 0 lib/code.rb

如果是还没有git add过的暂存区，可以看到暂存区文件的SHA-1与HEAD的相同

$ tree #看工作目录下的目录结构和文件名
~~~

### 7.7.2 关于HEAD，index，工作目录的工作流（图解演示P119-P202)

~~~
git init #创建本地Git仓库，HEAD和master指向一个尚未出现的分支
vim file.txt #工作目录增加file.txt文件，版本号取名为v1
git add file.txt #index也增加file.txt文件，版本为v1
git commit #Git仓库增加一个Commit，Commit包含file.txt版本v1，HEAD指向master，而master指向这个新的Commit
git status #看不到有改动，因为此时三棵树都相同
vim file.txt #工作目录的file.txt版本更新为v2
git status #会提示file.txt属于"Changes not staged for commit"
git add file.txt #暂存区的file.txt版本增加到v2
git status #会提示file.txt属于"Changes to be committed"
git commit #Git仓库新增一个Commit，包含版本为v2的file.txt
该Commit的父Commit是上次执行git commit生成的
HEAD指向master，而master指向这个新的Commit
git status #看不到有改动，因为此时三棵树又全部相同了
~~~

备注：切换分支或者克隆也差不多，切换分支时Git会修改HEAD使其指向分支的最新Commit，其他都与上面的代码演示相同

###7.7.3 用git reset回滚当前分支

在上面7.2.2一组命令操作完毕之后，远端仓库HEAD及master、本地仓库HEAD及master、暂存区、工作目录都是最新版本的代码，lib/code.rb都是v3版本。此时执行**git reset**会执行三步操作

以git reset --hard HEAD~为例（HEAD~表示reset到HEAD的父提交）

1. **回滚本地仓库：回退本地HEAD所指向的目标(master,branch)：** 

	当前HEAD指向master、master指向v3的CommitObject。git reset会移动HEAD指向的目标（即master），让master由指向v3改为指向其父提交v2
	
	这一步只修改本地Git仓库，不修改暂存区(index)和工作目录，效果相当于撤销了先前的git commit操作

2. **更新索引（暂存区）：回滚暂存区的内容** 
	
	将索引区的代码版本也从v3回滚到v2，效果相当于撤销git add操作

3. **更新工作目录：**

	将工作目录的代码版本也从v3回滚到v2，效果相当于撤销工作区的编辑操作（有丢代码的危险）

~~~
git reset --soft HEAD~ 
	# 只执行上面的第1步，以便从新git commit
git reset --mixed HEAD~ 
	# 只执行上面的第1、2步，以便重新git add和commit
git reset --hard HEAD~ 
	# 执行上面的1、2、3步，包括工作区在内全部回滚
	# 注意使用--hard参数有丢代码的风险
git reset HEAD~   
	# 相当于git reset --mixed HEAD~，
	# 将当前HEAD所指向的目标（master或其他分支）回滚到其父提交
git reset 9e5e6a4 
	# 将当前HEAD所指向的目标（master或其他分支）回滚到commit 9e5e6a4
~~~

#### 7.7.4 用git reset回滚某个文件或目录

跳过上面的第1步，但是可以执行第2步和第3步，参数规则与前面的相同
注意第1步（回退HEAD所指向的目标）被跳过了，因此第2、3步的效果也发生了变化，将不再是从HEAD~取代码，而是直接从HEAD取代码（如果没指定用哪个commit的文件回退）

~~~
git reset --mixed lib/code.rb 
	# 执行步骤2，回滚暂存区的code.rb到
git reset lib/code.rb 
	# 与上一条命令效果相同，默认是--mixed
git reset HEAD lib/code.rb
	# 与上一条命令效果相同，对于文件或目录，默认回退目标就是HEAD
git reset --hard lib/code.rb 
	# 执行步骤2、3
	# 暂存区工作目录的code.rb都回滚到前一个commit中的版本
	# 注意使用--hard参数会有丢代码的风险
git reset eb43bf8 lib/code.rb
	# 将暂存区lib/code.rb回退到commit eb43bf8的版本
~~~

#### 7.7.5 用git reset将多个commit压缩为一个

7.6.4介绍了用git rebase -i来把多个commit压缩为1个，本节使用git reset命令来压缩，更加简单

~~~
git reset --soft HEAD~2 
	#将本地仓库中HEAD指向的目标（也就是当前分支的头部）指向HEAD~2
git commit
	#重新提交暂存区中的变更，之前HEAD、HEAD~1指向的commit会被合并成一个commit提交
~~~

### 7.7.6 git checkout与git reset的区别

#### 作用在分支上时

           | git checkout [branch] | git reset --hard [branch]
---------  | --------------------- | -------------------------
对于工作目录 | 进行merge              | 简单覆盖
对于HEAD   | HEAD移动到branch的头部   | 会让当前HEAD指向的分支头部，也指向branch [*]

备注[\*]：这句话的意思是，比如当前在master分支下，HEAD指向master

* 执行git checkout iss52只是将HEAD切换到分支iss52
* 执行git reset --hard iss52，除了HEAD，master也会指向iss52的最新一个commit（**整个master分支都被改了**）

#### 作用在文件/目录上时

**git checkout lib/code.rb**与**git reset --hard lib/code.rb**一样，会从HEAD取出git仓库中的版本，覆盖暂存区（index）、工作目录下的lib/code.rb文件

**git checkout --patch lib/code.rb**可以以交互式的方式，逐个hank（diff块）的方式来选择要覆盖哪些代码

###7.7.7 git reset小结

HEAD/REF一列：HEAD表示是修改HEAD、REF表示是修改

命令                     | HEAD/REF | index | 工作目录 | 工作目录是否安全 
------------------------|----------|--------|--------|--------------
reset --soft [commit]   | REF      | 否     | 否      | 是
reset [commit]          | REF      | 是     | 否      | 是
reset --hard [commit]   | REF      | 是     | 是      | 否（覆盖工作目录)
checkout [commit]       | HEAD     | 是     | 是      | 是 (merge到)
reset (commit) [file]   | 否       | 是     | 否      | 是 
checkout (commit) [file]| 否       | 是     | 是      | 否 (覆盖指定文件)

### 7.8 高级合并(merge)技巧 

用于很久都无法迅速合并的两个分支，**注意合并之前先确保工作目录是干净的（可以用git stash暂存，或者将工作目录的更改提交到一个临时分支上）**

####7.8.1 合并冲突

#####一、简单冲突：冲突原因可以立即定位

**创造合并冲突场景**

~~~
$ git checkout -b branch1 #创建分支branch1
$ unix2dos hello.rb #每一行都被这个命令修改了，确保之后能出现合并冲突
$ git commit -am "modify 1 on branch1"
$ vim hello.rb #修改一行(A)
$ git commit -am "modify 2 on branch1"

$ git checkout master #切换到master分支
$ vim hello.rb #修改另一行(B)
$ git commit -am "modify 1 on master"

$ git merge branch1 #因为两个分支视图把同一行改成不同的样子，引发merge冲突
~~~

**选择A：放弃合并**

~~~
 $ git status -sb #上一步merge一半冲突后，status显式工作目录、暂存区都被更改了
 ## master
 UU hello.rb
 $ git merge --abort #放弃merge
 $ git status -sb #现在已经没有更改了，就像没有merge过一样
 ## master
~~~

备注：这里git merge --abort效果与git reset --hard HEAD相同，但是注意这两个命令都会覆盖暂存区和工作目录，要确定这两个位置的变更都不需要了

**选择B：merge时忽略white space**

上面例子的冲突时因为两个分支改了同一行，其中一个分支的修改是修改这行行尾的white space

~~~
$ git checkout master
$ git merge -Xignore-space-change branch1 #合并成功
~~~

**选择C：手动处理merge冲突**

在7.8.1末尾git merge branch1合并冲突时，另一个选择是手动处理冲突

step1：拿到冲突文件的3个版本（共同祖先版本，个人版本，对方版本）

~~~
$ git show :1:hello.rb > hello.common.rb #共同祖先
$ git show :2:hello.rb > hello.ours.rb #个人版本
$ git shwo :3:hello.rg > hello.theirs.rb #对方版本
原理：:${n}:hello.rg其实是git ls-files -u plumbing输出文件的简写形式
$ git ls-files -u
100755 ac51efdc3df4f45fd328d1a02ad05331d8e2c9111 1 hello.rb #第三列是1
100755 35c06c8752c78d2aff89571131f3bf7941a7b53ca 2 hello.rb #第三列是2
100755 e85207e04dfdd5eb0a1e9febbc67fd837c5501cd1 3 hello.rb #第三列是3
~~~

step2：手动修复，冲突原因是branch1把行尾换行符改成DOS模式了，其实应该更正为unix

~~~
$ dos2unix hello.theirs.rb #手动解决冲突
$ git merge-file -p \
	hello.ours.rb hello.common.rb hello.theirs.rb > hello.rb 
	# 用merge-file命令在本地merge这个文件
	# 接下来检查merge后的代码对不对(-b表示不显示white space的diff）
$ git diff -b #对比工作区代码与暂存区代码的差别，即合并会引入哪些变化
$ git diff --ours #对于工作区代码与个人分支的差别
$ git diff --theirs -b #对比工作区代码与合入的branch1的差别
$ git diff --base -b #对比工作区代码与共同祖先的差别
$ git clean -n #清理前先确认不会误删文件
$ git clean -f 
	#清理合并时生成的3个临时文件
	#hello.ours.rb, hello.common.rb, hello.theirs.rb
~~~

**选择D：直接选择一方的版本，不进行手动merge**

~~~
git checkout --ours
git checkout --theirs
~~~

##### 二、复杂冲突：两个分支分叉后各自修改较复杂，冲突原因不容易定位

**背景**

~~~
$ git log --graph --oneline --decorate --all #两个分支都有较多commit
* f1270f7 (HEAD,master) msg1
* 9af9d3b msg
* 494971d msg
| * e3eb223 (branch1) msg
| * 7cff591 msg
| * c3ffff1 msg
| /
* b7dcc89 msg
$ git merge branch1
CONFILCT(content): Merge conflict in hello.rb
$ # 打开hello.rb，可以看到在发生冲突的行、HEAD和branch1的版本都是什么
~~~

**用diff3格式的冲突标记来查看两个分支都是如何修改的**

~~~
$ git checkout --conflict=merge hello.rb #重置到上面刚发生冲突时的状态，用于重新解决冲突
$ git checkout --conflict=diff3 hello.rb #重置到发生冲突时的状态，但不仅仅在文件中添加ours,theirs的代码版本，还会添加共同祖先的代码版本
$ git config --global merge.conflictstyle diff3 #可以把默认的merge改为diff3
~~~

**用加--merge, --left-right，-p选项及三点语法的git log命令，定位两个分支的冲突修改各是在哪个commit中引入的**

~~~
$ git log --oneline --left-right HEAD...MERGE_HEAD #从共同祖先开始两个分支的commit
< f1270f7 msg_master_3 #当前HEAD
< 9af9d3b msg_master_2 
< 694871d msg_master_1
> d3eb223 msg_branch_3 #branch1上最后一次commit
> 7cff591 msg_branch_2
> c3ffff1 msg_branch_1
~~~

给上面命令加上--merge选项，就会只显示在合并的任意一侧，接触了冲突文件的哪些提交

~~~
$ git log --oneline --left-right --merge
< 694871d msg_master_1
> c3ffff1 msg_branch_1
~~~

如果给上面命令在加上-p选项，就可以看到冲突文件之间的diff，方便快速定位冲突成因

~~~
$ git log --oneline --left-right --merge -p
~~~

**git merge冲突时，git diff的输出会增加组合式差异输出，能够帮助判断**

执行git merge并报告冲突时，git diff会显示ours,theirs的代码版本

~~~
$ git diff
diff --cc hello.rb
...
++<<<<<< HEAD
 + puts 'hola world'
++======
+  puts 'hello mundo'
++>>>>>> branch1
...
~~~

解决冲突之后，git diff显示内容如下（注意“+”位置）

~~~
$ git diff
diff --cc hello.rb
 + puts 'hola world' #这行显示的是解决前ours的代码版本
+  puts 'hello mundo' #这行显示的是解决前theirs的代码版本
++ puts 'hola mundo' #这行显示解决冲突后工作目录中的代码版本
~~~

**分支合并后查看冲突是如何解决的**

用git show命令

~~~
git show [merge_commit]
~~~

或者用-cc选项来让git log -p显示merge commit（没有-cc时只显示非merge的commit）

~~~
$ git log -cc -p 1
# -cc：显示非merge的commit
# -p：显示diff
# 1：只显示最近的一个commit（就是刚刚commit的branch merge）
~~~

备注：显示内容与上面的相同，用组合式差异的方式来显示diff，可以看出ours、theirs，after-conflict-resolve的三个版本

###7.8.2 撤销合并（图解演示 P220-P222）

假定不小心把topic分支合并进了master（P220），现在想撤销

####方法1：重置HEAD到前一个commit

~~~
git reset --hard HEAD~
# 会把HEAD指针指向的引用（这个例子中是master以及topic），重置到更早的一个commit（即merge之前的commit），master被重置到merge之前master分支上的最新commit，topic被重置到merge之前topic分支上最新的topic
~~~

注意：只能用于自己还没有push的commit

####方法2：新建一个commit，在这个commit中把之前的merge回滚掉

用git revert相关的命令，操作较复杂，图解演示见P221-P222

~~~
git revert -m 1 HEAD 
	#追加一个revert commit，用于在代码层面回滚刚push的merge commit
	#-m 1用来指名哪个父节点是要保留的“主线”
	#	前面merge时，merge commit有两个父commit，一个是HEAD，一个是合入分支branch1的最新commit
	#	这里-m 1 HEAD表明当时的HEAD是要保留的“主线”，新建的revert commit会把并入的branch1 commit的代码revert掉，只保留从HEAD继承到的代码
~~~

这个操作的问题是，如果后悔revert了，想把branch1的代码重新merge进来
* 不能直接使用git merge branch1，见P222说明
* 需要先把刚提交的revert commit先revert掉，然后再git merge branch1

~~~
$ git revert ^M #追加一个新的revert commit，用来revert刚刚提交的旧revert commit
$ git merge branch1 #现在可以重新合并branch1了
~~~

### 7.8.3 其他类型合并

####1.ours或theirs偏好，或略whitespace变更偏好

~~~
$ git merge -Xignore-all-space [branch]
$ git merge -Xignore-space-change [branch]
$ git merge -Xours [branch] #冲突时以ours为准，非冲突代码merge进来
$ git merge -Xtheirs [branch] #冲突时以theirs为准，非冲突代码merge进来
~~~

类似地git merge-file --ours可以来进行单个文件的合并

~~~
$ git merge -s ours branch 
$ #与-Xours不同，-s ours会暴力地直接用整个ours版本作为合并结果
~~~

####2.子树合并：两个项目，一个项目映射到另一个项目的子目录中

git可以发现分支A的子目录，其实是另一个分支B
分支B与分支A存储的项目不是同一个，分支B存储了分支A的一个子项目
通过这种机器，git提供了一个类似submodule工作流，代码更加集中（在一个项目中），代价是操作更加复杂

~~~
$ #在自己的项目的工作目录下执行下列命令
$ git remote add sub_proj https://github.com/rack/sub_proj
$ git fetch sub_proj
$ git checkout -b sub_proj_branch sub_proj/master

$ #现在master分支是自己的项目，而sub_proj_branch分支是sub_proj项目的根目录，目前仓库里所有分支不是都属于同一个项目
$ git checkout sub_proj_branch
$ ls
lib code_of_sub_proj_branch.rb 
$ git checkout master
$ ls
lib code_of_master.rb

$ #把sub_proj_branch分支拉取到master分支的sub_proj子目录中
$ git read-tree --prefix sub_proj -u sub_proj_branch
$ git commit -am
$ ls
lib code_of_master.rb sub_proj #sub_proj已经添加为master的子树

$ #之后sub_proj_branch在远端有了更新，可以直接拉取更新到sub_proj_branch
$ git checkout sub_proj_branch
$ git pull
$ #再把sub_proj_branch的更新merge到master的子目录sub_proj中
$ git checkout master
$ git merge --squash -s recursive -Xsubtree=sub_proj_branch sub_proj
~~~

其实也可以反向更新，先更新sub_proj子目录，再将更新推送到sub_proj_branch上

比较工作区子目录与子项目分支的代码区别

~~~
$ git diff-tree -p sub_proj_branch #子目录与本地分支比较
$ git diff-tree -p sub_proj_branch/master #子目录与远程分支的最新版本比较 
~~~

### 7.9 rerere

用来“重用记录过的解决办法（reuse recorded resolution）”，例如

* 维护长期的topic分支，希望能干净第合并，但又不像提交太多merge commit
* 维护变基分支，不用每次处理相同的编辑冲突
* 对一个已经resolve冲突的分支进行变基，避免重新解决这些相同的冲突
* 将多个topic分支合并到一个可测试的头部，测试失败时撤销合并，不使用导致测试失败的topic分支，而已经解决过的合并冲突，日后在遇到时不需要再重新解决了

让rerere默认启用

~~~
git config --global rerere.enabled true
~~~

开启后git merge会复用之前相同冲突的resolve

~~~
$ git merge some_branch
...
CONFICT (content): Merge conflict in hello.rb
**Recorded preimage for 'hello.rb'** #这行输出表示有resolve可复用
Automatic merge failed; fix conflicts and then commit the result.
$ git status
...
both modified: hello.rb #可以查看复用resolve对hello.rb做的修改
$ git rerere status #可以查看rerere用在哪些文件上了
$ git rerere diff 
$ #可以查看rerere当前所使用的解决方案（解决前分支1，解决前分支2，当前工作区） P227
$ git ls-files -u #可以查看冲突文件版本
$ vim hello.rb #如果对rerere复用的resolve不满意，在这里修改
$ git rerere diff #可以观察到rerere解决方案发生了变化
$ git add hello.rb #确认resolve完成
$ git commit #提交
~~~

现在对这个合并好之后的HEAD进行测试，如果测试满意，希望将分支变基到master上时，就不用再重新resolve merge了

~~~
$ git reset --hard HEAD^ #撤销上一个merge commit（包括本地仓库、暂存区、工作区）
$ git checkout branch1 #切换到branch1
$ git rebase master #将branch1变基到master
...
Resolved 'hello.rb' using previous resolution #自动解决冲突
...
$ git diff #用diff命令，可以在组合式差异输出中，看到两个分支以及解决方案的代码版本
$ git checkout --conflict=merge hello.rb 
$ #也可以取消rerere的解决方案，回到刚发现冲突时的代码状态，以便手动resolve
$ git rerere #也可以再次执行rerere，复用前面的解决方案
$ git add hello.rb #确认冲突解决
$ git rebase --continue #继续完成变基
~~~

### 7.10 使用Git调试

#### 7.10.1 git blame

查看代码文件中每一行是哪次提交、被谁引入的，各输出列的内容以及原理见P231

~~~
$ git blame -L 12,22 code.rb #用-L参数指定blame哪几行
$ git blame -C -L 141,153 code.rb 
$ #用了-C参数后，即使code.rb被改过文件名，也能被git blame命令跟踪到
$ #因为Git是通过记录快照来跟踪文件修改的、而不是通过记录文件名
~~~

#### 7.10.2 git biset 二分查找定位问题

在一组commit记录中，用二分查找的方式不断检出代码，执行传入的测试脚本，以定位问题是被哪个commit引入的

P232

### 7.11 git submodule 子模块

场景：想将两个项目独立对待，但同时又想在一个项目中使用另一个项目的代码

* 方法1：一个项目作为另一个项目的代码库，不方便对代码库进行自定义
* 方法2：把子项目复制到父项目中，无法借用上游子项目新的变更
* 方法3：用git submodule或者7.8.3中的子树合并

#### 7.11.1 向repo中添加submodule

~~~
$ git submodule add https://github.com/chaconinc/DbConnector
$ # 默认本地仓库中submodule的名称会与远程同名，都是DbConnector
$ git status 
$ # 发现新增了.gitmodules文件和DbConnector目录
$ # cat .gitmodules会发现内容包括子模块名、本地路径、远程url
$ git diff --cached DbConnector
$ # 发现diff内容是Subproject commit ${SOME_SHA-1}
$ # 说明Git并不会跟踪子项目的内容，而是把子项目的加入当做是长裤的一次特殊提交
$ git diff --cached --submodule
$ # 功能与前面的相同，只是输出格式更美观些
$ git commit -am "added DbConnector module"
$ git push origin master
~~~

#### 7.11.2 克隆含有子模块的项目

~~~
$ git clone https://github.com/chaconic/MainProject #目录DbConnector是空的
$ git submodule init
$ git submodule update
$ # 现在目录DbConnector有文件了
~~~

另一种方法

~~~
$ git clone --recursive https://github.com/chaconinc/MainProject
~~~

#### 7.11.3 开发含有子模块的项目

##### 1.拉取子模块上游的变更

使用子模块的最新版本（todo：如何锁定子模块版本）

~~~
$ cd DbConnector
$ git fetch
$ git merge origin/master
$ cd -
$ git diff --submodule #可以看到子模块的版本已经被更新了
$ git config --global diff.submodule log #开启配置，diff时默认使用--submodule
$ git diff #现在直接使用git diff就可以
~~~

上面的操作有简便方法，可用于单个子模块，也可以用于所有子模块

~~~
$ git submodule update --remote DbConnector #单个子模块DbConnector
$ git submodule update --remote #所有子模块
~~~

git submodule update命令默认跟中子模块仓库的master分支，下面的命令修改配置，让子模块跟踪一个指定的分支，比如名为stable的分支（也可以通过修改.gitmodules文件或者本地.git/config文件来设置）

~~~
$ git config -f .gitmodules submodule.DbConnector.branch stable
$ #-f是为了保证这个修改不仅仅是对自己有效，还能推送到远程仓库，对其他人也生效
$ git submodule update --remote #这次是从stable分支update
$ git config status.submodulesummary 1 #配置git status时显式子模块摘要
$ git status #可看到子模块有更新
$ git config --global diff.submodule log #配置diff时包含子模块变更
$ git diff #可看到子模块配置中增加了branch = stable
$ git commit -am "xxx"
$ git log -p --submodule #可以在git log中也看到子模块的变更记录
$ git push origin master
~~~

##### 2.使用子模块：P239-241

**用途：**同时修改子模块和主项目，同时提交和发布这些变更

**问题：**git submodule update时，虽然得到了自仓库的变更，但子仓库会停留在“detached HEAD”的状态，并没有本地分支去跟踪这些变更，对子仓库做出的修改也无法被跟踪

**解决：**让子模块可以被修改，避免出现分离式HEAD的情况

**选择1**：从submodule远程分支更新时，用--merge来让其与本地分支合并，现在

~~~
cd DbConnector #进入submodule的本地目录
git checkout stable #切换到submodule的stable分支
git submodule update --remote --merge #拿到仓库变更后还要用merge选项让其与本地分支merge
~~~

**选择2**：从submodule远程分支更新时，用--rebase来把本地修改也变基上去

~~~
cd DbConnector #进入submodule的本地目录
vim src/db.c #修改submodule的代码
git commit -am "unicode support" #提交对submodule的修改
git submodule update --remote --rebase #来取远端的分支到本地，同时用rebase重放本地的修改，使其变基到本地分支上
~~~

**选择3**：从submodule远程分支更新时，忘了使用--merge或--rebase参数，导致submodule处于分离式HEAD的状态时，重新用选择1或者2执行一遍就好 (TODO:使用前先在试一下，P241）

~~~
$ cd DbConnector #进入submodule的本地目录
$ git submodule update --remote #忘了加--merge或者--rebase
$ git submodule update --remote --merge #加上merge重新执行
$ #如果遇到merge冲突，像之前一样处理冲突就可以
~~~

##### 3.发布子模块的变更：P241-P242

查看有哪些导致submodule变更的commit（有些是从远端引入，有些是本地的）

~~~
cd DbConnector
git diff
~~~

**问题**：如果对子模块的本地分支做了变更，此时主模块很可能已经依赖了这些变更。如果只push主模块（子模块的变更不会一起被push到远端），其他人使用主模块就会产生问题

**解决**：

push主模块时加上--recurse-submodules=check或on-demand选项，让git检查子模块是否被推送

~~~
$ # 推送主模块时，如果子模块没推送，git会报错
$ git push --recurse-submodules=check 
$ # 推送主模块时，如果子模块没推送，git会尝试把子模块一起推送
$ git push --recurse-submodules=on-demand
~~~

##### 4.合并子模块的变更：P242-P244

多人的项目同时引用一个子模块时，子模块合并会遇到出现分叉，需要merge的情况

报"merge following commits not found" 是git试图在submodule本地找一个合并提交（该提交的父提交是两个分叉分支的头部），来自动合并，如果找到，会报"Found a possible merge resolution for the submodue:"并"Auto-merging DbConnector" 

如果找到可以复用的merge commit，建议按照P245的方法手动复用，来有机会验证操作是否有效。如果找不到，按照下面的方法来合并。

~~~
$ git pull #拉取子模块更新
Fetch submodule DbConnector
warning: Failed to merge submodule DbConnector (merge following commits not found)
Auto-merging DbConnector
~~~

遇到冲突时的解决方法

1. 在主模块目录，找到两个冲突的子模块分支的头部提交

	~~~
	$ git diff #找到两个冲突分支的头部commit的SHA-1
	diff --cc DbConnector
	index eb41d76,c771610,..000000 #eb41d76是共有提交，c771610是从远端pull下来的提交
	...
	$ 
	~~~

2. 在子模块目录，合并两个分支

	~~~
	$ cd DbConnector #submodule的本地目录
	$ git rev-parse HEAD
	eb41d76... #共有提交的SHA-1
	$ git branch try-merge c771610 #merge从远端pull下来的提交，这时会报出合并冲突
	$ vim src/main.c #修复冲突代码
	$ git add src/main.c #确认冲突解决
	$ git commit -am ',merged our changes' #创建一个merge commit
	~~~

3. 在主模块目录，再次检查子模块的SHA-1值

	~~~
	$ git diff
	diff --cc DbConnector
	index eb41d76,c771610..000000 #eb41d76是共有提交，c771610是从远端pull下来的提交
	... #下面的组合差异显式了submodule冲突如何解决
	-  Subproject commit eb41d76... #共有commit（本地）
	 - Subproject commit c771610... #从远端pull下来的提交（远端）
	++ Subproject commit 9fd905e... #新建的merge commite
	~~~

4. 确定更新主模块对子模块的引用为新的merge commit，并提交

	~~~
	git add DbConnector
	git commit -m "Merge Tom's Changes"
	~~~

#### 7.11.4 子模块的技巧：P245-P246

##### 1.foreach命令

略

##### 2.alias

略

#### 7.11.5 子模块的问题：P246-P247

##### 1.主模块分支切换问题

**问题场景**：(1) 主模块在分支中新加入子模块 (2) 主模块切换到没有子模块的分支 (3) 此时本地多出来一个未被跟中的子模块目录

~~~
$ git checkout -b add-crypto #主模块建新分支
$ git submodule add https://github.com/chaconinc/CryptoLibrary
$ git commit -am "adding crypto library" #主模块新分支中加了新的子模块
$ git checkout master #切换回master分支
$ git status #多出来一个未被track的子目录
Untracked files
	CryptoLibrary/
~~~

**解决**

进主模块目录时清理，在子模块目录是重新update --init

~~~
$ git checkout master 
$ git clean -fdx #在主模块目录时清理
$ git checkout add-crypto
$ ls CryptoLibrary/ #因为被清理了，是空的
$ git submodule update --init #重新拉取子模块数据
$ ls CryptoLibrary/
Makefile includes scripts src #有数据了
~~~

##### 问题2：把主模块的子目录转换为子模块

背景：主模块下有个CryptoLibrary，假设已经新建了一个项目https://github.com/chaconinc/CryptoLibrary，并把CryptoLibrary中的代码都迁移上去了，现在要把CryptoLibrary由子目录的形式改成子模块

问题1：直接用rm -Rf会报错，要用git rm -r 

~~~
$ git checkout dev_branch
$ rm -Rf CryptoLibrary/
$ git submodule add https://githubcom/chaconic/CryptoLibrary/ #报错，添加不进来
$ #正确方法
$ git rm -r CryptoLibrary
$ git submodule add https://githubcom/chaconic/CryptoLibrary
~~~

问题2：切到主模块另一个分支时会报错，要加上-f来checkout

~~~
$ git checkout another_branch #接下来切换到另一个分支时
$ #报错，报告CryptoLibrary会被覆盖
$ git checkout -f another_branch
~~~

问题3：切换回之前新建子模块的目录时，代码已经被覆盖了，要git checkout .

~~~
$ git checkout dev_branch
$ git submodule update #没有用，依然拿不到子模块的数据
$ git checkout . #可以找回子模块的数据
~~~

TODO：自己敲命令验证一下

### 7.12 打包

TODO

































​	

