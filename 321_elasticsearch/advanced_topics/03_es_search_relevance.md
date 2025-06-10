# Elasticsearch 3：提升搜索相关性

## 1. 相关性的概述

### 1.1 什么是相关性（Relevance）

用户关心的搜索结果的相关性，包括

- 是否可以找到所有相关的内容
- 有多少不相关的内容被返回了
- 文档的打分是否合理
- 结合业务需求，平衡结果排序

搜索引擎通过相关性分数（_score）度量文档与查询语句的匹配程度，评分越高表示相关性越高。

例如：查询“JAVA多线程设计模式”，对查询条件分词得到三个关键词，每个关键词能够找不同的文档。从下表来看，文档2,3的相关性更高

| 关键词   | 文档ID      |
| :------- | :---------- |
| JAVA     | 1,2,3       |
| 设计模式 | 1,2,3,4,5,6 |
| 多线程   | 2,3,7,9     |

Elasticsearch有默认的相关性计算方法，但是它也容许用户自定义相关性计算来满足特定业务需求。

### 1.2 计算相关性评分

#### (1) 相关性计算方法

Elasticsearch使用布尔模型查找匹配文档，并用一个名为“实用评分函数”的公式来计算相关性。

* 这个公式借鉴了TF-IDF（词频-逆向文档频率）和向量空间模型
* 加入了一些现代的新特性，如协调因子、字段长度归一化以及词/查询语句权重提升。
* Elasticsearch 5 之前的版本，评分机制或者打分模型是基于TF-IDF实现的。从Elasticsearch 5 之后，默认的打分机制改成了Okapi BM25。其中BM是Best Match的缩写，25是指经过25次迭代调整之后得出的算法，它是由TF-IDF机制进化来的。

#### (2) TF-IDF 与 BM25

这两种算法的用途相同

- 都使用逆向文档频率来区分普通词（不重要）和非普通词（重要）
- 都使用词频来衡量某个词在文档中出现的频率。

一方面：某个词在当前文档中出现得越频繁，它就与这个文档越相关，得分越高；而另一方面，这个词如果在其它文档里出现的频次高，就说明它使用的越普遍、得分越低。也就是说，越罕见的词，越在当前文档出频繁出现，它就越能表达这篇文章的语义。

BM25 的改进点:

* 在传统TF-IDF的基础上增加了可调节的参数，使其更加更佳灵活强大，具有较高的实用性。
* TF-IDF：TF(t) 部分的值越大，整个公式返回的值就会越大；BM25：随着 TF(t) 的逐步加大，该算法的返回值会趋于一个数值。

#### (3) TF-IDF 与 字段长度归一值

词频（term frequency）、逆向文本频率（inverse document frequency）和字段长度归一值（field-length norm）在索引时计算并存储的，最后将它们结合在一起计算单个词在特定文档中的权重。

TF：词频（Term Frequency）

* 检索词在文档中出现的频率越高，相关性也越高。

* 词频（TF）= 某个词在文档中出现的次数 / 文档的总词数

IDF：逆向文本频率（Inverse Document Frequency）

* 每个检索词在索引中出现的频率。

* 频率越高，相关性越低。总文档中有些词比如“是”、“的”、“在”在所有文档中出现频率都很高，并不重要，可以减少多个文档中都频繁出现的词的权重。

* 逆向文本频率（IDF）= log (语料库的文档总数 / (包含该词的文档数+1))

字段长度归一值（ field-length norm）

* 检索词出现在一个内容短的 title 要比同样的词出现在一个内容长的 content 字段权重更大。

#### (4) 通过 Explain API 查看 TF-IDF

```text
PUT /test_score/_bulk
{"index":{"_id":1}}
{"content":"we use Elasticsearch to power the search"}
{"index":{"_id":2}}
{"content":"we like elasticsearch"}
{"index":{"_id":3}}
{"content":"Thre scoring of documents is caculated by the scoring formula"}
{"index":{"_id":4}}
{"content":"you know,for search"}

GET /test_score/_search
{
  "explain": true,
  "query": {
    "match": {
      "content": "elasticsearch"
    }
  }
}

GET /test_score/_explain/2
{
  "query": {
    "match": {
      "content": "elasticsearch"
    }
  }
}
```

### 1.3 自定义相关性评分

#### (1) 用途

自定义相关性评分：通过修改ElasticSearch默认的评分计算，来调整文档相关性，在最前面的位置返回用户最期望的结果，满足特定应用场景的需求。用途如下：

1. 排序偏好：通过在搜索结果中给每个文档自定义评分，可以更好地满足搜索用户的排序偏好。
2. 特殊字段权重：通过给特定字段赋予更高权重，可以让这些字段对搜索结果的影响更大。
3. 业务逻辑需求：根据业务需求，可以定义复杂的评分逻辑，使搜索结果更符合业务需求。
4. 自定义用户行为：可以使用用户行为数据（如点击率）作为评分因素，提高用户搜索体验。

#### (2) 自定义评分的意义

搜索引擎本质是一个匹配过程，即从海量的数据中找到匹配用户需求的内容。判定内容与用户查询的相关性一直是搜索引擎领域的核心研究课题之一。如果搜索引擎不能准确地识别用户查询的意图并将相关结果排在前面的位置，那么搜索结果就不能满足用户的需求，从而影响用户对搜索引擎的满意度。

## 2. 自定义评分的策略

然而，如何实现这样的自定义评分策略，以确保搜索结果能够最大限度地满足用户需求呢？我们可以从多个层面，包括索引层面、查询层面以及后处理阶段着手。

以下是几种主要的自定义评分策略：

1. Index Boost：在索引层面修改相关性
2. boosting：修改文档相关性
3. negative_boost：降低相关性
4. function_score：自定义评分
5. rescore_query：查询后二次打分

### 2.1 index boost: 不同索引叠加不同权重

使用场景：搜索跨多个索引时，可以为每个索引设置不同的权重，让搜索结果更偏向于权重高的索引

实战举例：一批数据里有不同的标签，数据结构一致，要将不同的标签存储到不同的索引 (A、B、C)，并严格按照标签来分类展示（先展示A类，然后展示B类，最后展示C类）

```text
PUT my_index_100a/_doc/1
{
  "subject": "subject 1"
}

PUT my_index_100b/_doc/1
{
  "subject": "subject 1"
}

PUT my_index_100c/_doc/1
{
  "subject": "subject 1"
}

POST my_index_100*/_search
{
  "query": {
    "term": {
      "subject.keyword": {
        "value": "subject 1"
      }
    }
  }
}

POST my_index_100*/_search
{
  "query": {
    "term": {
      "subject.keyword": {
        "value": "subject 1"
      }
    }
  },
  "indices_boost": [
    {
      "my_index_100a": 1.5
    },
    {
      "my_index_100b": 1.2
    },
    {
      "my_index_100b": 1.2
    }
  ]
}
```

### 2.2 boosting: 不同匹配条件叠加不同的权重

有多个查询条件时，每个查询条件都可能会匹配到一批文档，为这个查询条件设置boosting参数，就可以调整匹配到文档的相关性权重（即、有的匹配到的的文档权重高，有的则权重低）

**boosting参数**：

- 值为 0 ～ 1 ：代表降权重（如0.2）
- 值＞1：代表提升权重（如1.5）

**例子**：

```text
POST /blogs/_bulk
{"index":{"_id":1}}
{"title":"Apple iPad","content":"Apple iPad,Apple iPad"}
{"index":{"_id":2}}
{"title":"Apple iPad,Apple iPad","content":"Apple iPad"}

GET /blogs/_search
{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "title": {
              "query": "apple,ipad",
              "boost": 4
            }
          }
        },
        {
          "match": {
            "content": {
              "query": "apple,ipad",
              "boost": 1
            }
          }
        }
      ]
    }
  }
}
```

### 2.3 negative_boost: 为满足特定条件的搜索结果降权

若对某些返回结果不满意，但又不想将其排除 (must_not)，则可以考虑采用negative_boost进行降权的方式。原理说明如下：

* negative_boost仅对查询中定义为negative的部分生效（见下面例子）

* 它不会修改boosting部分评分，而是给命中negative条件的文档，乘以negative_boost的值（取值范围为0～1.0，例如0.3）来进行降权

案例：要求苹果公司的产品信息优先展示。使用`must_not`会完全过滤其它公司的产品，不符合需求；而使用 negative_boost 则只是为其它公司的产品降权、但让然能展示。

```text
POST /news/_bulk
{"index":{"_id":1}}
{"content":"Apple Mac"}
{"index":{"_id":2}}
{"content":"Apple iPad"}
{"index":{"_id":3}}
{"content":"Apple employee like Apple Pie and Apple Juice"}

GET /news/_search
{
  "query": {
    "bool": {
      "must": {
        "match": {
          "content": "apple"
        }
      }
    }
  }
}

# 利用must not排除不是苹果公司产品的文档
GET /news/_search
{
  "query": {
    "bool": {
      "must": {
        "match": {
          "content": "apple"
        }
      },
      "must_not": {
        "match": {
          "content": "pie"
        }
      }
    }
  }
}

# 利用negative_boost降低相关性
GET /news/_search
{
  "query": {
    "boosting": {
      "positive": {
        "match": {
          "content": "apple"
        }
      },
      "negative": {
        "match": {
          "content": "pie"
        }
      },
      "negative_boost": 0.2
    }
  }
}
```

### 2.4 function_score: 自定义评分公式

通过自定义一个或多个查询语句及脚本，达到精细化控制评分的目的，实现高度个性化的排序设置。适用于需进行复杂查询的自定义评分业务场景。

案例1：商品搜索结果排序时，除了文字相关性，还希望能够根据销量和浏览人数调整权重

| 商品 | 销量 | 浏览人数 |
| :--- | :--- | :------- |
| A    | 10   | 10       |
| B    | 20   | 20       |

我们调整后的评分公式：评分 = 原始评分 ×（销量+浏览人数）

这样，销量和浏览人数较高的文档就会有更高的评分，从而在搜索结果中排名更靠前。这种评分方式不仅考虑了文档与查询的匹配度（由_score表示），还考虑了文档的销量和浏览人数，非常适用于电子商务等场景。

该需求可以借助script_score实现，代码如下：

```text
PUT my_index_products/_bulk
{"index":{"_id":1}}
{"name":"A","sales":10,"visitors":10}
{"index":{"_id":2}}
{"name":"B","sales":20,"visitors":20}
{"index":{"_id":3}}
{"name":"C","sales":30,"visitors":30}

# 基于function_score实现自定义评分检索
POST my_index_products/_search
{
  "query": {
    "function_score": {
      "query": {
        "match_all": {}
      },
      "script_score": {
        "script": {
          "source": "_score*(doc['sales'].value+doc['visitors'].value)"
        }
      }
    }
  }
}
```

### 2.5 rescore_query：对查询结果二次打分用于排序

二次评分：查询返回文档后，重新计算他们的评分用于排序

* 可以自定义二次评分的计算逻辑

* 可以只对结果集的自己进行处理，节省性能开销

* 适用于对查询语句的结果不满意，需要重新打分的场景。

例子如下：

```text
PUT my_index_books-demo/_bulk
{"index":{"_id":"1"}}
{"title":"ES实战","content":"ES的实战操作，实战要领，实战经验"}
{"index":{"_id":"2"}}
{"title":"MySQL实战","content":"MySQL的实战操作"}
{"index":{"_id":"3"}}
{"title":"MySQL","content":"MySQL一定要会"}

GET my_index_books-demo/_search # 普通搜索
{
  "query": {
    "match": {
      "content": "实战"
    }
  }
}

GET my_index_books-demo/_search
{
  "query": {
    "match": {
      "content": "实战"  # 查询content字段中包含”实战“的文档，权重为0.7。
    }
  },
  "rescore": {
    "query": {
      "rescore_query": {
        "match": {
          "title": "MySQL"
        }
      },
      "query_weight": 0.7,
      "rescore_query_weight": 1.2  # 对文档中title为MySQL的文档增加评分，权重为1.2，
    },
    "window_size": 50 # window_size为50，表示取分片结果的前50进行重新算分
  }
}
```

通过 rescore_query 我们可以对检索结果进行二次评分，增加自己更复杂的评分逻辑，提供更准确的结果排序，但是相应的也会增加查询的计算成本与响应时间。

## 3. 多字段搜索评分策略

### 3.1 场景

对多个字段进行组合搜索，例如同时匹配 title 和 content 字段，如何自定义评分策略？

### 3.2 三种评分策略：最佳字段、多数字段、混合字段

1. 最佳字段（Best Fields）：哪个字段的评分最高，就用哪个字段作为整个文档的评分
2. 多数字段（Most Fields）：返回各个字段评分之和。
3. 混合字段（Cross Fields）：跨字段匹配，待查询内容在多个字段中都显示

对于某些实体，例如人名，地址，图书信息，需要在多个字段中确定信息。

单个字段只能作为整体的一部分，希望在任何这些列出的字段中找到尽可能多的词。

### 3.3 最佳字段搜索（Best Fields）

#### (1) 介绍

哪个字段的评分最高，就用哪个字段作为整个文档的评分

官方文档：https://www.elastic.co/guide/en/elasticsearch/reference/8.14/query-dsl-dis-max-query.html

#### (2) 例子：bool should 的问题

```text
DELETE /blogs
PUT /blogs/_doc/1
{
  "title": "Quick brown rabbits",
  "body": "Brown rabbits are commonly seen."
}

PUT /blogs/_doc/2
{
  "title": "Keeping pets healthy",
  "body": "My quick brown fox eats rabbits on a regular basis."
}

# 查询棕色的狐狸
POST /blogs/_search
{
  "query": {
    "bool": {
      "should": [
        { "match": { "title": "Brown fox" }},
        { "match": { "body":  "Brown fox" }}
      ]
    }
  }
}
```

这个查询：文档1的分数高于文档2，并不是我们希望的。

首先bool should 的算法过程：

1. 执行 should 语句中的两个子 match 查询
2. 加和两个子查询的评分
3. 乘以匹配语句的总数，再除以所有语句的总数

上述例子中，文档2的body中包含“brown fox”，我们希望它的分数高于文档1。但由于上面bool should的分数计算过程，文档1在title和body都部分匹配了“brown”，而文档2只在body完整匹配。

我们希望的是title和body中：哪个匹配了完整的query、就用哪个的得分；即使用单个匹配最佳字段的评分。他们是竞争关系、而不是分数叠加。这需要使用dis_max来实现。

#### (3) 用 dis_max 实现最佳字段搜索

Disjunction Max Query （dis_max）用来组合几个子查询。对每个匹配的文档，用几个子查询中的最高分，作为这个文档的分数。

```text
POST /blogs/_search
{
  "query": {
    "dis_max": {
      "queries": [
        { "match": { "title": "Brown fox" }},
        { "match": { "body":  "Brown fox" }}
      ]
    }
  }
}
```

#### (4) 调节 dis_max 最佳匹配偏向度：用 tie_breaker 参数

有时候，我们希望文档的打分方式，能够介于`最佳字段匹配`和`字段分数加总（多数字段匹配）`之间，而不是“非黑即白”一定要完全按照某一种。这可以通过`Tie Breaker`来实现。

Tie Breaker 是一个介于 0-1 之间的浮点数，它能够给其它的匹配字段一定的权重：0 代表使用最佳匹配；1 代表所有语句同等重要。

score 计算方法如下：

1. 获得最佳匹配语句的评分 _score。
2. 将其他匹配语句的评分与 tie_breaker 相乘
3. 对以上评分求和并规范化

最终得分 = 最佳匹配字段的分数 + 其他匹配字段分数 * tie_breaker

具体说就是：

- 最佳匹配字段分数的权重是 1.0
- 其他匹配字段的分数权重是 tie_breaker

例子如下

```text
POST /blogs/_search
{
  "query": {
    "dis_max": {
      "queries": [
        { "match": { "title": "Quick pets" }},
        { "match": { "body":  "Quick pets" }}
      ],
      "tie_breaker": 0.1  # 其它最佳匹配字段，其分数也能获得 0.1 的权重
    }
  }
}
```

#### (5) 用 type 为 best_field 的 multi_match 实现最佳字段搜索

代码如下：

```text
POST /blogs/_search
{
  "query": {
    "multi_match": {
      "type": "best_fields",
      "query": "Brown fox",
      "fields": ["title","body"],
      "tie_breaker": 0.2
    }
  }
}
```

#### (6) 案例

```text
PUT /employee
{
  "settings": {
    "index": {
      "analysis.analyzer.default.type": "ik_max_word"
    }
  }
}

POST /employee/_bulk
{"index":{"_id":1}}
{"empId":"1","name":"员工001","age":20,"sex":"男","mobile":"19000001111","salary":23343,"deptName":"技术部","address":"湖北省武汉市洪山区光谷大厦","content":"i like to write best elasticsearch article"}
{"index":{"_id":2}}
{"empId":"2","name":"员工002","age":25,"sex":"男","mobile":"19000002222","salary":15963,"deptName":"销售部","address":"湖北省武汉市江汉路","content":"i think java is the best programming language"}
{"index":{"_id":3}}
{"empId":"3","name":"员工003","age":30,"sex":"男","mobile":"19000003333","salary":20000,"deptName":"技术部","address":"湖北省武汉市经济开发区","content":"i am only an elasticsearch beginner"}
{"index":{"_id":4}}
{"empId":"4","name":"员工004","age":20,"sex":"女","mobile":"19000004444","salary":15600,"deptName":"销售部","address":"湖北省武汉市沌口开发区","content":"elasticsearch and hadoop are all very good solution, i am a beginner"}
{"index":{"_id":5}}
{"empId":"5","name":"员工005","age":20,"sex":"男","mobile":"19000005555","salary":19665,"deptName":"测试部","address":"湖北省武汉市东湖隧道","content":"spark is best big data solution based on scala, an programming language similar to java"}
{"index":{"_id":6}}
{"empId":"6","name":"员工006","age":30,"sex":"女","mobile":"19000006666","salary":30000,"deptName":"技术部","address":"湖北省武汉市江汉路","content":"i like java developer"}
{"index":{"_id":7}}
{"empId":"7","name":"员工007","age":60,"sex":"女","mobile":"19000007777","salary":52130,"deptName":"测试部","address":"湖北省黄冈市边城区","content":"i like elasticsearch developer"}
{"index":{"_id":8}}
{"empId":"8","name":"员工008","age":19,"sex":"女","mobile":"19000008888","salary":60000,"deptName":"技术部","address":"湖北省武汉市江汉大学","content":"i like spark language"}
{"index":{"_id":9}}
{"empId":"9","name":"员工009","age":40,"sex":"男","mobile":"19000009999","salary":23000,"deptName":"销售部","address":"河南省郑州市郑州大学","content":"i like java developer"}
{"index":{"_id":10}}
{"empId":"10","name":"张湖北","age":35,"sex":"男","mobile":"19000001010","salary":18000,"deptName":"测试部","address":"湖北省武汉市东湖高新","content":"i like java developer, i also like elasticsearch"}
{"index":{"_id":11}}
{"empId":"11","name":"王河南","age":61,"sex":"男","mobile":"19000001011","salary":10000,"deptName":"销售部","address":"河南省开封市河南大学","content":"i am not like java"}
{"index":{"_id":12}}
{"empId":"12","name":"张大学","age":26,"sex":"女","mobile":"19000001012","salary":11321,"deptName":"测试部","address":"河南省开封市河南大学","content":"i am java developer, java is good"}
{"index":{"_id":13}}
{"empId":"13","name":"李江汉","age":36,"sex":"男","mobile":"19000001013","salary":11215,"deptName":"销售部","address":"河南省郑州市二七区","content":"i like java and java is very best, i like it, do you like java"}
{"index":{"_id":14}}
{"empId":"14","name":"王技术","age":45,"sex":"女","mobile":"19000001014","salary":16222,"deptName":"测试部","address":"河南省郑州市金水区","content":"i like c++"}
{"index":{"_id":15}}
{"empId":"15","name":"张测试","age":18,"sex":"男","mobile":"19000001015","salary":20000,"deptName":"技术部","address":"河南省郑州市高新开发区","content":"i think spark is good"}

GET /employee/_search
{
  "query": {
    "multi_match": {
      "query": "elasticsearch beginner 湖北省 开封市",
      "type": "best_fields",
      "fields": ["content", "address"]
    }
  },
  "size": 15
}

# 查看执行计划

GET /employee/_explain/3
{
  "query": {
    "multi_match": {
      "query": "elasticsearch beginner 湖北省 开封市",
      "type": "best_fields",
      "fields": ["content", "address"]
    }
  }
}

GET /employee/_explain/3
{
  "query": {
    "multi_match": {
      "query": "elasticsearch beginner 湖北省 开封市",
      "type": "best_fields",
      "fields": ["content", "address"],
      "tie_breaker": 0.1
    }
  }
}
```

### 3.4 使用多数字段搜索（Most Fields）

#### (1) 介绍

使用全部匹配字段分数加总，作为文档的分数

等价于 bool should 查询方式

```text
GET /employee/_explain/3
{
  "query": {
    "multi_match": {
      "query": "elasticsearch beginner 湖北省 开封市",
      "type": "most_fields",
      "fields": ["content", "address"]
    }
  }
}
```

#### (2) 案例：一个不符合预期的查询

```text
DELETE /titles
PUT /titles
{
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "english",
        "fields": {  # 定义 title 的另一个版本，它用了不同的分词器
          "std": {
            "type": "text",
            "analyzer": "standard"
          }
        }
      }
    }
  }
}

POST titles/_bulk
{ "index": { "_id": 1 }}
{ "title": "My dog barks" }
{ "index": { "_id": 2 }}
{ "title": "I see a lot of barking dogs on the road" }

# 结果与预期不匹配：查询查的是 title，用的是 english 分词器，做了词干提取，没有给文档 2 提高权重
GET /titles/_search
{
  "query": {
    "match": {
      "title": "barking dogs"
    }
  }
}
```

这个例子用不同的分词器，为 title 字段提供了两个版本

文档 1: "My dog barks"

- english 分词器：["my", "dog", "bark"]
- standard 分词器：["My", "dog", "barks"]

文档 2: "I see a lot of barking dogs on the road"

- english 分词器：["i", "see", "lot", "bark", "dog", "road"]
- standard 分词器：["I", "see", "a", "lot", "of", "barking", "dogs", "on", "the", "road"]

我们查询用的字段是 title 而不是 title.std，导致的结果是匹配文档用的是 english 分词器。原本文档 2 与查询更接近，但是 english 把 barking 变成了 bark，导致文档 2 不能获得更高的权重

#### (3) 案例：用 type 为 most_fields 的 multi_match 来解决

期望：

* 用做了词干提取的 title 字段来匹配更多的文档，提升召回率。
* 用没做词干提取的 title.std 字段来找出相关度更高的文档，增加它们的权重，置于搜索结果顶部。

方法，用 type 为 most_fields 的 multi_mach 查询

例子如下：

```text
GET /titles/_search
{
  "query": {
    "multi_match": {
      "query": "barking dogs",
      "type": "most_fields",
      "fields": ["title", "title.std"]
    }
  }
}
```

#### (4) 案例：用 “^” 来自定义字段的权重

每个字段对于最终评分的贡献可以通过自定义值 boost 来控制。

下面的例子，提升了 title 字段的权重使其变得更重要，但同时这也降低了其它字段的重要性

```text
GET /titles/_search
{
  "query": {
    "multi_match": {
      "query": "barking dogs",
      "type": "most_fields",
      "fields": ["title^2", "title.std"] # 把 title 字段的权重增加到 2
    }
  }
}
```

### 3.5 把多个字段看做一个整体（Cross Fields）

#### (1) 介绍

方法：用 type 为 cross_fields 的 multi_match 来搜索

原理：它会先把所有指定的字段合并在一起，然后再进行分词和搜索。这种方式会把多个字段看做一个整体

#### (2) 案例：不符合预期的查询

```text
DELETE /address
PUT /address
{
  "settings": {
    "index": {
      "analysis.analyzer.default.type": "ik_max_word"
    }
  }
}

PUT /address/_bulk
{ "index": { "_id": "1" }}
{"province": "湖南","city": "长沙"}
{ "index": { "_id": "2" }}
{"province": "湖南","city": "常德"}
{ "index": { "_id": "3" }}
{"province": "广东","city": "广州"}
{ "index": { "_id": "4" }}
{"province": "湖南","city": "邵阳"}

# 使用 most_fields 不符合预期，它会先对查询条件“湖南常德”进行分词，然后分别与 provience 字段、city 字段进行相似度匹配，最后再把两个字段的分数进行加总。

# 它不能指定查询条件的匹配方式（即 operator 是 and 还是 or），这不是我们期望的。

GET /address/_search
{
  "query": {
    "multi_match": {
      "query": "湖南常德",
      "type": "most_fields",
      "fields": ["province", "city"]
    }
  }
}
```

#### (3) 案例：用 type 为 cross_fields 的 multi_match 来解决

解决办法是使用 cross_fields 进行查询，"operator": "and" 要求查询词分词后得到的 ["湖南", "常德"] 中，每个词都能在文档中被匹配。因此只有文档 2 符合条件，搜索结果里不再出现其他文档，满足了我们的需要。

```text
GET /address/_search
{
  "query": {
    "multi_match": {
      "query": "湖南常德",
      "type": "cross_fields",
      "operator": "and",
      "fields": ["province", "city"]
    }
  }
}
```

#### (4) 案例：用 copy_to 解决（有存储开销）

还可以用 copy_to 解决，copy_to 可以在插入文档时自动将指定的字段拷贝在一起。但是需要在 schema 中定义，也需要额外的存储空间

代码例子如下：

```text
DELETE /address
PUT /address
{
  "mappings": {
    "properties": {
      "province": {
        "type": "keyword",
        "copy_to": "full_address"
      },
      "city": {
        "type": "text",
        "copy_to": "full_address"
      }
    }
  },
  "settings": {
    "index": {
      "analysis.analyzer.default.type": "ik_max_word"
    }
  }
}

PUT /address/_bulk
{ "index": { "_id": "1" }}
{"province": "湖南","city": "长沙"}
{ "index": { "_id": "2" }}
{"province": "湖南","city": "常德"}
{ "index": { "_id": "3" }}
{"province": "广东","city": "广州"}
{ "index": { "_id": "4" }}
{"province": "湖南","city": "邵阳"}

GET /address/_search
{
  "query": {
    "match": {
      "full_address": {
        "query": "湖南常德",
        "operator": "and"
      }
    }
  }
}
```
