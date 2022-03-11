package com.ivan.search.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用来建索引的临时对象
 *
 * @author cuiyingfan
 * @date 2022/03/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchOne {
    private Integer cid;

    /**
     * 标题
     */
    private String title;

    /**
     * 排序码
     */
    private Integer sortCode;

    /**
     * 搜索关键字
     */
    private String searchName;

    /**
     * 内容
     */
    private String contents;

    public SearchOne(Integer cid, String title, Integer sortCode, String searchName) {
        this.cid = cid;
        this.title = title;
        this.sortCode = sortCode;
        this.searchName = searchName;
    }
}
