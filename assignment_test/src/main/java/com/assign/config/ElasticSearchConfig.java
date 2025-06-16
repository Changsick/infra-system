package com.assign.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableElasticsearchRepositories(basePackages = "com.assign.repository.elasticsearch")
public class ElasticSearchConfig {
	
	@Value("${spring.elasticsearch.rest.uris}")
	String host;
	
	@Bean
    public ElasticsearchClient elasticsearchClient() {
        // RestClient를 사용하여 연결을 설정합니다.
        RestClient restClient = RestClient.builder(HttpHost.create(host)).build();

        // RestClientTransport를 사용하여 ElasticsearchTransport 설정
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // ElasticsearchClient 생성
        return new ElasticsearchClient(transport);
    }
	
}
