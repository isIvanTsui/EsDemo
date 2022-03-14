package com.ivan.es;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Date;

/**
 * 创建索引测试
 *
 * @author cuiyingfan
 * @date 2022/03/14
 */
@SpringBootTest
public class CreateIndexTest {
    @Autowired
    private RestHighLevelClient client;

    /**
     * 创建索引
     */
    @Test
    public void createIndex() throws IOException {
        // 1、创建 创建索引request 参数：索引名mess
        CreateIndexRequest request = new CreateIndexRequest("mess1");

        // 2、设置索引的settings
        request.settings(Settings.builder().put("index.number_of_shards", 3) // 分片数
                .put("index.number_of_replicas", 2) // 副本数
                .put("analysis.analyzer.default.tokenizer", "ik_smart") // 默认分词器
        );

        // 3、设置索引的mappings
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("user", "中国人");
            builder.field("postDate", new Date());
            builder.field("message", "这是测试用例");
        }
        builder.endObject();
        request.source(builder);

        // 5、 发送请求
        // 5.1 同步方式发送请求
        CreateIndexResponse createIndexResponse = client.indices()
                .create(request, RequestOptions.DEFAULT);

        // 6、处理响应
        boolean acknowledged = createIndexResponse.isAcknowledged();
        boolean shardsAcknowledged = createIndexResponse
                .isShardsAcknowledged();
        System.out.println("acknowledged = " + acknowledged);
        System.out.println("shardsAcknowledged = " + shardsAcknowledged);

    }

    @Test
    public void f() throws IOException {
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                        .startObject("properties")
                            .startObject("house_id")
                            .field("type", "long")
                            .field("store", true)
                            .endObject()
                            .startObject("house_guid")
                            .field("type", "text")
                            .field("store", true)
                            .field("analyzer", "ik_smart")
                            .endObject()
                            .startObject("house_name")
                            .field("type", "text")
                            .field("store", true)
                            .field("analyzer", "ik_smart")
                            .endObject()
                        .endObject()
                .endObject();
        client.indices().putMapping(new PutMappingRequest("ivan").source(xContentBuilder), RequestOptions.DEFAULT);
    }
}
