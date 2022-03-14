package com.ivan.es;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ivan.search.mapper.CprMainMapper;
import com.ivan.search.vo.Content;
import com.ivan.search.vo.CprVo;
import com.ivan.search.vo.SearchOne;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
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
     * 添加索引
     */
    @Test
    public void addDoc() throws IOException {
        //查询出来是一个1对多的结构
        List<CprVo> drugs = cprMainMapper.getDrugs();
        //将它转换为我们需要的结构
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
     * 创建索引映射
     * 自定义创建索引
     *
     * @throws IOException ioexception
     */
    @Test
    public void createIndexMapping() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties")
                /*cid字段*/
                .startObject("cid")
                .field("type", "long")
                .field("store", true)
                .endObject()

                /*title字段*/
                .startObject("title")
                //字段类型
                .field("type", "text")
                //是否存储
                .field("store", true)
                //插入时分词
                .field("analyzer", "ik_smart")
                //搜索时分词
                .field("search_analyzer", "ik_max_word")
                .endObject()

                /*contents 字段*/
                .startObject("contents")
                .field("type", "text")
                .field("store", true)
                .field("analyzer", "ik_smart")
                .field("search_analyzer", "ik_max_word")
                .endObject()

                //sortCode字段
                .startObject("sortCode")
                .field("type", "long")
                .field("store", true)
                .endObject()

                /*searchName 字段*/
                .startObject("searchName")
                .field("type", "text")
                .field("store", true)
                .field("analyzer", "ik_smart")
                .field("search_analyzer", "ik_max_word")
                .endObject()

                .endObject()
                .endObject();
        AcknowledgedResponse response = client.indices().putMapping(new PutMappingRequest("ivan").source(builder), RequestOptions.DEFAULT);
        System.out.println(response.isAcknowledged());
        System.out.println(response);
    }

    /**
     * 完整测试搜索
     */
    @Test
    public void FullTestSearch() throws IOException {
        String index = "ivan";
        String keyword = "方";
        // 搜索请求
        SearchRequest searchRequest;
        if (StrUtil.isEmpty(index)) {
            searchRequest = new SearchRequest();
        } else {
            searchRequest = new SearchRequest(index);
        }
        // 条件构造
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 第几页
        searchSourceBuilder.from(0);
        // 每页多少条数据
        searchSourceBuilder.size(1000);
        // 配置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title").field("contents");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        // 精确查询
//        QueryBuilders.termQuery();
        // 匹配所有
//        QueryBuilders.matchAllQuery();
        // 最细粒度划分：ik_max_word，最粗粒度划分：ik_smart
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(keyword, "title", "contents").analyzer("ik_max_word"));
//        searchSourceBuilder.query(QueryBuilders.matchQuery("content", keyWord));
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(10));

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        List<Map<String, Object>> results = new ArrayList<>();
        for (SearchHit searchHit : searchResponse.getHits().getHits()) {
            Map<String, HighlightField> highlightFieldMap = searchHit.getHighlightFields();
            HighlightField title = highlightFieldMap.get("title");
            HighlightField description = highlightFieldMap.get("contents");
            // 原来的结果
            Map<String, Object> sourceMap = searchHit.getSourceAsMap();
            // 解析高亮字段，替换掉原来的字段
            if (title != null) {
                Text[] fragments = title.getFragments();
                StringBuilder n_title = new StringBuilder();
                for (Text text : fragments) {
                    n_title.append(text);
                }
                sourceMap.put("title", n_title.toString());
            }
            if (description != null) {
                Text[] fragments = description.getFragments();
                StringBuilder n_description = new StringBuilder();
                for (Text text : fragments) {
                    n_description.append(text);
                }
                sourceMap.put("contents", n_description.toString());
            }
            results.add(sourceMap);
        }
        System.out.println(results);
    }


}
