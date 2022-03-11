package com.ivan.es;

import cn.hutool.json.JSONUtil;
import com.ivan.search.domain.User;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 文档的CRUD
 *
 * @author Ivan
 * @date 2022/03/10
 */
@SpringBootTest
public class DocTests {
    @Autowired
    private RestHighLevelClient client;

    /**
     * 添加文档
     */
    @Test
    public void addDoc() throws IOException {
        User user = new User(1, "张三", 18);
        IndexRequest request = new IndexRequest("ivan");
        request.id(user.getUid() + "");
        request.timeout(TimeValue.timeValueMillis(1000));// request.timeout("1s")
        request.source(JSONUtil.toJsonStr(user), XContentType.JSON);
        // 将我们的数据放入请求中
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
        System.out.println(response);
    }

    /**
     * 批量添加文档
     */
    @Test
    public void addDocByBatch() throws IOException {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User(1, "张学友", 28));
        users.add(new User(2, "刘德华", 24));
        users.add(new User(3, "黎明", 38));
        users.add(new User(4, "周杰伦", 48));
        users.add(new User(5, "林俊杰", 22));
        users.add(new User(6, "周润发", 20));
        users.add(new User(7, "黄晓明", 17));
        users.add(new User(8, "古天乐", 33));
        BulkRequest request = new BulkRequest();
        for (User user : users) {
            request.add(new IndexRequest("user").id(user.getUid() + "")
                    .source(JSONUtil.toJsonStr(user), XContentType.JSON));
        }
        BulkResponse responses = client.bulk(request, RequestOptions.DEFAULT);
        System.out.println(responses.status());
        System.out.println(responses);
    }

    /**
     * 获取文档
     */
    @Test
    public void getDoc() throws IOException {
        GetResponse response = client.get(new GetRequest("ivan", "1"), RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
        System.out.println(response);
    }

    /**
     * 文档是否存在
     */
    @Test
    public void docIsExists() throws IOException {
        GetRequest request = new GetRequest("ivan", "1");
        // 不获取返回的 _source的上下文了
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        boolean exists = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * 更新文档
     */
    @Test
    public void updateDoc() throws IOException {
        User user = new User(1, "李四", 20);
        UpdateRequest request = new UpdateRequest("ivan", "1");
        request.doc(JSONUtil.toJsonStr(user), XContentType.JSON);
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
        System.out.println(response);
    }

    /**
     * 删除文档
     */
    @Test
    public void deleteDoc() throws IOException {
        DeleteResponse response = client.delete(new DeleteRequest("ivan", "1"), RequestOptions.DEFAULT);
        System.out.println(response.status());
        System.out.println(response);
    }

    /**
     * 全文检索
     */
    @Test
    public void fullSearch() throws IOException {
        // 1.创建查询请求对象
        SearchRequest searchRequest = new SearchRequest();
        // 2.构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // (1)查询条件 使用QueryBuilders工具类创建
        // 精确查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name.keyword", "周杰伦");
        //        // 匹配查询
        //        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        // (2)其他<可有可无>：（可以参考 SearchSourceBuilder 的字段部分）
        // 设置高亮
        searchSourceBuilder.highlighter(new HighlightBuilder());
        //        // 分页
        //        searchSourceBuilder.from();
        //        searchSourceBuilder.size();
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // (3)条件投入
        searchSourceBuilder.query(termQueryBuilder);
        // 3.添加条件到请求
        searchRequest.source(searchSourceBuilder);
        // 4.客户端查询请求
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        // 5.查看返回结果
        SearchHits hits = search.getHits();
        System.out.println(JSONUtil.toJsonStr(hits));
        System.out.println("=======================");
        for (SearchHit documentFields : hits.getHits()) {
            System.out.println(documentFields.getSourceAsMap());
        }
    }
}
