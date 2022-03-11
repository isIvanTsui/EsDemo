package com.ivan;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EsDemoApplicationTests {
    @Autowired
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
        System.out.println(client);
        System.out.println("aaa");
    }

}
