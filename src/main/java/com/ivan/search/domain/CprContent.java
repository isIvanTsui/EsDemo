package com.ivan.search.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;


/**
 * 内容 实体
 *
 * @author cuiyingfan
 * @date 2022/03/03
 */
@TableName(value = "cpr_content")
@Data
public class CprContent implements Serializable {

    /**
     * cprId
     */
    @TableField("cprid")
    private Integer cprId;

    /**
     * cpr名字
     */
    @TableField("cpr_phname")
    private String cprPhname;

    /**
     * cpr序列
     */
    @TableField("cpr_seqnum")
    private Integer cprSeqnum;

    /**
     * cpr序列2
     */
    @TableField("cpr_seqnum2")
    private Integer cprSeqnumTwo;

    /**
     * cpr内容
     */
    @TableField(value = "cpr_content")
    private String cprContent;

    @TableField(exist = false)
    private static final long serialVersionUID = -1777781502377203442L;
}