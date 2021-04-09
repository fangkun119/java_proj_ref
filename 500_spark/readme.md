<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
<!--**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*-->

- [Spark In Action](#spark-in-action)
  - [CH01 Spark介绍及环境设置](#ch01-spark%E4%BB%8B%E7%BB%8D%E5%8F%8A%E7%8E%AF%E5%A2%83%E8%AE%BE%E7%BD%AE)
  - [CH02 Spark基础](#ch02-spark%E5%9F%BA%E7%A1%80)
  - [CH03 Spark脚本编写和执行](#ch03-spark%E8%84%9A%E6%9C%AC%E7%BC%96%E5%86%99%E5%92%8C%E6%89%A7%E8%A1%8C)
  - [CH04 Spark API深入](#ch04-spark-api%E6%B7%B1%E5%85%A5)
  - [CH05 Spark SQL](#ch05-spark-sql)
  - [CH06 Spark Streaming](#ch06-spark-streaming)
  - [CH07 MLLib](#ch07-mllib)
  - [CH08 分类和聚类](#ch08-%E5%88%86%E7%B1%BB%E5%92%8C%E8%81%9A%E7%B1%BB)
  - [CH09 图计算](#ch09-%E5%9B%BE%E8%AE%A1%E7%AE%97)
  - [其他](#%E5%85%B6%E4%BB%96)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Spark In Action

> 原书地址：[http://livebook.manning.com/book/spark-in-action/](http://livebook.manning.com/book/spark-in-action/)
>
> 代码Fork：[https://github.com/fangkun119/spark_in_action_first_edition](https://github.com/fangkun119/spark_in_action_first_edition)

## CH01 Spark介绍及环境设置

> 笔记：[ch01_intro_and_env_setup.md](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/notes/ch01_intro_and_env_setup.md)
>

主要内容

> 1. 搭建用于实验和开发的Spark虚拟机
> 2. 关于Spark的介绍

## CH02 Spark基础

> 笔记：[ch02_spark_fundamentals.md](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/notes/ch02_spark_fundamentals.md)
>

主要内容

> 1. 在Spark虚拟机中使用Spark Shell或者PySpark，日志配置
>
> 2. Spark简单例子：(1) 匿名函数 (2) 将函数定义存储在变量中、交给其他函数来执行
>
> 3. RDD：Resilient Distributed Dataset，Immutable
>
>     (1) 两种操作：transformation（产生新RDD）；actions（在RDD上进行计算，如count、foreach）
>
>     (2) `transformation`: filter, take, map, flatMap, distinct, sample, takeSample, take
>
>     (3) `actions`：count, foreach, collect, first, collect.mkString(",")
>
> 4. `RDD<double>`
>
>     (1) 隐式转换（Scala Implict Conversion）类的编写
>
>     (2) 获得`RDD<int>`、`RDD<double>`的统计信息（如mean、sum、variance、stdev等）
>
>     (3) 柱状图统计（histogram）
>
>     (4) 使用sumApprox, meanApprox来节省计算时间

## CH03 Spark脚本编写和执行

> 笔记：[ch03_writing_spark_applications.md](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/notes/ch03_writing_spark_applications.md)
>

主要内容

> 代码1：[https://github.com/fangkun119/spark_in_action_first_edition/blob/master/ch03/.../App.scala](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/ch03/scala/org/sia/chapter03App/App.scala)
>
> (1) 将Json文件加载到DataFrame
>
> (2) DataFrame分组聚合、Schema打印、打印前N行、排序、
>
> (3) 将一个文件加载到Set中、并广播到所有Spark节点
>
> (4) 编写UDF，并用于DateFrame过滤（根据某一列的值过滤）
>
> 代码2：[https://github.com/fangkun119/spark_in_action_first_edition/blob/master/ch03/.../GitHubDay.scala](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/ch03/scala/org/sia/chapter03App/GitHubDay.scala)
>
> (1) 加载一个目录下的所有Json文件（其他与代码1相同）
>
> 将上述脚本编译成Jar包提交到Spark上执行

## CH04 Spark API深入

> 代码：[[ch04-listings.scala](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/ch04/scala/ch04-listings.scala)] [[ch04-listings.py](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/ch04/python/ch04-listings.py)] [[ch04/java](https://github.com/fangkun119/spark_in_action_first_edition/tree/master/ch04/java)] 
>
> 笔记：[ch04_spark_api_in_depth.md](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/notes/ch04_spark_api_in_depth.md)
>

主要内容

`Pair RDD`：存储key-value对的RDD

> 1. 创建方法
>
>     (1) 自动返回Pair RDD的操作（如读取HDFS文件） ；(2) 使用`keyBy`；(3) 使用`map`把普通RDD转换成`Pair RDD`
>
> 2. 单个RDD上的操作
>
>     (1) 常用：计数`countByKey`；查询`lookup`；更改值`mapValues`；增加新的值`flatMapValues`；按key分组聚合`foldByKey`或`reduceByKey`；合并`union`；自定义聚合`aggregateByKey`
>
>     (2) 排序：`sortBy`、`sortByKey`、`groupByKeyAndSortValues`
>
>     (3) 分组：`groupBy`、`groupByKey`、`combineByKey`
>
>     (4) 使用`parallelize`把本地Collection转换成普通RDD（2）使用`:::`拼接两个List
>
> 3. 跨RDD的操作
>
>     (1) 联表：`join`、`leftOuterJoin`、`rightOuterJoin`、`fullOuterJoin`
>
>     (2) 删除共同值：`substract`、`substractByKey`；求交集：`intersection`
>
>     (3) 其他：`cogroup`（转换成 `key=>(itererate(1), itererate(2), ...)`）、`cartesian`、`zip`
>
> 4. RDD数据分区
>
>     (1) `HashPartitioner`、`RangePartitioner`
>
>     (2) 为Pair RDD的操作设置自定义Partitioner（会引发Data Shuffle）
>
>     (3) 产生Data Shuffle的情况：(a) 使用自定义Partitioner  (b) 导致Partitioner变更的操作（c）map/flatMap也可能引发Data Shuffle（d）显示指定`shuffle=true`
>
>     (4) 重新分区：`partitionBy`、`coalesce`、`repartition`、`repartitionAndSortWithinPartition`
>
>     (5) 按partition执行操作（不会有shuffle）：`mapPartitions`，`mapPartitionsWithIndex`，`glom`
>
> 5. RDD依赖：(1) 依赖`RDD`--(transformation)-->`RDD`；(2) `rdd.toDebugString`；(3) check point
>
> 6. 向所有节点共享数据：`Accumulators`和`broadcast`

## CH05 Spark SQL

> 代码：[[ch05-listings.scala](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/ch05/scala/ch05-listings.scala)] [[ch05-listings.py](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/ch05/python/ch05-listings.py)] [[ch05_java](https://github.com/fangkun119/spark_in_action_first_edition/tree/master/ch05/java)]
>
> 笔记：[ch05_spark_sql.md](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/notes/ch05_spark_sql.md)

主要内容

> 1. DataFrames：带有column name和column type类似数据表的分布式数据
>
>     **(1) 创建方法**：从RDD创建（需要设置schema）；运行SQL创建；通过载入外部数据创建
>
>     **(2) 基础API**： `select`， `drop`， `filter`/`where`， `limit`， `withColumnRenamed`，` withColumn`， `orderBy`，`show`，……
>
>     **(3) SQL Function**：嵌套在where语句中用于数据计算
>
>     **标量函数**：`abs`, `exp`, `substring`, `hypot`, `log`, `cbrt`, `length`, `concat`, `year`, `date_add`, `date_diff`, ...
>
>     **聚合函数**（与groupBy结合使用）：`avg`, `min`, `max`, `count`, `sum`, ...
>
>     **窗口函数**例如`max('score).over(Window.partitionBy('ownerUserId)) as 'maxPerUser')`等
>
>     **UDF**：自定义函数、例如`udf((tags: String) => "&lt;".r.findAllMatchIn(tags).length)`
>
>     **(4) 分组计算**：df.groupBy(...).agg(...)；上卷及Cube Function：类似OLAP Cube的上卷操作
>
>     **(5) 联表**：`inner`、`outer`、`left_outer`、`right_outer`、`leftsemi`、`leftanti`、……
>
> 2. 缺失值/异常值处理：(1) 删除所有有null值的数据；(2) 删除特定列为null的数据；(3) 默认值填充；(4) 替换某个特定值
>
> 3. DataFrame转换成RDD（需要对字符串做一些解码处理）；DataFrame转换成DataSet
>
> 4. Spark SQL配置：是否大小写敏感等
>
> 5. 在Spark中执行SQL、保存和加载数据表

## CH06 Spark Streaming

> 笔记：[ch06_spark_streaming.md](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/notes/ch06_spark_streaming.md)（TODO）

## CH07 MLLib

> 笔记：[ch07_mllib.md](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/notes/ch07_mllib.md)
>
> 代码：[ch07](https://github.com/fangkun119/spark_in_action_first_edition/tree/master/ch07)（scala, pySpark, java）

主要内容

> 1. 本地数据结构
>
>     (1) 向量、矩阵、稀疏向量、稀疏矩阵
>
>     (2) 与Breeze（底层的线性代数Lib）数据类型相互转换
>
> 2. 分布式数据结构
>
>     (1) RowMatrix（在RDD Vector中存储Row）；IndexedRowMatrix；CoordinateMatrix（通过下标i、j访问MatrixEntry）；BlockMatrix（存储`((i,j), Matrix)`）二元组
>
>     (2) 与Breeze数据类型相互转换
>
> 3. 线性回归例子
>
>     (1) 数据分析、模型训练、超参数调参、增加高阶多项式特征、Bias-Variance Tradeoff、使用Lasso或Ridge正则化、K折交叉验证、使用LBFG梯度下降提前停止来防止过拟合

## CH08 分类和聚类

> 笔记：[ch08_classification_and_clustering.md](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/notes/ch08_classification_and_clustering.md)
>
> 代码：[[ch08](https://github.com/fangkun119/spark_in_action_first_edition/tree/master/ch08)] (scala, pySpark, java)

主要内容

> 1. MLLib的机器学习Pipeline的各个概念：Transformers（ML组件）、Estimators（模型训练）、Evaluators（模型评估）、ParamMap（Pipeline参数、含模型超参数）
>
> 2. 使用`LogisticRegression`进行二分类
>
>     (1) 将文件数据转换为特征
>
>     (2) 缺失值处理
>
>     (3) 对类别特征进行one-hot编码
>
>     (4) 训练集验证集拆分、使用训练集训练、使用验证集验证
>
>     (5) ROC和PR曲线
>
>     (6) 交叉验证
>
> 3. 多分类：(1) 使用`OneVsRest`的方法及例子；(2) 使用Softmax方法的链接
>
> 4. 使用随机森林进行分类的例子
>
> 5. 使用Kmeans进行聚类的例子

## CH09 图计算

> 笔记：[ch09_graph_x.md](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/notes/ch09_graph_x.md) 
>
> 代码：[ch09-listings.scala](https://github.com/fangkun119/spark_in_action_first_edition/blob/master/ch09/scala/ch09-listings.scala)

主要内容：

> 1. GraphX API：
>
>     (1) 图对象创建`Graph`
>
>     (2) 图对象的Transforming：`mapEdges`、 `mapVertices`、 `mapTriplets`、 `aggregatingMessages`、`arrgegatingMessages`、 `subgraph`、 `mask`、 `filter`
>
> 2. Spark GraphX内置图算法举例：
>
>     (1) 最短路径算法
>
>     (2) Page Rank算法
>
>     (3) Connected Components算法
>
>     (4) Strongly Connected Components算法 
>
> 3. 实现图算法：以实现A*算法为例

CH09 - CH11:  Spark运维

> * 运行Spark
> * Spark独立集群
>
> * 在YARN和Mesos上运行Spark

CH13 - CH14：实例

> * 实时DashBoard
> * 使用H2O在Spark上进行深度学习

