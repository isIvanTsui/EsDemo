package com.ivan.es;

import cn.hutool.json.JSONUtil;
import com.ivan.search.domain.User;
import com.ivan.search.mapper.CprMainMapper;
import com.ivan.search.vo.Content;
import com.ivan.search.vo.CprVo;
import com.ivan.search.vo.SearchOne;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 创建正式的全文检索索引
 *
 * @author cuiyingfan
 * @date 2022/03/11
 */
@SpringBootTest
public class FullTextSearchIndex {
    @Autowired
    private RestHighLevelClient client;

    @Resource
    private CprMainMapper cprMainMapper;

    /**
     * 创建全文搜索索引
     */
    @Test
    public void createFullTextSearchIndex() throws IOException {
        List<CprVo> drugs = cprMainMapper.getDrugs();
        ArrayList<SearchOne> list = new ArrayList<>();
        for (CprVo drug : drugs) {
            SearchOne searchOne = new SearchOne(drug.getCid(), drug.getTitle(), drug.getSortCode(), drug.getSearchName());
            StringBuilder builder = new StringBuilder();
            for (Content content : drug.getContents()) {
                builder.append(content.getSubtitle() + content.getContent());
            }
            searchOne.setContents(builder.toString());
            list.add(searchOne);
        }
        BulkRequest request = new BulkRequest();
        for (SearchOne one : list) {
            request.add(new IndexRequest("ivan").id(one.getCid() + "")
                    .source(JSONUtil.toJsonStr(one), XContentType.JSON));
        }
        BulkResponse responses = client.bulk(request, RequestOptions.DEFAULT);
        System.out.println(responses.status());
        System.out.println(responses);
    }

    /**
     * 完整测试搜索
     */
    @Test
    public void FullTestSearch() throws IOException {
        // 1.创建查询请求对象
        SearchRequest searchRequest = new SearchRequest();
        // 2.构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // (1)查询条件 使用QueryBuilders工具类创建
        // 精确查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title.keyword", "大柴胡成方");
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
