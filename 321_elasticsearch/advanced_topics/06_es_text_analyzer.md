# Elasticsearch参考6：自定义文本分析器

## 1. 分词器概述

分词是构建倒排索引的重要一环，分类如下：

- 根据语言环境：英文分词、中文分词、……；
- 根据分词实现：标准分词器、空格分词器、停用词分词器、……

在传统的分词器不能解决特定业务场景的问题时，往往需要自定义分词器。

### 1.1 分词

分词：把句子拆分成单词的过程

英语单词容易辨认和区分，因为单词之间会以空格或者标点隔开：

```
1    you cannot use from and size to page through more than 10,000 hits
2    you / cannot / use / from / and / size / to / page / through / more / than / 10,000 / hits
```

中文单词、句子甚至段落之间没有空格。而同样几个文字，在不同语境中，拆分方式也不同。例如：

```
1   杭州市长春药店
2    杭州 / 市长  / 春药  / 店  (错误)
3   杭州市 / 长春  / 药店  (正确)
```

### 1.2 为什么需要中文分词

主要有如下3个维度：

- 语义维度：单字往往表达不了语义，而词可以。分词对语义分析有帮助
- 存储维度：按照词来索引和检索文章，比按照文字节省性能
- 时间维度：通过倒排索引，我们能以O(1)的时间复杂度，通过词组找到对应的文章。

例如：以“深入浅出Elasticsearch”这一字符串的检索为例。“深”“入”“浅”“出”这些字在全体内容中可能会无数次出现，如果以这些单独的字为索引，那么就需要添加无数条记录。而以“深入”为索引，所需记录就少了一些；以“深入浅出”为索引，则少得更多；最后以“深入浅出Elasticsearch”为索引，可能就剩余寥寥几条数据。但只有剩余的这些全字符匹配的文档才是我们期望召回的结果。

### 1.3 设计ES索引需要考虑分词

注意：设计索引的Mapping阶段，要根据业务用途确定是否需要分词。

- 如果不需要分词，则建议设置 keyword类型；
- 如果需要分词，则建议设置为text类型并指定分词器。

### 1.3 分词发生的阶段

#### (1) 写入数据阶段

分词发生在数据写入阶段，也就是数据索引化阶段，其分词逻辑取决于参数analyzer。例如，当使用ik_smart分词器，下面例子分词结果如下（用_analyze可以查看分词结果）：

```shell
POST _analyze
{
  "analyzer":"ik_max_word",
  "text":"昨天，小明和他的朋友们去了市中心的图书馆"
}
```

返回结果：

```shell
{
  "tokens": [
    {
      "token": "昨天",
      "start_offset": 0,
      "end_offset": 2,
      "type": "CN_WORD",
      "position": 0
    },
    {
      "token": "小明",
      "start_offset": 3,
      "end_offset": 5,
      "type": "CN_WORD",
      "position": 1
    },
    {
      "token": "和他",
      "start_offset": 5,
      "end_offset": 7,
      "type": "CN_WORD",
      "position": 2
    },
    {
      "token": "的",
      "start_offset": 7,
      "end_offset": 8,
      "type": "CN_CHAR",
      "position": 3
    },
    {
      "token": "朋友们",
      "start_offset": 8,
      "end_offset": 11,
      "type": "CN_WORD",
      "position": 4
    },
    {
      "token": "去了",
      "start_offset": 11,
      "end_offset": 13,
      "type": "CN_WORD",
      "position": 5
    },
    {
      "token": "市中心",
      "start_offset": 13,
      "end_offset": 16,
      "type": "CN_WORD",
      "position": 6
    },
    {
      "token": "的",
      "start_offset": 16,
      "end_offset": 17,
      "type": "CN_CHAR",
      "position": 7
    },
    {
      "token": "图书馆",
      "start_offset": 17,
      "end_offset": 20,
      "type": "CN_WORD",
      "position": 8
    }
  ]
}
```

#### (2) 执行检索阶段

检索阶段，分词仅对搜索词产生作用。例如在执行“春娇与志明”检索时，Elasticsearch会对“春娇与志明”进行分词，用分词得到的token去查询倒排索引。

## 2. 文本分析器的组成

Elasticsearch首先需要进行文本分析，然后才创建倒排索引。文本分析基于分词器（analyzer），可以是内置的、也可以是自定义的。

分词器由以下三部分组成：

* 字符过滤器（Characgter）
* 分词器（Tokenizer）
* 词项过滤器（Token Filter）

### 2.1 字符过滤器（Character Filter）

#### (1) 介绍

用于在分词之前的预处理阶段、过滤无用字符。 它会对输入的字符流，进行字符添加、删除、更改。 下面是一些 Character Filter

#### (2) HTML Strip Character Filter

删除文本中的HTML标签，同时可以通过“escaped_tags"配置例外，让某些标签不被移除、而是被转义，从而 保留写在标签内的文字。

例子如下：

```shell
DELETE example_index
PUT example_index 
{
  "settings": {
    "analysis": {
      "char_filter": {
        "my_char_filter": {  # 创建自定义字符过滤器
          "type": "html_strip",  # 类型是 Html Strip Characgter Filter
          "escaped_tags": [
            "a" # <a>标签不会被移除，而是被转义
          ]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "content": {
        "type": "text",
        "analyzer": "my_char_filter" # 为content字段使用自定义过滤器
      }
    }
  }  
}

GET example_index/_analyze
{
  "tokenizer": "standard",
  "char_filter": ["my_char_filter"],
  # 输出：This is my &lt;a href="https://page.me"&gt;homepage&lt;/a&gt.
  "text": ["<p>This is my <a href=\"https://page.me\">homepage</a>.</p>"]
}
```

#### (2) Mapping Character Filter

用于替换指定的字符。

```shell
DELETE example_index
PUT example_index
{
  "settings": {
    "analysis": {
      "char_filter": {
        "my_char_filter": { # 自定义 char filter 
          "type": "mapping", # 类型是 Mapping Character Filter 
          "mappings": [
            "滚  => *", # 配置字符串替换
            "垃  => *",
            "圾  => *"
          ]
        }
      }
    }
  }
}

GET example_index/_analyze
{
  "char_filter": ["my_char_filter"],
  "text": "你就是个垃圾！滚"
}
```

#### (4) Pattern Replace Character Filter

可以基于正则表达式替换指定的字符。

```shell
DELETE example_index
PUT example_index
{
  "settings": {
    "analysis": {
      "char_filter": {
        "my_char_filter": { # 自定义 char filter 
          "type": "pattern_replace",  # 类型是 Pattern Replace Character Filter
          "pattern": "(\\d{3})\\d{4}(\\d{4})", 
          "replacement": "$1****$2"
        }
      }
    }
  }
}

GET example_index/_analyze
{
  "char_filter": ["my_char_filter"], # 使用自定义char filter
  "text": "您的手机号是18868686688"
}
```

### 2.2 分词器（Tokenizer）

**输入**：如果用了Filter就是过滤后的字符流；如果没用Filter，就是原始字符流。

**操作**：对其进行分词，并记录分词后的顺序或位置(position)、起始值(start_offset)以及偏移量(end_offset-start_offset)

官方内置了很多种分词器，默认的切词器为 standard。

### 2.3 词项过滤器（Token Filter）

用来处理切词完成之后的词项，例如：大小写转换，删除停用词、同义词处理等。

官方同样预置了很多词项过滤器，基本可以满足日常开发的需要。也支持第三方词项过滤器。

#### (1) 大小写转换

```shell
GET _analyze
{
  "tokenizer": "standard",
  "filter": ["uppercase"],
  "text": ["www.elastic.org.cn","www elastic org cn"]
}
```

#### (2) 停用词

在切词完成之后，停用词会被删除。停用词可以自定义

英文停用词（english）：a, an, and, are, as, at, be, but, by, for, if, in, into, is, it, no, not, of, on, or, such, that, the, their, then, there, these, they, this, to, was, will, with。

中日韩停用词（cjk）：a, and, are, as, at, be, but, by, for, if, in, into, is, it, no, not, of, on, or, s, such,t, that, the, their, then, there, these, they, this, to, was, will, with.

```shell
GET _analyze
{
  "tokenizer": "standard",
  "filter": ["stop"],
  "text": ["What are you doing"]
}
```

#### (3) 自定义词项过滤器

```shell
DELETE example_index
PUT example_index
{
  "settings": {
    "analysis": {
      "filter": {
        "my_filter": {
          "type": "stop",
          "stopwords": [
            "www"
          ],
          "ignore_case": true
        }
      }
    }
  }
}

GET example_index/_analyze
{
  "tokenizer": "standard",
  "filter": ["my_filter"],
  "text": ["What www WWW are you doing"]
}
```

#### (4) 同义词

同义词定义规则：

- a, b, c => d：这种方式，a、 b、 c 会被 d 代替
- a, b, c, d：这种方式下，a、 b、 c、 d 是等价的

```shell
DELETE example_index
PUT example_index
{
  "settings": {
    "analysis": {
      "filter": {
        "my_synonym": {
          "type": "synonym",
          "synonyms": [
            "good, nice => excellent"
          ]
        }
      }
    }
  }
}

GET example_index/_analyze
{
  "tokenizer": "standard",
  "filter": ["my_synonym"],
  "text": ["good"]
}
```

### 2.4 例子：用自定义文本分析器实现对书籍作者的精确匹配

#### (1) 需求

索引有一个作者姓名的字段“name”

* 取值例如：Li、LeiLei、Han、MeiMei、LeiLei Li、……

* 要精确匹配

默认的分词器搜不出结果的例子

```shell
POST /booksdemo/_bulk
{"index":{"_id":1}}
{"name":"Li,LeiLei;Han,MeiMei"}  # 分词结果：["Li", "LeiLei", "Han", "MeiMei"]
{"index":{"_id":2}}
{"name": "LeiLei,Li;MeiMei,Han"} # 分词结果：["LeiLei", "Li", "MeiMei", "Han"]

# 查不出数据
POST /booksdemo/_search
{
  "query": {
    "match": {
      "name": "lileilei"  # 分词结果：["lileilei"]
    }
  }
}
```

如代码注释中的分词，这个查询查不出结果：

* 搜索词“lileilei”不会被分词

* 而文档1的“Li,LeiLei”是一个名字，但是被分成了两个词Li和Leilei

#### (2) 自定义文本分析器

```shell
DELETE /booksdemo
PUT /booksdemo
{
  "settings": {
    "analysis": {
      # 自定义字符过滤器
      "char_filter": {
        "my_char_filter": { 
          "type": "mapping",
          "mappings": [
            ", => "  # 首先把","过滤掉"，例如“Li,LeiLei”将变成LiLeiLei
          ]
        }
      },
      # 自定义分词器
      "tokenizer": {
        "my_tokenizer": {
          "type": "pattern", # 将";"作为自定义分词分隔符
          "pattern": ";"
        }
      },
      # 自定义Token过滤器
      "filter": {
        "my_synonym_filter": {
          "type": "synonym",
          "expand": true,
          "synonyms": [ # 添加同义词替换配置
            "leileili  => lileilei", # 将 leileili 替换为 lileilei
            "meimeihan => hanmeimei" # 将 meimeihan 替换为 hanmeimei。
          ]
        }
      },
      "analyzer": {
        # 自定义文本分析器，它使用了前面自定义的字符过滤器、分词器、Token过滤器
        "my_analyzer": {
          "tokenizer": "my_tokenizer",  # 自定义分词器
          "char_filter": [
            "my_char_filter" # 自定义字符过滤器
          ],
          "filter": [
            "lowercase",
            "my_synonym_filter" # 自定义token过滤器
          ]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "name": {
        "type": "text",
        "analyzer": "my_analyzer" # name 字段使用自定义文本分析器
      }
    }
  }
}
```

#### (3) 测试自定义分词器效果

```shell
# 借助 analyzer API 验证分词结果是否正确
POST booksdemo/_analyze
{
  "analyzer": "my_analyzer",
  "text": "Li,LeiLei;Han,MeiMei"
}

POST booksdemo/_analyze
{
  "analyzer": "my_analyzer",
  "text": "LeiLei,Li;MeiMei,Han"
}

POST /booksdemo/_bulk
{"index":{"_id":1}}
{"name":"Li,LeiLei;Han,MeiMei"}  # 分词结果：["lileilei", "han meimei"]
{"index":{"_id":2}}
{"name": "LeiLei,Li;MeiMei,Han"} # 分词结果：["leileili", "meimei han"]

POST /booksdemo/_search
{
  "query": {
    "match": {
      "name": "lileilei" # 索引使用了自定义文本分析器之后，用 lileilei 就能搜出结果了
    }
  }
}
```

## 3. Ngram自定义分词

### 3.1 用途

解决这样的问题：明明只想查询ID的一部分，但高亮结果是整个ID串，此时应该怎么办？

例如：当对keyword类型的字段进行高亮查询时，值为123asd456，查询sd4，则高亮结果是＜em＞123asd456＜em＞。那么，有没有办法只对sd4高亮呢？

### 3.2 问题演示

下面例子中，存储的数据是电话号码，对电话号码进行子串匹配，然后希望只对匹配部分高亮

分词器无法对电话号码分词完成子串匹配

wildcard能完成子串匹配，但是无法只对子串部分高亮

```shell
PUT my_index_phone
{
  "mappings": {
    "properties": {
      "phoneNum": {
        "type": "keyword" # 类型是keyword
      }
    }
  }
}

POST my_index_phone/_bulk
{"index":{"_id":1}}
{"phoneNum":"13611112222"}
{"index":{"_id":2}}
{"phoneNum":"13944248474"}

# 执行模糊检索和高亮相识
POST my_index_phone/_search
{
  "highlight": {
    "fields": {
      "phoneNum": {}
    }
  },
  "query": {
    "bool": {
      "should": [
        {
          "wildcard": {
            "phoneNum": "*1111*" # wildcard能完成子串匹配，却无法完成子串高亮
          }
        }
      ]
    }
  }
}
```

### 3.3 Ngram分词介绍

#### (1) 基本思想

基本思想如下：

按照字节大小进行滑动窗口操作，形成长度是N的字节片段序列，每个字节片段称为一个gram。

对所有gram进行词频统计，按照预设的阈值进行过滤，形成关键gram列表，作为文本的向量特征空间。列表中的每一种gram就是一个特征向量维度。

该模型基于这样一种假设，第N个词的出现只与前面N-1个词相关，而与其他任何词都不相关，整句的概率就是各个词出现概率的乘积。这些概率可以通过直接从语料中统计N个词同时出现的次数得到。

常用的是二元的Bi-Gram（二元语法）和三元的Tri-Gram（三元语法）。

以“你今天吃饭了吗“这一中文句子为例，它的Bi-Gram分词结果是：

```
["你今", "今天", "天吃", "吃饭", "饭了", "了吗"]
```

#### (2) Ngram分词应用场景

场景1：文本压缩、检查拼写错误、加速字符串查找、文献语种识别。

场景2：自然语言处理自动化领域得到新的应用。如自动分类、自动索引、超链的自动生成、文献检索、无分隔符语言文本的切分等。

场景3：自然语言的自动分类功能。针对Elasticsearch检索，Ngram针对无分隔符语言文本的分词（比如手机号 检索），可提高检索效率（相较于wildcard检索和正则匹配检索来说）。

### 3.4 Ngram分词使用实例

接下来用Ngram来解决前面的手机号码子串匹配局部高亮的需求：

```shell
DELETE my_index_phone
PUT my_index_phone
{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 0,
    "index.max_ngram_diff": 10,
    "analysis": {
      "analyzer": {
        "phoneNo_analyzer": { # 自定义文本分析器
          "tokenizer": "phoneNo_tokenizerr"  # 使用自定义分词器
        }
      },
      "tokenizer": {
        "phoneNo_tokenizerr": { # 自定义分词器
          "type": "ngram", # 使用ngram
          "min_gram": 4,   # ngram最小长度为4 
          "max_gram": 11,  # ngram最大长度为11
          "token_chars": [
            "letter",
            "digit" # 分词器识别字符类型：字母、数组
          ]
        }
      }
    }
  },
  "mappings": {
    "dynamic": "strict",
    "properties": {
      "phoneNo": {
        "type": "text",
        "analyzer": "phoneNo_analyzer" # 使用自定义文本分析器
      }
    }
  }
}

POST my_index_phone/_bulk
{"index":{"_id":1}}
{"phoneNo":"13611112222"}
{"index":{"_id":2}}
{"phoneNo":"13944248474"}

POST my_index_phone/_analyze
{
  "analyzer": "phoneNo_analyzer",
  "text": "13611112222"
}

POST my_index_phone/_search
{
  "highlight": {
    "fields": {
      "phoneNo": {}
    }
  },
  "query": {
    "bool": {
      "should": [
        {
          "match_phrase": {
            "phoneNo": "1111"
          }
        }
      ]
    }
  }
}
```

如上示例共有3个核心参数：

- min_gram ：最小字符长度（切分），默认为1。
- max_gram ：最大字符长度（切分），默认为2。
- token_chars ：表示生成的分词结果中包含的字符类型，默认是全部类型，而在如上的示例中代表保留数字、字母。若只指定letter分词器，则数字就会被过滤掉，分词结果只剩下串中的字符。

借助analyzer API查看分词结果：

```shell
POST my_index_phone/_analyze
{
  "analyzer": "phoneNo_analyzer",
  "text": "13611112222"
}
```

检索及高亮的执行语句如下：

```shell
POST my_index_phone/_search
{
  "highlight": {
    "fields": {
      "phoneNo": {}
    }
  },
  "query": {
    "bool": {
      "should": [
        {
          "match_phrase": {
            "phoneNo": "1111"
          }
        }
      ]
    }
  }
}
```

代码已经能满足检索和高亮的双重需求，解决了提出的问题。

### 3.5 Ngram也带来了性能开销

Ngram 分词器会将每个电话号码生成多个子串（gram），这会增加索引过程中的数据量和处理时间。例如，一个长度为 11 的电话号码会生成 8 个 4 长度的 gram（位置 1-4、2-5、...、8-11），7 个 5 长度的 gram，依此类推，直到 1 个 11 长度的 gram。

#### (1) 索引阶段

- 增加索引时间：相比于使用标准分词器，这显著增加了索引时需要处理的 token 数量。
- 增大索引存储空间：由于每个电话号码都被拆分成多个 gram 并存储在索引中，这会导致索引占用的存储空间增加。这不仅涉及到 gram 本身的数据，还包括相关的元数据和索引结构。

#### (2) 查询阶段

- 提高查询速度（对于部分匹配查询）：可以利用索引来快速定位包含这些 gram 的文档。例如，查找电话号码中包含“1234”的号码，不需要通配符计算、而是直接查索引
- 可能增加查询解析时间：如果查询条件比较复杂，涉及到多个 gram 的组合或模糊匹配等情况，查询解析的时间可能会增加。需要将查询字符串拆分成多个 gram，还可能产生复杂的匹配计