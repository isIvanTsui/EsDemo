package com.ivan.search.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * cpr主表 实体
 *
 * @author cuiyingfan
 * @date 2022/03/02
 */
@TableName(value = "cpr_main")
@Data
public class CprMain implements Serializable {

    /**
     * cprId
     */
    @TableId("cprid")
    private Integer cprId;

    /**
     * genId
     */
    @TableField("genid")
    private Integer genId;

    /**
     * 药品名Id
     */
    @TableField("drug_nameid")
    private Integer drugNameId;

    /**
     * cpr标题
     */
    @TableField("cpr_title")
    private String cprTitle;

    /**
     * 第一个Code
     */
    @TableField("first_code")
    private String firstCode;

    /**
     * 是警告药品吗
     */
    @TableField("is_warndrug")
    private Integer isWarnDrug;

    /**
     * 排序码
     */
    @TableField("sortcode")
    private Integer sortCode;

    /**
     * 开始日期
     */
    @TableField("launch_date")
    private String launchDate;

    /**
     * 编辑日期
     */
    @TableField("cpr_edit_time")
    private String cprEditTime;

    /**
     * 更新日期
     */
    @TableField("cpr_update_time")
    private String cprUpdateTime;

    /**
     * 查询名称
     */
    @TableField(value = "search_name")
    private String searchName;

    @TableField(exist = false)
    private String markName;

    @TableField(exist = false)
    private static final long serialVersionUID = 3946709673478420814L;
}