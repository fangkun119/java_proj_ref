---
title: Elasticsearch参考9：Spring Boot 整合 ES8.X
author: fangkun119
date: 2025-06-10 18:00:00 +0800
categories: [中间件, Elasticsearch]
tags: [elasticsearch]
pin: true
math: true
mermaid: true
image:
  path: /imgs/cover/elasticsearch.svg
  lqip: data:image/webp;base64,UklGRpoAAABXRUJQVlA4WAoAAAAQAAAADwAABwAAQUxQSDIAAAARL0AmbZurmr57yyIiqE8oiG0bejIYEQTgqiDA9vqnsUSI6H+oAERp2HZ65qP/VIAWAFZQOCBCAAAA8AEAnQEqEAAIAAVAfCWkAALp8sF8rgRgAP7o9FDvMCkMde9PK7euH5M1m6VWoDXf2FkP3BqV0ZYbO6NA/VFIAAAA
  alt: Responsive rendering of Chirpy theme on multiple devices.
---

{: .no_toc }

<details close markdown="block">
  <summary>
    目录
  </summary>
  {: .text-delta }
- TOC
{:toc}
</details>

### 1. Spring Data Elasticsearch的介绍

Spring Data Elasticsearch 基于 spring data API 简化 Elasticsearch 操作，提供搜索引擎集成。

对Native Elasticsearch Client API 进行封装，支持轻松地编写Elasticsearch 数据访问层。

官方网站: https://spring.io/projects/spring-data-elasticsearch

### 2. Spring Boot整合Spring Data Elasticsearch

### 2.1 版本选型

- Elasticsearch 8.14.x 对应依赖是：Spring Data Elasticsearch 5.3.x
- Spring Data Elasticsearch 5.3.x 对应：
  - Spring6.1.x
  - Spring Boot 3.3.x

### 2.2 引入依赖

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

如果Spring Boot版本选择3.3.2，对应的Spring Data Elasticsearch为5.3.2。

### 2.3 配置ElasticSearch

两种方式，选择一种即可：

#### 2.3.1 方式1：yml配置

```yaml
spring:
  elasticsearch:
    uris: http://localhost:9200
    connection-timeout: 3s
```

#### 2.3.2 方式2: @Configuration配置

```java
@Configuration
public class MyESClientConfig extends ElasticsearchConfiguration {
  @Override
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder().connectedTo("localhost:9200").build();
  }
}
```

### 2.4 方式1：使用ElasticsearchRepository

#### (1) 介绍

ElasticsearchRepository是Spring Data Elasticsearch项目中的一个接口

它用于简化对Elasticsearch的集成，包括集群的CRUD以及高级搜索功能。

它允许开发者通过声明式编程模型来执行操作，从而避免直接编写复杂的REST API调用代码。

#### (2) 创建实体类

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "employees")
public class Employee {
  @Id
  private Long id;

  @Field(type = FieldType.Keyword)
  private String name;

  private int sex;

  private int age;

  @Field(type = FieldType.Text, analyzer = "ik_max_word")
  private String address;

  private String remark;
}
```

#### (3) 实现ElasticsearchRepository接口

```java
@Repository
public interface EmployeeRepository extends ElasticsearchRepository<Employee, Long> {
  List<Employee> findByName(String name);
}
```

#### (4) 测试

```java
@Autowired
EmployeeRepository employeeRepository;

@Test
public void testDocument() {
  Employee employee = new Employee(10L, "fox666", 1, 32, "长沙麓谷", "java architect");
  //插入文档
  employeeRepository.save(employee);

  //根据id查询
  Optional<Employee> result = employeeRepository.findById(10L);
  if (!result.isEmpty()) {
    log.info(String.valueOf(result.get()));
  }

  //根据name查询
  List<Employee> list = employeeRepository.findByName("fox666");
  if (!list.isEmpty()) {
    log.info(String.valueOf(list.get(0)));
  }
}
```

更多用法见官方文档：[Spring Data Elasticsearch 官方文档](https://docs.spring.io/spring-data/elasticsearch/reference/elasticsearch/repositories/elasticsearch-repository-queries.html)

### 2.5 方式2：使用ElasticsearchTemplate

#### (1) 介绍

ElasticsearchTemplate模板类，封装了便捷操作Elasticsearch的模板方法，包括 索引 / 映射 / 文档 CRUD 等底层操作和高级操作。

```java
1   @Autowired
2    ElasticsearchTemplate elasticsearchTemplate;
```

#### (2) 诞生背景

从 Java Rest Client 7.15.0 版本开始，Elasticsearch 官方决定将 RestHighLevelClient 标记为废弃 的，并推荐使用新的 Java API Client，即 ElasticsearchClient。

Spring Data ElasticSearch 对 ElasticsearchClient做了进一步的封装，成了新的客户端 ElasticsearchTemplate

#### (3) 例子1：创建索引并查看索引元数据

```java
@Slf4j
public class ElasticsearchClientTest extends VipEsDemoApplicationTests {

  @Autowired
  ElasticsearchTemplate elasticsearchTemplate;

  @Test
  public void testCreateIndex() {
    // 索引是否存在
    boolean exist = elasticsearchTemplate.indexOps(Employee.class).exists();
    if (exist) {
      // 删除索引
      elasticsearchTemplate.indexOps(Employee.class).delete();
    }

    // 创建索引
    // 1）配置settings
    Map<String, Object> settings = new HashMap<>();
    settings.put("number_of_shards", 1);
    settings.put("number_of_replicas", 1);

    // 2) 配置mapping
    String json = "{\n" +
        "     \"properties\": {\n" +
        "       \"_class\": {\n" +
        "         \"type\": \"text\",\n" +
        "         \"fields\": {\n" +
        "           \"keyword\": {\n" +
        "              \"type\": \"keyword\",\n" +
        "            \"ignore_above\": 256\n" +
        "             }\n" +
        "           }\n" +
        "       },\n" +
        "       \"address\": {\n" +
        "         \"type\": \"text\",\n" +
        "         \"fields\": {\n" +
        "           \"keyword\": {\n" +
        "            \"type\": \"keyword\"\n" +
        "           }\n" +
        "         },\n" +
        "          \"analyzer\": \"ik_max_word\"\n" +
        "       },\n" +
        "       \"age\": {\n" +
        "          \"type\": \"integer\"\n" +
        "       },\n" +
        "       \"id\": {\n" +
        "         \"type\": \"long\"\n" +
        "       },\n" +
        "       \"name\": {\n" +
        "          \"type\": \"keyword\"\n" +
        "       },\n" +
        "       \"remark\": {\n" +
        "         \"type\": \"text\",\n" +
        "         \"fields\": {\n" +
        "           \"keyword\": {\n" +
        "            \"type\": \"keyword\"\n" +
        "           }\n" +
        "         },\n" +
        "          \"analyzer\": \"ik_smart\"\n" +
        "       },\n" +
        "       \"sex\": {\n" +
        "          \"type\": \"integer\"\n" +
        "       }\n" +
        "     }\n" +
        "   }";

    Document mapping = Document.parse(json);

    // 3) 创建索引
    elasticsearchTemplate.indexOps(Employee.class).create(settings, mapping);

    // 查看索引mappings信息
    Map<String, Object> mappings = elasticsearchTemplate.indexOps(Employee.class).getMapping();
    log.info(mappings.toString());
  }
}
```

#### (4) 例子2：Bulk Insert / Delete / Insert / Get

```java
@Test
public void testBulkBatchInsert() {
  List<Employee> employees = new ArrayList<>();
  employees.add(new Employee(2L, "张三", 1, 25, "广州天河公园", "java developer"));
  employees.add(new Employee(3L, "李四", 1, 28, "广州荔湾大厦", "java assistant"));
  employees.add(new Employee(4L, "小红", 0, 26, "广州白云山公园", "php developer"));

  List<IndexQuery> bulkInsert = new ArrayList<>();
  for (Employee employee : employees) {
    IndexQuery indexQuery = new IndexQuery();
    indexQuery.setId(String.valueOf(employee.getId()));
    String json = JSONObject.toJSONString(employee);
    indexQuery.setSource(json);
    bulkInsert.add(indexQuery);
  }

  // bulk批量插入文档
  elasticsearchTemplate.bulkIndex(bulkInsert, Employee.class);
}

@Test
public void testDocument() {
  // 根据id删除文档
  // 对应： DELETE /employee/_doc/12
  elasticsearchTemplate.delete(String.valueOf(12L), Employee.class);

  // 插入文档
  Employee employee = new Employee(12L, "张三三", 1, 25, "广州天河公园", "java developer");
  elasticsearchTemplate.save(employee);

  // 根据id查询文档
  // 对应：GET /employee/_doc/12
  Employee emp = elasticsearchTemplate.get(String.valueOf(12L), Employee.class);
  log.info(String.valueOf(emp));
}
```

#### (5) 例子3：term / match / 分页高亮排序 / bool

```java
@Test
public void testQueryDocument() {
  // 条件查询
  Query query = NativeQuery.builder()
      .withQuery(q -> q.term(t -> t.field("name").value("张三")))
      .build();

  SearchHits<Employee> search = elasticsearchTemplate.search(query, Employee.class);
  List<SearchHit<Employee>> searchHits = search.getSearchHits();
  for (SearchHit hit : searchHits) {
    log.info("返回结果： " + hit.toString());
  }
}

@Test
public void testMatchQueryDocument() {
  Query query = NativeQuery.builder()
      .withQuery(q -> q.match(m -> m.field("address").query("广州公园").minimumShouldMatch("2")))
      .build();

  SearchHits<Employee> search = elasticsearchTemplate.search(query, Employee.class);
  List<SearchHit<Employee>> searchHits = search.getSearchHits();
  for (SearchHit hit : searchHits) {
    log.info("返回结果： " + hit.toString());
  }
}

@Test
public void testQueryDocument3() {
  Query query = new StringQuery("{\n" +
      "       \"match\": {\n" +
      "         \"remark\": {\n" +
      "           \"query\": \"JAVA\"\n" +
      "         }\n" +
      "       }\n" +
      "     }");
  query.setPageable(PageRequest.of(0, 3));
  query.addSort(Sort.by(Order.desc("age")));

  HighlightField highlightField = new HighlightField("*");
  HighlightParameters highlightParameters = new HighlightParameters.HighlightParametersBuilder()
      .withPreTags("<font color='red'>")
      .withPostTags("<font/>")
      .withRequireFieldMatch(false)
      .build();
  Highlight highlight = new Highlight(highlightParameters, Arrays.asList(highlightField));
  HighlightQuery highlightQuery = new HighlightQuery(highlight, Employee.class);
  query.setHighlightQuery(highlightQuery);

  SearchHits<Employee> search = elasticsearchTemplate.search(query, Employee.class);
  List<SearchHit<Employee>> searchHits = search.getSearchHits();
  for (SearchHit hit : searchHits) {
    log.info("返回结果： " + hit.toString());
  }
}

@Test
public void testBoolQueryDocument() {
  Query query = NativeQuery.builder()
      .withQuery(q -> q.bool(m -> m.must(
          QueryBuilders.match(q1 -> q1.field("address").query("广州")),
          QueryBuilders.match(q2 -> q2.field("remark").query("java"))
      )))
      .build();

  SearchHits<Employee> search = elasticsearchTemplate.search(query, Employee.class);
  List<SearchHit<Employee>> searchHits = search.getSearchHits();
  for (SearchHit hit : searchHits) {
    log.info("返回结果： " + hit.toString());
  }
}
```

### 2.6 方式3：使用ElasticsearchClient

#### (1) 介绍

上一小节ElasticsearchTemplate就是用ElastisearchClient实现的，ElasticsearchClient是一个更底层的接口。

官网文档：[Elasticsearch Java API Client 官方文档](https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/8.14/getting-started-java.html)

#### (2) 诞生背景

从 Java Rest Client 7.15.0 版本开始，Elasticsearch 官方决定将 RestHighLevelClient 标记为废弃的， 并推荐使用新的 Java API Client，即 ElasticsearchClient.

#### (3) 例子：索引 / Bulk插入 / 普通插入 / match

```java
@Autowired
ElasticsearchClient elasticsearchClient;
String indexName = "employee_demo";

@Test
public void testCreateIndex() throws IOException {
  // 索引是否存在
  BooleanResponse exist = elasticsearchClient.indices().exists(e -> e.index(indexName));
  if (exist.value()) {
    // 删除索引
    elasticsearchClient.indices().delete(d -> d.index(indexName));
  }

  // 创建索引
  elasticsearchClient.indices().create(c -> c.index(indexName)
      .settings(s -> s.numberOfShards("1").numberOfReplicas("1"))
      .mappings(m -> m.properties("name", p -> p.keyword(k -> k))
          .properties("sex", p -> p.long_(l -> l))
          .properties("address", p -> p.text(t -> t.analyzer("ik_max_word")))
      )
  );

  // 查询索引
  GetIndexResponse getIndexResponse = elasticsearchClient.indices().get(g -> g.index(indexName));
  log.info(getIndexResponse.result().toString());
}

@Test
public void testBulkBatchInsert() throws IOException {
  List<Employee> employees = new ArrayList<>();
  employees.add(new Employee(2L, "张三", 1, 25, "广州天河公园", "java developer"));
  employees.add(new Employee(3L, "李四", 1, 28, "广州荔湾大厦", "java assistant"));
  employees.add(new Employee(4L, "小红", 0, 26, "广州白云山公园", "php developer"));

  List<BulkOperation> list = new ArrayList<>();
  for (Employee employee : employees) {
    BulkOperation bulkOperation = new BulkOperation.Builder()
        .create(c -> c.id(String.valueOf(employee.getId())).document(employee))
        .build();
    list.add(bulkOperation);
  }

  // bulk批量插入文档
  elasticsearchClient.bulk(b -> b.index(indexName).operations(list));
}

@Test
public void testDocument() throws IOException {
  Employee employee = new Employee(12L, "张三三", 1, 25, "广州天河公园", "java developer");
  IndexRequest<Employee> request = IndexRequest.of(i -> i
      .index(indexName)
      .id(employee.getId().toString())
      .document(employee)
  );
  IndexResponse response = elasticsearchClient.index(request);
  log.info("response:" + response);
}

@Test
public void testQuery() throws IOException {
  SearchRequest searchRequest = SearchRequest.of(s -> s
      .index(indexName)
      .query(q -> q.match(m -> m.field("name").query("张三三")))
  );

  log.info("构建的DSL语句:" + searchRequest.toString());
  SearchResponse<Employee> searchResponse = elasticsearchClient.search(searchRequest, Employee.class);
  List<Hit<Employee>> hits = searchResponse.hits().hits();
  hits.stream().map(Hit::source).forEach(employee -> {
    log.info("员工信息:" + employee);
  });
}

@Test
public void testBoolQueryDocument() throws IOException {
  BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
  boolQueryBuilder.must(m -> m.match(q -> q.field("address").query("广州")))
      .must(m -> m.match(q -> q.field("remark").query("java")));

  SearchRequest searchRequest = new SearchRequest.Builder()
      .index("employee")
      .query(q -> q.bool(boolQueryBuilder.build()))
      .build();

  SearchResponse<Employee> searchResponse = elasticsearchClient.search(searchRequest, Employee.class);
  List<Hit<Employee>> list = searchResponse.hits().hits();
  for (Hit<Employee> hit : list) {
    log.info(String.valueOf(hit.source()));
  }
}
```