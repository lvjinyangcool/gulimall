package com.atguigu.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.search.bean.Account;
import com.atguigu.gulimall.search.bean.User;
import com.atguigu.gulimall.search.config.GuliMallElasticSearchConfig;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
class GulimallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient esClient;

    /**
     * 测试存储请求到es
     * 更新也可以
     */
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("uesrs");
        indexRequest.id("1");
        //第一种方式
//        indexRequest.source("userName","zhangsan","age",18,"gender","男");

        // 第2种方式
        User user = new User();
        user.setUserName("yang");
        user.setAge("20");
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        // 传入json时 指定类型
        indexRequest.source(jsonString, XContentType.JSON);

        //执行保存操作
        IndexResponse index = esClient.index(indexRequest, GuliMallElasticSearchConfig.COMMON_OPTIONS);

        System.out.println(index);
        System.out.println(index.status());
    }

    /**
     * 检索数据
     */
    @Test
    public void searchDataTest() throws IOException {
        //1.创建检索请求
        SearchRequest searchRequest = new SearchRequest();

        //指定索引
        searchRequest.indices("newbank");

        //指定DSL，检索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //1.1 构造检索条件
        sourceBuilder.query(QueryBuilders.matchQuery("address","mill"));

        // 1.2 按照年龄值分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        sourceBuilder.aggregation(ageAgg);

        //1.3 计算平均薪资
        AvgAggregationBuilder balanceAgg = AggregationBuilders.avg("balanceAvg").field("balance");
        sourceBuilder.aggregation(balanceAgg);

        System.out.println("检索条件:"+ sourceBuilder.toString());

        searchRequest.source(sourceBuilder);

        // 2.执行检索
        SearchResponse searchResponse = esClient.search(searchRequest, GuliMallElasticSearchConfig.COMMON_OPTIONS);

        //3.分析结果 searchResponse
        System.out.println(searchResponse.toString());
//		Map map = JSON.parseObject(searchResponse.toString(), Map.class);
//		System.out.println(map);
        //3.1 获取所有的查到的数据
        SearchHits hits = searchResponse.getHits();
        // 详细记录
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            //	String index = hit.getIndex();
            //	String id = hit.getId();
            String source = hit.getSourceAsString();
            Account account = JSON.parseObject(source, Account.class);
            System.out.println(account);
        }

        // 获取分析数据
        Aggregations aggregations = searchResponse.getAggregations();
//		List<Aggregation> list = aggregations.asList();
//        for (Aggregation aggregation : list) {
//            Terms agg = aggregations.get(aggregation.getName());
//            System.out.println(agg.getBuckets());
//        }
        Terms age= aggregations.get("ageAgg");
        for (Terms.Bucket bucket : age.getBuckets()) {
            System.out.println("年龄: " + bucket.getKeyAsString() + "-->" + bucket.getDocCount() + "人");
        }

        Avg avg = aggregations.get("balanceAvg");
        System.out.println("平均薪资： " + avg.getValue());

    }


    @Test
    public void contextLoads() {

        System.out.println(esClient);
    }

}
