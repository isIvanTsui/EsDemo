package com.ivan.search.controller;

import cn.hutool.core.util.NumberUtil;
import com.ivan.search.service.MySearchService;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @Autowired
    private MySearchService searchService;

    /**
     * 搜索
     *
     * @param keyword 关键字
     * @param current 当前
     * @param size    大小
     * @return {@link List}
     */
    @PostMapping("/search")
    public List search(String keyword, Integer current, Integer size) {
        current = 1;
        size = 9999;
        List list = searchService.search(keyword, current, size);
        return list;
    }

    /**
     * 创建索引和添加文z
     */
    @PostMapping("addIndexAndDoc")
    public void createIndexAndAddDocs() {
        searchService.createIndexAndMapping();
        searchService.addDoc();
    }
}
