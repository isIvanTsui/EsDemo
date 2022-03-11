package com.ivan.search.controller;

import cn.hutool.json.JSONUtil;
import com.ivan.search.vo.SearchVo;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 搜索控制器
 *
 * @author cuiyingfan
 * @date 2022/03/11
 */
@RestController
public class SearchController {
    @Autowired
    private RestHighLevelClient client;

    @GetMapping("/search")
    public List<Map<String, Object>> highlightSearch(@RequestBody SearchVo searchVo) throws IOException {
        // 1.创建查询请求对象
        SearchRequest searchRequest = new SearchRequest();
        // 2.构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // (1)查询条件 使用QueryBuilders工具类创建
        // 精确查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title.keyword", searchVo.getKeyword());
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
        return null;
    }
}
