package com.ivan.search.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户
 *
 * @author Ivan
 * @date 2022/03/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * uid
     */
    private Integer uid;
    /**
     * 名字
     */
    private String name;
    /**
     * 年龄
     */
    private Integer age;
}
