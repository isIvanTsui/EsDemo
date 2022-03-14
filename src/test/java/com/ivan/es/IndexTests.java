package com.ivan.es;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;

/**
 * 索引的CRUD
 *
 * @author Ivan
 * @date 2022/03/10
 */
@SpringBootTest
public class IndexTests {
    @Autowired
    private RestHighLevelClient client;

    /**
     * 创建索引
     */
    @Test
    public void createIndex() throws IOException {
        CreateIndexResponse response = client.indices().create(new CreateIndexRequest("ivan"), RequestOptions.DEFAULT);
        //是否创建成功
        System.out.println("索引是否创建成功:" + response.isAcknowledged());
        // 查看返回对象
        System.out.println(response);
    }

    /**
     * 索引是否存在
     */
    @Test
    public void IndexIsExists() throws IOException {
        boolean exists = client.indices().exists(new GetIndexRequest("ivan"), RequestOptions.DEFAULT);
        System.out.println("索引存在吗：" + exists);
    }

    /**
     * 删除索引
     */
    @Test
    public void deleteIndex() throws IOException {
        AcknowledgedResponse response = client.indices().delete(new DeleteIndexRequest("ivan"), RequestOptions.DEFAULT);
        System.out.println("删除成功:" + response.isAcknowledged());
        System.out.println(response);
    }

    @Test
    public void add() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("user", "kimchy");
            builder.timeField("postDate", new Date());
            builder.field("message", "trying out Elasticsearch");
            builder.field("analyzer", "ik_max_word");
        }
        builder.endObject();
        IndexRequest request = new IndexRequest("posts1");
        request.id("1");
        request.source(builder);
        client.index(request, RequestOptions.DEFAULT);
    }
}
