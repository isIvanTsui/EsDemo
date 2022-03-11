package com.ivan.search.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全文检索Vo
 *
 * @author cuiyingfan
 * @date 2022/03/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchVo {
    /**
     * 关键字
     */
    private String keyword;
    /**
     * 当前
     */
    private Long current = 1L;
    /**
     * 大小
     */
    private Long size = 10L;
}
