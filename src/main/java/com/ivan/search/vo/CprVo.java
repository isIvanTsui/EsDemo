package com.ivan.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author cuiyingfan
 * @date 2022/02/18
 */
@Data
public class CprVo {
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
    private List<Content> contents;

}
