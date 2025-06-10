# Elasticsearch 5：深度分页

## 1. 什么是深度分页

### 1.1 深度分页

处理大数据集查询时，访问很多页中、较后面页面时遇到的问题。 

例如，访问排序后数据列表第1000页之后页面时，带来大量的数据扫描和排序，负载极高，成为性能瓶颈。

### 1.2 Elasticsearch分页查询

#### (1) 过程

如果使用 ES默认的分页查询，大致过程如下：

数据存储在各个分片中：

1. 协调节点将查询请求转发给各个节点。
2. 各个节点执行搜索后，将排序后的前N条数据返回给协调节点。
3. 协调节点汇总各个分片返回的数据，再次排序，最终返回前N条数据给客户端。

#### (2) 问题

在分布式系统中，对结果排序的成本随分页的深度成指数上升。例如：如果要查询第 10000 ～ 10100 条数据，那么 N 就等于 10100。而协调节点二次排序，则是对所有节点返回的 10100 条数据、在 Heap 中排序。这个流程带来的问题就是，翻页越多，性能越差，甚至导致ES出现OOM。

ES为了避免用户在不了解其内部原理的情况下而做出错误的操作，设置了一个阈值：`max_result_window`：默认值为10000。

其作用是为了保护堆内存不被错误操作导致溢出。

## 2. 深度分页不推荐使用from + size

### 2.1 from + size分页及问题

在Elasticsearch中，分页查询的实现主要通过两个参数from和size来实现

* `from`参数指定了从结果集中的第几条数据开始返回

* `size`参数指定了返回数据的数量

通常分页代码是下面这样的：

```bash
GET /employee/_search
{
    "query": {
        "match_all": {}
    },
    "from": 0,
    "size": 5
}
```

当我们查询的数据页数特别大，例如 `from + size`大于10000的时候，就会报错："查询结果的窗口大小超过了最大窗口的限制"（`index.max_result_window`默认值为 10000，如果每页有 10 条数据，那么最多翻页到 1000 页）。

虽然可以调大`index.max_result_window`默认值。

```shell
PUT /employee/_settings
{
    "index.max_result_window": 20000
}
```

但这是不建议的，原因如下：这会导致所有分片上的检索都增加显著的内存和 CPU 使用率，导致性能下降，甚至节点崩溃。

### 2.2 from+size查询的优缺点及适用场景

优点：支持随机翻页。

缺点：

- 限于`max_result_window`设置，不能无限制翻页；
- 存在深度翻页问题，越往后翻页越慢。

适用场景如下：

- 小型数据集或者从大数据集中返回Top N(N≤10000)结果集的场景
- 主流PC搜索引擎中支持随机跳转分页的业务场景

## 3. 深度分页问题常见解决方法

### 3.1 避免使用深度分页

解决深度分页问题最好的办法就是避免深度分页。谷歌、百度目前作为全球和国内做大的搜索引擎不约而同的在分页条中删除了“跳页”功能，目的就是为了避免用户使用深度分页检索。

例如：

* 在百度中搜索“Elasticsearch”，在搜索结果中翻到第20页，就无法再往下翻页了。

* 淘宝虽然没有删除“跳页”功能，但不管我们搜索什么内容，只要商品结果足够多，返回的商品列表都是仅展示前100页的数据，其本质和ES中的`max_result_window`作用是一样的，都是限制你去搜索更深页数的数据。

* 手机端APP就更不用说了，直接是下拉加载更多，连分页条都没有，相当于你只能点击“下一页”。

### 3.2 Scroll Search滚动查询（ES7之后不再建议）

官方文档：[https://www.elastic.co/guide/en/elasticsearch/reference/8.14/paginate-search-results.html#scroll-search-results](https://www.elastic.co/guide/en/elasticsearch/reference/8.14/paginate-search-results.html#scroll-search-results)

#### (1) 特性

- 可从单个搜索请求中检索大量结果（甚至所有结果），这种方式与传统数据库中的游标（cursor）类似。
- Scroll滚动遍历查询是非实时的，数据量大的时候，响应时间可能会比较长。
- ES7之后官方已经不再建议使用scroll API进行深度分页。如果要分页检索超过Top 10,000+，推荐使用search_after。

#### (2) 使用步骤

第一次进行scroll查询：指定检索语句，同时设置上下文保留时间

scroll请求返回的结果反映了发出初始搜索请求时索引的状态，就像在那一个时刻做了快照，随后对文档的更改（写入、更新或删除）只会影响以后的搜索请求。

```shell
GET /kibana_sample_data_flights/_search?scroll=5m
{
    "query": {
        "term": {
            "OriginWeather": "Sunny"
        }
    },
    "size": 100
}
```

向后翻页，继续获取数据，直到没有要返回的结果为止

```shell
GET /_search/scroll
{
    "scroll": "5m",
    "scroll_id": "FGluY2x1ZGVfY29udGV4dF91dWlkDXF1ZXJ5QW5kRmV0Y2gBFnQ5MUF6M3dYUkhPQW81czY3RXBDckEAAAAAABkMUBZPOVotS1A1MlI1dU43QXFsdkRGUEhB"
}
```

多次根据`scroll_id`游标查询，直到没有数据返回则结束查询。采用游标查询索引全量数据，更安全高效，限制了单次对内存的消耗。

删除游标scroll

Scroll超过超时后，搜索上下文会自动删除。然而，保持scroll打开是有代价的，因此一旦不再使用，就应明确清除scroll上下文

```shell
DELETE /_search/scroll
{
    "scroll_id": "FGluY2x1ZGVfY29udGV4dF91dWlkDXF1ZXJ5QW5kRmV0Y2gBFmNwcVdjblRxUzVhZXlicG9HeU02bWcAAAAAAABmzRY2YlV3Z0o5VVNTdWJobkE5Z3MtXzJB"
}
```

#### (3) 优缺点及适用场景

优点：

- 支持全量遍历，是检索大量文档的重要方法
- 但单次遍历的`size`值不能超过`max_result_window`的大小（分成多次即可）

缺点：

- 响应是非实时的
- 保留上下文需要具有足够的堆内存空间
- 需要通过更多的网络请求才能获取所有结果

适用场景

- 大数量文档检索：检索的文档数量很大，甚至需要全量召回数据时
- 大数量的文档数据处理：例如索引迁移或将数据导入其他技术栈

注意：

* ES7.x之后不建议使用scroll API进行深度分页，推荐使用search_after。

### 3.3 search_after查询

#### (1) 介绍

工作原理：以前一页结果的排序值作为参照点，检索与这个参照点相邻的下一页数据

高效实用：

- 在后续翻页的过程中，即便有新数据写入等操作，也不会对原有结果集构成影响：后续的多个请求返回与第一次查询相同的排序结果序列。
- 规避了Scroll Search API滚动上下文成本高的问题：通过提供一个活动光标，使用上一页的结果来帮助检索下一页。

官方文档：[https://www.elastic.co/guide/en/elasticsearch/reference/8.14/paginate-search-results.html#search-after](https://www.elastic.co/guide/en/elasticsearch/reference/8.14/paginate-search-results.html#search-after)

#### (2) 使用步骤

##### 步骤1：创建索引的PIT

为避免出现跨页不一致问题，先创建一个时间点 (PIT) 以保留搜索中的当前索引状态。

* 跨页不一致问题：是指使用search_after翻页过程中有多个请求。如果在这些请求之间发生刷新，结果的顺序可能会发生变化，从而导致跨页面的结果不一致。

* PIT（Point In Time）：ES7.10开始才有的新特性、是索引数据状态的轻量级视图。PIT创建好之后，search_after的后续查询都会基于这个PIT视图进行的，能有效保障数据的一致性

创建PIT的请求如下，`keep_alive=5m`是一个类似于scroll的参数，表示滚动视图的保留5min，超时Elasticsearch会清除这个滚动视图并报错

```shell
POST /kibana_sample_data_flights/_pit?keep_alive=5m
```

返回结果如下，会返回一个PID的值

```shell
{
    "id": "4YyPBAEaa2liYW5hX3NhbXBsZV9kYXRhX2ZsaWdodHMWZENSdWh0NWNSai1EdUhpcnBCZXgyZwAWTzlaLUtQNTJSNXVON0NxbHZERlBIQQAAAAAAABkI4hZ0OTFBejN3WFJIT0FvNXM2N0VwQ3JBAAEWZENSdWh0NWNSai1EdUhpcnBCZXgyZwAA"
}
```

##### 步骤2：根据PIT首次查询

创建基础查询语句：包括 query 及排序条件，创建好的 pit，每页包含多少个查询结果

```shell
GET /_search
{
    "query": {
        "term": {
            "OriginWeather": "Sunny"
        }
    },
    "pit": {
        "id": "4YyPBAEaa2liYW5hX3NhbXBsZV9kYXRhX2ZsaWdodHMWZENSdWh0NWNSai1EdUhpcnBCZXgyZwAWTzlaLUtQNTJSNXVON0NxbHZERlBIQQAAAAAAABkI4hZ0OTFBejN3WFJIT0FvNXM2N0VwQ3JBAAEWZENSdWh0NWNSai1EdUhpcnBCZXgyZwAA",
        "keep_alive": "1m"
    },
    "size": 10,
    "sort": [
        {
            "timestamp": "asc"
        }
    ]
}
```

##### 步骤3：根据search_after和pit实现后续翻页。

要获得下一页结果：

* 使用上一次的排序值（包括 tiebreaker）作为`search_after`参数重新执行先前的搜索。

* 如果使用 PIT，需要在`pit.id`参数中使用最新的PIT ID。

* 搜索的查询和排序参数必须保持不变。

```shell
GET /_search
{
    "query": {
        "term": {
            "OriginWeather": "Sunny"
        }
    },
    "pit": {
        "id": "4YyPBAEaa2liYW5hX3NhbXBsZV9kYXRhX2ZsaWdodHMWZENSdWh0NWNSai1EdUhpcnBCZXgyZwAWTzlaLUtQNTJSNXVON0NxbHZERlBIQQAAAAAAABkI4hZ0OTFBejN3WFJIT0FvNXM2N0VwQ3JBAAEWZENSdWh0NWNSai1EdUhpcnBCZXgyZwAA",
        "keep_alive": "5m"
    },
    "size": 10,
    "sort": [
        {
            "timestamp": "asc"
        }
    ],
    "search_after": [
        1723434063000,
        130
    ]
}
```

从这个过程中也可以看出，`search_after`查询仅支持向后翻页。

#### (3) 优缺点

优点：

- 不严格受制于`max_result_window`，可以无限地往后翻页。
- 此处的“不严格”是指单次请求值不能超过`max_result_window`，但总翻页结果集可以超过。

缺点：

- 只支持向后翻页，不支持随机翻页。
- 更适合在手机端应用的场景中使用，类似今日头条等产品的分页搜索。

## 4. ES三种分页方式总结

| 分页方式 | 性能 | 优点 | 缺点 | 适用场景 |
| --- | --- | --- | --- | --- |
| from + size | 低 | 支持随机翻页 | 受制于`max_result_window`设置，不能无限制翻页；存在深度翻页问题，越往后翻页越慢 | 需要随机跳转不同页（PC端主流搜索引擎）；在10000条数据之内分页显示 |
| scroll | 中 | 支持全量遍历，但单次遍历的`size`值不能超过`max_result_window`的大小 | 响应是非实时的；保留上下文需要具有足够的堆内存空间；需要通过更多的网络请求才能获取所有结果 | 需要遍历全量数据 |
| search_after | 高 | 不严格受制于`max_result_window`，可以无限地往后翻页 | 只支持向后翻页，不支持随机翻页 | 仅需要向后翻页；超过10000条数据，需要分页 |