package com.ivan.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 跳转主页
 *
 * @author cuiyingfan
 * @date 2022/03/11
 */
@Controller
public class HomeController {
    /**
     * 回家
     *
     * @return {@link String}
     */
    @GetMapping("/")
    public String toHome() {
        return "home.html";
    }
}
