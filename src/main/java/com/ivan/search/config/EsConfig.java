package com.ivan.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * es配置
 *
 * @author cuiyingfan
 * @date 2022/03/10
 */
@Configuration
public class EsConfig {
    /**
     * 地址
     */
    @Value("${es.ip}")
    private String ip;

    /**
     * 端口
     */
    @Value("${es.port}")
    private Integer port;

    /**
     * 将Elasticsearch的Client加入到Spring容器中
     *
     * @return {@link RestHighLevelClient}
     */
    @Bean
    public RestHighLevelClient getRestHighLevelClient() {
        RestClientBuilder builder = RestClient.builder(new HttpHost(ip, port, HttpHost.DEFAULT_SCHEME_NAME));
        return new RestHighLevelClient(builder);
    }
}
