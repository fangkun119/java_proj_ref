# Elasticsearch 4：聚合操作

## 1. 聚合的概述

Elasticsearch 除搜索以外，提供了针对 ES 数据进行统计分析的功能。聚合（aggregations）可以让我们极其方便地实现对数据的统计、分析、运算。例如：

- 什么品牌的手机最受欢迎？
- 这些手机的平均价格、最高价格、最低价格？
- 这些手机每月的销售情况如何？

### 1.1 使用场景

聚合查询可以用于各种场景，比如商业智能、数据挖掘、日志分析等等。

- **电商平台的销售分析**：统计每个地区的销售额、每个用户的消费总额、每个产品的销售量等，以便更好地了解销售情况和趋势。
- **社交媒体的用户行为分析**：统计每个用户的发布次数、转发次数、评论次数等，以便更好地了解用户行为和趋势，同时可以将数据按照地区、时间、话题等维度进行分析。
- **物流企业的运输分析**：统计每个区域的运输量、每个车辆的运输次数、每个司机的行驶里程等，以便更好地了解运输情况和优化运输效率。
- **金融企业的交易分析**：统计每个客户的交易总额、每个产品的销售量、每个交易员的业绩等，以便更好地了解交易情况和优化业务流程。
- **智能家居的设备监控分析**：统计每个设备的使用次数、每个家庭的能源消耗量、每个时间段的设备使用率等，以便更好地了解用户需求和优化设备效能。

### 1.2 基本语法

聚合查询的语法结构与其他查询相似，通常包含以下部分：

- 查询条件：指定需要聚合的文档，可以使用标准的 Elasticsearch 查询语法，如 `term`、`match`、`range` 等等。
- 聚合函数：指定要执行的聚合操作，如 `sum`、`avg`、`min`、`max`、`terms`、`date_histogram` 等等。每个聚合命令都会生成一个聚合结果。
- 聚合嵌套：聚合命令可以嵌套，以便更细粒度地分析数据。

```bash
GET <index_name>/_search
{
  "aggs": {
    "<aggs_name>": { # 聚合名称需要自己定义
      "<agg_type>": { # 就是上面的term, avg, ...
        "field": "<field_name>" # 在哪些字段上聚合
      }
    }
  }
}
```

## 2. 聚合的分类

### 2.1 三种聚合：指标聚合、桶聚合、管道聚合

#### (1) 指标聚合（Metric Aggregation）

用数学运算对文档字段进行统计分析，类比 MySQL 的 `min()`、`max()`、`sum()` 等操作。

```sql
SELECT MIN(price), MAX(price) FROM products
```

类比实现：

```bash
{
  "aggs": {
    "avg_price": {
      "avg": {
        "field": "price"
      }
    }
  }
}
```

#### (2) 桶聚合（Bucket Aggregation）

满足特定条件的文档放到一个桶里，每一个桶关联一个 key，类比 MySQL 中的 `group by` 操作。

```sql
SELECT size, COUNT(*) FROM products GROUP BY size
```

类比实现：

```shell
{
  "aggs": {
    "by_size": {
      "terms": {
        "field": "size"
      }
    }
  }
}
```

#### (3) 管道聚合（Pipeline Aggregation）

会用聚合的结果，进行二次聚合。

### 2.2 测试数据

#### (1) 创建 ES 索引

```bash
DELETE /employees
PUT /employees
{
    "mappings": {
        "properties": {
            "age": {
                "type": "integer"
            },
            "gender": {
                "type": "keyword"
            },
            "job": {
                "type": "text",
                "fields": {
                    "keyword": {
                        "type": "keyword",
                        "ignore_above": 50
                    }
                }
            },
            "name": {
                "type": "keyword"
            },
            "salary": {
                "type": "integer"
            }
        }
    }
}
```

#### (2) 向索引中添加数据

```bash

PUT /employees/_bulk
{ "index" : {  "_id" : "1" } }
{ "name" : "Emma","age":32,"job":"Product Manager","gender":"female","salary":35000 }
{ "index" : {  "_id" : "2" } }
{ "name" : "Underwood","age":41,"job":"Dev Manager","gender":"male","salary": 50000}
{ "index" : {  "_id" : "3" } }
{ "name" : "Tran","age":25,"job":"Web Designer","gender":"male","salary":18000 }
{ "index" : {  "_id" : "4" } }
{ "name" : "Rivera","age":26,"job":"Web Designer","gender":"female","salary": 22000}
{ "index" : {  "_id" : "5" } }
{ "name" : "Rose","age":25,"job":"QA","gender":"female","salary":18000 }
{ "index" : {  "_id" : "6" } }
{ "name" : "Lucy","age":31,"job":"QA","gender":"female","salary": 25000}
{ "index" : {  "_id" : "7" } }
{ "name" : "Byrd","age":27,"job":"QA","gender":"male","salary":20000 }
{ "index" : {  "_id" : "8" } }
{ "name" : "Foster","age":27,"job":"Java Programmer","gender":"male","salary": 20000}
{ "index" : {  "_id" : "9" } }
{ "name" : "Gregory","age":32,"job":"Java Programmer","gender":"male","salary":22000 }
{ "index" : {  "_id" : "10" } }
{ "name" : "Bryant","age":20,"job":"Java Programmer","gender":"male","salary": 9000}
{ "index" : {  "_id" : "11" } }
{ "name" : "Jenny","age":36,"job":"Java Programmer","gender":"female","salary":38000 }
{ "index" : {  "_id" : "12" } }
{ "name" : "Mcdonald","age":31,"job":"Java Programmer","gender":"male","salary": 32000}
{ "index" : {  "_id" : "13" } }
{ "name" : "Jonthna","age":30,"job":"Java Programmer","gender":"female","salary":30000 }
{ "index" : {  "_id" : "14" } }
{ "name" : "Marshall","age":32,"job":"Javascript Programmer","gender":"male","salary": 25000}
{ "index" : {  "_id" : "15" } }
{ "name" : "King","age":33,"job":"Java Programmer","gender":"male","salary":28000 }
{ "index" : {  "_id" : "16" } }
{ "name" : "Mccarthy","age":21,"job":"Javascript Programmer","gender":"male","salary": 16000}
{ "index" : {  "_id" : "17" } }
{ "name" : "Goodwin","age":25,"job":"Javascript Programmer","gender":"male","salary": 16000}
{ "index" : {  "_id" : "18" } }
{ "name" : "Catherine","age":29,"job":"Javascript Programmer","gender":"female","salary": 20000}
{ "index" : {  "_id" : "19" } }
{ "name" : "Boone","age":30,"job":"DBA","gender":"male","salary": 30000}
{ "index" : {  "_id" : "20" } }
{ "name" : "Kathy","age":29,"job":"DBA","gender":"female","salary": 20000}
```

### 2.3 指标聚合（Metric Aggregation）

#### (1) 支持的聚合操作

- **单值分析**：只输出一个分析结果、操作包括`min`、`max`、`avg`、`sum`、`cardinality`（类似 `distinct Count`）、……。
- **多值分析**：封装了多个预置的分析指标、操作包括`stats`、`extended stats`、`percentile`（百分位）、`percentile rank`、`top hits`（排在前面的示例）、……

#### (2) 例子

查询员工的最低、最高和平均工资

建索引

```bash
POST /employees/_search
{
  "size": 0,
  "aggs": {
    "max_salary": {
      "max": {
        "field": "salary"
      }
    },
    "min_salary": {
      "min": {
        "field": "salary"
      }
    },
    "avg_salary": {
      "avg": {
        "field": "salary"
      }
    }
  }
}
```

对 `salary` 进行统计，用 `stats` 操作生成一个多值的统计结果

```bash
POST /employees/_search
{
  "size": 0,
  "aggs": {
    "stats_salary": {
      "stats": { # 输出会包含count,min,max,avg,sum,std_devision一组指标
        "field": "salary" 
      }
    }
  }
}
```

用 `cardinality` 对搜索结果去重

```bash
POST /employees/_search
{
  "size": 0,
  "aggs": {
    "cardinate": {
      "cardinality": { 
        "field": "job.keyword"
      }
    }
  }
}
```

### 2.4 桶聚合（Bucket Aggregation）

#### (1) 用途

按照一定的规则，将文档分配到不同的桶中，从而达到分类的目的。桶聚合可以用于各种场景，例如：

- 对数据进行分组统计，比如按照地区、年龄段、性别等字段进行分组统计。
- 对时间序列数据进行时间段分析，比如按照每小时、每天、每月、每季度、每年等时间段进行分析。
- 对各种标签信息分类，并统计其数量。

#### (2) 常见桶聚合：terms、数字类型、嵌套聚合

`terms`：需要字段支持 `field_data`。

* `keyword`： 默认支持 `field_data`
* `text`： 需要在 mapping 中开启 `field_data`，否则会按照分词后的结果进行分桶。

数字类型：

* `range` / `date Range`
* `histogram` / `date Histogram`

嵌套：

* 在桶里再做分桶。

#### (3) term 聚合

参数演示

```bash
GET /employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": { # 这里 aggragate type 不再是 avg 等，而就是 tearm
        "field": "job.keyword"
      }
    }
  }
}
```

聚合可配置属性有：

- `field`：指定聚合字段
- `size`：指定聚合结果数量
- `order`：指定聚合结果排序方式

设置排序方式：

```bash
GET /employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword",
        "size": 10,  # 返回分桶数是10，不指定时按照默认值返回
        "order": { # 排序
          "_count": "desc" 
        }
      }
    }
  }
}
```

限定聚合范围：

```bash
GET /employees/_search
{
  "query": {
    "range": { # 限定聚合范围是 salary 大于等于 100000 的文档
      "salary": {
        "gte": 10000
      }
    }
  },
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword",
        "size": 10, # 返回分桶数是10，不指定时按照默认值返回
        "order": { # 按照 _count 排序
          "_count": "desc" 
        }
      }
    }
  }
}
```

#### (4) text 字段 terms 聚合异常问题

直接对 Text 字段进行 terms 聚合查询，会失败抛出异常：

```shell
POST /employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job" # job 的类型是 text，不写成 job.keyword时会抛异常
      }
    }
  }
}
```

如果修改mapping，让这个 text 字段字段支持fielddata，它将能够支持terms aggregation，但是这种聚合是使用**分词后的字段**进行聚合的，并且性能开销大

```bash
PUT /employees/_mapping
{
  "properties": {
    "job": {
      "type": "text",
      "fielddata": true # 让job字段支持fielddata
    }
  }
}

POST /employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job" # 将使用分词后的字段进行聚合
      }
    }
  }
}
```

另一种方法是，用 `job.keyword` 进行聚合，将不会对 job 分词，而是把整个字段当做keyword直接聚合

```bash
POST /employees/_search
{
  "size": 0,
  "aggs": {
    "cardinate": {
      "cardinality": {
        "field": "job.keyword" # 不分词，用job这个字段进行聚合
      }
    }
  }
}
```

#### (5) 数字聚合

Range` & `Histogram 聚合

* 按照数字的范围，进行分桶。
* 在 `range` aggregation 中，可以自定义 key。

`range` 示例：按照工资的 range 分桶：

```bash
# 按照 salary range 分桶，可以自定义用于分桶的 range 区间段
POST employees/_search
{
  "size": 0,
  "aggs": {
    "salary_range": {
      "range": {
        "field": "salary",
        "ranges": [
          { "to": 10000 },
          { "from": 10000, "to": 20000 },
          { "key": ">20000", "from": 20000 }
        ]
      }
    }
  }
}
```

`histogram` 示例：按照工资的间隔分桶：

```bash
# 以salary 每 5000 一个区间段进行分桶
POST employees/_search
{
  "size": 0,
  "aggs": {
    "salary_histrogram": {
      "histogram": {
        "field": "salary",
        "interval": 5000,
        "extended_bounds": {
          "min": 0,
          "max": 100000
        }
      }
    }
  }
}
```

#### (6)  查看每个桶桶内最匹配的顶部文档

`top_hits` 示例：当获取分桶后，桶内最匹配的顶部文档列表：

```shell
POST /employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword"
      },
      "aggs": {
        "old_employee": {
          "top_hits": {
            "size": 3,
            "sort": [
              {
                "age": {
                  "order": "desc"
                }
              }
            ]
          }
        }
      }
    }
  }
}
```

#### (7) 嵌套分桶聚合

嵌套聚合示例：

```bash
POST employees/_search
{
  "size": 0,
  "aggs": { 
    "Job_salary_stats": { # 先用桶聚合，根据 job.keyword 进行分桶
      "terms": {
        "field": "job.keyword"
      },
      "aggs": { # 对每个桶内的文档，用 stat 对 salary 进行指标聚合，得到 max, min, avg 等统计指标
        "salary": {
          "stats": {
            "field": "salary"
          }
        }
      }
    }
  }
}
```

三重嵌套：根据工作类型分桶，然后按照性别分桶，计算工资的统计信息：

```bash
POST employees/_search
{
  "size": 0,
  "aggs": {
    "Job_gender_stats": { # 先用桶聚合，根据 job.keyword 进行分桶
      "terms": {
        "field": "job.keyword"
      },
      "aggs": {
        "gender_stats": { # 对每个 job，根据 gender 字段进行分桶
          "terms": {
            "field": "gender"
          },
          "aggs": { # 对每个 job 下，每个 gender 的文档，对 salary 字段进行 stat 指标聚合得到 max、min 等统计指标
            "salary_stats": {
              "stats": {
                "field": "salary"
              }
            }
          }
        }
      }
    }
  }
}
```

### 2.5 管道聚合（Pipeline Aggregation）

#### (1) 用途：

支持对聚合分析的结果，再次进行聚合分析。

#### (2) 输出位置：

Pipeline 的分析结果会输出到原结果中。根据位置的不同，分为两类：

**同级（Sibling）**：结果和现有分析结果同级

* `max`、`min`、`avg`、`sum bucket`
* `stats`、`extended stats bucket`
* `percentiles bucket`

**内嵌（Parent）**：结果内嵌到现有的聚合分析结果之中

* `derivative`（求导）
* `cumulative sum`（累计求和）
* `moving function`（移动平均值）

#### (2) 例子：min bucket

在员工数最多的工种里，找出平均工资最低的工种：

```bash
POST employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword",
        "size": 10
      },
      "aggs": {
        "avg_salary": {
          "avg": {
            "field": "salary"
          }
        }
      }
    },
    "min_salary_by_job": {
      # 对 jobs>avg_salary 聚合结果进行二次聚合
      # 聚合方法是 min_bucket，即找到 avg_salary 最小的分桶
      # 输出结果与 jobs>avg_salary 这个聚合并列
      "min_bucket": { 
        "buckets_path": "jobs>avg_salary"
      }
    }
  }
}
```

#### (3) 例子：stats 示例

平均工资的统计分析：

```bash
POST employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": { # 嵌套聚合外层 jobs
      "terms": {
        "field": "job.keyword",
        "size": 10
      },
      "aggs": {
        "avg_salary": { # 嵌套聚合内层 avg_salary
          "avg": {
            "field": "salary"
          }
        }
      }
    },
    "stats_salary_by_job": {
      # 对 jobs>avg_salary 聚合结果进行二次聚合
      # 聚合方法是 stats_bucket，对所有分桶的 avg_salary 进行 max、min、avg 等统计
      # 输出结果与 jobs>avg_salary 这个聚合并列    
      "stats_bucket": {
        "buckets_path": "jobs>avg_salary"
      }
    }
  }
}
```

#### (4) 例子：percentiles 示例

平均工资的百分位数：

```bash
POST employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": { # 嵌套聚合外层 jobs
      "terms": {
        "field": "job.keyword",
        "size": 10
      },
      "aggs": {
        "avg_salary": { # 嵌套聚合内层 avg_salary
          "avg": {
            "field": "salary"
          }
        }
      }
    },
    "percentiles_salary_by_job": {
      # 对 jobs>avg_salary 聚合结果进行二次聚合
      # 聚合方法是 percentiles_bucket，得到这些桶 avg_salary 在每个分为数的值
      # 输出结果与 jobs>avg_salary 这个聚合并列    
      "percentiles_bucket": {
        "buckets_path": "jobs>avg_salary"
      }
    }
  }
}
```

#### (5) 例子：cumulative_sum 示例

累计求和：

```bash
POST employees/_search
{
  "size": 0,
  "aggs": {
    "age": {  # 嵌套聚合外层 age
      "histogram": {
        "field": "age",
        "min_doc_count": 0,
        "interval": 1
      },
      "aggs": {
        "avg_salary": { # 嵌套聚合内层 avg_salary
          "avg": {
            "field": "salary"
          }
        },
        "cumulative_salary": {
        	# 对 age>avg_salary 聚合结果进行二次聚合
          # 聚合方法是 cumulative_salary，计算每个 age > avg_salary 二级分桶 avg_salary 的累计求和
          # 输出结果嵌套在 avg_salary 这个二级分桶的输出内部
          "cumulative_sum": {
            "buckets_path": "avg_salary"
          }
        }
      }
    }
  }
}
```

### 2.5 聚合的作用范围

ES 聚合分析的默认作用范围是 query 的查询结果集。ES 还支持以下方式改变聚合的作用范围：

- `Filter`
- `Post Filter`
- `Global`

#### (1) 默认对 query 的查询结果进行聚合

```shell
POST employees/_search
{
  "size": 0,
  "query": {
    "range": {
      "age": {
        "gte": 20
      }
    }
  },
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword"
      }
    }
  }
}
```

#### (2) 用 filter 改变聚合范围

```shell
POST employees/_search
{
  "size": 0,
  "aggs": {
    "older_person": {
      "filter": {
        "range": {
          "age": {
            "from": 35
          }
        }
      },
      "aggs": {
        "jobs": {
          "terms": {
            "field": "job.keyword"
          }
        }
      }
    },
    "all_jobs": {
      "terms": {
        "field": "job.keyword"
      }
    }
  }
}
```

#### (3) 用 post filter 对聚合后的结果进行筛选

```shell
POST employees/_search
{
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword"
      }
    }
  },
  "post_filter": {
    "match": {
      "job.keyword": "Dev Manager"
    }
  }
}
```

#### (4) 用 global 切换到全局聚合范围

[https://www.elastic.co/docs/reference/aggregations/search-aggregations-bucket-global-aggregation](https://www.elastic.co/docs/reference/aggregations/search-aggregations-bucket-global-aggregation)

```bash
POST employees/_search
{
  "size": 0,
  "query": { # 查询年龄字段大于 40 的文档
    "range": {
      "age": {
        "gte": 40
      }
    }
  },
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword"
      }
    },
    "all": {
      # 聚合范围不受query影响，对整个索引进行聚合
      "global": {}, 
      "aggs": {
        "salary_avg": {
          "avg": {
            "field": "salary"
          }
        }
      }
    }
  }
}
```

### 2.6 排序

下面的例子中指定 `order`，按照 `count` 和 `key` 进行排序。

* size 为 0 表示只返回聚合结果，不返回 query 语句匹配的文档
* size 不为 0 时还会返回 query 语句匹配的文档

按 count 降序排序

```shell
POST employees/_search
{
  "size": 0,
  "query": {
    "range": {
      "age": {
        "gte": 20
      }
    }
  },
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword",
        "order": [
          { "_count": "asc" },
          { "_key": "desc" }
        ]
      }
    }
  }
}
```

按照下一层聚合计算出的 avg_salary 排序

```shell
POST employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword",
        "order": [
          { "avg_salary": "desc" }
        ]
      },
      "aggs": {
        "avg_salary": {
          "avg": {
            "field": "salary"
          }
        }
      }
    }
  }
}
```

按照下一层 stat 为每个分桶计算出的 min 进行降序排序

```shell
POST employees/_search
{
  "size": 0,
  "aggs": {
    "jobs": {
      "terms": {
        "field": "job.keyword",
        "order": [
          { "stats_salary.min": "desc" }
        ]
      },
      "aggs": {
        "stats_salary": {
          "stats": {
            "field": "salary"
          }
        }
      }
    }
  }
}
```

---

## 3. ES 聚合分析不精准原因分析

### 3.1 问题和原因

ElasticSearch 在对海量数据进行聚合分析的时候会损失搜索精准度来满足实时性的需求。不精准的原因：

- 数据分散到多个分片，聚合是每个分片的取 Top X，导致结果不精准。
- ES 可以不每个分片 Top X，而是全量聚合，但势必这会有很大的性能问题。

### 3.2 如何提高聚合精确度？

#### (1) 设置主分片为 1

注意 7.x 版本已经默认为 1。

适用场景：数据量小的小集群规模业务场景。

#### (2) 调大 shard_size 值 （推荐 size * 1.5 + 10）

设置 shard_size 为比较大的值，官方推荐：`size * 1.5 + 10`。

- `size`：是聚合结果的返回值，客户期望返回聚合排名前3，那么`size` 值就是 3。
- `shard_size`：每个分片上聚合的数据条数。`shard_size` 原则上要大于等于 `size`。
- `shard_size` 值越大，结果越趋近于精准聚合结果值。
- 还可通过 `show_term_doc_count_error` 参数显示最差情况下的错误值，用于辅助确定 `shard_size` 大小。

适用场景：数据量大、分片数多的集群业务场景。

例子如下：使用 Kibana 的一个测试数据集

```bash
DELETE my_flights
PUT my_flights
{
  "settings": {
    "number_of_shards": 20
  },
  "mappings": {
    "properties": {
      "AvgTicketPrice": {
        "type": "float"
      },
      "Cancelled": {
        "type": "boolean"
      },
      "Carrier": {
        "type": "keyword"
      },
      "Dest": {
        "type": "keyword"
      },
      "DestAirportID": {
        "type": "keyword"
      },
      "DestCityName": {
        "type": "keyword"
      },
      "DestCountry": {
        "type": "keyword"
      },
      "DestLocation": {
        "type": "geo_point"
      },
      "DestRegion": {
        "type": "keyword"
      },
      "DestWeather": {
        "type": "keyword"
      },
      "DistanceKilometers": {
        "type": "float"
      },
      "DistanceMiles": {
        "type": "float"
      },
      "FlightDelay": {
        "type": "boolean"
      },
      "FlightDelayMin": {
        "type": "integer"
      },
      "FlightDelayType": {
        "type": "keyword"
      },
      "FlightNum": {
        "type": "keyword"
      },
      "FlightTimeHour": {
        "type": "keyword"
      },
      "FlightTimeMin": {
        "type": "float"
      },
      "Origin": {
        "type": "keyword"
      },
      "OriginAirportID": {
        "type": "keyword"
      },
      "OriginCityName": {
        "type": "keyword"
      },
      "OriginCountry": {
        "type": "keyword"
      },
      "OriginLocation": {
        "type": "geo_point"
      },
      "OriginRegion": {
        "type": "keyword"
      },
      "OriginWeather": {
        "type": "keyword"
      },
      "dayOfWeek": {
        "type": "integer"
      },
      "timestamp": {
        "type": "date"
      }
    }
  }
}

POST _reindex
{
  "source": {
    "index": "kibana_sample_data_flights"
  },
  "dest": {
    "index": "my_flights"
  }
}

GET my_flights/_count
GET kibana_sample_data_flights/_search
{
  "size": 0,
  "aggs": {
    "weather": {
      "terms": {
        # 未使用 shard_size 参数
        "field": "OriginWeather",
        "size": 5,
        "show_term_doc_count_error": true
      }
    }
  }
}

GET my_flights/_search
{
  "size": 0,
  "aggs": {
    "weather": {
      "terms": {
        # 使用 shard_size 参数
        "field": "OriginWeather",
        "size": 5,
        "shard_size": 10, 
        "show_term_doc_count_error": true
      }
    }
  }
}
```

在 Terms Aggregation 的返回中有两个特殊的数值，可用于调试和确定参数的值：

- `doc_count_error_upper_bound`：被遗漏的 term 分桶，包含的文档，有可能的最大值。
- `sum_other_doc_count`：除了返回结果 bucket 的 terms 以外，其他 terms 的文档总数（总数 - 返回的总数）。

#### (3) 将 size 设置为全量值，来解决精度问题

将 size 设置为 2 的 32 次方减 1 也就是分片支持的最大值，来解决精度问题。

原因：1.x 版本，size 等于 0 代表全部，高版本取消 0 值，所以设置了最大值（大于业务的全量值）。全量带来的弊端就是：如果分片数据量极大，这样做会耗费巨大的 CPU 资源来排序，而且可能会阻塞网络。

适用场景：对聚合精准度要求极高的业务场景，由于性能问题，不推荐使用。

#### (4) 使用 Clickhouse/Spark 进行精准聚合

适用场景：数据量非常大、聚合精度要求高、响应速度快的业务场景。

---

## 4. Elasticsearch 聚合性能优化

### 4.1 Index Sorting：插入数据时对索引进行预排序

在插入时对索引进行预排序，而不是在查询时再对索引进行排序，这将提高

* 范围查询（range query）
* 排序操作

的性能

Elasticsearch 6.X 之后版本支持在创建索引时配置如何在分片中排序。

```bash
PUT /my_index
{
  "settings": {
    "index": {
      # 插入数据时按照 create_time 逆序预排序
      "sort.field": "create_time",
      "sort.order": "desc"
    }
  },
  "mappings": {
    "properties": {
      "create_time": {
        "type": "date"
      }
    }
  }
}
```

注意：预排序将增加 Elasticsearch 写入的成本。在某些用户特定场景下，开启索引预排序会导致大约 40%-50% 的写性能下降。也就是说，如果用户场景更关注写性能的业务，开启索引预排序不是一个很好的选择。

### 4.2 使用节点查询缓存

#### (1) 功能

节点查询缓存（Node query cache）可用于有效缓存过滤器（filter）操作的结果。

* 如果多次执行同一 filter 操作，这将很有效。
* 但即便更改过滤器中的某一个值，也将意味着需要计算新的过滤器结果。

#### (2) 自动启用

执行一个带有过滤查询的搜索请求，Elasticsearch 将自动尝试使用节点查询缓存来优化性能。

#### (3) 例子

```shell
GET /your_index/_search
{
  "query": {
    "bool": {
      "filter": {
        "term": {
          "your_field": "your_value"
        }
      }
    }
  }
}
```

### 4.3 使用分片请求缓存

#### (1) 使用方法

聚合语句中，设置：`size`：0，就会使用分片请求缓存缓存结果。

`size = 0` 的含义是：只返回聚合结果，不返回查询结果。

#### (2) 例子

```shell
GET /es_db/_search
{
  "size": 0,
  "aggs": {
    "remark_agg": {
      "terms": {
        "field": "remark.keyword"
      }
    }
  }
}
```

### 4.4 拆分聚合，再使用 msearch 让聚合并行化

#### (1) 串行聚合与并行聚合

查询中同时有多个条件聚合时，默认情况下聚合不是并行运行的。使用 `msearch` 可以让这些聚合并行运行，显著缩短响应时间，当然也会消耗更多 CPU。

#### (2) 例子

常规的多条件聚合，不能并行执行：

```shell
GET /employees/_search
{
  "size": 0,
  "aggs": {
    "job_agg": {
      "terms": {
        "field": "job.keyword"
      }
    },
    "max_salary": {
      "max": {
        "field": "salary"
      }
    }
  }
}
```

`msearch` 拆分多个聚合，做到并行运行：

```shell
GET _msearch
{"index": "employees"}
{"size": 0, "aggs": {"job_agg": {"terms": {"field": "job.keyword"}}}}
{"index": "employees"}
{"size": 0, "aggs": {"max_salary": {"max": {"field": "salary"}}}}
```