package com.ivan.search.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 跳转主页
 *
 * @author cuiyingfan
 * @date 2022/03/11
 */
@Controller
@Slf4j
public class HomeController {
    /**
     * 回家
     *
     * @return {@link String}
     */
    @GetMapping("/")
    public String toHome() {
        log.info("访问主页");
        return "home.html";
    }
}
